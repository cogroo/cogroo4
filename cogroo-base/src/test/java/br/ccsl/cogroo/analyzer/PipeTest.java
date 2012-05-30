package br.ccsl.cogroo.analyzer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.Span;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.Token;
import br.ccsl.cogroo.text.impl.DocumentImpl;
import br.ccsl.cogroo.text.impl.SentenceImpl;
import br.ccsl.cogroo.text.impl.TokenImpl;

public class PipeTest {
  private Pipe pipe = new Pipe();
  SentenceDetector mockedSentenceDetector;
  Tokenizer mockedTokenizer;
  DocumentImpl document = new DocumentImpl();
  
  
  @Before
  public void setUp() throws Exception {
    mockedSentenceDetector = mock(SentenceDetector.class);
    mockedTokenizer = mock(Tokenizer.class);
    
    String text = "Este é um teste. Ele contém duas frases.";
    document.setText(text);
    
    doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        DocumentImpl d = (DocumentImpl)args[0];
        
        List<Sentence> sentences = new ArrayList<Sentence>();
        Span[] spans = { new Span(0, 16), new Span(17, 40) };
        
        for (Span span : spans) {
          SentenceImpl sentence = new SentenceImpl(span, document); 
          sentences.add(sentence);
        }
        d.setSentences(sentences);
        
        return null;
    }
  }).when(mockedSentenceDetector).analyze(document);
  
  doAnswer(new Answer<Void>() {
    public Void answer(InvocationOnMock invocation) {
      Object[] args = invocation.getArguments();
      DocumentImpl d = (DocumentImpl)args[0];
      
      List<Token> tokens = new ArrayList<Token>();
      
      TokenImpl token = new TokenImpl(new Span(0,3), "Uma");
      tokens.add(token);
      
      token = new TokenImpl(new Span(3,8), "frase");
      tokens.add(token);
      
      token = new TokenImpl(new Span(8,9), ".");
      tokens.add(token);
      
      d.getSentences().get(0).setTokens(tokens);
      return null;
    }
    }).when(mockedTokenizer).analyze(document);
}

  @Test
  public void testAnalyzeOneAnalyzer() {
    pipe.add(mockedSentenceDetector);
    
    pipe.analyze(document);
    
    assertEquals(2, document.getSentences().size());
  }
  
  @Test
  public void testAnalyzeMoreAnalyzers() {
    pipe.add(mockedSentenceDetector);
    pipe.add(mockedTokenizer);
    
    pipe.analyze(document);
    
    assertEquals(3, document.getSentences().get(0).getTokens().size());
  }
}
