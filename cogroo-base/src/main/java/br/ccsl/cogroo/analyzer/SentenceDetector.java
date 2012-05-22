package br.ccsl.cogroo.analyzer;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.util.Span;
import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;

/**
 *  The <code> SentenceDetector</code> class gets all the sentences in the document text and store them in a list of sentences. 
 *
 */
public class SentenceDetector implements AnalyzerI {

  private SentenceDetectorME sentenceDetector;

  public SentenceDetector(SentenceDetectorME sentenceDetector) {
    this.sentenceDetector = sentenceDetector;
  }

  /**
   * @throws IllegalArgumentException
   *           if document text is null.
   */
  public void analyze(Document document) {

    if (document.getText() == null)
      throw new IllegalArgumentException("Document text is null.");

    Span[] spans = sentenceDetector.sentPosDetect(document.getText());

    List<Sentence> sentences = new ArrayList<Sentence>(spans.length);

    for (int i = 0; i < spans.length; i++) {
      Sentence sentence = new Sentence(spans[i]);
      sentences.add(sentence);
    }

    document.setSentences(sentences);
  }
}
