package br.ccsl.cogroo.analyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import com.google.common.io.Closeables;

import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.Token;
import br.ccsl.cogroo.text.impl.TokenImpl;
import br.ccsl.cogroo.util.TextUtils;

public class POSTagger implements Analyzer {
  protected static final Logger LOGGER = Logger.getLogger(POSTagger.class);
  private POSTaggerME tagger;

  public POSTagger() throws FileNotFoundException {
    InputStream modelIn = new FileInputStream("models/pt-pos-maxent.bin");

    try {
      POSModel model = new POSModel(modelIn);
      tagger = new POSTaggerME(model);
    } catch (IOException e) {
      LOGGER.fatal("Couldn't load POS-tagger model!", e);
    } finally {
      Closeables.closeQuietly(modelIn);
    }
  }

  public POSTagger(POSTaggerME tagger) throws FileNotFoundException {
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
