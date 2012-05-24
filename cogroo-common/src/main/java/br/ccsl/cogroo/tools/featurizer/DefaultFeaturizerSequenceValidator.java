/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
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

package br.ccsl.cogroo.tools.featurizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.util.SequenceValidator;
import br.ccsl.cogroo.dictionary.FeatureDictionaryI;
import br.ccsl.cogroo.interpreters.FlorestaTagInterpreter;

public class DefaultFeaturizerSequenceValidator implements
    SequenceValidator<WordTag> {

  private FeatureDictionaryI tagDict = null;
  private Set<String> poisonedTags;

  public DefaultFeaturizerSequenceValidator(FeatureDictionaryI tagDict,
      Set<String> poisonedTags) {
    this.tagDict = tagDict;
    this.poisonedTags = poisonedTags;
  }

  public boolean validSequence(int i, WordTag[] sequence, String[] s,
      String outcome) {

    String word = sequence[i].getWord();
    String postag = sequence[i].getPostag();

//    // if isCont, we only validate if this outcome equals to previous
//    if (postag.startsWith("I-")) {
//      return s[i - 1].equals(outcome);
//    }
//    
//    if (postag.startsWith("B-") && s[i - 1].startsWith("B-")) {
//      return false; // MWE should have at least two tokens
//    }
    
    if (tagDict == null) {
      return true;
    }

//    if (postag.startsWith("B-")) {
//      postag = postag.substring(2);
//    }

    String[] tagsArr = expandedSearch(word, postag, true);
    
    List<String> tags = null;
    if(tagsArr != null) {
      tags = filterPoisoned(tagsArr);
    }
    
    if (tags != null) {
      // System.err.println("-- eval: " + word + " (" + postag + ") "+ tags +
      // " outcome: " + outcome);
      return matches(outcome, tags);
    }

    return true;
  }

  // private boolean isCont(WordTag[] sequence, int i) {
  // if(i > 0) {
  // String prev = sequence[i-1].getPostag();
  // if(prev.startsWith("B-") || prev.startsWith("I-"))
  // return true;
  // }
  // return false;
  // }

  private String[] expandedSearch(String word, String postag, boolean recurse) {
    String[] tagsArr = tagDict.getFeatures(word, postag);
    if(tagsArr == null || tagsArr.length == 0) {
      tagsArr = tagDict.getFeatures(word.toLowerCase(), postag);
    }
    
    if((tagsArr == null  || tagsArr.length == 0) && recurse) {
      if(postag.equals("n-adj")) {
        tagsArr = expandedSearch(word, "n", false);
        if(tagsArr == null) {
          tagsArr = expandedSearch(word, "adj", false);
        }
      } else if(postag.equals("n")) {
        tagsArr = expandedSearch(word, "n-adj", false);
      } else if(postag.equals("adj")) {
        tagsArr = expandedSearch(word, "n-adj", false);
      }
    }
    return tagsArr;
  }

  private List<String> filterPoisoned(String[] featureTag) {
    if (featureTag == null) {
      return null;
    }
    List<String> filtered = new ArrayList<String>();
    for (String tag : featureTag) {
      if (!this.poisonedTags.contains(tag)) {
        filtered.add(tag);
      } else {
        System.err.println("found poisoned tag. Will ignore all! " + tag);
        return null;
      }
    }
    if (filtered.size() == 0) {
      return null;
    }
    return Collections.unmodifiableList(filtered);
  }

  FlorestaTagInterpreter ti = new FlorestaTagInterpreter();

  private boolean matches(String outcome, List<String> tags) {
    if(tags.contains(outcome)) {
      return true;
    }
    Set<String> tagParts = new HashSet<String>();
    for (String tag : tags) {
      tagParts.addAll(Arrays.asList(tag.split("[_=/]")));
    }
    for (String part : outcome.split("[_=/]")) {
      if (!tagParts.contains(part)) {
//        System.err.println("  -- missing: " + part);
        return false;
      }
    }

    return true;
  }

}
