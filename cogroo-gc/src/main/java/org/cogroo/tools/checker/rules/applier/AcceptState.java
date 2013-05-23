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
package org.cogroo.tools.checker.rules.applier;

import org.cogroo.tools.checker.rules.model.PatternElement;
import org.cogroo.tools.checker.rules.model.Rule;

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
