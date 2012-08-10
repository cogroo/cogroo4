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
import org.cogroo.text.Sentence;
import org.cogroo.text.SyntacticChunk;
import org.cogroo.text.Token;
import org.cogroo.tools.checker.AbstractChecker;
import org.cogroo.tools.checker.JavaRuleDefinition;
import org.cogroo.tools.checker.RuleDefinitionI;
import org.cogroo.tools.checker.rules.model.Example;
import org.cogroo.tools.checker.rules.verbs.Prep;
import org.cogroo.tools.checker.rules.verbs.VerbPlusPreps;
import org.cogroo.tools.checker.rules.verbs.Verbs;

public class WordCombinationChecker extends AbstractChecker {

	private static final String ID_PREFIX = "word combination:";

	public WordCombinationChecker() {
		List<Example> examples = new ArrayList<Example>();
		examples.add(createExample("Ele assiste o filme.",
				"Ele assiste ao filme."));

		RuleDefinitionI wordCombination = new JavaRuleDefinition(ID, CATEGORY,
				GROUP, DESCRIPTION, MESSAGE, SHORT, examples);
		add(wordCombination);
	}

	static final String ID = ID_PREFIX + "WORD_COMB_TOKEN";
	static final String CATEGORY = "Regência verbal";
	static final String GROUP = "Erros sintáticos";
	static final String DESCRIPTION = "Procura por verbos e analisa sua regência.";
	static final String MESSAGE = "Verifique a regência verbal.";
	static final String SHORT = "Regência verbal.";

	public String getIdPrefix() {
		return ID_PREFIX;
	}

	public int getPriority() {
		return 211;
	}

	public List<Mistake> check(Sentence sentence) {
		List<Mistake> mistakes = new LinkedList<Mistake>();
		int offset = sentence.getStart();

		Verbs verbs = new Verbs();
		Token verb = findVerb(sentence);
		List<String> nouns = findNoun(sentence);

		VerbPlusPreps vpp = verbs.getVerb(verb.getLemmas()[0]); // only gives
																// the first
																// lemma %TODO
																// improve this
																// case.

		Token sentPrep = findPrep(sentence);

		for (String noun : nouns) {
			if (vpp != null) {
				Prep prep = vpp.findWord(noun);

				// prep == the preposition that is correct and should be in the
				// sentence
				// if prep is null, then no indirect object to be linked with
				// the
				// main verb was found
				if (prep != null) {

					if (sentPrep == null) {

						if (!prep.getPreposition().equals("_")) {

							System.out
									.println("Não tem prep na frase original e deveria ter a prep: "
											+ prep.getPreposition());

							int start = verb.getStart() + offset;
							int end = verb.getEnd() + offset;

							mistakes.add(createMistake(ID,
									createSuggestion(verb, prep), start, end,
									sentence.getText()));
						}
					}

					else {
						if (!sentPrep.getLexeme().equals(prep.getPreposition())) {

							System.out
									.println("A frase original já tem uma prep, mas deveria ter essa outra: "
											+ prep.getPreposition());

							int start = sentPrep.getStart() + offset;
							int end = sentPrep.getEnd() + offset;

							mistakes.add(createMistake(ID,
									createSuggestion(verb, prep), start, end,
									sentence.getText()));
						}
					}
				}
			}
		}

		return mistakes;
	}

	private List<String> findNoun(Sentence sentence) {
		List<String> nouns = new ArrayList<String>();

		List<SyntacticChunk> syntChunks = sentence.getSyntacticChunks();

		for (int i = 0; i < syntChunks.size(); i++) {
			String tag = syntChunks.get(i).getTag();

			if (tag.equals("PIV") || tag.equals("ACC")) {

				for (Token token : syntChunks.get(i).getTokens()) {
					if (token.getPOSTag().equals("n")) {
						if (token.getLemmas() == null)
							nouns.add(token.getLemmas()[0]);
						else
							nouns.add(token.getLexeme());
					}
				}
			}
		}

		return nouns;
	}

	public Token findPrep(Sentence sentence) {
		List<SyntacticChunk> syntChunks = sentence.getSyntacticChunks();

		for (int i = 0; i < syntChunks.size(); i++) {
			String tag = syntChunks.get(i).getTag();

			if (tag.equals("PIV") || tag.equals("ACC")) {

				for (Token token : syntChunks.get(i).getTokens()) {
					if (token.getPOSTag().equals("prp")) {
						return token;
					}
				}
			}
		}
		return null;
	}

	// Returns the token which contains a verb.
	// In case there are two verbs in the sentence it will only return the first
	// one
	// %TODO Improve the case above.
	public Token findVerb(Sentence sentence) {
		List<SyntacticChunk> syntChunks = sentence.getSyntacticChunks();

		for (int i = 0; i < syntChunks.size(); i++) {
			String tag = syntChunks.get(i).getTag();

			if (tag.equals("P"))
				return syntChunks.get(i).getTokens().get(0);
		}

		return null;
	}

	private String[] createSuggestion(Token token, Prep prep) {

		String[] array = { token.getLexeme() + " " + prep.getPreposition() };

		return array;
	}

}
