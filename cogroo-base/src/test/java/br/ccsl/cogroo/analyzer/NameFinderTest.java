package br.ccsl.cogroo.analyzer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.Span;

import org.junit.Before;
import org.junit.Test;

import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.Token;
import br.ccsl.cogroo.text.impl.DocumentImpl;
import br.ccsl.cogroo.text.impl.SentenceImpl;
import br.ccsl.cogroo.text.impl.TokenImpl;

public class NameFinderTest {
  private NameFinder nameFinder;
  NameFinderME mockedNameFinder;

  @Before
  public void setUp() throws Exception {
    mockedNameFinder = mock(NameFinderME.class);
    nameFinder = new NameFinder(mockedNameFinder);
  }

  @Test
  public void testAnalyze() throws FileNotFoundException {
    DocumentImpl document = new DocumentImpl();
    String text = "O Sr. Luis Carlos faleceu ontem .";
    String[] textArray = text.split(" ");

    document.setText(text);
    Sentence sentence = new SentenceImpl(0, 33,document);
    document.setSentences(Collections.singletonList(sentence));
    Span[] spans = { new Span(1, 4) };
    List<Token> tokens = createTokens(textArray);
    sentence.setTokens(tokens);

    when(mockedNameFinder.find(textArray)).thenReturn(spans);

    nameFinder.analyze(document);

    assertEquals("Sr._Luis_Carlos", document.getSentences().get(0).getTokens()
        .get(1).getLexeme());
  }

  @Test
  public void testAnalyzeManyNames() throws FileNotFoundException {
    DocumentImpl document = new DocumentImpl();
    String text = "O Sr. Luis Carlos e sua esposa Ana Maria foram para a praia .";
    String[] textArray = text.split(" ");

    document.setText(text);
    Sentence sentence = new SentenceImpl(0, 61, document);
    document.setSentences(Collections.singletonList(sentence));

    Span[] spans = { new Span(1, 4), new Span(7, 9) };

    List<Token> tokens = createTokens(textArray);
    sentence.setTokens(tokens);

    when(mockedNameFinder.find(textArray)).thenReturn(spans);

    nameFinder.analyze(document);

    assertEquals("Sr._Luis_Carlos", document.getSentences().get(0).getTokens()
        .get(1).getLexeme());
    assertEquals("Ana_Maria", document.getSentences().get(0).getTokens().get(5)
        .getLexeme());
  }

  @Test
  public void testAnalyzeNoNames() throws FileNotFoundException {
    DocumentImpl document = new DocumentImpl();
    String text = "Eles saíram .";
    String[] textArray = text.split(" ");

    document.setText(text);
    Sentence sentence = new SentenceImpl(0, 61, document);
    document.setSentences(Collections.singletonList(sentence));

    Span[] spans = new Span[0];

    List<Token> tokens = createTokens(textArray);
    sentence.setTokens(tokens);

    when(mockedNameFinder.find(textArray)).thenReturn(spans);

    nameFinder.analyze(document);

    assertEquals(3, document.getSentences().get(0).getTokens().size());
  }

  private List<Token> createTokens(String[] textArray) {
    List<Token> tokens = new ArrayList<Token>();
    int ini = 0;

    for (int i = 0; i < textArray.length; i++) {
      TokenImpl tokenImpl = new TokenImpl(ini, ini + textArray[i].length(), textArray[i]);
      ini = ini + textArray[i].length() + 1;
      tokens.add(tokenImpl);
    }
    return tokens;
  }
}
