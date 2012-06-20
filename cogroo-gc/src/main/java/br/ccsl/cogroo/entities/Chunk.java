/**
 * Copyright (C) 2008 CoGrOO Team (cogroo AT gmail DOT com)
 * 
 * CoGrOO Team (cogroo AT gmail DOT com)
 * LTA, PCS (Computer and Digital Systems Engineering Department),
 * Escola Politécnica da Universidade de São Paulo
 * Av. Prof. Luciano Gualberto, trav. 3, n. 380
 * CEP 05508-900 - São Paulo - SP - BRAZIL
 * 
 * http://cogroo.sourceforge.net/
 * 
 * This file is part of CoGrOO.
 * 
 * CoGrOO is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public as published by 
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CoGrOO is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with CoGrOO. If not, see <http://www.gnu.org/licenses/>.
 */

package br.ccsl.cogroo.entities;

import java.io.Serializable;
import java.util.List;

import br.ccsl.cogroo.entities.impl.MorphologicalTag;
import br.ccsl.cogroo.entities.impl.SyntacticTag;

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

  @Override
  public String toString() {

    return Objects.toStringHelper(this).add("tokens", tokens)
        .add("morphologicalTag", morphologicalTag)
        .add("morphologicalTag", morphologicalTag).add("tokens", tokens)
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
