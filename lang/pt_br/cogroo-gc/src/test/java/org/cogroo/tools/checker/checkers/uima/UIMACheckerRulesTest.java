package org.cogroo.tools.checker.checkers.uima;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Locale;

import org.cogroo.analyzer.Analyzer;
import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.checker.CheckDocument;
import org.cogroo.checker.GrammarChecker;
import org.cogroo.entities.Mistake;
import org.junit.BeforeClass;
import org.junit.Test;

public class UIMACheckerRulesTest {

	private static GrammarChecker cogroo;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		ComponentFactory factory = ComponentFactory.create(new Locale("pt",
				"BR"));
		Analyzer a = factory.createPipe();
		cogroo = new GrammarChecker(a);

	}

	@Test
	public void testCheck34() {
		String sentence = "segue anexos o documento solicitado.";
		List<Mistake> mistakes = checkText(sentence);

		assertEquals(33, mistakes.get(0).getRuleIdentifier());
		assertEquals("segue anexo o", mistakes.get(0).getSuggestions()[0]);
	}

	private List<Mistake> checkText(String text) {
		CheckDocument document = new CheckDocument(text);
		cogroo.analyze(document);

		return document.getMistakes();
	}

}
