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

import org.cogroo.tools.checker.rules.model.TagMask;


/**
 * Represents a classification of a token (or chunk) according to its
 * morphological or syntactical classification.
 * 
 * @author William Colen
 */
public abstract class Tag implements Serializable {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  // protected String tag;
  //
  // public Tag(String tagAsString) {
  // this.tag = tagAsString;
  // }

  public boolean match(Tag tag) {
    return this.toString().equals(tag.toString());
  }

  public abstract boolean match(TagMask tagMask);

  public abstract String toVerboseString();

}
