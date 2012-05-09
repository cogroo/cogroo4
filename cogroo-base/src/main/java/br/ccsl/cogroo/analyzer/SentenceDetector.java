package br.ccsl.cogroo.analyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.io.Closeables;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;
import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;

public class SentenceDetector implements Analyzer {

  protected static final Logger LOGGER = Logger
      .getLogger(SentenceDetector.class);
  private SentenceDetectorME sentenceDetector;

  public SentenceDetector() throws FileNotFoundException {
    InputStream modelIn = new FileInputStream("models/pt-sent.model");

    try {
      SentenceModel model = new SentenceModel(modelIn);
      sentenceDetector = new SentenceDetectorME(model);
    } catch (IOException e) {
      LOGGER.fatal("Couldn't load sentence model!", e);
    } finally {
      Closeables.closeQuietly(modelIn);
    }
  }

  public void analyze(Document document) {
    Span[] spans = sentenceDetector.sentPosDetect(document.getText());

    List<Sentence> sentences = new ArrayList<Sentence>(spans.length);

    for (int i = 0; i < spans.length; i++) {
      Sentence sentence = new Sentence(spans[i]);
      sentences.add(sentence);
    }

    document.setSentences(sentences);
  }
}
