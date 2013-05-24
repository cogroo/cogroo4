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
package org.cogroo.checker;

import java.util.List;

import org.cogroo.entities.Mistake;
import org.cogroo.entities.Sentence;
import org.cogroo.text.impl.DocumentImpl;
import org.cogroo.util.TextUtils;

import com.google.common.base.Strings;


public class CheckDocument extends DocumentImpl {
  
  public CheckDocument() {
    super();
  }

  public CheckDocument(String text) {
    super(text);
  }
  
  private List<Mistake> mistakes;
  private List<Sentence> legacySentences;

  public List<Mistake> getMistakes() {
    return mistakes;
  }

  public void setMistakes(List<Mistake> mistakes) {
    this.mistakes = mistakes;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(TextUtils.nicePrint(this) + "\n");
    sb.append(getMistakesAsString());
    
    return sb.toString();
  }
  
  public String getMistakesAsString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Mistakes count: ").append(mistakes.size()).append("\n");
    for (int i = 0; i < mistakes.size(); i++) {
      sb.append("{Mistake [").append(i).append("]\n");
      sb.append(mistakes.get(i)).append("\n");
      sb.append("}").append("\n");
    }
    
    return sb.toString();
  }

  public List<Sentence> getSentencesLegacy() {
    return legacySentences;
  }

  public void setSentencesLegacy(
      List<org.cogroo.entities.Sentence> typedSentences) {
    this.legacySentences = typedSentences;
  }

}
