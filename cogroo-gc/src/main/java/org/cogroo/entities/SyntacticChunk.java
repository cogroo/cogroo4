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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.entities.impl.SyntacticTag;

import org.cogroo.tools.checker.rules.model.TagMask.Gender;
import org.cogroo.tools.checker.rules.model.TagMask.Number;
import org.cogroo.tools.checker.rules.model.TagMask.SyntacticFunction;

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
    if (tag == null) {
      // here we try to guess a mtag for the syntactic chunk.
      if (syntacticTag.match(NONE)) {
        tag = getChildChunks().get(0).getMainToken().morphologicalTag;
      } else if (syntacticTag.match(MV)) {
        for (Chunk verbChunk : getChildChunks()) {
          if (verbChunk.getMainToken() != null) {
            tag = verbChunk.getMainToken().getMorphologicalTag();
            break;
          }
        }
      } else if (syntacticTag.match(SUBJ)) {
        boolean hasMale = false;
        boolean hasFemale = false;
        boolean hasSingular = false;
        boolean hasPlural = false;
        for (Chunk subjChunk : getChildChunks()) {
          if (tag == null && subjChunk.getMainToken() != null) {
            tag = subjChunk.getMainToken().getMorphologicalTag();
          }

          if (subjChunk.getMainToken() != null) {
            MorphologicalTag mt = subjChunk.getMainToken()
                .getMorphologicalTag();
            if ((!hasFemale || !hasMale) && mt.getGenderE() != null
                && mt.getGenderE().equals(Gender.NEUTRAL)) {
              hasFemale = true;
              hasMale = true;
            } else if (!hasFemale && mt.getGenderE() != null
                && mt.getGenderE().equals(Gender.FEMALE)) {
              hasFemale = true;
            } else if (!hasMale && mt.getGenderE() != null
                && mt.getGenderE().equals(Gender.MALE)) {
              hasMale = true;
            }

            if ((!hasSingular || !hasPlural) && mt.getNumberE() != null
                && mt.getNumberE().equals(Number.NEUTRAL)) {
              hasSingular = true;
              hasPlural = true;
            } else if (!hasSingular && mt.getNumberE() != null
                && mt.getNumberE().equals(Number.SINGULAR)) {
              hasSingular = true;
            } else if (!hasPlural && mt.getNumberE() != null
                && mt.getNumberE().equals(Number.PLURAL)) {
              hasPlural = true;
            }
          }
        }
        tag = tag.clone();
        if (hasFemale && hasMale) {
          tag.setGender(Gender.NEUTRAL);
        } else if (hasFemale) {
          tag.setGender(Gender.FEMALE);
        } else if (hasMale) {
          tag.setGender(Gender.MALE);
        }

        if (hasSingular && hasPlural) {
          tag.setNumber(Number.NEUTRAL);
        } else if (hasSingular) {
          tag.setNumber(Number.SINGULAR);
        } else if (hasPlural) {
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
      return /*
              * Objects.equal(this.tokens, that.tokens) &&
              * Objects.equal(this.firstToken, that.firstToken) &&
              */Objects.equal(this.getChildChunks(), that.getChildChunks())
          && Objects.equal(this.syntacticTag, that.syntacticTag);
    }
    return false;
  }

  public List<Chunk> getChildChunks() {
    return this.chunks;
  }

  @Override
  public String toString() {

    return Objects.toStringHelper(this).add("cks", chunks)
        .add("mtag", this.getMorphologicalTag())
        .add("syntacticTag", syntacticTag).toString();
  }

  public int getFirstToken() {
    return chunks.get(0).getFirstToken();
  }

  private List<Token> tokens = null;

  public List<Token> getTokens() {

    if (tokens == null) {
      List<Token> tks = new ArrayList<Token>();
      for (Chunk c : chunks) {
        tks.addAll(c.getTokens());
      }
      tokens = Collections.unmodifiableList(tks);
    }

    return tokens;
  }

}
