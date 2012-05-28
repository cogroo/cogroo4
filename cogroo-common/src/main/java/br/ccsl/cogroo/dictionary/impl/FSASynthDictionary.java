package br.ccsl.cogroo.dictionary.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import morfologik.stemming.Dictionary;
import morfologik.stemming.DictionaryLookup;
import morfologik.stemming.WordData;
import br.ccsl.cogroo.tools.checker.rules.dictionary.PairWordPOSTag;

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
        result.add(new PairWordPOSTag(wordData.getWord().toString(), wordData
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
    FSASynthDictionary td = (FSASynthDictionary) create("fsa_dictionaries/featurizer/pt_br_feats.dict");
    
    List<PairWordPOSTag> x = td.synthesize("casa");
    
    System.out.println(Arrays.toString(x.toArray()));
  }
}
