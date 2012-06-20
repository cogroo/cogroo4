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

public class ContractionFinderTest {
  private ContractionFinder contractionFinder;
  NameFinderME mockedContractionFinder;

  @Before
  public void setUp() throws Exception {
    mockedContractionFinder = mock(NameFinderME.class);
    contractionFinder = new ContractionFinder(mockedContractionFinder);
  }

  @Test
  public void testAnalyze() throws FileNotFoundException {
    DocumentImpl document = new DocumentImpl();
    String text = "A filha dela vai à tarde ao cinema do centro .";
    document.setText(text);

    Sentence sentence = new SentenceImpl(0, text.length(),document);
    document.setSentences(Collections.singletonList(sentence));

    String[] textArray = text.split(" ");
    List<Token> tokens = createTokens(textArray);
    sentence.setTokens(tokens);

    Span[] spans = { new Span(2, 3), new Span(4, 5), new Span(6, 7),
        new Span(8, 9) };

    when(mockedContractionFinder.find(textArray)).thenReturn(spans);

    contractionFinder.analyze(document);

    assertEquals(15, document.getSentences().get(0).getTokens().size());
    assertEquals("de", document.getSentences().get(0).getTokens().get(2)
        .getLexeme());
    assertEquals("ela", document.getSentences().get(0).getTokens().get(3)
        .getLexeme());
  }

  @Test
  public void testAnalyzeNoContractions() throws FileNotFoundException {
    DocumentImpl document = new DocumentImpl();
    String text = "A filha de ela vai a a tarde a o cinema de o centro .";
    document.setText(text);

    Sentence sentence = new SentenceImpl(0, text.length(), document);
    document.setSentences(Collections.singletonList(sentence));

    String[] textArray = text.split(" ");
    List<Token> tokens = createTokens(textArray);
    sentence.setTokens(tokens);

    Span[] spans = new Span[0];

    when(mockedContractionFinder.find(textArray)).thenReturn(spans);

    contractionFinder.analyze(document);

    assertEquals(15, document.getSentences().get(0).getTokens().size());
    assertEquals("de", document.getSentences().get(0).getTokens().get(2)
        .getLexeme());
    assertEquals("ela", document.getSentences().get(0).getTokens().get(3)
        .getLexeme());
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
