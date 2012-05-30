package br.ccsl.cogroo.analyzer;

import java.util.List;

import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.Token;
import br.ccsl.cogroo.tools.featurizer.FeaturizerME;
import br.ccsl.cogroo.util.TextUtils;

public class Featurizer implements AnalyzerI {
  private FeaturizerME featurizer;

  public Featurizer(FeaturizerME featurizer) {
    this.featurizer = featurizer;
  }

  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();

    for (Sentence sentence : sentences) {
      List<Token> tokens = sentence.getTokens();
      
      String[] tags = new String[tokens.size()];
      
      for (int i = 0; i < tokens.size(); i++)
        tags[i] = tokens.get(i).getPOSTag();
      
      String[] features = featurizer.featurize(TextUtils.tokensToString(tokens), tags);
      
      for (int i = 0; i < features.length; i++)
      tokens.get(i).setFeatures(features[i]);
      
    }
  }
}
