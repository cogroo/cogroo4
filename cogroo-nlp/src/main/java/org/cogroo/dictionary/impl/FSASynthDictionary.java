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
package org.cogroo.dictionary.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.cogroo.util.PairWordPOSTag;

import morfologik.stemming.Dictionary;
import morfologik.stemming.DictionaryLookup;
import morfologik.stemming.WordData;

public class FSASynthDictionary {

  private DictionaryLookup dictLookup;

  public FSASynthDictionary(DictionaryLookup dictLookup) {
    this.dictLookup = dictLookup;
  }
  
  public List<PairWordPOSTag> synthesize(String lemma) {
    List<PairWordPOSTag> result = new ArrayList<PairWordPOSTag>();
    synchronized (dictLookup) {
      List<WordData> data = dictLookup.lookup(lemma);
      for (WordData wordData : data) {
        result.add(new PairWordPOSTag(wordData.getStem().toString(), wordData
            .getTag().toString()));
      }
    }
    return Collections.unmodifiableList(result);
  }
  
  public static FSASynthDictionary create(String path)
      throws IllegalArgumentException, IOException {
    FileInputStream fsaData = new FileInputStream(path);
    FileInputStream featuresData = new FileInputStream(
        Dictionary.getExpectedFeaturesName(path));
    return create(fsaData, featuresData);
  }
  
  public static FSASynthDictionary createFromResources(String path)
      throws IllegalArgumentException, IOException {
    
    InputStream dic = FSASynthDictionary.class.getResourceAsStream(path);
    InputStream info = FSASynthDictionary.class.getResourceAsStream(Dictionary.getExpectedFeaturesName(path));
    
    FSASynthDictionary fsa = create(dic, info);
    
    dic.close();
    info.close();
    
    return fsa;
  }
  
  public static FSASynthDictionary create(InputStream fsaData,
      InputStream featuresData) throws IllegalArgumentException, IOException {
    DictionaryLookup dictLookup = new DictionaryLookup(Dictionary.readAndClose(
        fsaData, featuresData));
    return new FSASynthDictionary(dictLookup);
  }
  
  public static void main(String[] args) throws IllegalArgumentException, IOException {
    
    //String dic = "fsa_dictionaries/featurizer/pt_br_feats_synth.dict";
    String dic = "fsa_dictionaries/pos/pt_br_jspell_synth.dict";
    FSASynthDictionary td = (FSASynthDictionary) create(dic);
    
    List<PairWordPOSTag> x = td.synthesize("árvore");
    
    System.out.println(Arrays.toString(x.toArray()));
  }
}
