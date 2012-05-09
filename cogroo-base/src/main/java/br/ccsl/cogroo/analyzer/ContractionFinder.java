package br.ccsl.cogroo.analyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.io.Closeables;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;
import br.ccsl.cogroo.ContractionUtility;
import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.Token;
import br.ccsl.cogroo.text.impl.TokenImpl;
import br.ccsl.cogroo.util.TextUtils;

public class ContractionFinder implements Analyzer {

  protected static final Logger LOGGER = Logger
      .getLogger(ContractionFinder.class);
  private NameFinderME contractionFinder;

  public ContractionFinder() throws FileNotFoundException {
    InputStream modelIn = new FileInputStream("models/pt-con.model");

    try {
      TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
      contractionFinder = new NameFinderME(model);
    } catch (IOException e) {
      LOGGER.fatal("Couldn't load contractions finder model!", e);
    } finally {
      Closeables.closeQuietly(modelIn);
    }
  }

  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();

    for (Sentence sentence : sentences) {
      Span[] contractionsSpan = contractionFinder.find(TextUtils
          .tokensToString(sentence.getTokens()));
      List<Token> newTokens = sentence.getTokens();

      for (int i = 0; i < contractionsSpan.length; i++) {
        int start = contractionsSpan[i].getStart(), end = contractionsSpan[i]
            .getEnd();

        String lexeme = sentence.getTokens().get(start).getLexeme();
        String[] contractions = ContractionUtility.expand(lexeme);

        newTokens.remove(start);

        for (int j = contractions.length - 1; j >= 0; j--) {
          Span span = new Span(start, end);
          Token token = new TokenImpl(span, contractions[j]);
          newTokens.add(start, token);
        }
      }
      sentence.setTokens(newTokens);
    }
  }
}
