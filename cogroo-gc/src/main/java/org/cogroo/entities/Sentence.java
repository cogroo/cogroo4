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

package org.cogroo.entities;

import java.io.Serializable;
import java.util.List;

import org.cogroo.entities.tree.Node;

import opennlp.tools.util.Span;

import com.google.common.base.Objects;

/**
 * Data structure that represents a natural language sentence. The annotations
 * are added to the structure while the sentence is processed.
 * 
 * @author William Colen
 * 
 */
public class Sentence implements Serializable, TokenGroup {

  /**
	 * 
	 */
  private static final long serialVersionUID = -5370072688009577273L;

  /**
   * The original sentence that will be processed.
   */
  protected String sentence;

  /**
   * The sentence separated in tokens.
   */
  protected List<Token> tokens;

  /**
   * The sentence separated in chunks.
   */
  protected List<Chunk> chunks;

  /**
   * Tree structure of this sentence
   */
  private Node root;

  private int offset;

  private Span span;

  /**
   * Gets the representation of the sentence as a plain text.
   * 
   * @return The sentence as a string.
   */
  public String toPlainText() {
    return this.sentence;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("sent", sentence)
        .add("tks", tokens).add("cks", chunks).add("root", root)
        // .add("offset", offset)
        .toString();
  }

  /**
   * Gets the original sentence.
   * 
   * @return the original sentence
   */
  public String getSentence() {
    return this.sentence;
  }

  /**
   * Sets the original sentence.
   * 
   * @param sentence
   */
  public void setSentence(String sentence) {
    this.sentence = sentence;
  }

  public void setSpan(Span aSpan) {
    this.span = aSpan;
  }

  public Span getSpan() {
    return this.span;
  }

  /**
   * Gets the Tokens of this sentence.
   * 
   * @return Array of Tokens
   */
  public List<Token> getTokens() {
    return this.tokens;
  }

  /**
   * Sets the Tokens of this sentence.
   * 
   * @param tokens
   */
  public void setTokens(List<Token> tokens) {
    this.tokens = tokens;
  }

  /**
   * Sets the Chunks of this sentence.
   * 
   * @return Array of Chunks
   */
  public List<Chunk> getChunks() {
    return this.chunks;
  }

  /**
   * Sets the Chunks of this sentence.
   * 
   * @param chunks
   */
  public void setChunks(List<Chunk> chunks) {
    this.chunks = chunks;
  }

  private List<SyntacticChunk> syntacticChunks = null;

  public List<SyntacticChunk> getSyntacticChunks() {
    return syntacticChunks;
  }

  public void setSyntacticChunks(List<SyntacticChunk> sc) {
    syntacticChunks = sc;
  }

  public void setRoot(Node root) {
    this.root = root;
  }

  public synchronized Node getRoot() {
    if (this.root != null) {
      return root;
    }
    synchronized (this) {
      if (this.root == null) {
        this.root = new Node();//OldStyleModel.createTree(this);
      }
      return root;
    }
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public int getOffset() {
    return offset;
  }

  public String getSyntaxTree() {
    return getRoot().toSyntaxTree();
  }

  public String getTree() {
    return getRoot().toString();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Sentence) {
      Sentence that = (Sentence) object;
      return Objects.equal(this.sentence, that.sentence)
          && Objects.equal(this.tokens, that.tokens)
          && Objects.equal(this.chunks, that.chunks)
          && Objects.equal(this.root, that.root)
          && Objects.equal(this.offset, that.offset);
    }
    return false;
  }
}
