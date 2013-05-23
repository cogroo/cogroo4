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
package org.cogroo.tools.checker.rules.dictionary;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.cogroo.dictionary.impl.FSADictionary;
import org.cogroo.dictionary.impl.FSASynthDictionary;
import org.cogroo.util.PairWordPOSTag;


public class FSALexicalDictionary implements LexicalDictionary {
  
  private FSADictionary dictionary = null;
  private FSASynthDictionary synthDict = null;
  
  public FSALexicalDictionary() throws IllegalArgumentException, IOException {
    this.synthDict = (FSASynthDictionary) FSASynthDictionary.createFromResources("/fsa_dictionaries/featurizer/pt_br_feats_synth.dict");
    this.dictionary = (FSADictionary) FSADictionary.createFromResources("/fsa_dictionaries/featurizer/pt_br_feats.dict");
  }

  public boolean wordExists(String word) {
    if(dictionary.getTags(word) == null)
      return false;
    return true;
  }

  public List<PairWordPOSTag> getWordsAndPosTagsForLemma(String aLemma) {
    return synthDict.synthesize(aLemma);
  }

  public List<PairWordPOSTag> getLemmasAndPosTagsForWord(String aWord) {
    return dictionary.getTagsAndLemms(aWord);
  }

  public List<String> getPOSTagsForWord(String word) {
    String[] tags = dictionary.getTags(word);
    if(tags != null) {
      return Collections.unmodifiableList(Arrays.asList(tags));
    }
    return Collections.emptyList();
  }
}
