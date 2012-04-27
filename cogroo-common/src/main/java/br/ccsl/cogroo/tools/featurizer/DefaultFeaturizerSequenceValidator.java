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

import opennlp.tools.postag.ExtendedTagDictionary;
import opennlp.tools.util.SequenceValidator;
import br.ccsl.cogroo.interpreters.FlorestaTagInterpreter;

public class DefaultFeaturizerSequenceValidator implements
    SequenceValidator<WordTag> {

  private ExtendedTagDictionary tagDict = null;
  private Set<String> poisonedTags;

  // public DefaultFeaturizerSequenceValidator() {
  // }

  public DefaultFeaturizerSequenceValidator(ExtendedTagDictionary tagDict,
      Set<String> poisonedTags) {
    this.tagDict = tagDict;
    this.poisonedTags = poisonedTags;
  }

  public boolean validSequence(int i, WordTag[] sequence, String[] s,
      String outcome) {

    if (tagDict == null) {
      return true;
    }

    String word = sequence[i].getWord();
    String postag = sequence[i].getPostag();

    // if isCont, we only validate if this outcome equals to previous
    if (postag.startsWith("I-")) {
      return s[i - 1].equals(outcome);
    }

    if (postag.startsWith("B-")) {
      postag = postag.substring(2);
    }

    List<String> tags = filterPoisoned(tagDict.getFeatureTag(word, postag));

    if (tags != null) {
      // System.err.println("-- eval: " + word + " (" + postag + ") "+ tags +
      // " outcome: " + outcome);
      return matches(outcome, tags);
    } else {
      String lower = word.toLowerCase();
      if (!lower.equals(word)) {
        tags = filterPoisoned(tagDict.getFeatureTag(lower, postag));
        if (tags != null) {
          // System.err.println("-- eval: " + lower + " (" + postag + ") " +
          // " tags: " + Arrays.toString(tags) + " outcome: " + outcome);
          return matches(outcome, tags);
        }
      }
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

  private List<String> filterPoisoned(String[] featureTag) {
    if (featureTag == null) {
      return null;
    }
    List<String> filtered = new ArrayList<String>();
    for (String tag : featureTag) {
      if (!this.poisonedTags.contains(tag)) {
        filtered.add(tag);
      } else {
        System.err.println("found poisoned tag! " + tag);
      }
    }
    if (filtered.size() == 0) {
      return null;
    }
    return Collections.unmodifiableList(filtered);
  }

  FlorestaTagInterpreter ti = new FlorestaTagInterpreter();

  private boolean matches(String outcome, List<String> tags) {
    Set<String> tagParts = new HashSet<String>();
    for (String tag : tags) {
      tagParts.addAll(Arrays.asList(tag.split("[_/]")));
    }
    for (String part : outcome.split("[_/]")) {
      if (!tagParts.contains(part)) {
        // System.err.println("  -- missing: " + part);
        return false;
      }
    }

    return true;
  }

}
