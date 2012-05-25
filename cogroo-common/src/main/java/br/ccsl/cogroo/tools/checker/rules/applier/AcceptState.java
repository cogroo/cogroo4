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

package br.ccsl.cogroo.tools.checker.rules.applier;

import br.usp.pcs.lta.cogroo.tools.checker.rules.model.PatternElement;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Rule;

/**
 * An accept state, that is part of rules DFA. The accept state holds the rule
 * it accepts.
 * 
 * @author Marcelo Suzumura
 * @author William Colen
 * @version $Id: AcceptState.java 186 2007-03-07 01:08:07Z colen $
 */
public class AcceptState extends State {

	/**
	 * Id for serialization.
	 */
	private static final long serialVersionUID = -220428074316894531L;

	private Rule rule;

	/**
	 * Package private constructor.
	 * 
	 * @param name
	 *            the integer that represents the name of the state
	 * @param element
	 *            the element of the rule
	 * @param rule
	 *            the rule that is accepted by this state
	 */
	public AcceptState(int name, PatternElement element, Rule rule) {
		super(name, element);
		this.rule = rule;
	}

	/**
	 * @return the rule that is accepted by this state
	 */
	public Rule getRule() {
		return this.rule;
	}

}
