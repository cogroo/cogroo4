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

import opennlp.tools.postag.TagDictionary;
import opennlp.tools.util.SequenceValidator;

public class PortuguesePOSSequenceValidator implements
    SequenceValidator<String> {

  public TagDictionary tagDictionary;
  private SortedSet<String> unknown;

  public PortuguesePOSSequenceValidator(TagDictionary tagDictionary) {
    unknown = new TreeSet<String>();
    this.tagDictionary = tagDictionary;
  }

  public boolean validSequence(int i, String[] inputSequence,
      String[] outcomesSequence, String outcome) {

    boolean isValid = false;
    boolean tokExists = false;

    String word = inputSequence[i];

    // validate B- and I-
    if (!validOutcome(outcome, outcomesSequence)) {
      return false;
    }

    if (outcome.startsWith("I-")) {
      String prev = outcomesSequence[i - 1].substring(2);
      return outcome.substring(2).equals(prev);
    }

    if (tagDictionary == null) {
      return true;
    } else {
      if ((outcome.startsWith("B-") || outcome.startsWith("I-")) && inputSequence.length > 1 ) {
        return true;
      }

      String[] tags = queryDictionary(word, true);

      if (word.equals(outcome)) {
        isValid = true;
      }

      if (tags != null) {
        tokExists = true;
        List<String> tagList = Arrays.asList(tags);
        if (contains(tagList, outcome)) {
          isValid = true;
        }
      }

      if (!tokExists) {
        this.unknown.add(word);
        isValid = true;
      }

      return isValid;
    }
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
    return tags;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    // System.out.println("... palavras desconhecidas ...");
    // for (String unk : this.unknown) {
    // System.out.println(unk);
    // }
    // System.out.println("... fim ...");
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
