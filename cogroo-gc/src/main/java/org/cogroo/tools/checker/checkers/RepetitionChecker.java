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
package org.cogroo.tools.checker.checkers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.cogroo.entities.Mistake;
import org.cogroo.entities.Sentence;
import org.cogroo.entities.Token;
import org.cogroo.tools.checker.AbstractTypedChecker;
import org.cogroo.tools.checker.JavaRuleDefinition;
import org.cogroo.tools.checker.RuleDefinitionI;

import org.cogroo.tools.checker.rules.model.Example;
import org.cogroo.tools.checker.rules.model.TagMask.Class;

/**
 * 
 * The RepetitionChecker class, looks in a sentence for repeated subsequent
 * words. Except when the first word is "se" meaning the subordinate
 * conjunction, followed by the word "se" meaning the personal pronoun.
 * 
 * <p>
 * Indicates false errors when dealing with contractions, for example: "em no (em + o)", "por pelo (por + o)"
 * </p>
 * 
 */
public class RepetitionChecker extends AbstractTypedChecker {

	public RepetitionChecker() {
		List<Example> examples = new ArrayList<Example>();
		examples.add(createExample("Ele ele foi ao mercado.",
				"Ele foi ao mercado."));

		RuleDefinitionI repetition = new JavaRuleDefinition(ID, CATEGORY,
				GROUP, DESCRIPTION, MESSAGE, SHORT, examples);
		add(repetition);
	}

	private static final String ID_PREFIX = "repetition:";

	static final String ID = ID_PREFIX + "DUPLICATED_TOKEN";
	static final String CATEGORY = "Repetição de símbolos";
	static final String GROUP = "Erros mecânicos";
	static final String DESCRIPTION = "Procura por palavras consecutivas repetidas.";
	static final String MESSAGE = "Verifique a repetição de palavras.";
	static final String SHORT = "Repetição de palavras.";

	public String getIdPrefix() {
		return ID_PREFIX;
	}

	public List<Mistake> check(Sentence sentence) {

		List<Mistake> mistakes = new LinkedList<Mistake>();
		int offset = sentence.getSpan().getStart();

		List<Token> tokens = sentence.getTokens();

		String token = tokens.get(0).getLexeme().toLowerCase();

		for (int i = 1; i < tokens.size(); i++) {
			String next = tokens.get(i).getLexeme().toLowerCase();

			if (token.equals(next) && !isException(tokens, i)) {

				int start = tokens.get(i-1).getSpan().getStart() + offset;
				int end = tokens.get(i).getSpan().getEnd() + offset;

				mistakes.add(createMistake(ID, createSuggestion(tokens.get(i-1)
						.getLexeme()), start, end, sentence.getSentence()));
			}

			token = next;
		}

		return mistakes;
	}

	private boolean isException(List<Token> tokens, int i) {
		String word = tokens.get(i).getLexeme().toLowerCase();

		Class first = tokens.get(i - 1).getMorphologicalTag().getClazzE();
		Class second = tokens.get(i).getMorphologicalTag().getClazzE();

		if (word.equals("se")) {
			if (first.equals(Class.SUBORDINATING_CONJUNCTION)
					&& second.equals(Class.PERSONAL_PRONOUN))
				return true;
			return false;
		}

		if (word.equals("a")) {
			if (first.equals(Class.PREPOSITION)
					&& second.equals(Class.ARTICLE))
				return true;
			return false;
		}
		
		
		// TODO: Remove this exception. 
		if (word.equals("1")) {
			return true;
		}

		return false;
	}

	private String[] createSuggestion(String error) {

		String[] array = { error };

		return array;
	}

	public int getPriority() {
		return 190;
	}

}
