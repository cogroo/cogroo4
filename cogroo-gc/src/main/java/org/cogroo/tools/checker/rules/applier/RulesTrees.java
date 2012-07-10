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
import java.util.List;

/**
 * Give access to the set of {@link RulesTree}. There are one for general, 
 * local or subject-verb rules
 * 
 * @author Marcelo Suzumura
 */
public class RulesTrees implements Serializable {

	/**
	 * Id.
	 */
	private static final long serialVersionUID = -2459804475520219761L;
	
	private List<RulesTree> rulesTrees;

	public RulesTrees(List<RulesTree> rulesTrees) {
		this.rulesTrees = rulesTrees;
	}

	public RulesTree getGeneral() {
		return this.rulesTrees.get(0);
	}

	public RulesTree getPhraseLocal() {
		return this.rulesTrees.get(1);
	}

	public RulesTree getSubjectVerb() {
		return this.rulesTrees.get(2);
	}

}
