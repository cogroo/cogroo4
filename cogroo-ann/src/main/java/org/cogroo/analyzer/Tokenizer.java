package org.cogroo.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.Token;
import org.cogroo.text.impl.TokenImpl;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.util.Span;

/**
 * The <code>Tokenizer</code> class separates every word in a given sentence and allocates them in a
 * list of tokens.
 * 
 */
public class Tokenizer implements AnalyzerI {

  private TokenizerME tokenizer;

  public Tokenizer(TokenizerME tokenizer) {
    this.tokenizer = tokenizer;
  }

  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();

    for (Sentence sentence : sentences) {
      String sentenceString = sentence.getText();
      Span tokensSpan[] = tokenizer.tokenizePos(sentenceString);

      List<Token> tokens = new ArrayList<Token>(tokensSpan.length);

      for (int i = 0; i < tokensSpan.length; i++) {
        Token token = new TokenImpl(tokensSpan[i].getStart(), tokensSpan[i].getEnd() , tokensSpan[i]
            .getCoveredText(sentenceString).toString());
        tokens.add(token);
      }
      sentence.setTokens(tokens);
    }
  }
}
