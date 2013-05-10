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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cogroo.entities.Mistake;
import org.cogroo.entities.Sentence;
import org.cogroo.tools.checker.AbstractTypedChecker;
import org.cogroo.tools.checker.JavaRuleDefinition;
import org.cogroo.tools.checker.RuleDefinition;

import org.cogroo.tools.checker.rules.model.Example;

public class PunctuationChecker extends AbstractTypedChecker {

	private static final String ID_PREFIX = "punctuation:";

	private static final String[] SUGGESTION_NO_PUNCTUATION = { "" };

	private static final String CATEGORY = "Erros mecânicos";
	private static final String GROUP = "Pontuação";

	// !.?abc
	static final String BEFORE_SENTENCES_ID = ID_PREFIX + "BEFORE_SENTENCES";
	private static final Pattern BEFORE_SENTENCES = Pattern
			.compile("^([])},.;:?!]+)");

	// abc!!
	static final String EXTRA_PUNCTUATION_ID = ID_PREFIX + "EXTRA_PUNCTUATION";
	private static final Pattern EXTRA_PUNCTUATION = Pattern
			.compile("([,.;:?!]{2,})");

	// abc.. abc....
	private static final Pattern EXCEPTION = Pattern
			.compile("^(([.]{3})|((!\\?)|(\\?!)))$");

	public PunctuationChecker() {
		add(
				createRuleDefinition(
						BEFORE_SENTENCES_ID,
						BEFORE_SENTENCES,
						"Pontuação no início da frase. A frase não deve começar com um sinal de pontuação.",
						"Pontuação no início da frase.",
						createExample("!Este programa é bom.",
								"Este programa é bom.")))
				.add(createRuleDefinition(
						EXTRA_PUNCTUATION_ID,
						EXTRA_PUNCTUATION,
						"Excesso de sinais de pontuação. A repetição dos sinais de pontuação deve ser evitada.",
						"Excesso de sinais de pontuação.",
						createExample("Este programa é bom!!",
								"Este programa é bom!")));
	}

	private RuleDefinition createRuleDefinition(String id, Pattern regex,
			String message, String shortMessage, Example example) {
		String description = "Aplica a expressão regular " + regex.pattern()
				+ " na sentença.";
		List<Example> examples = new LinkedList<Example>();
		examples.add(example);
		return new JavaRuleDefinition(id, CATEGORY, GROUP, description,
				message, shortMessage, examples);
	}

	public String getIdPrefix() {
		return ID_PREFIX;
	}

	public List<Mistake> check(Sentence sentence) {
		String text = sentence.getSentence();
		List<Mistake> mistakes = new LinkedList<Mistake>();
		int offset = sentence.getSpan().getStart();

		if (isCheckRule(BEFORE_SENTENCES_ID)) {
			Matcher m = BEFORE_SENTENCES.matcher(text);
			while (m.find()) {
				int start = m.start(1) + offset;
				int end = m.end(1) + offset;
				mistakes.add(createMistake(BEFORE_SENTENCES_ID,
						SUGGESTION_NO_PUNCTUATION, start, end, sentence.getSentence()));
			}
		}

		if (isCheckRule(EXTRA_PUNCTUATION_ID)) {
			Matcher m = EXTRA_PUNCTUATION.matcher(text);
			while (m.find()) {

				String error = m.group(1);
				if (!EXCEPTION.matcher(error).matches()) {

					int start = m.start(1) + offset;
					int end = m.end(1) + offset;
					String[] suggestion = { createSuggestion(m.group(1)) };
					mistakes.add(createMistake(EXTRA_PUNCTUATION_ID,
							suggestion, start, end, sentence.getSentence()));
				}
			}
		}

		return mistakes;
	}

	private String createSuggestion(String error) {
		char first = error.charAt(0);
		String suggestion = Character.toString(first);

		switch (first) {

		case '.':
			if (error.charAt(1) == '.') {
				suggestion = "...";
			}
			break;

		case '!':
			if (error.charAt(1) == '?') {
				suggestion = "!?";
			}
			break;

		case '?':
			if (error.charAt(1) == '!') {
				suggestion = "?!";
			}
			break;

		default:
		}

		return suggestion;
	}

	public int getPriority() {
		return 2000;
	}
}
