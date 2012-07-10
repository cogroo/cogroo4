/**
 * Copyright (C) 2012 cogroo <cogroo@cogroo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cogroo.analyzer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.Span;

import org.cogroo.analyzer.ContractionFinder;
import org.cogroo.text.Sentence;
import org.cogroo.text.Token;
import org.cogroo.text.impl.DocumentImpl;
import org.cogroo.text.impl.SentenceImpl;
import org.cogroo.text.impl.TokenImpl;
import org.junit.Before;
import org.junit.Test;


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
