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

package br.ccsl.cogroo.entities;

import java.io.Serializable;

import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask;

/**
 * Represents a classification of a token (or chunk) according to its morphological or syntactical classification.
 * 
 * @author William Colen
 */
public abstract class Tag implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
//	protected String tag;
//
//	public Tag(String tagAsString) {
//		this.tag = tagAsString;
//	}
	
	public boolean match(Tag tag) {
		return this.toString().equals(tag.toString());
	}
	
	public abstract boolean match(TagMask tagMask);

	public abstract String toVerboseString();

}
