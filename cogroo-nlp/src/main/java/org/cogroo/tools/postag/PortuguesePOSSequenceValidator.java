/**
 * Copyright (C) 2012 cogroo <cogroo@cogroo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cogroo.tools.postag;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import opennlp.tools.postag.TagDictionary;
import opennlp.tools.util.SequenceValidator;

public class PortuguesePOSSequenceValidator implements
    SequenceValidator<String> {

  private boolean storeUnknown = false;
  public TagDictionary tagDictionary;
  private SortedSet<String> unknown;

  public PortuguesePOSSequenceValidator(TagDictionary tagDictionary) {
    if(storeUnknown) {
      unknown = new TreeSet<String>();
    }
    this.tagDictionary = tagDictionary;
  }

  public boolean validSequence(int i, String[] inputSequence,
      String[] outcomesSequence, String outcome) {

    boolean isValid = false;
    boolean tokExists = false;

    String word = inputSequence[i];
    
    if(i > 0 && "nm".equals(outcome) && "a".equalsIgnoreCase(inputSequence[i-1]) && "artf".equals(outcomesSequence[i-1])) {
      return false;
    }
    
    
    outcome = GenderUtil.removeGender(outcome);

    // lets start with some punctuation check
    if(isPunctuation(word)) {
      // this is only true for BOSQUE! XXX: remember this! 
      return outcome.equals(word);
    }
    if(i < inputSequence.length - 1 && isPunctuation(inputSequence[i+1])) {
      // we can't start a MWE here :(
      if(outcome.startsWith("B-")) {
        return false;
      }
    }
    
    // validate B- and I-
    if (!validOutcome(outcome, outcomesSequence)) {
      return false;
    }
    
    if (tagDictionary == null) {
      return true;
    } else {
      if ((outcome.startsWith("B-") || outcome.startsWith("I-")) && inputSequence.length > 1 ) {
        return true;
      }

      if (word.equals(outcome)) {
        isValid = true;
      }
      
      List<String> tagList = filterMWE(queryDictionary(word, true));

      if (tagList != null && tagList.size() > 0) {
        tokExists = true;
        if("prop".equals(outcome) && Character.isUpperCase(word.charAt(0))) {
          return true;
        } else 
        if (contains(tagList, outcome)) {
          isValid = true;
        }
      }

      if (!tokExists) {
        if(storeUnknown) {
          this.unknown.add(word);
        }
        isValid = true;
      }

      return isValid;
    }
  }

  private List<String> filterMWE(String[] arr) {
    if(arr == null) return null;
    List<String> out = new ArrayList<String>(arr.length);
    for (String t : arr) {
      if (!(t.startsWith("B-") || t.startsWith("I-")))
        out.add(t);
    }
    return out;
  }

  private String[] queryDictionary(String word, boolean recurse) {
    String[] tags = tagDictionary.getTags(word);
    if (tags == null) {
      tags = tagDictionary.getTags(word.toLowerCase());
    }
    if(recurse == true) {
      if(word.startsWith("-") && word.length() > 1) {
        tags = queryDictionary(word.substring(1), false);
      }
    }
    return GenderUtil.removeGender(tags);
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    if (storeUnknown) {
      System.out.println("... palavras desconhecidas ...");
      for (String unk : this.unknown) {
        System.out.println(unk);
      }
      System.out.println("... fim ...");
    }
  }

  static boolean validOutcome(String outcome, String[] sequence) {
    String prevOutcome = null;
    if (sequence.length > 0) {
      prevOutcome = sequence[sequence.length - 1];
    }
    return validOutcome(outcome, prevOutcome);
  }

  static boolean validOutcome(String outcome, String prevOutcome) {

    boolean prevIsBoundary = false, prevIsIntermediate = false, isBoundary = false, isIntermediate = false;

    if (prevOutcome != null) {
      prevIsBoundary = prevOutcome.startsWith("B-");
      prevIsIntermediate = prevOutcome.startsWith("I-");
    }

    if (outcome != null) {
      isBoundary = outcome.startsWith("B-");
      isIntermediate = outcome.startsWith("I-");
    }

    boolean isSameEntity = false;
    if ((prevIsBoundary || prevIsIntermediate) && isIntermediate) {
      isSameEntity = prevOutcome.substring(2).equals(outcome.substring(2));
    }

    if (isIntermediate) {
      if (prevOutcome == null) {
        return (false);
      } else {
        if (!isSameEntity) {
          return (false);
        }
      }
    } else if (isBoundary) {
      if (prevIsBoundary) {
        return false; // MWE should have at least two tokens
      }
    }

    if (prevIsBoundary && !isIntermediate) {
      return false; // MWE should have at least two tokens
    }

    return true;
  }
  
  private static boolean isPunctuation(String word) {
    return word.matches("^[\\.,;:()?-]$");
  }

  private boolean contains(List<String> tagList, String outcome) {

    if (tagList.contains(outcome)) {
      return true;
    }

    if (outcome.equals("n-adj")) {
      if (tagList.contains("n") || tagList.contains("adj")) {
        return true;
      }
    } else if (outcome.equals("n") || outcome.equals("adj")) {
      if (tagList.contains("n-adj")) {
        return true;
      }
    } else if (outcome.contains("=")) {
      String outcomeClass = outcome.substring(0, outcome.indexOf('='));
      for (String tag : tagList) {
        if (tag.startsWith(outcomeClass)
            && (tag.contains("/") || outcome.contains("/"))) {
          String[] outcomeParts = outcome.split("=");
          String[] tagParts = tag.split("="); // will only check parts without /
                                              // for simplicity
          if (outcomeParts.length != tagParts.length) {
            return false;
          }
          for (int i = 0; i < outcomeParts.length; i++) {
            String outcomePart = outcomeParts[i];
            String tagPart = tagParts[i];
            if (!outcomePart.contains("/") && !tagPart.contains("/")) {
              if (!outcomePart.equals(tagPart)) {
                return false;
              }
            }
          }
          return true;
        }
      }
    }
    return false;
  }

}
