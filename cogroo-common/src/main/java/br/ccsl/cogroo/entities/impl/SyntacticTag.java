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

package br.ccsl.cogroo.entities.impl;

import com.google.common.base.Objects;

import br.ccsl.cogroo.entities.Tag;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.SyntacticFunction;

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
		if (tagMask.getSyntacticFunction() != null ) {
			SyntacticFunction sf = tagMask.getSyntacticFunction();
			if (sf.equals(SyntacticFunction.SUBJECT) && this.syntacticFunction.equals(SyntacticFunction.SUBJECT)/*this.tag.equals("SUBJ")*/) {
				return true;
			} else if (sf.equals(SyntacticFunction.VERB) && this.syntacticFunction.equals(SyntacticFunction.VERB)/*this.tag.equals("MV")*/) {
				return true;
			} else if(sf.equals(SyntacticFunction.NONE) && this.syntacticFunction.equals(SyntacticFunction.NONE))
				return true;
		}
		else if (this.syntacticFunction.equals(SyntacticFunction.NONE))
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
	          return Objects.equal(this.syntacticFunction, that.syntacticFunction);
	        }
	        return false;
	    }

}
