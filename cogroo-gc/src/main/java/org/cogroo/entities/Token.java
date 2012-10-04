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
import java.util.Arrays;

import opennlp.tools.util.Span;

import org.cogroo.entities.impl.ChunkTag;
import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.entities.impl.SyntacticTag;

import com.google.common.base.Objects;

/**
 * A token is the smallest annotated unit of the text. Examples: "home" "," "."
 * "12.55"
 * 
 * @author William Colen
 * 
 */
public abstract class Token implements Serializable {

  private static final long serialVersionUID = 5748072170017854287L;

  /**
   * The string of the token as it is written in the text.
   */
  protected String lexeme;

  /**
   * A lexeme from which derives the lexeme of this token. Example: lexeme =
   * meninas, primitive = menino
   */
  protected String[] primitive;

  /**
   * States the morphological function of this token.
   */
  protected MorphologicalTag morphologicalTag;

  /**
   * States if the token starts a phrase, is part of a phrase, or none of them,
   * in the sentence.
   */
  protected ChunkTag chunkTag;

  /**
   * States the chunk that the token is part of.
   */
  protected Chunk chunk;

  /**
   * States the type of the lexeme. Example: a word, a punctuation mark, a
   * number, etc.
   */
  protected LexemeTypes lexemeType;

  /**
   * The indexes, counted by chars, that represents the position of the token in
   * the sentence. The first char of the token is the start index and the last
   * char of the token + 1 is the end index.
   */
  protected Span span;

  private SyntacticChunk syntacticChunk;

  /**
   * Constructs a token without a lexeme and with a default span (0, 0).
   * 
   */
  public Token() {
    this.span = new Span(0, 0);
  }

  /**
   * Constructs a token with the given span.
   * 
   * @param span
   */
  public Token(Span span) {
    this.span = span;
  }

  /**
   * Constructs a token without a lexeme and with the given span indexes.
   * 
   * @param start
   *          the start index of the span
   * @param end
   *          the end index of the span
   */
  public Token(int start, int end) {
    this.span = new Span(start, end);
  }

  public String getLexeme() {
    return this.lexeme;
  }

  public abstract void setLexeme(String lexeme);

  public String[] getPrimitive() {
    return this.primitive;
  }

  public void setPrimitive(String[] primitive) {
    this.primitive = primitive;
  }

  public MorphologicalTag getMorphologicalTag() {
    return this.morphologicalTag;
  }

  public void setMorphologicalTag(MorphologicalTag tag) {
    this.morphologicalTag = tag;
  }

  public ChunkTag getChunkTag() {
    return this.chunkTag;
  }

  public void setChunkTag(ChunkTag ct) {
    this.chunkTag = ct;
  }

  public void setSpan(Span span) {
    this.span = span;
  }

  public SyntacticTag getSyntacticTag() {
    if (this.getSyntacticChunk() == null) {
      return null;
    }
    return this.getSyntacticChunk().getSyntacticTag();
  }

  public Chunk getChunk() {
    return this.chunk;
  }

  public void setChunk(Chunk chunk) {
    this.chunk = chunk;
  }

  public LexemeTypes getLexemeType() {
    return this.lexemeType;
  }

  public Span getSpan() {
    return this.span;
  }

  @Override
  public String toString() {

    return Objects.toStringHelper(this).add("lxm", lexeme).add("pr", Arrays.toString(primitive))
        .add("mp", morphologicalTag).add("ch", chunkTag)
        // .add("lexemeType", lexemeType)
        // .add("span", span)
        .toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Token) {
      Token that = (Token) obj;
      return Objects.equal(this.lexeme, that.lexeme)
          && Objects.equal(this.primitive, that.primitive)
          && Objects.equal(this.morphologicalTag, that.morphologicalTag)
          && Objects.equal(this.chunkTag, that.chunkTag)
//          && Objects.equal(this.syntacticChunk, that.syntacticChunk)
//          && Objects.equal(this.chunk, that.chunk)
          && Objects.equal(this.lexemeType, that.lexemeType)
          && Objects.equal(this.span, that.span);
    }
    return false;
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(this.lexeme, this.primitive, this.morphologicalTag, 
                                this.chunkTag, this.lexemeType, this.span);
  }

  public void setSyntacticChunk(SyntacticChunk syntacticChunk) {
    this.syntacticChunk = syntacticChunk;
  }

  public SyntacticChunk getSyntacticChunk() {
    return this.syntacticChunk;
  }

}
