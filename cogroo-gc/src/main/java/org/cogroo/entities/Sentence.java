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
