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
package org.cogroo.tools.checker.rules.util;

import org.cogroo.tools.checker.rules.model.Composition;
import org.cogroo.tools.checker.rules.model.Element;
import org.cogroo.tools.checker.rules.model.Mask;
import org.cogroo.tools.checker.rules.model.PatternElement;
import org.cogroo.tools.checker.rules.model.TagMask;

/**
 * Utility class to compare patterns {@link Element}s, {@link Composition}s, 
 * {@link TagMask}s, {@link Boolean}s and {@link String}s.
 * @author Marcelo Suzumura
 * @author William Colen
 */
public class EqualsUtils {
	
	public static boolean arePatternElementEquals(PatternElement p1, PatternElement p2) {
		Element e1 = p1.getElement();
		Element e2 = p2.getElement();
		Composition c1 = p1.getComposition();
		Composition c2 = p2.getComposition();
		
		if ( e1 != null )
		{
			if( e2 != null )
				return areElementEquals(e1, e2);
			else if( c2 != null )
				return false;
		} 
		if ( c1 != null )
		{
			if( c2 != null )
				return areCompositionEquals(c1, c2);
			else if( e2 != null )
				return false;
		}
		return false;
	}
	
	public static boolean areCompositionEquals(Composition c1, Composition c2) {
		// TODO implemt this
		
		return false;
	}
	
	/**
	 * Checks if two elements are equals.
	 * 
	 * @param element1 from tree element {@link Element}
	 * @param element2 from rule element {@link Element}
	 * @return true if the elements are equal, false otherwise
	 */
	public static boolean areElementEquals(Element element1, Element element2) {
		for (Mask mask1 : element1.getMask()) {
			if (!areBooleanEquals(element1.isNegated(), element2.isNegated())) {
				// Negated.
				return false;
			}
			for (Mask mask2 : element2.getMask()) {
				if (!areStringEquals(mask1.getLexemeMask(), mask2.getLexemeMask())) {
					// LexemeMask.
					return false;
				} else if (!areStringEquals(mask1.getPrimitiveMask(), mask2.getPrimitiveMask())) {
					// PrimitiveMask.
					return false;
				} else if (!areTagMaskEquals(mask1.getTagMask(), mask2.getTagMask())) {
					// TagMask.
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Checks if two tag masks are equals.
	 * The tag masks can be both null, one of them null or none of them null.
	 * 
	 * @param tagMask1 from tree element {@link TagMask}
	 * @param tagMask2 from rule element {@link TagMask}
	 * @return true if equals, otherwise
	 */
	public static boolean areTagMaskEquals(TagMask tagMask1, TagMask tagMask2) {
		if ((tagMask1 == null && tagMask2 != null)
				|| (tagMask1 != null && tagMask2 == null)) {
			return false;
		} else if (tagMask1 != null && tagMask2 != null) {
			if (tagMask1.getCase() != tagMask2.getCase()) {
				return false;
			} else if (tagMask1.getClazz() != tagMask2.getClazz()) {
				return false;
			} else if (tagMask1.getGender() != tagMask2.getGender()) {
				return false;
			} else if (tagMask1.getMood() != tagMask2.getMood()) {
				return false;
			} else if (tagMask1.getNumber() != tagMask2.getNumber()) {
				return false;
			} else if (tagMask1.getPerson() != tagMask2.getPerson()) {
				return false;
			} else if (tagMask1.getPunctuation() != tagMask2.getPunctuation()) {
				return false;
			} else if (tagMask1.getSyntacticFunction() != tagMask2.getSyntacticFunction()) {
				return false;
			} else if (tagMask1.getTense() != tagMask2.getTense()) {
				return false;
			}
			// TODO complete with the rest of the tag masks (if any)
		}
		return true;
	}
	
	/**
	 * Checks if two Booleans are equals.
	 * The booleans can be both null, one of them null or none of them null.
	 * 
	 * @param boolean1 from tree tree element 
	 * @param boolean2 from rule element 
	 * @return true if and only if the two booleans are equal (both equal or both null)
	 */
	public static boolean areBooleanEquals(Boolean boolean1, Boolean boolean2) {
		if (boolean1 != null && !boolean1.equals(boolean2)) {
			return false;
		} else if (boolean2 != null && !boolean2.equals(boolean1)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if two strings are equals.
	 * The strings can be both null, one of them null or none of them null.
	 * 
	 * @param string1 from tree element 
	 * @param string2 from rule element 
	 * @return true if and only if the two strings are equal (both equal or both null)
	 */
	public static boolean areStringEquals(String string1, String string2) {
		/* string1	string2	outcome
		 * null		null	true
		 * null		x		false
		 * x		null	false
		 * x		y		false
		 * x		x		true
		 */
		// XXX both null must be unequal? If yes, boolean must be too?
		if (string1 == null && string2 == null) {
			return false;
		} else if (string1 != null && !string1.equals(string2)) {
			return false;
		} else if (string2 != null && !string2.equals(string1)) {
			return false;
		}
		return true;
	}
	
}
