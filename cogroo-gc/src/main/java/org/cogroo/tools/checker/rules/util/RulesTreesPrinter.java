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
package org.cogroo.tools.checker.rules.util;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.cogroo.tools.checker.rules.applier.AcceptState;
import org.cogroo.tools.checker.rules.applier.RulesProvider;
import org.cogroo.tools.checker.rules.applier.RulesTrees;
import org.cogroo.tools.checker.rules.applier.RulesTreesAccess;
import org.cogroo.tools.checker.rules.applier.RulesTreesBuilder;
import org.cogroo.tools.checker.rules.applier.RulesTreesFromScratchAccess;
import org.cogroo.tools.checker.rules.applier.RulesXmlAccess;
import org.cogroo.tools.checker.rules.applier.State;


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
