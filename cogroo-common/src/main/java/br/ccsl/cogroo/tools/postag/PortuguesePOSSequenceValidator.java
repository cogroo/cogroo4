/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreemnets.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.ccsl.cogroo.tools.postag;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import opennlp.tools.postag.POSDictionary;
import opennlp.tools.util.SequenceValidator;

public class PortuguesePOSSequenceValidator implements SequenceValidator<String> {

  public POSDictionary tagDictionary;
  private SortedSet<String> unknown;

  public PortuguesePOSSequenceValidator(POSDictionary tagDictionary) {
    unknown = new TreeSet<String>();
    this.tagDictionary = tagDictionary;
  }

  public boolean validSequence(int i, String[] inputSequence,
      String[] outcomesSequence, String outcome) {
    
    boolean isValid = false;
    boolean outcomeIsME = false;
    boolean tokExists = false;
    
    String word = inputSequence[i];
    
    //System.err.println("# evaluating: " + word + " " + outcome + " seq.: " + Arrays.toString(outcomesSequence));
    
    // validate B- and I-
    if(!validOutcome(outcome, outcomesSequence)) {
      System.err.println("# invalid: " + word + " " + outcome + " seq.: " + Arrays.toString(outcomesSequence));
      return false;
    }
    
    if(outcome.startsWith("I-")) {
      String prev = outcomesSequence[i-1].substring(2);
      return outcome.substring(2).equals(prev);
    }

    if (tagDictionary == null) {
      return true;
    } else {
      if(isME(outcome)) {
        outcomeIsME = true;
        outcome = outcome.substring(2);
      } else if(i > 0 && outcomesSequence[i - 1].startsWith("B-")) {
        // not valid because there is no ME with size one
        return false;
      }
      
      
      String[] tags = tagDictionary.getTags(word);
      
      if(word.equals(outcome)) {
        isValid = true;
      }
      
      if (tags != null) {
        tokExists = true;
        List<String> tagList = Arrays.asList(tags);
        if(contains(tagList, outcome)) {
          isValid = true;
        } 
      } else {
        String lower = word.toLowerCase();
        if(!lower.equals(word)) {
          tokExists = true;
          if("prop".equals(outcome)) {
            isValid = true;
          }
          tags = tagDictionary.getTags(lower);
          if (tags != null){
            List<String> tagList1 = Arrays.asList(tags);
            if(contains(tagList1, outcome)) {
              isValid = true;
            }
          }
        } 
      }
      if(!tokExists) {
        this.unknown.add(word);
        System.err.println("-- unknown: " + word);
        isValid = true;
      } else if(tokExists && outcomeIsME) {
        isValid = true;
      }
      
      if(isValid) {
        System.err.print("validated: " + word + " " + outcome);
        if(outcomeIsME) {
          System.err.println(" (me)");
        } else {
          System.err.println();
        }
      }
      
      return isValid;
    }
  }
  
  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    System.err.println("... palavras desconhecidas ...");
    for (String unk : this.unknown) {
      System.err.println(unk);
    }
    System.err.println("... fim ...");
  }
  
  private boolean isME(String outcome) {
    return outcome.startsWith("B-") || outcome.startsWith("I-");
  }

  protected boolean validOutcome(String outcome, String[] sequence) {
    String prevOutcome = null;
    if (sequence.length > 0) {
      prevOutcome = sequence[sequence.length-1];
    }
    return validOutcome(outcome,prevOutcome);
  }
  
  private boolean validOutcome(String outcome, String prevOutcome) {
    if (outcome.startsWith("I-")) {
      if (prevOutcome == null) {
        return (false);
      }
      else {
        if (!prevOutcome.startsWith("B-")) {
          return (false);
        }
        if (!prevOutcome.substring(2).equals(outcome.substring(2))) {
          return (false);
        }
      }
    }
    return true;
  }
  
  private boolean contains(List<String> tagList, String outcome) {

    if(tagList.contains(outcome)) {
      return true;
    }
    
    if(outcome.equals("n-adj")) {
      if(tagList.contains("n") || tagList.contains("adj")) {
        return true;
      }
    } else if(outcome.equals("n") || outcome.equals("adj")) {
      if(tagList.contains("n-adj")) {
        return true;
      }
    } else if(outcome.contains("=")) {
      String outcomeClass = outcome.substring(0, outcome.indexOf('='));
      for (String tag : tagList) {
        if(tag.startsWith(outcomeClass) && (tag.contains("/") || outcome.contains("/"))) {
          String[] outcomeParts = outcome.split("=");
          String[] tagParts = tag.split("="); // will only check parts without / for simplicity
          if(outcomeParts.length != tagParts.length) {
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
