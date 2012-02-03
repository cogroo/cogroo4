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

import com.google.common.base.Objects;

import opennlp.tools.util.Span;
import br.ccsl.cogroo.entities.impl.ChunkTag;
import br.ccsl.cogroo.entities.impl.MorphologicalTag;
import br.ccsl.cogroo.entities.impl.SyntacticTag;

/**
 * A token is the smallest annotated unit of the text. Examples: "home" "," "." "12.55"
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
	 * A lexeme from which derives the lexeme of this token.
	 * Example: lexeme = meninas, primitive = menino
	 */
	protected String primitive;

	/**
	 * States the morphological function of this token.
	 */
	protected MorphologicalTag morphologicalTag;

	/**
	 * States if the token starts a phrase, is part of a phrase,
	 * or none of them, in the sentence.
	 */
	protected ChunkTag chunkTag;
	
	/**
	 * States the chunk that the token is part of.
	 */
	protected Chunk chunk;

	/**
	 * States the type of the lexeme.
	 * Example: a word, a punctuation mark, a number, etc.
	 */
	protected LexemeTypes lexemeType;

	/**
	 * The indexes, counted by chars, that represents the position of the token in the sentence.
	 * The first char of the token is the start index and the last char of the token + 1 is the end index.
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
	 * @param span
	 */
	public Token(Span span) {
		this.span = span;
	}

	/**
	 * Constructs a token without a lexeme and with the given span indexes.
	 * @param start the start index of the span
	 * @param end the end index of the span
	 */
	public Token(int start, int end) {
		this.span = new Span(start, end);
	}
	
	public String getLexeme() {
		return this.lexeme;
	}
	
	public abstract void setLexeme(String lexeme);

	public String getPrimitive() {
		return this.primitive;
	}
	
	public void setPrimitive(String primitive) {
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
		if(this.getSyntacticChunk() == null) {
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

	    return Objects.toStringHelper(this)
	        .add("lxm", lexeme)
	        .add("pr", primitive)
	        .add("mp", morphologicalTag)
            .add("ch", chunkTag)
            //.add("lexemeType", lexemeType)
            //.add("span", span)
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
          && Objects.equal(this.syntacticChunk, that.syntacticChunk)
          && Objects.equal(this.chunk, that.chunk)
          && Objects.equal(this.lexemeType, that.lexemeType)
          && Objects.equal(this.span, that.span);
        }
        return false;
	}

	public void setSyntacticChunk(SyntacticChunk syntacticChunk) {
		this.syntacticChunk = syntacticChunk;
	}
	
	public SyntacticChunk getSyntacticChunk() {
		return this.syntacticChunk;
	}

}
