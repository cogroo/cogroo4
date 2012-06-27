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

package br.ccsl.cogroo.tools.checker.checkers;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.ccsl.cogroo.entities.Mistake;
import br.ccsl.cogroo.entities.Sentence;
import br.ccsl.cogroo.tools.checker.AbstractTypedChecker;
import br.ccsl.cogroo.tools.checker.JavaRuleDefinition;
import br.ccsl.cogroo.tools.checker.RuleDefinitionI;
import br.ccsl.cogroo.tools.checker.rules.model.Example;

public class PunctuationChecker extends AbstractTypedChecker {

	private static final String ID_PREFIX = "punctuation:";

	private static final String[] SUGGESTION_NO_PUNCTUATION = { "" };

	private static final String CATEGORY = "Uso do espaço";
	private static final String GROUP = "Erros mecânicos";

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

	private RuleDefinitionI createRuleDefinition(String id, Pattern regex,
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
		return 210;
	}
}
