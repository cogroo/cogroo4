package br.ccsl.cogroo.dictionary;

import java.util.Arrays;
import java.util.List;

import morfologik.stemming.Dictionary;
import morfologik.stemming.DictionaryLookup;
import morfologik.stemming.WordData;
import opennlp.tools.postag.TagDictionary;

public class FSASynthDictionary {

  private DictionaryLookup dictLookup;

  public FSASynthDictionary(DictionaryLookup dictLookup) {
    this.dictLookup = dictLookup;
  }
  
  public String[] synthesize(String lemma) {
    synchronized (dictLookup) {
      List<WordData> data = dictLookup.lookup(lemma);
      for (WordData wordData : data) {
        System.out.println(wordData);
      }
    }
    return null;
  }
  
  public static FSASynthDictionary create(String language) {
    DictionaryLookup dictLookup = new DictionaryLookup(Dictionary.getForLanguage(language));
    return new FSASynthDictionary(dictLookup);
  }
  
  public static void main(String[] args) {
    FSASynthDictionary td = (FSASynthDictionary) create("pt_br_synth1");
    
    System.out.println(Arrays.toString(td.synthesize("casa")));
  }
}
