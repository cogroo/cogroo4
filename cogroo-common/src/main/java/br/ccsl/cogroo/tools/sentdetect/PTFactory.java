package br.ccsl.cogroo.tools.sentdetect;

import java.util.Set;

import opennlp.tools.sentdetect.SDContextGenerator;
import opennlp.tools.sentdetect.lang.Factory;

public class PTFactory extends Factory {

  @Override
  public SDContextGenerator createSentenceContextGenerator(String languageCode,
      Set<String> abbreviations) {
    if ("pt".equals(languageCode)) {
      return new PortugueseSDContextGenerator(abbreviations, ptEosCharacters);
    }
    return super.createSentenceContextGenerator(languageCode, abbreviations);
  }
}
