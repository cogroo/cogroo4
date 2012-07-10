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
package org.cogroo.tools.featurizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cogroo.dictionary.FeatureDictionaryI;

import opennlp.tools.util.SequenceValidator;

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
      } else if(word.length() > 1 && word.charAt(0) == '-') {
        tagsArr = expandedSearch(word.substring(1), postag, false);
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

  private boolean matches(String outcome, List<String> tags) {
    if(tags.contains(outcome)) {
      return true;
    }
    
    for (String t : tags) {
      if(matches(outcome, t))
        return true;
    }
    return false;
  }

  public static boolean matches(String outcome, String tag) {
    if("-".equals(tag) || "-".equals(outcome)) {
      return false;
    }
    
    Set<String> outcomeParts = new HashSet<String>(Arrays.asList(outcome
        .split("[=-]")));
    Set<String> tagParts = new HashSet<String>(Arrays.asList(tag.split("[=-]")));

    if(outcomeParts.size() != tagParts.size()) {
      return false;
    }
    
    List<String> remove = new ArrayList<String>();
    for (String t : tagParts) {
      if (outcomeParts.contains(t)) {
        remove.add(t);
      }
    }
    
    tagParts.removeAll(remove);
    if(tagParts.size() == 0) {
      return true;
    }
    
    if(!tag.contains("/")) {
      return false;
    }
    
    outcomeParts.removeAll(remove);
    
    // lets split the outcome, we don't need to consume all of this tags.
    Set<String> outcomeParts2 = new HashSet<String>();
    for (String o : outcomeParts) {
      outcomeParts2.addAll(Arrays.asList(o.split("/")));
    }
    remove.clear();
    // the tagPars we use as it is... we iterate and eliminate the parts that match with outcomeParts2 
    for (String t : tagParts) {
      for (String o : outcomeParts2) {
        if(t.contains(o) && !Character.isDigit(o.charAt(0))) {
          remove.add(t);remove.add(o);
          break;
        }
      }
    }
    
    tagParts.removeAll(remove);
    outcomeParts2.removeAll(remove);
    if(tagParts.size() == 0 && outcomeParts2.size() == 0) {
      return true;
    }
    
    if(tagParts.size() == 1 && outcomeParts2.size() >= 1) {
      for (String op : outcomeParts2) {
        String t = new ArrayList<String>(tagParts).get(0);
        if(Character.isDigit(op.charAt(0)) && Character.isDigit(t.charAt(0))) {
          boolean ok = true;
          for (int i = 0; i < op.length(); i++) {
            if(!t.contains(Character.toString(op.charAt(i)))) {
              ok = false;
              break;
            }
          }
          if(ok)
            return true;
        }
      }
    }
    

    return false;
  }
}
