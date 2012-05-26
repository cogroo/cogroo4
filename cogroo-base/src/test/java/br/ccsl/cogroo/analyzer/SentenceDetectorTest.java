package br.ccsl.cogroo.analyzer;

import static org.junit.Assert.*;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.util.Span;

import org.junit.Before;
import org.junit.Test;

import br.ccsl.cogroo.text.Document;
import static org.mockito.Mockito.*;

public class SentenceDetectorTest {

  private SentenceDetector sentenceDetector;
  SentenceDetectorME mockedSentenceDetector;

  @Before
  public void setUp() throws Exception {
    mockedSentenceDetector = mock(SentenceDetectorME.class);
    sentenceDetector = new SentenceDetector(mockedSentenceDetector);
  }

  @Test
  public void testAnalyze() {
    Document document = new Document();
    String text = "Este é um teste. Ele contém duas frases.";
    document.setText(text);

    Span[] spans = { new Span(0, 16), new Span(17, 40) };

    when(mockedSentenceDetector.sentPosDetect(text)).thenReturn(spans);

    sentenceDetector.analyze(document);

    assertNotNull(document.getSentences());
    assertEquals(2, document.getSentences().size());

    assertEquals("Este é um teste.", document.getSentences().get(0)
        .getText());
    assertEquals("Ele contém duas frases.", document.getSentences().get(1)
        .getText());
  }

  @Test
  public void testAnalyzeEmpty() {
    Document document = new Document();
    String text = "";
    document.setText(text);

    Span[] spans = new Span[0];

    when(mockedSentenceDetector.sentPosDetect(text)).thenReturn(spans);

    sentenceDetector.analyze(document);

    assertNotNull(document.getSentences());
    assertEquals(0, document.getSentences().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAnalyzeNull() {
    Document document = new Document();
    String text = null;
    document.setText(text);

    Span[] spans = new Span[0];

    when(mockedSentenceDetector.sentPosDetect(text)).thenReturn(spans);

    sentenceDetector.analyze(document);
  }
}
