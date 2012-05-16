package br.ccsl.cogroo.analyzer;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.util.Span;
import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.Token;
import br.ccsl.cogroo.text.impl.TokenImpl;

public class Tokenizer implements Analyzer {

  private TokenizerME tokenizer;
  
  public Tokenizer(TokenizerME tokenizer) {
    this.tokenizer = tokenizer;
  }

  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();

    for (Sentence sentence : sentences) {
      String sentenceString = sentence.getCoveredSentence(document.getText());
      Span tokensSpan[] = tokenizer.tokenizePos(sentenceString);

      List<Token> tokens = new ArrayList<Token>(tokensSpan.length);

      for (int i = 0; i < tokensSpan.length; i++) {
        Token token = new TokenImpl(tokensSpan[i], tokensSpan[i]
            .getCoveredText(sentenceString).toString());
        tokens.add(token);
      }
      sentence.setTokens(tokens);
    }
  }
}
