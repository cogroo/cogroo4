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

import org.apache.log4j.Logger;

import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Rules;

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
