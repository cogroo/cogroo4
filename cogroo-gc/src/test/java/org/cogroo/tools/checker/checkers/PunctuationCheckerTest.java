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

import static junit.framework.Assert.assertEquals;

import java.util.List;

import opennlp.tools.util.Span;

import org.cogroo.entities.Mistake;
import org.cogroo.entities.Sentence;
import org.junit.Test;

public class PunctuationCheckerTest {

	private Sentence createSentence(String test) {
		Sentence sentence = new Sentence();

		sentence.setSentence(test);

		sentence.setSentence(test);
		sentence.setSpan(new Span(0, test.length()));

		return sentence;
	}

	public void testForErrors(String test, String id, String newSentence) {
		Sentence beforeSentences = createSentence(test);

		PunctuationChecker checker = new PunctuationChecker();

		List<Mistake> errors = checker.check(beforeSentences);

		assertEquals(1, errors.size());
		assertEquals(id, errors.get(0).getRuleIdentifier());
		assertEquals(
				newSentence,
				applySuggestion(test, errors.get(0).getSuggestions()[0], errors
						.get(0).getStart(), errors.get(0).getEnd()));

	}

	public String applySuggestion(String test, String suggestion, int start,
			int end) {
		StringBuilder sb = new StringBuilder(test);

		sb.replace(start, end, suggestion);

		return sb.toString();
	}

	public void testWithoutErrors(String test) {
		Sentence beforeSentences = createSentence(test);

		PunctuationChecker checker = new PunctuationChecker();

		List<Mistake> errors = checker.check(beforeSentences);

		assertEquals(0, errors.size());
	}

	@Test
	public void tests() {
		testForErrors("!Frase", PunctuationChecker.BEFORE_SENTENCES_ID, "Frase");
		testForErrors("Frase!!", PunctuationChecker.EXTRA_PUNCTUATION_ID,
				"Frase!");
		testForErrors("Frase!?!?", PunctuationChecker.EXTRA_PUNCTUATION_ID,
				"Frase!?");
		testForErrors("Frase..", PunctuationChecker.EXTRA_PUNCTUATION_ID,
				"Frase...");
		testForErrors("Frase....", PunctuationChecker.EXTRA_PUNCTUATION_ID,
				"Frase...");
		testWithoutErrors("Frase!?");
		testWithoutErrors("Frase?!");
		testWithoutErrors("Frase...");
		testWithoutErrors("Pausa... e fim.");
		testWithoutErrors("Frase maior.");
		testWithoutErrors("Sr. João.");
		testWithoutErrors("Os sistemas (digestivo, respiratório, etc.)");
	}

}
