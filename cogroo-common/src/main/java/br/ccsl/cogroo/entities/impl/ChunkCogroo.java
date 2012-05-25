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

package br.ccsl.cogroo.entities.impl;

import java.util.ArrayList;
import java.util.List;

import br.ccsl.cogroo.entities.Chunk;
import br.ccsl.cogroo.entities.Token;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Gender;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Number;

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
