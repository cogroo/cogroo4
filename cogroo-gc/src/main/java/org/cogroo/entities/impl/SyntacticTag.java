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

import java.util.Objects;

import org.cogroo.entities.Tag;
import org.cogroo.tools.checker.rules.model.TagMask;
import org.cogroo.tools.checker.rules.model.TagMask.SyntacticFunction;

/**
 * Implements a {@link Tag} for shallow-parser annotation
 * 
 * @author Fábio Wang Gusukuma
 * 
 */
public class SyntacticTag extends Tag {

  public SyntacticFunction getSyntacticFunction() {
    return syntacticFunction;
  }

  public void setSyntacticFunction(SyntacticFunction syntacticFunction) {
    this.syntacticFunction = syntacticFunction;
  }

  private static final long serialVersionUID = -8695340746088802844L;

  private SyntacticFunction syntacticFunction;

  @Override
  public boolean match(TagMask tagMask) {
    if (tagMask.getSyntacticFunction() != null) {
      SyntacticFunction sf = tagMask.getSyntacticFunction();
      if (sf.equals(SyntacticFunction.SUBJECT)
          && this.syntacticFunction.equals(SyntacticFunction.SUBJECT)/*
                                                                      * this.tag.
                                                                      * equals
                                                                      * ("SUBJ")
                                                                      */) {
        return true;
      } else if (sf.equals(SyntacticFunction.SUBJECT_PREDICATIVE)
          && this.syntacticFunction.equals(SyntacticFunction.SUBJECT_PREDICATIVE)) {
        return true;
      } else if (sf.equals(SyntacticFunction.VERB)
          && this.syntacticFunction.equals(SyntacticFunction.VERB)/*
                                                                   * this.tag.equals
                                                                   * ("MV")
                                                                   */) {
        return true;
      } else if (sf.equals(SyntacticFunction.NONE)
          && this.syntacticFunction.equals(SyntacticFunction.NONE))
        return true;
    } else if (this.syntacticFunction.equals(SyntacticFunction.NONE))
      return true;
    return false;
  }

  @Override
  public String toVerboseString() {
    return this.syntacticFunction.name();
  }

  @Override
  public String toString() {
    String tagAsString = "";
    tagAsString += this.syntacticFunction.name();
    return tagAsString;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SyntacticTag) {
      SyntacticTag that = (SyntacticTag) obj;
      return Objects.equals(this.syntacticFunction, that.syntacticFunction);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.syntacticFunction);
  }
}
