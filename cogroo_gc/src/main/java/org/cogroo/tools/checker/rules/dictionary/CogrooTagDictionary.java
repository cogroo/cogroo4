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
package org.cogroo.tools.checker.rules.dictionary;

import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.interpreters.TagInterpreter;

import org.cogroo.tools.checker.rules.model.TagMask;

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

  public TagInterpreter getTagInterpreter();

}
