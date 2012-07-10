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
package org.cogroo.entities.impl;

import java.util.ArrayList;
import java.util.List;

import org.cogroo.entities.Chunk;
import org.cogroo.entities.Token;

import org.cogroo.tools.checker.rules.model.TagMask.Gender;
import org.cogroo.tools.checker.rules.model.TagMask.Number;

/**
 * Implements a {@link Chunk} - group of {@link Token}s.
 * 
 * @author William Colen
 * 
 */
public class ChunkCogroo extends Chunk {

  /**
   * Id for serialization.
   */
  private static final long serialVersionUID = -3501790388693489863L;

  private ChunkCogroo(List<Token> tokens) {
    this.tokens = tokens;
    this.morphologicalTag = null;
  }

  public ChunkCogroo(List<Token> tokens, int firstToken) {
    this(tokens);
    this.firstToken = firstToken;
  }

  @Override
  public String toPlainText() {
    StringBuilder chunkAsString = new StringBuilder();
    for (int i = 0; i < this.tokens.size(); i++) {
      chunkAsString.append(this.tokens.get(i).getLexeme());
      if (i + 1 != this.tokens.size()) {
        chunkAsString.append(' ');
      }
    }
    return chunkAsString.toString();
  }

  private void processTag() {
    List<MorphologicalTag> ml = new ArrayList<MorphologicalTag>();
    // find the MAIN tags
    boolean firstMainFound = false;
    for (int i = 0; i < this.tokens.size(); i++) {
      Token token = this.tokens.get(i);
      String chunkTag = token.getChunkTag().toVerboseString();
      if (chunkTag.lastIndexOf("MAIN") != -1) {
        if (!firstMainFound) {
          firstMainFound = true;
          this.morphologicalTag = token.getMorphologicalTag();
        }
        if (chunkTag.lastIndexOf("NOUN") != -1)
          ml.add(token.getMorphologicalTag());

      } else if (chunkTag.lastIndexOf("OTHER") != -1) {
        return;
      }
    }

    if (ml.size() > 1) {
      // TODO: MT should have setters.
      // take the first as base
      this.morphologicalTag = new MorphologicalTag();
      this.morphologicalTag.setClazz(ml.get(0).getClazzE());
      // always plural
      this.morphologicalTag.setNumber(Number.PLURAL);

      // if any is male, set the full phrase male
      for (MorphologicalTag m : ml) {
        if (m.getGenderE() != null && m.getGenderE().equals(Gender.MALE)) {
          this.morphologicalTag.setGender(Gender.MALE);
          break;
        }
      }
    } else {
      return;
    }

  }

  public void setMorphologicalTag(MorphologicalTag morphologicalTag) {
    this.morphologicalTag = morphologicalTag;
    processTag();
  }
}
