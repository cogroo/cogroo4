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
package org.cogroo.text.tree;

import com.google.common.base.Joiner;

public class Leaf extends TreeElement {

  private String word;
  private String[] lemma;

  public Leaf(String word, String[] lemma) {
    this.word = word;
    this.lemma = lemma;
  }

  public void setLexeme(String lexeme) {
    this.word = lexeme;
  }

  public String getLexeme() {
    return word;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    // print itself and its children
    for (int i = 0; i < this.getLevel(); i++) {
      sb.append("=");
    }
    if (this.getSyntacticTag() != null) {
      sb.append(this.getSyntacticTag() + "(" + this.getMorphologicalTag()
          + ") ");
    }
    sb.append(this.word + "\n");
    return sb.toString();
  }

  public void setLemma(String[] lemma) {
    this.lemma = lemma;
  }

  public String[] getLemma() {
    return lemma;
  }

  @Override
  public String toSyntaxTree() {
    return "[" + getMorphologicalTag() + " " + word + "]";
  }

  @Override
  public String toTreebank() {
    return "(" + createTag() + " " + word + ")";
  }

  private String createTag() {
    boolean hasLemma = getLemma() != null && getLemma().length > 0;
    boolean hasFeats = getFeatureTag() != null && !getFeatureTag().equals("-");

    if (getLexeme().equals(getMorphologicalTag())) {
      return "PUNCT";
    }

    StringBuilder sb = new StringBuilder(getMorphologicalTag().replace("-", ""));
    if (hasLemma || hasFeats) {
      sb.append("-");
      if (hasFeats) {
        sb.append(getFeatureTag());
      } else {
        sb.append("*");
      }
      if (hasLemma) {
        sb.append("-");
        sb.append(Joiner.on('|').join(getLemma()));
      }
    }

    return sb.toString();
  }
}
