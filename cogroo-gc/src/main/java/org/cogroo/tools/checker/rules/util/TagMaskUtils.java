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

package org.cogroo.tools.checker.rules.util;

import org.cogroo.tools.checker.rules.model.TagMask;

/**
 * Utility methods to work with {@link TagMask}s. Specially to clone a {@link TagMask}
 * .
 * 
 * @author colen
 *
 */
public class TagMaskUtils {

	/**
	 * Creates an identical copy of the parameter <code>tagMask</code>.
	 * 
	 * @param tagMask
	 *            the tag mask to be cloned
	 * @return the clone
	 */
	public static TagMask clone(TagMask tagMask) {
		TagMask clone = new TagMask();
		clone.setCase(tagMask.getCase());
		clone.setChunkFunction(tagMask.getChunkFunction());
		clone.setClazz(tagMask.getClazz());
		clone.setGender(tagMask.getGender());
		clone.setMood(tagMask.getMood());
		clone.setNumber(tagMask.getNumber());
		clone.setPerson(tagMask.getPerson());
		clone.setPunctuation(tagMask.getPunctuation());
		clone.setSyntacticFunction(tagMask.getSyntacticFunction());
		clone.setTense(tagMask.getTense());
		return clone;
	}

}
