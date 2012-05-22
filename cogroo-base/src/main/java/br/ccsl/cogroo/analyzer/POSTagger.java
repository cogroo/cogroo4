package br.ccsl.cogroo.analyzer;

import java.util.List;

import opennlp.tools.postag.POSTaggerME;

import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.Token;
import br.ccsl.cogroo.text.impl.TokenImpl;
import br.ccsl.cogroo.util.TextUtils;

/**
 *  The <code>POSTagger</code> class analyzes each token of a sentence and classifies it grammatically. 
 *
 */
public class POSTagger implements AnalyzerI {
  private POSTaggerME tagger;

  public POSTagger(POSTaggerME tagger) {
    this.tagger = tagger;
  }

  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();

    for (Sentence sentence : sentences) {
      List<Token> tokens = sentence.getTokens();
      String[] tags = tagger
          .tag(TextUtils.tokensToString(sentence.getTokens()));

      for (int i = 0; i < tags.length; i++) {
        ((TokenImpl) tokens.get(i)).setPOSTag(tags[i]);
      }
    }
  }
}
