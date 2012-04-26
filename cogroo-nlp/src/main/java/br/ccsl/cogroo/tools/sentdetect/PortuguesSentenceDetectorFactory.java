package br.ccsl.cogroo.tools.sentdetect;

import java.util.Collections;
import java.util.Set;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.sentdetect.SDContextGenerator;
import opennlp.tools.sentdetect.SentenceDetectorFactory;
import opennlp.tools.sentdetect.lang.Factory;
import opennlp.tools.util.model.ArtifactProvider;

public class PortuguesSentenceDetectorFactory extends SentenceDetectorFactory {

  private char[] eos;

  public PortuguesSentenceDetectorFactory(ArtifactProvider artifactProvider) {
    super(artifactProvider);
    this.eos = super.getEOSCharacters();
  }

  public PortuguesSentenceDetectorFactory(String languageCode,
      boolean useTokenEnd, Dictionary abbreviationDictionary,
      char[] eosCharacters) {
    super(languageCode, useTokenEnd, abbreviationDictionary, eosCharacters);
    if(eosCharacters != null) {
      this.eos = eosCharacters;
    } else {
      this.eos = Factory.ptEosCharacters;
    }
  }

  public PortuguesSentenceDetectorFactory() {
  }

  @Override
  public char[] getEOSCharacters() {
    return eos;
  }

  @Override
  public SDContextGenerator getSDContextGenerator() {
    Set<String> abb = null;
    Dictionary abbDic = getAbbreviationDictionary();
    if (abbDic != null) {
      abb = abbDic.asStringSet();
    } else {
      abb = Collections.emptySet();
    }
    return new PortugueseSDContextGenerator(abb, getEOSCharacters());
  }
}
