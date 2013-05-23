package org.cogroo.tools.headfinder;

import opennlp.tools.util.SequenceValidator;

import org.cogroo.tools.featurizer.WordTag;

public class HeadFinderSequenceValidator implements SequenceValidator<WordTag>{

  @Override
  public boolean validSequence(int i, WordTag[] inputSequence,
      String[] outcomesSequence, String outcome) {
    
    int size = inputSequence.length;
    String[] chunkTags = new String[size];
    String[] posTags = new String[size];
    String[] lexemes = new String[size];
    
    WordTag.extract(inputSequence, lexemes, posTags, chunkTags);
    
    // if it is boundary, accept any
    if(isBoundary(chunkTags[i])) {
      return true;
    }
    
    boolean isHead = isHead(outcome); 
    
    // only chunks has head
    if(chunkTags[i].equals("O") && isHead) {
      return false;
    }
    
    if(isIntermediate(chunkTags[i]) && isHead) {
      // only if it is the only head...
      boolean foundBoundary = false;
      for(int j = i - 1; j >= 0 && !foundBoundary; j--) {
        if(isHead(outcomesSequence[j])) {
          return false;
        }
        foundBoundary = isBoundary(chunkTags[j]);
      }
    }
    
    return true;
  }

  private boolean isIntermediate(String tag) {
    return tag.startsWith("I-");
  }

  private boolean isBoundary(String tag) {
    return tag.startsWith("B-");
  }

  private boolean isHead(String outcome) {
    return !outcome.equals("O");
  }


}
