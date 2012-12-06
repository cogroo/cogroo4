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
package org.cogroo.entities.tree;

public class Leaf extends TreeElement {

  private String word;
  private String features;
  private String[] lemma;
  private boolean isChunkHead;

  public void setLexeme(String lexeme) {
    this.word = lexeme;
  }
  
  public void setIsChunkHead(boolean value) {
    this.isChunkHead = value;
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
      sb.append(this.getSyntacticTag() + "(" + isChunkHeadStr() + this.getMorphologicalTag() + ":" + this.getFeatures()
          + ") ");
    }
    sb.append(this.word + "\n");
    return sb.toString();
  }

  private String isChunkHeadStr() {
    if(isChunkHead)
      return "*";
    return "";
  }

  public void setLemma(String[] lemma) {
    this.lemma = lemma;
  }

  public String[] getLemma() {
    return lemma;
  }

  @Override
  public String toSyntaxTree() {

    return "[" + this.isChunkHeadStr() + this.getMorphologicalTag() + " " + word + "]";
  }

  public String getFeatures() {
    return features;
  }

  public void setFeatures(String features) {
    this.features = features;
  }
}
