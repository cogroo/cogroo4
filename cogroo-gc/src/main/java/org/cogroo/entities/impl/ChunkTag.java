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

import org.cogroo.entities.Tag;

import org.cogroo.tools.checker.rules.model.TagMask;
import org.cogroo.tools.checker.rules.model.TagMask.ChunkFunction;

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
