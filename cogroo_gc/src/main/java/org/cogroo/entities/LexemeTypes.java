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

/**
 * <b>[Development]</b> Describe the possible classification of a lexeme: word,
 * compound word, punctuation mark, other marks, number, hyphen. <br/>
 * It is required only while performing Corpus sentence realization (from corpus
 * notation to plain text).
 * 
 * @author William Colen
 * 
 */
public enum LexemeTypes {
  /** */
  WORD,

  /** */
  COMPOUND_WORD_SEQUENCE,

  /**
   * Closing: new old"
   * <p>
   * normal punctuation: It is it, isn't it?
   * </p>
   */
  OPENING_PUNCTUATION_MARK,

  /** Opening: The "new */
  CLOSING_PUNCTUATION_MARK,

  /** Things like -- */
  ANOTHER_MARK,

  /** */
  NUMBER,

  /** */
  HYPHEN
}
