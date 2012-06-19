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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import br.ccsl.cogroo.entities.Mistake;
import br.ccsl.cogroo.entities.Sentence;
import br.ccsl.cogroo.entities.Token;
import br.ccsl.cogroo.tools.checker.AbstractChecker;
import br.ccsl.cogroo.tools.checker.JavaRuleDefinition;
import br.ccsl.cogroo.tools.checker.RuleDefinitionI;
import br.ccsl.cogroo.tools.checker.rules.model.Example;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Class;

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
public class RepetitionChecker extends AbstractChecker {

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
