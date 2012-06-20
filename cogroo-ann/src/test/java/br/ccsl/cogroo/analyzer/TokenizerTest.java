package br.ccsl.cogroo.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.util.Span;

import org.junit.Before;
import org.junit.Test;

import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.impl.DocumentImpl;
import br.ccsl.cogroo.text.impl.SentenceImpl;


public class TokenizerTest {
  
  private Tokenizer tokenizer;
  TokenizerME mockedTokenizer;
  
  @Before
  public void setUp() throws Exception {
    mockedTokenizer = mock(TokenizerME.class);
    tokenizer = new Tokenizer(mockedTokenizer);
  }
  
  @Test
  public void testAnalyze() throws FileNotFoundException {
    DocumentImpl document = new DocumentImpl();
    String text = "A menina pequena andava para lá.";
    document.setText(text);
    
    List<Sentence> sentences = new ArrayList<Sentence>();
    
    Sentence sentence = new SentenceImpl(0, 32, document);
    sentences.add(sentence);
    
    document.setSentences(sentences);
    
    Span[] spansTokens = { new Span(0,1), new Span(2,8), new Span(9,16), new Span (17, 23), new Span (24,28), new Span (29, 31), new Span (31,32)};
    when(mockedTokenizer.tokenizePos(text)).thenReturn(spansTokens);
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
    DocumentImpl document = new DocumentImpl();
    String text = "";
    document.setText(text);

    document.setSentences(Collections.<Sentence>emptyList());
    
    Span[] spans = new Span[0];
    
    when(mockedTokenizer.tokenizePos(text)).thenReturn(spans);
    
    assertNotNull(document.getSentences());
    assertEquals(0, document.getSentences().size());
  }
}
