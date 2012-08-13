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
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.Span;

import org.cogroo.entities.Mistake;
import org.cogroo.entities.Sentence;
import org.cogroo.entities.Token;
import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.entities.impl.TokenCogroo;
import org.junit.Test;

import org.cogroo.tools.checker.rules.model.TagMask.Class;

/**
 * 
 * Tests for {@link RepetitionChecker}.
 * 
 */
public class RepetitionCheckerTest {

	private Sentence createSentence(String text, Class... classes) {
		Sentence sentence = new Sentence();
		sentence.setOffset(0);
		sentence.setSpan(new Span(0, text.length()));

		List<Token> tokens = new ArrayList<Token>();

		String[] words = text.split(" ");

		if (words.length != classes.length)
			throw new IllegalArgumentException(
					"Number of tokens doesn't match the number of tags");

		int index = 0;

		for (int i = 0; i < words.length; i++) {

			MorphologicalTag tag = new MorphologicalTag();
			tag.setClazz(classes[i]);

			Token token = new TokenCogroo(words[i], index);

			token.setMorphologicalTag(tag);
			tokens.add(token);

			index = index + words[i].length() + 1;
		}

		sentence.setTokens(tokens);

		return sentence;
	}

	@Test
	public void testCorrectWordSe() {

		Sentence sentence = createSentence("Se se morre de amor .",
				Class.SUBORDINATING_CONJUNCTION, Class.PERSONAL_PRONOUN,
				Class.FINITIVE_VERB, Class.PREPOSITION, Class.NOUN,
				Class.PUNCTUATION_MARK);

		RepetitionChecker checker = new RepetitionChecker();
		List<Mistake> errors = checker.check(sentence);
		assertEquals(0, errors.size());
	}

	@Test
	public void testPronounRepetition() {
		String error = "Ele ele se foi .";
		StringBuilder strBuilder = new StringBuilder(error);
		
		Sentence sentence = createSentence(error,
				Class.PERSONAL_PRONOUN, Class.PERSONAL_PRONOUN,
				Class.PERSONAL_PRONOUN, Class.FINITIVE_VERB, Class.PUNCTUATION_MARK);

		RepetitionChecker checker = new RepetitionChecker();
		List<Mistake> errors = checker.check(sentence);
		assertEquals(1, errors.size());
		
		Mistake mistake = errors.get(0);
		strBuilder.replace(mistake.getStart(),mistake.getEnd(), mistake.getSuggestions()[0]);
		
		assertEquals("Ele se foi .", strBuilder.toString());
	}
	
    @Test
    public void testRuleDetails() {
        String error = "Ele ele se foi .";
        StringBuilder strBuilder = new StringBuilder(error);
        
        Sentence sentence = createSentence(error,
                Class.PERSONAL_PRONOUN, Class.PERSONAL_PRONOUN,
                Class.PERSONAL_PRONOUN, Class.FINITIVE_VERB, Class.PUNCTUATION_MARK);

        RepetitionChecker checker = new RepetitionChecker();
        List<Mistake> errors = checker.check(sentence);
        assertEquals(1, errors.size());
        
        Mistake mistake = errors.get(0);
        assertEquals(RepetitionChecker.ID, mistake.getRuleIdentifier());
        assertTrue(mistake.getFullMessage().contains(RepetitionChecker.MESSAGE));
        assertEquals(RepetitionChecker.SHORT, mistake.getShortMessage());
        assertNotNull(mistake.getSuggestions());
        
        strBuilder.replace(mistake.getStart(),mistake.getEnd(), mistake.getSuggestions()[0]);
        
        
        assertEquals("Ele se foi .", strBuilder.toString());
    }	

	@Test
	public void testAllRepetition() {

		Sentence sentence = createSentence(
				"Ela ela se se portava portava calmamente calmamente por por lá lá .",
				Class.PERSONAL_PRONOUN, Class.PERSONAL_PRONOUN,
				Class.PERSONAL_PRONOUN, Class.PERSONAL_PRONOUN, Class.FINITIVE_VERB,
				Class.FINITIVE_VERB, Class.ADVERB, Class.ADVERB, Class.PREPOSITION,
				Class.PREPOSITION, Class.ADVERB, Class.ADVERB,
				Class.PUNCTUATION_MARK);

		RepetitionChecker checker = new RepetitionChecker();
		List<Mistake> errors = checker.check(sentence);
		assertEquals(6, errors.size());
	}
	
	@Test
	public void testGraveAccent() {

		Sentence sentence = createSentence(
				"Caminhava a a tarde toda .",
				Class.FINITIVE_VERB, Class.PREPOSITION,
				Class.ARTICLE, Class.ADVERB, Class.ADVERB,
				Class.PUNCTUATION_MARK);

		RepetitionChecker checker = new RepetitionChecker();
		List<Mistake> errors = checker.check(sentence);
		assertEquals(0, errors.size());
	}
	
	@Test
	public void testNumber() {

		Sentence sentence = createSentence(
				"Dia 11 .",
				Class.NOUN, Class.NUMERAL,
				Class.PUNCTUATION_MARK);

		RepetitionChecker checker = new RepetitionChecker();
		List<Mistake> errors = checker.check(sentence);
		assertEquals(0, errors.size());
	}
}
