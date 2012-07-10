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
