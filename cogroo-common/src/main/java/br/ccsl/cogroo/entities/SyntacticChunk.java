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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.ccsl.cogroo.entities.impl.MorphologicalTag;
import br.ccsl.cogroo.entities.impl.SyntacticTag;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Gender;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Number;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.SyntacticFunction;

import com.google.common.base.Objects;

/**
 * Initially a subject or verb group of Chunks
 */
public class SyntacticChunk implements Serializable {


	private static final long serialVersionUID = 4768788694700581906L;

	protected List<Chunk> chunks;

	protected SyntacticTag syntacticTag;
	
	private final SyntacticTag SUBJ;
	private final SyntacticTag MV;
	private final SyntacticTag NONE;
	
	public SyntacticChunk(List<Chunk> childChunks) {
		this.chunks = childChunks;
		
		SUBJ = new SyntacticTag();
		SUBJ.setSyntacticFunction(SyntacticFunction.SUBJECT);
		
		MV = new SyntacticTag();
		MV.setSyntacticFunction(SyntacticFunction.VERB);

		NONE = new SyntacticTag();
		NONE.setSyntacticFunction(SyntacticFunction.NONE);
	}

	public String toPlainText() {
		StringBuilder chunkAsString = new StringBuilder();
		for (int i = 0; i < this.chunks.size(); i++) {
			chunkAsString.append(this.chunks.get(i).toPlainText());
			if (i + 1 != this.chunks.size()) {
				chunkAsString.append(' ');
			}
		}
		return chunkAsString.toString();
	}
	
	private MorphologicalTag tag = null;
	
	/**
	 * @return the morphologicalTag
	 */
	public MorphologicalTag getMorphologicalTag() {
		if(tag == null) {
			// here we try to guess a mtag for the syntactic chunk.
			if(syntacticTag.match(NONE)) {
				tag = getChildChunks().get(0).getMainToken().morphologicalTag;
			} else if(syntacticTag.match(MV)) {
				for (Chunk verbChunk : getChildChunks()) {
					if(verbChunk.getMainToken() != null) {
						tag = verbChunk.getMainToken().getMorphologicalTag();
						break;
					}
				}
			} else if(syntacticTag.match(SUBJ)) {
				boolean hasMale = false;
				boolean hasFemale = false;
				boolean hasSingular = false;
				boolean hasPlural = false;
				for (Chunk subjChunk : getChildChunks()) {
					if(tag == null && subjChunk.getMainToken() != null) {
						tag = subjChunk.getMainToken().getMorphologicalTag();
					}
					
					if(subjChunk.getMainToken() != null) {
						MorphologicalTag mt = subjChunk.getMainToken().getMorphologicalTag();
						if((!hasFemale || !hasMale) && mt.getGenderE() != null && mt.getGenderE().equals(Gender.NEUTRAL)) {
							hasFemale = true; hasMale = true;
						} else if(!hasFemale && mt.getGenderE() != null && mt.getGenderE().equals(Gender.FEMALE)) {
							hasFemale = true;
						} else if(!hasMale && mt.getGenderE() != null && mt.getGenderE().equals(Gender.MALE)) {
							hasMale = true;
						}

						if((!hasSingular || !hasPlural) && mt.getNumberE() != null && mt.getNumberE().equals(Number.NEUTRAL)) {
							hasSingular = true; hasPlural = true;
						} else if(!hasSingular && mt.getNumberE() != null && mt.getNumberE().equals(Number.SINGULAR)) {
							hasSingular = true;
						} else if(!hasPlural && mt.getNumberE() != null && mt.getNumberE().equals(Number.PLURAL)) {
							hasPlural = true;
						}
					}
				}
				tag = tag.clone();
				if(hasFemale && hasMale) {
					tag.setGender(Gender.NEUTRAL);
				} else if(hasFemale) {
					tag.setGender(Gender.FEMALE);
				} else if(hasMale) {
					tag.setGender(Gender.MALE);
				}

				if(hasSingular && hasPlural) {
					tag.setNumber(Number.NEUTRAL);
				} else if(hasSingular) {
					tag.setNumber(Number.SINGULAR);
				} else if(hasPlural) {
					tag.setNumber(Number.PLURAL);
				}
			}

		}
		return tag;
	}

	public SyntacticTag getSyntacticTag() {
		return this.syntacticTag;
	}

	public void setSyntacticTag(SyntacticTag syntacticTag) {
		this.syntacticTag = syntacticTag;
	}
	
	   @Override
	    public boolean equals(Object obj) {
	      if (obj instanceof SyntacticChunk) {
	        SyntacticChunk that = (SyntacticChunk) obj;
	          return /*Objects.equal(this.tokens, that.tokens) 
	          && Objects.equal(this.firstToken, that.firstToken)
              &&*/ Objects.equal(this.getChildChunks(), that.getChildChunks())
              && Objects.equal(this.syntacticTag, that.syntacticTag);
	        }
	        return false;
	    }

	public List<Chunk> getChildChunks() {
		return this.chunks;
	}

	@Override
	   public String toString() {

	     return Objects.toStringHelper(this)
         .add("cks", chunks)
         .add("mtag", this.getMorphologicalTag())
	         .add("syntacticTag", syntacticTag).toString();
	   }

	public int getFirstToken() {
		return chunks.get(0).getFirstToken();
	}

	private List<Token> tokens = null;
	
	public List<Token> getTokens() {
		
		if(tokens == null) {
			List<Token> tks = new ArrayList<Token>();
			for (Chunk c : chunks) {
				tks.addAll(c.getTokens());
			}
			tokens = Collections.unmodifiableList(tks);
		}
		
		return tokens;
	}

}
