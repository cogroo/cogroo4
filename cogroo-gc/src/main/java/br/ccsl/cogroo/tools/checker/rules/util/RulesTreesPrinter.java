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

package br.ccsl.cogroo.tools.checker.rules.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.List;

import br.ccsl.cogroo.tools.checker.rules.applier.AcceptState;
import br.ccsl.cogroo.tools.checker.rules.applier.RulesProvider;
import br.ccsl.cogroo.tools.checker.rules.applier.RulesTrees;
import br.ccsl.cogroo.tools.checker.rules.applier.RulesTreesAccess;
import br.ccsl.cogroo.tools.checker.rules.applier.RulesTreesBuilder;
import br.ccsl.cogroo.tools.checker.rules.applier.RulesTreesFromScratchAccess;
import br.ccsl.cogroo.tools.checker.rules.applier.RulesTreesSerializedAccess;
import br.ccsl.cogroo.tools.checker.rules.applier.RulesXmlAccess;
import br.ccsl.cogroo.tools.checker.rules.applier.State;

/**
 * <b>Developement</b> Print serialized rules to a file. Useful to understand what
 * is wrong with the rule file,
 * 
 * @author William Daniel
 *
 */
public class RulesTreesPrinter {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

	  RulesProvider xmlProvider = new RulesProvider(RulesXmlAccess.getInstance(),
	        false);
	  RulesTreesBuilder rtb = new RulesTreesBuilder(xmlProvider);
	  RulesTreesAccess rta = new RulesTreesFromScratchAccess(rtb);
	  
	  //		OutputStreamWriter serialOut = StreamFactory.createOutputStreamWriter("target/treesSerial.txt", "ISO-8859-1");
	    
	    OutputStreamWriter xmlOut = new OutputStreamWriter(new FileOutputStream("target/treesXml.txt"),"UTF-8");
//		RulesTrees serialTrees = rc.getContainerForSerializedAccess().getComponent(RulesTreesSerializedAccess.class).getTrees();
		RulesTrees xmlTrees = rta.getTrees();
		
//		printRulesTrees(serialTrees, serialOut);
		printRulesTrees(xmlTrees, xmlOut);
//		serialOut.close();
		xmlOut.close();
	}
	
	private static void printRulesTrees(RulesTrees rulesTrees, OutputStreamWriter out) throws Exception {
		printRulesTree(rulesTrees.getGeneral().getRoot(), out);
		printRulesTree(rulesTrees.getPhraseLocal().getRoot(), out);
		printRulesTree(rulesTrees.getSubjectVerb().getRoot(), out);
	}
	
	private static void printRulesTree(State rootState, OutputStreamWriter out) throws Exception {
		List<State> nextStates = rootState.getNextStates();
		if (nextStates.isEmpty()) {
			return;
		}
		for (int i = 0; i < rootState.getNextStates().size(); i++) {
			State currState = nextStates.get(i);
			String accept = "";
			String suggestions = "";
			if (currState instanceof AcceptState) {
				AcceptState acceptState = (AcceptState) currState;
				accept = Long.toString(acceptState.getRule().getId());
				suggestions = RuleUtils.getSuggestionsAsString(acceptState.getRule());
			}
			out.append("state[").append(Integer.toString(currState.getName())).append("], ");
			out.append("parent[").append(Integer.toString(rootState.getName())).append("], ");
			out.append("rule[").append(accept).append("], ");
			out.append("element[").append(RuleUtils.getPatternElementAsString(currState.getElement())).append("]");
			out.append("suggestions[").append(suggestions).append("]");
			out.append("\n");
			printRulesTree(nextStates.get(i), out);
		}
	}

}
