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
