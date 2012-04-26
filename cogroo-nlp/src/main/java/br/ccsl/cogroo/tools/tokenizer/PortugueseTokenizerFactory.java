package br.ccsl.cogroo.tools.tokenizer;

import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.tokenize.TokenContextGenerator;
import opennlp.tools.tokenize.TokenizerFactory;
import opennlp.tools.util.model.ArtifactProvider;

public class PortugueseTokenizerFactory extends TokenizerFactory {
  
  public PortugueseTokenizerFactory(ArtifactProvider artifactProvider) {
    super(artifactProvider);
  }
  
  public PortugueseTokenizerFactory(String languageCode,
      Dictionary abbreviationDictionary, boolean useAlphaNumericOptimization,
      Pattern alphaNumericPattern) {
    super(languageCode,abbreviationDictionary,useAlphaNumericOptimization,alphaNumericPattern);
  }
  
  @Override
  public TokenContextGenerator getContextGenerator() {
    Dictionary dic = this.getAbbreviationDictionary();
    Set<String> abbSet;
    if(dic != null) {
      abbSet = dic.asStringSet();
    } else {
      abbSet = Collections.emptySet();
    }
    
    TokenContextGenerator cg = new PortugueseTokenContextGenerator(abbSet);
    return cg;
  }

}
