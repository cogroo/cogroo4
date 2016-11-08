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

import java.util.Objects;

import opennlp.tools.util.Span;

public class HeadedSpan extends Span {

  private int headPosition = -1;

  public HeadedSpan(int s, int e, String type) {
    super(s, e, type);
  }

  /**
   * Initializes a new Span Object.
   * 
   * @param s
   *          start of span.
   * @param e
   *          end of span.
   */
  public HeadedSpan(int s, int e) {
    super(s, e);
  }

  /**
   * Initializes a new Span object with an existing Span which is shifted by an
   * offset.
   * 
   * @param span
   * @param offset
   */
  public HeadedSpan(Span span, int offset) {
    super(span, offset);
  }

  public void setHead(int headPosition) {
    this.headPosition = headPosition;
  }

  public boolean equals(Object o) {

    boolean result = super.equals(o);
    if (result == true) {
      if (o instanceof HeadedSpan) {
        HeadedSpan s = (HeadedSpan) o;

        result = this.headPosition == s.headPosition;
      }
    }

    return result;
  }
  
  /* (non-Javadoc)
   * @see opennlp.tools.util.Span#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.headPosition);
  }

  /**
   * Generates a human readable string.
   */
  public String toString() {
    StringBuffer toStringBuffer = new StringBuffer(25);
    if (getType() != null) {
      toStringBuffer.append(getType());
      toStringBuffer.append('-');
    }
    toStringBuffer.append(getStart());
    toStringBuffer.append("..");
    toStringBuffer.append(getEnd());
    if (headPosition >= 0) {
      toStringBuffer.append("-");
      toStringBuffer.append(headPosition);
    }

    return toStringBuffer.toString();
  }

  public int getHead() {
    return headPosition;
  }

}
