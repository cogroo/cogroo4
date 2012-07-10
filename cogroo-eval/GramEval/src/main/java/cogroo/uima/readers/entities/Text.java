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

public class Text {

  private final List<Paragraph> paragraphs;

  private final String text;

  private final int id;

  public Text(List<Paragraph> paragraphs, int id) {
    this.paragraphs = Collections.unmodifiableList(paragraphs);
    StringBuilder sb = new StringBuilder();
    for (Paragraph paragraph : paragraphs) {
      paragraph.setStart(sb.length());
      sb.append(paragraph.getText());
      paragraph.setEnd(sb.length());
      sb.append("\n\n");
    }

    this.text = sb.toString().trim();

    this.id = id;
  }

  public List<Paragraph> getParagraphs() {
    return paragraphs;
  }

  public String getText() {
    return text;
  }

  public int getId() {
    return id;
  }
}
