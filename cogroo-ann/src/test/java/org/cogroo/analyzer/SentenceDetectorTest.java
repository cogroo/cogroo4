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

import static org.junit.Assert.*;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.util.Span;

import org.cogroo.analyzer.SentenceDetector;
import org.cogroo.text.impl.DocumentImpl;
import org.junit.Before;
import org.junit.Test;

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
    DocumentImpl document = new DocumentImpl();
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
    DocumentImpl document = new DocumentImpl();
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
    DocumentImpl document = new DocumentImpl();
    String text = null;
    document.setText(text);

    Span[] spans = new Span[0];

    when(mockedSentenceDetector.sentPosDetect(text)).thenReturn(spans);

    sentenceDetector.analyze(document);
  }
}
