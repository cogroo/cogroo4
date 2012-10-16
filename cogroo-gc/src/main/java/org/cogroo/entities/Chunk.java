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
package org.cogroo.entities;

import java.io.Serializable;
import java.util.List;

import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.entities.impl.SyntacticTag;

import com.google.common.base.Objects;

/**
 * A Chunk is an annotated group of {@link Token}s. The annotation could be SN,
 * SN or Other.
 */
public abstract class Chunk implements Serializable, TokenGroup {

  /**
	 * 
	 */
  private static final long serialVersionUID = -3742023174482515909L;

  protected List<Token> tokens;

  protected int firstToken;

  protected MorphologicalTag morphologicalTag;

  /**
   * Gets the representation of the sentence as a plain text.
   * 
   * @return The sentence as a string.
   */
  public abstract String toPlainText();

  /**
   * Gets the Tokens of this sentence
   * 
   * @return Array of Tokens
   */
  public List<Token> getTokens() {
    return this.tokens;
  }

  public int getFirstToken() {
    return this.firstToken;
  }

  public void setFirstToken(int firstToken) {
    this.firstToken = firstToken;
  }

  /**
   * @return the morphologicalTag
   */
  public MorphologicalTag getMorphologicalTag() {
    return this.morphologicalTag;
  }

  /**
   * @param morphologicalTag
   *          the morphologicalTag to set
   */
  public void setMorphologicalTag(MorphologicalTag morphologicalTag) {
    this.morphologicalTag = morphologicalTag;
  }

  public SyntacticTag getSyntacticTag() {
    return this.tokens.get(0).getSyntacticTag();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Chunk) {
      Chunk that = (Chunk) obj;
      return Objects.equal(this.tokens, that.tokens)
          && Objects.equal(this.firstToken, that.firstToken)
          && Objects.equal(this.morphologicalTag, that.morphologicalTag);
    }
    return false;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(this.tokens, this.firstToken, this.morphologicalTag);
  }

  @Override
  public String toString() {

    return Objects.toStringHelper(this).add("text", toPlainText())
        .add("main", getMainToken().getLexeme())
        .add("mtag", morphologicalTag)
        .add("tag", getTokens().get(0).getChunkTag())
        .toString();
  }

  public Token getMainToken() {
    for (Token t : this.getTokens()) {
      if (t.getChunkTag().toString().contains("MAIN")) {
        return t;
      }
    }
    return this.getTokens().get(0);
  }

}
