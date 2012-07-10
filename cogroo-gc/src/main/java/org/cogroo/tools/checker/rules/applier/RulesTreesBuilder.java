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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.cogroo.tools.checker.rules.util.EqualsUtils;
import org.cogroo.tools.checker.rules.util.RuleUtils;
import org.cogroo.tools.checker.rules.util.RulesProperties;

import org.cogroo.tools.checker.rules.model.PatternElement;
import org.cogroo.tools.checker.rules.model.Rule;
import org.cogroo.tools.checker.rules.model.Rules;
import org.cogroo.tools.checker.rules.model.Rule.Method;

/**
 * This class builds rules trees that will be used to match mistakes in the 
 * sentences.
 * 
 * @author Marcelo Suzumura
 * @author William Colen
 * @see CoGrOO 1.0's Rules Tree (legacy code written in Perl)
 */
public class RulesTreesBuilder {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(RulesTreesBuilder.class);

	public RulesTreesBuilder(RulesProvider rulesProvider) {
		this.rulesProvider = rulesProvider;
		this.trees = this.buildTrees();
	}

	private final RulesProvider rulesProvider;

	/**
	 * Each position in the list represents a different tree that matches different types of rules
	 * as described in {@link RulesProperties}.
	 */
	private RulesTrees trees;
	
	/**
	 * Current state of each of the trees.
	 */
	private int[] currentState = new int[RulesProperties.NUMBER_OF_TREES];
	
	/**
	 * The next state of each of the trees.
	 */
	private int[] nextState = new int[RulesProperties.NUMBER_OF_TREES];
	
	/**
	 * Builds the trees using a {@link Rules} object.
	 */
	private RulesTrees buildTrees() {
		long start = System.nanoTime();
		
		// Reset the trees.
		List<List<State>> rawRulesTrees = new ArrayList<List<State>>(RulesProperties.NUMBER_OF_TREES);
		// Creating trees (and their initial state) and setting state vars
		// for each of them.
		// For this implementation, we have:
		// - tree.get(0) = the general tree
		// - tree.get(1) = the phrase-local tree
		// - tree.get(2) = the subject-verb tree
		for (int i = 0; i < RulesProperties.NUMBER_OF_TREES; i++) {
			rawRulesTrees.add(new ArrayList<State>());
			// Start state is the only state that does not need an element.
			rawRulesTrees.get(i).add(new State(0, new PatternElement()));
			this.currentState[i] = 0;
			// Next state to be created.			
			this.nextState[i] = 1;
		}
		
		// For each active rule.
		for (Rule rule : this.rulesProvider.getRules().getRule()) {
			if (rule.isActive()) {
				// For each element.
				int i = 0;
				// See to which tree the rules refers to.
				int treeIndex = 0;
				if (rule.getMethod() == Method.GENERAL) {
					treeIndex = RulesProperties.GENERAL_TREE;
				} else if (rule.getMethod() == Method.PHRASE_LOCAL) {
					treeIndex = RulesProperties.PHRASE_LOCAL_TREE;
				} else if (rule.getMethod() == Method.SUBJECT_VERB) {
					treeIndex = RulesProperties.SUBJECT_VERB_TREE;
				}
				for (PatternElement element : rule.getPattern().getPatternElement()) {
					int reuseStateIndex = -1;
					List<State> nextStates;
					nextStates = rawRulesTrees.get(treeIndex).get(this.currentState[treeIndex]).getNextStates();
					if (nextStates.isEmpty()) {
						// A state must be created.
						this.createState(rawRulesTrees, i, element, nextStates, rule, treeIndex);
					} else {
						// Check if from the current state in the tree there's a reachable state that has the same element.
						for (int j = 0; j < nextStates.size(); j++) {
							if (EqualsUtils.arePatternElementEquals(nextStates.get(j).getElement(), element)) {
								reuseStateIndex = nextStates.get(j).getName();
								break;
							}
						}
						if (reuseStateIndex == -1) {
							this.createState(rawRulesTrees, i, element, nextStates, rule, treeIndex);
						} else {
							this.currentState[treeIndex] = reuseStateIndex;
						}
					}
					i++;
				}
			}
		}
		
		LOGGER.info("Rules trees built in " + (System.nanoTime() - start) / 1000000 + "ms");
		
		return this.buildRulesTrees(rawRulesTrees);
	}
	
	private RulesTrees buildRulesTrees(List<List<State>> rawRulesTrees) {
		List<RulesTree> rulesList = new ArrayList<RulesTree>();
		for (int i = 0; i < rawRulesTrees.size(); i++) {
			rulesList.add(new RulesTree(rawRulesTrees.get(i)));
		}
		return new RulesTrees(rulesList);
	}
	
	private void createState(List<List<State>> tree, int elementPosition, PatternElement element, List<State> nextStates, Rule rule, int treeIndex) {
		if (elementPosition < rule.getPattern().getPatternElement().size() - 1) {
			// Non-accept state.
			State newState = new State(this.nextState[treeIndex], element);
			nextStates.add(newState);
			tree.get(treeIndex).add(newState);
			this.currentState[treeIndex] = this.nextState[treeIndex];
		} else {
			// Accept state.
			State newState = new AcceptState(this.nextState[treeIndex], element, rule);
			nextStates.add(newState);
			tree.get(treeIndex).add(newState);
			this.currentState[treeIndex] = 0;
		}
		this.nextState[treeIndex]++;
	}
	
	public RulesTrees getRulesTrees() {
		return (this.trees == null || RulesProperties.isRereadRules()) ? this.buildTrees() : this.trees;
	}

	/**
	 * Prints the contents of a rules tree.
	 * 
	 * @param rootState
	 *            the top state of the rules DFA
	 */
	public void printRulesTree(State rootState) {
		List<State> nextStates = rootState.getNextStates();
		if (nextStates.isEmpty()) {
			return;
		}
		for (int i = 0; i < rootState.getNextStates().size(); i++) {
			State currState = nextStates.get(i);
			String accept = "";
			if (currState instanceof AcceptState) {
				accept = Long.toString(((AcceptState) currState).getRule().getId());
			}
			System.out.printf("state[%4d], parent[%4d], rule[%4s], element[%s]\n", Integer.valueOf(currState.getName()), Integer.valueOf(rootState.getName()), accept, RuleUtils.getPatternElementAsString(currState.getElement()));
			this.printRulesTree(nextStates.get(i));
		}
	}

}
