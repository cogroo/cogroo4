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

package org.cogroo.tools.checker.rules.applier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.cogroo.tools.checker.rules.model.PatternElement;

/**
 * A state, which is part of rules DFA.
 * 
 * @author Marcelo Suzumura
 * @version $Id: State.java 186 2007-03-07 01:08:07Z colen $
 */
public class State implements Serializable {

	/**
	 * Id for serialization.
	 */
	private static final long serialVersionUID = -3634379850399711427L;

	private int name;

	private PatternElement element;

	private List<State> nextStates;

	/**
	 * @param name
	 *            the integer that represents the name of the state
	 * @param element
	 *            the element from the rules file
	 */
	public State(int name, PatternElement element) {
		this.name = name;
		this.element = element;
		this.nextStates = new ArrayList<State>();
	}

	public int getName() {
		return this.name;
	}

	public PatternElement getElement() {
		return this.element;
	}

	public List<State> getNextStates() {
		return this.nextStates;
	}

	@Override
	public String toString() {
		return Integer.toString(this.getName()) + " " + this.getElement();
	}

}
