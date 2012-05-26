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

package br.ccsl.cogroo.tools.checker.rules.dictionary;

import br.ccsl.cogroo.entities.impl.MorphologicalTag;
import br.ccsl.cogroo.interpreters.TagInterpreterI;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask;

/**
 * Provides a way of determining which tags are valid for a particular word based on a tag dictionary read
 * from a file.
 * 
 * @author William Colen
 */
public interface CogrooTagDictionary {
	
	/**
	 * Returns a list of valid tags for the specified word.
	 * 
	 * @param word
	 *            The word.
	 * @param caseSensitive
	 *            Specifies whether the tag dictionary is case sensitive or not.
	 * @return A list of valid tags for the specified word or null if no information is available for that
	 *         word.
	 */
	public MorphologicalTag[] getTags(String word);

	/**
	 * Returns a list of valid tags for the specified word.
	 * 
	 * @param word
	 *            The word.
	 * @param caseSensitive
	 *            Specifies whether the tag dictionary is case sensitive or not.
	 * @return A list of valid tags for the specified word or null if no information is available for that
	 *         word.
	 */
	public MorphologicalTag[] getTags(String word, boolean cs);

	/**
	 * Tells if a lexeme inflected as determined by the tagMask, exists in the dictionary.
	 * 
	 * @param lexeme
	 *            the lexeme to be searched
	 * @param tagMask
	 *            the inflection of the lexeme
	 * @param cs
	 *            case sensitive?
	 * @return true if the lexeme is found, false otherwise
	 */
	public boolean match(String lexeme, TagMask tagMask, boolean cs);

	/**
	 * Given a <code>lexeme</code>, returns its inflected form as determined by the <code>tagMask</code>.
	 * Returns an array with an empty string if the inflection could not be found.
	 * 
	 * @param tokens
	 *            the lexeme to be inflected
	 * @param tagMask
	 *            the tag mask will determine the inflection
	 * @param cs
	 *            case sensitive?
	 * @return the inflected form of the lexeme
	 */
	public String[] getInflectedPrimitive(String primitive, TagMask tagMask, boolean cs);

	/**
	 * Given a <code>lexeme</code> and its inflected form as determined by the <code>tagMask</code>,
	 * returns its primitive.
	 * 
	 * @param lexeme
	 *            the lexeme of which the primitive will be searched
	 * @param tagMask
	 *            the mask that represents the inflection of the lexeme
	 * @param cs
	 *            case sensitive?
	 * @return the primitive of the lexeme
	 */
	public String[] getPrimitive(String lexeme, TagMask tagMask, boolean cs);

	/**
	 * Given a lexeme and its morphological tag, returns the possible primitives of the lexeme or an array
	 * with an empty string, if none is found.
	 * 
	 * @param lexeme
	 *            a lexeme
	 * @param morphologicalTag
	 *            a morphological tag
	 * @param cs
	 *            tells whether the match of the lexeme must be case sensitive or not
	 * @return the primitives of the lexeme with the associated morphological tag or an array with an empty
	 *         string as the first element if no primitive is found
	 */
	public String[] getPrimitive(String lexeme, MorphologicalTag morphologicalTag, boolean cs);

	
	public boolean exists(String word, boolean cs);

  public TagInterpreterI getTagInterpreter();

}
