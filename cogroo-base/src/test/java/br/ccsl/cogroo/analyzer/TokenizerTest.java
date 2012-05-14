package br.ccsl.cogroo.analyzer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.util.Span;


public class TokenizerTest {
  
  private Tokenizer tokenizer;
  TokenizerME mockedTokenizer;
  
  SentenceDetector sentenceDetector;
  
  @Before
  public void setUp() throws Exception {
    mockedTokenizer = mock(TokenizerME.class);
    tokenizer = new Tokenizer(mockedTokenizer);
    
    sentenceDetector = new SentenceDetector();
  }
  
  @Test
  public void testAnalyze() throws FileNotFoundException {
    Document document = new Document();
    String text = "A menina pequena andava para lá.";
    document.setText(text);
    sentenceDetector.analyze(document);
    
    Span[] spans = { new Span(0,1), new Span(2,8), new Span(9,16), new Span (17, 23), new Span (24,28), new Span (29, 31), new Span (31,32)};
    
    when(mockedTokenizer.tokenizePos(text)).thenReturn(spans);
    
    tokenizer.analyze(document);
    
    assertEquals(7, document.getSentences().get(0).getTokens().size());
    
    assertEquals("A", document.getSentences().get(0).getTokens().get(0)
        .getLexeme());
    assertEquals("menina", document.getSentences().get(0).getTokens().get(1).getLexeme());
    assertEquals("pequena", document.getSentences().get(0).getTokens().get(2).getLexeme());
    assertEquals("andava", document.getSentences().get(0).getTokens().get(3).getLexeme());
    assertEquals("para", document.getSentences().get(0).getTokens().get(4).getLexeme());
    assertEquals("lá", document.getSentences().get(0).getTokens().get(5).getLexeme());
    assertEquals(".", document.getSentences().get(0).getTokens().get(6).getLexeme());
  }
  
  @Test
  public void testAnalyzeEmpty() throws FileNotFoundException {
    Document document = new Document();
    String text = "";
    document.setText(text);

    document.setSentences(Collections.<Sentence>emptyList());
    
    Span[] spans = new Span[0];
    
    when(mockedTokenizer.tokenizePos(text)).thenReturn(spans);
    
    SentenceDetector sentenceDetector = new SentenceDetector();
    sentenceDetector.analyze(document);
    
    assertNotNull(document.getSentences());
    assertEquals(0, document.getSentences().size());
  }
}
