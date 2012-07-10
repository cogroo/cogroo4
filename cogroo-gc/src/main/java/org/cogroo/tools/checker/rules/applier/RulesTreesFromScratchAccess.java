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

import org.cogroo.tools.checker.rules.exception.RulesException;

/**
 * Creates an empty {@link RulesTrees} that could be populated with data from
 * XML file.
 * 
 * @author Marcelo Suzumura
 */
public class RulesTreesFromScratchAccess implements RulesTreesAccess {


	public RulesTreesFromScratchAccess(RulesTreesBuilder rulesTreesBuilder) {
		this.rulesTreesBuilder = rulesTreesBuilder;
	}
	
	private final RulesTreesBuilder rulesTreesBuilder;

	public RulesTrees getTrees() {
		return this.rulesTreesBuilder.getRulesTrees();
	}

	public void persist(RulesTrees rulesTrees) {
		throw new RulesException("Cannot persist rules trees.");
	}

}
