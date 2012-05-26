package br.ccsl.cogroo.tools.checker.rules.dictionary;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import br.ccsl.cogroo.dictionary.FSADictionary;
import br.ccsl.cogroo.dictionary.FSASynthDictionary;

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
