package br.ccsl.cogroo.analyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.io.Closeables;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.Token;
import br.ccsl.cogroo.text.impl.TokenImpl;

public class Tokenizer implements Analyzer {

  protected static final Logger LOGGER = Logger.getLogger(Tokenizer.class);
  private TokenizerME tokenizer;

  public Tokenizer() throws FileNotFoundException {
    InputStream modelIn = new FileInputStream("models/pt-tok.model");

    try {
      TokenizerModel model = new TokenizerModel(modelIn);
      tokenizer = new TokenizerME(model);
    } catch (IOException e) {
      LOGGER.fatal("Couldn't load tokenizer model!", e);
    } finally {
      Closeables.closeQuietly(modelIn);
    }
  }
  
  public Tokenizer(TokenizerME tokenizer) throws FileNotFoundException {
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
