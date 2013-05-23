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
package cogroo.uima.readers.entities;

import java.util.Collections;
import java.util.List;

public class Paragraph {

  private final List<SentenceEx> sentences;

  private final int id;
  private int start;
  private int end;

  private final String text;

  public Paragraph(List<SentenceEx> sentences, int id) {
    this.sentences = Collections.unmodifiableList(sentences);

    this.start = sentences.get(0).getStart();

    StringBuilder sb = new StringBuilder();
    for (SentenceEx sentence : sentences) {
      sentence.setStart(sb.length());
      sb.append(sentence.getSentence().getText() + " ");
    }
    text = sb.substring(0, sb.length() - 1);

    end = this.start + text.length();

    this.id = id;
  }

  public List<SentenceEx> getSentences() {
    return sentences;
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return end;
  }

  public String getText() {
    return text;
  }

  public int getId() {
    return id;
  }

  public void setStart(int start) {
    this.start = start;

    for (SentenceEx sentence : sentences) {
      sentence.setStart(sentence.getStart() + start);
    }
  }

  public void setEnd(int end) {
    this.end = end;
  }
}
