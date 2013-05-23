package org.cogroo.tools.shallowparser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.cogroo.tools.chunker2.DefaultChunkerSequenceValidator;
import org.cogroo.tools.featurizer.WordTag;

public class ShallowParserSequenceValidator extends DefaultChunkerSequenceValidator {
  
  private static final Set<String> PRONOMES_OBLIQUOS; 
  static {
    String[] pronomes_obliq = {"me", "te", "nos", "vos", "o", "os", "a", "as", /*"se",*/
        "lhe", "lhes",  "mim", "ti", "comigo", "contigo", "conosco", "convosco", "consigo", "sí"};
    PRONOMES_OBLIQUOS = new HashSet<String>(Arrays.asList(pronomes_obliq));
  }
  
  @Override
  public boolean validSequence(int i, WordTag[] inputSequence,
      String[] outcomesSequence, String outcome) {
    boolean isValid = validOutcome(outcome, outcomesSequence);
    isValid = isValid && validateNested(i, inputSequence, outcomesSequence, outcome);
    isValid = isValid && validateSubj(i, inputSequence, outcomesSequence, outcome);
    return isValid;
  }

  private boolean validateSubj(int i, WordTag[] inputSequence,
      String[] outcomesSequence, String outcome) {
    // check if previous outcome was SUBJ, and if yes, we can't close it with only an article
    if(i > 0) {
      if(outcomesSequence[i-1].equals("B-SUBJ") && !outcome.equals("I-SUBJ")) { // this checks singleton subjects
        if(inputSequence[i-1].getPostag().startsWith("art") || isPronObli(inputSequence[i-1])) {
          return false;
        }
      }
      
//      if(outcomesSequence[i-1].endsWith("SUBJ") && outcome.equals("B-SUBJ")) {
//         return false;
//      }
      
      if(outcomesSequence[i-1].endsWith("SUBJ") && !outcome.endsWith("SUBJ") && inputSequence[i-1].getPostag().equals(",")) {
        return false;
     }
    }
    return true;
  }

  private boolean isPronObli(WordTag wordTag) {
    if(PRONOMES_OBLIQUOS.contains(wordTag.getWord().toLowerCase()) && extractPOS(wordTag.getPostag()).equals("pron-pers")) {
//      System.out.println(wordTag.getWord().toLowerCase());
      return true;
    }
    return false;
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
  
  private String extractPOS(String postag) {
    int i = postag.indexOf('|');
    return postag.substring(0, i);
  }
  

 }
