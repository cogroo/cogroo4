package org.cogroo.tools.shallowparser;

import org.cogroo.tools.chunker2.DefaultChunkerSequenceValidator;
import org.cogroo.tools.featurizer.WordTag;

public class ShallowParserSequenceValidator extends DefaultChunkerSequenceValidator {
  
  @Override
  public boolean validSequence(int i, WordTag[] inputSequence,
      String[] outcomesSequence, String outcome) {
    boolean isValid = validOutcome(outcome, outcomesSequence);
    return isValid && validateNested(i, inputSequence, outcomesSequence, outcome);
  }

  private boolean validateNested(int i, WordTag[] inputSequence, String[] outcomesSequence, String outcome) {
    // we can't start a new sequence if we are in the middle of a chunk
    if(i > 0) {
      String previousChunk = extractChunk(inputSequence[i-1].getPostag());
      String chunk = extractChunk(inputSequence[i].getPostag());
      
      if(isContinuation(previousChunk, chunk)) {
        if(isOther(outcomesSequence[i-1], outcome)) {
          return true;
        } else if(!isContinuation(outcomesSequence[i-1], outcome)) {
          return false;  
        }
      }
      
    }
    return true;
  }

  private boolean isOther(String a, String b) {
    return "O".equals(a) && "O".equals(b);
  }

  private boolean isContinuation(String a, String b) {
    if((a.startsWith("B-") || a.startsWith("I-")) && b.startsWith("I-") ) 
      return true;
    return false;
  }

  private String extractChunk(String postag) {
    int i = postag.indexOf('|');
    return postag.substring(i + 1);
  }
  

 }
