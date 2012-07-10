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

import org.cogroo.tools.checker.rules.model.Rules;

/**
 * Singleton objetc that provides access to the {@link Rules}
 * 
 * @author Marcelo Suzumura
 */
public class RulesProvider {
	
	private static final Logger LOGGER = Logger.getLogger(RulesProvider.class);

	/**
	 * Instantiates the singleton.
	 */
	public RulesProvider(RulesAccess rulesAccess, boolean reread) {
		this.rulesAccess = rulesAccess;
		this.rules = this.getRules();
		this.reread = reread;
	}
	
	private final RulesAccess rulesAccess;
	
	private Rules rules;
	
	private boolean reread;
	
//	private RulesAccess getRulesAccess() {
//		RulesAccess access;
//		if ("xml".equals(RulesProperties.DATA_SOURCE)) { // Source = xml.
//			LOGGER.info("Rules data source = xml");
//			access = RulesXmlAccess.getInstance();
//		} else if ("db".equals(RulesProperties.DATA_SOURCE)) { // Source = db = database (not implemented).
//			LOGGER.info("Rules data source = db");
//			throw new RulesException("Invalid data.source in properties file. db access is not implemented.");
//		} else { // Unknown source.
//			LOGGER.info("Rules data source = unknown");
//			throw new RulesException("Unknown data.source in properties file.");
//		}
//		return access;
//	}

	public Rules getRules() {
		if (this.rules == null || reread) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Rules will be reread");
			this.rules = this.rulesAccess.getRules();
		}
		return this.rules;
	}

}
