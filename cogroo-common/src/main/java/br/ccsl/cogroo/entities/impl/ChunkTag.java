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

import br.ccsl.cogroo.entities.Tag;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.ChunkFunction;

import com.google.common.base.Objects;

/**
 * Implements a {@link Tag} for chunk annotation
 * 
 * @author William Colen
 * 
 */
public class ChunkTag extends Tag {

  /**
   * Id for serialization.
   */
  private static final long serialVersionUID = 7032402552075677239L;

  private ChunkFunction chunkFunction;

  public ChunkFunction getChunkFunction() {
    return chunkFunction;
  }

  public void setChunkFunction(ChunkFunction chankFunction) {
    this.chunkFunction = chankFunction;
  }

  @Override
  public boolean match(TagMask tagMask) {
    if (this.chunkFunction != null && tagMask.getChunkFunction() != null) {
      return this.chunkFunction == tagMask.getChunkFunction();
    } else if (this.chunkFunction == null && tagMask.getChunkFunction() == null) {
      return true;
    } else {
      return false;
    }
  }

  public boolean match(Tag tag) {
    if (tag instanceof ChunkTag) {
      ChunkTag chunkTag = (ChunkTag) tag;
      if (this.chunkFunction != null && chunkTag.getChunkFunction() != null) {
        return this.chunkFunction == chunkTag.getChunkFunction();
      } else if (this.chunkFunction == null
          && chunkTag.getChunkFunction() == null) {
        return true;
      } else {
        return false;
      }

    } else {
      return false;
    }
  }

  @Override
  public String toVerboseString() {
    return chunkFunction.name();
  }

  @Override
  public String toString() {
    String tagAsString = "";
    tagAsString += this.chunkFunction.name();
    return tagAsString;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ChunkTag) {
      ChunkTag that = (ChunkTag) obj;
      return Objects.equal(this.chunkFunction, that.chunkFunction);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(chunkFunction);
  }

}
