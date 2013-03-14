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

import java.util.ArrayList;
import java.util.List;

import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.impl.SentenceImpl;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.util.Span;

/**
 *  The <code> SentenceDetector</code> class gets all the sentences in the document text and store them in a list of sentences. 
 *
 */
public class SentenceDetector implements Analyzer {

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

    Span[] spans;
    synchronized (sentenceDetector) {
      spans = sentenceDetector.sentPosDetect(document.getText());
    }

    List<Sentence> sentences = new ArrayList<Sentence>(spans.length);

    for (int i = 0; i < spans.length; i++) {
      Sentence sentence = new SentenceImpl(spans[i].getStart(), spans[i].getEnd(), document);
      sentences.add(sentence);
    }

    document.setSentences(sentences);
  }
}
