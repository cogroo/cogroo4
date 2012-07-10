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

import org.apache.log4j.Logger;

/**
 * Decides if the rules trees must be obtained from a serialized file or if
 * they must be built from scratch.
 * 
 * @author Marcelo Suzumura
 */
public class RulesTreesProvider {
	
	private static final Logger LOGGER = Logger.getLogger(RulesTreesProvider.class);

	public RulesTreesProvider(RulesTreesAccess rulesTreesAccess, boolean rereadRules) {

		this.rulesTreesAccess = rulesTreesAccess;

		this.rulesTrees = this.rulesTreesAccess.getTrees();
		
		this.rereadRules = rereadRules;
	}
	
	private final RulesTreesAccess rulesTreesAccess;
	
	private RulesTrees rulesTrees;
	
	private boolean rereadRules;

	/**
	 * @return the rules trees
	 */
	public RulesTrees getTrees() {
		if (this.rulesTrees == null || rereadRules) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Rules trees will be reread");
			this.rulesTrees = this.rulesTreesAccess.getTrees();
		}
		return this.rulesTrees;
	}

}
