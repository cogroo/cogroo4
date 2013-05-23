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

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.util.StringList;

import org.cogroo.entities.Mistake;
import org.cogroo.entities.Sentence;
import org.cogroo.tools.checker.AbstractTypedChecker;
import org.cogroo.tools.checker.JavaRuleDefinition;
import org.cogroo.tools.checker.RuleDefinition;
import org.cogroo.tools.checker.rules.model.Example;

public class SpaceChecker extends AbstractTypedChecker {

	private static final String ID_PREFIX = "space:";

	private static final String[] SUGGESTION_ONE_SPACE = { " " };

	private static final String[] SUGGESTION_NO_SPACE = { "" };

	private static final String CATEGORY = "Erros mecânicos";
	private static final String GROUP = "Uso do espaço";

	// abc abc
	static final String EXTRA_BETWEEN_WORDS_ID = ID_PREFIX
			+ "EXTRA_BETWEEN_WORDS";
	private static final Pattern EXTRA_BETWEEN_WORDS = Pattern
			.compile("\\S(\\s{2,})\\S");

	// abc . abc
	static final String EXTRA_BEFORE_RIGHT_PUNCT_ID = ID_PREFIX
			+ "EXTRA_BEFORE_RIGHT_PUNCT";
	private static final Pattern EXTRA_BEFORE_RIGHT_PUNCT = Pattern
			.compile("\\S(\\s{1,})[.?!;,:”)}\\]]");

	// abc ( abc
	static final String EXTRA_AFTER_LEFT_PUNCT_ID = ID_PREFIX
			+ "EXTRA_AFTER_LEFT_PUNCT";
	private static final Pattern EXTRA_AFTER_LEFT_PUNCT = Pattern
			.compile("[\\[{(](\\s{1,})");

	// abc...abc
	static final String MISSING_SPACE_AFTER_PUNCT_ID = ID_PREFIX
			+ "MISSING_SPACE_AFTER_PUNCT";
	private static final Pattern MISSING_SPACE_AFTER_PUNCT = Pattern
			.compile("([.”?!;,:)}\\]]+)[^\\s.”'\",;:!?)}\\]]");

	// e-mail%10_+A@linux.ime.usp.br
	private static final Pattern EMAIL = Pattern
			.compile("([\\S]+@([\\w]+\\.)+[\\w]+)");

	// R$ 4,00 || 1.000.000 || Chapter 1.2.4
	private static final Pattern NUMBER = Pattern.compile("((\\d+[,.])+\\d+)");

	// Ele, J.B.C. morreu em 154 a.C.!
	private static final Pattern INITIALS = Pattern
			.compile("((\\p{L}\\.){2,})(?!\\p{L})");

	// Ele, J.B.C. morreu em 154 a.C.!
	private static final Pattern URL = Pattern.compile("(^(http|www)[\\S]+)");

	private Dictionary dic;

	public SpaceChecker(Dictionary dic) {
		this.dic = dic;
		add(
				createRuleDefinition(
						EXTRA_BETWEEN_WORDS_ID,
						EXTRA_BETWEEN_WORDS,
						"Excesso de espaços entre as palavras. Entre palavras deve haver apenas um espaço.",
						"Excesso de espaços entre as palavras.",
						createExample("Este programa é  bom.",
								"Este programa é bom.")))
				.add(createRuleDefinition(
						EXTRA_BEFORE_RIGHT_PUNCT_ID,
						EXTRA_BEFORE_RIGHT_PUNCT,
						"Excesso de espaço antes de símbolo. O símbolo deve ser mantido junto à palavra que o precede.",
						"Excesso de espaço antes de símbolo.",
						createExample("Este programa é bom .",
								"Este programa é bom.")))
				.add(createRuleDefinition(
						EXTRA_AFTER_LEFT_PUNCT_ID,
						EXTRA_AFTER_LEFT_PUNCT,
						"Excesso de espaço depois de símbolo. O símbolo deve ser mantido junto à palavra que o sucede.",
						"Excesso de espaço depois de símbolo.",
						createExample("Este programa é ( era) bom.",
								"Este programa é (era) bom.")))
				.add(createRuleDefinition(
						MISSING_SPACE_AFTER_PUNCT_ID,
						MISSING_SPACE_AFTER_PUNCT,
						"Falta espaço entre símbolo e palavra à direita. Deve haver um espaço entre o símbolo e a palavra que o sucede.",
						"Falta espaço entre símbolo e palavra à direita.",
						createExample(
								"Este programa é era bom.Mas agora é melhor.",
								"Este programa é era bom. Mas agora é melhor.")));
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

		if (isCheckRule(EXTRA_BETWEEN_WORDS_ID)) {
			Matcher m = EXTRA_BETWEEN_WORDS.matcher(text);
			while (m.find()) {
				int start = m.start(1) + offset;
				int end = m.end(1) + offset;
				mistakes.add(createMistake(EXTRA_BETWEEN_WORDS_ID,
						SUGGESTION_ONE_SPACE, start, end, sentence.getSentence()));
			}
		}

		if (isCheckRule(EXTRA_BEFORE_RIGHT_PUNCT_ID)) {
			Matcher m = EXTRA_BEFORE_RIGHT_PUNCT.matcher(text);
			while (m.find()) {
				int start = m.start(1) + offset;
				int end = m.end(1) + offset;
				mistakes.add(createMistake(EXTRA_BEFORE_RIGHT_PUNCT_ID,
						SUGGESTION_NO_SPACE, start, end, sentence.getSentence()));
			}
		}

		if (isCheckRule(EXTRA_AFTER_LEFT_PUNCT_ID)) {
			Matcher m = EXTRA_AFTER_LEFT_PUNCT.matcher(text);
			while (m.find()) {
				int start = m.start(1) + offset;
				int end = m.end(1) + offset;
				mistakes.add(createMistake(EXTRA_AFTER_LEFT_PUNCT_ID,
						SUGGESTION_NO_SPACE, start, end, sentence.getSentence()));
			}
		}

		if (isCheckRule(MISSING_SPACE_AFTER_PUNCT_ID)) {
			Matcher m = MISSING_SPACE_AFTER_PUNCT.matcher(text);
			while (m.find()) {
				String error = getsSupposedError(text, m.start(1));
				boolean initials = getsSupposedAbbreviation(text, m.start(1));

				if (!isEmail(error) && !isNumber(error) && !isURL(error)
						&& !(initials)) {

					int start = m.start(1) + offset;
					int end = m.end(1) + offset;
					String[] suggestion = { m.group(1) + " " };
					mistakes.add(createMistake(MISSING_SPACE_AFTER_PUNCT_ID,
							suggestion, start, end, sentence.getSentence()));
				}
			}
		}

		return mistakes;
	}

	private boolean isOpenBracket(char c) {

		switch (c) {
			case '“':
			case '(':
			case '[':
			case '{':
				return true;
		}
		return false;
	}
	
	private boolean isEnding(char c) {

		switch (c) {
			case '”':
			case ')':
			case ']':
			case '}':
			case '.':
			case ',':
			case ';':
			case '!':
			case '?':
				return true;
		}
		return false;
	}

	/**
	 * Analyze a sentence and gets the word which contains the position of the
	 * error in the parameter
	 * 
	 * @param text
	 *            the entire sentence to be analyzed
	 * @param position
	 *            where in the sentence the supposed error was found
	 * @return the word which contains the supposed error
	 */
	private String getsSupposedError(String text, int position) {
		int ini;
		boolean end = false;
		String word = text;

		// Indicates where the position of the supposed word begin
		for (ini = position; ini >= 0; ini--)
			if (Character.isWhitespace(text.charAt(ini))
					|| isOpenBracket(text.charAt(ini)))
				break;

		// Indicates where the supposed word should end
		for (int i = position + 1; i < text.length() && end == false; i++) {

			switch (text.charAt(i)) {
			// Indicates the end of the supposed error
			case ' ':
			case '!':
			case '?':
			case ',':
			case ';':
			case ')':
			case ']':
			case '}':
			case '”':
			case '\n':
				// The supposed e-mail is attributed in its proper variable
				word = word.substring(ini + 1, i);
				end = true;
				break;

			// Possible end of sentence or just part of the supposed error
			case '.':
				if (Character.isWhitespace(text.charAt(i + 1))) {
					word = word.substring(ini + 1, i);
					end = true;
					break;
				}

				// Character or digit that is part of the supposed error
			default:
				break;
			}
		}

		return word;
	}

	/**
	 * Analyze a sentence and gets the word which contains the position of the
	 * error in the parameter and tells if it is an initial or if the
	 * abbreviation dictionary contains it or not.
	 * 
	 * @param text
	 *            the entire sentence to be analyzed
	 * @param position
	 *            where in the sentence the supposed error was found
	 * @return true if the error is actually an initial, and false if not.
	 */
	private boolean getsSupposedAbbreviation(String text, int position) {
		int ini;
		boolean end = false;
		String word = text;

		// Indicates where the position of the supposed abbreviation begins
		for (ini = position; ini >= 0; ini--)
			if (Character.isWhitespace(text.charAt(ini))
					|| isOpenBracket(text.charAt(ini)) )
				break;

		// Indicates where the supposed abbreviation should end
		for (int i = position + 1; i < text.length() - 1 && end == false; i++) {

			switch (text.charAt(i)) {

			// Possible end of the sentence or just part of the supposed
			// abbreviation
			case '.':
				if (Character.isWhitespace(text.charAt(i + 1))
						|| isEnding(text.charAt(i + 1)) ) {
					word = word.substring(ini + 1, i + 1);
					end = true;
				}
				break;

			// Character that is part of the abbreviation
			default:
				break;
			}
		}

		if (end == true) {
			if (INITIALS.matcher(word).find())
				return true;
			else if (this.dic.contains(new StringList(word)))
				return true;
		}

		return false;
	}

	/**
	 * Verifies whether the String is or isn't an e-mail.
	 * 
	 * @param email
	 *            supposed e-mail in the sentence
	 * @return true if the variable email is an e-mail, otherwise returns false.
	 */
	private boolean isEmail(String email) {
		return (EMAIL.matcher(email).find());
	}

	/**
	 * Verifies whether the String is or isn't some sort of number combination
	 * 
	 * @param number
	 *            String to be verified whether it is a number or not.
	 * @return true if the String is a number and false in the contrary.
	 */
	private boolean isNumber(String number) {
		return (NUMBER.matcher(number).find());
	}

	private boolean isURL(String url) {
		return (URL.matcher(url).find());
	}

	public int getPriority() {
		return 2000;
	}

}
