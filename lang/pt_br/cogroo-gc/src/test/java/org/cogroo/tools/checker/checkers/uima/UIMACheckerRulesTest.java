package org.cogroo.tools.checker.checkers.uima;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.Charsets;
import org.apache.commons.lang.StringUtils;
import org.cogroo.analyzer.Analyzer;
import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.checker.CheckDocument;
import org.cogroo.checker.GrammarChecker;
import org.cogroo.entities.Mistake;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Resources;

public class UIMACheckerRulesTest {

	private static GrammarChecker cogroo;

	private static Pattern PROBLEM = Pattern
			.compile("\\[(.*?)\\]\\s*\\((.+?)\\)");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		ComponentFactory factory = ComponentFactory.create(new Locale("pt",
				"BR"));
		Analyzer a = factory.createPipe();
		cogroo = new GrammarChecker(a);

	}

	// ACCEPTANCE TEST
	// Test all the lines of the filename. This method assumes that there is at
	// most one problem per line.
	private List<String> testSuggestions(String filename) throws IOException {
		List<String> errors = new ArrayList<String>();
		URL url = Resources.getResource(filename);
		int lineNumber = 0;
		for (String line : Resources.readLines(url, Charsets.UTF_8)) {
			lineNumber++;
			line = line.trim();
			if (line.isEmpty() || line.charAt(0) == '#')
				continue;
			StringBuilder l = new StringBuilder(line);
			Matcher m = PROBLEM.matcher(l);

			List<String> expectedSuggestions = new ArrayList<String>();
			List<Mistake> mistakes;
			if (m.find()) {
				String id = m.group(2);
				// parts[0] = original excerpt; parts[1] = suggestions
				String[] parts = m.group(1).split("=>", 2);
				// original = original clean text
				StringBuilder original = l.replace(m.start(), m.end(),
						parts[0].trim());

				for (String sugg : parts[1].split("/")) {
					expectedSuggestions.add(new StringBuilder(line).replace(
							m.start(), m.end(), sugg.trim()).toString());
				}

				List<String> obtainedSuggestions = new ArrayList<String>();
				mistakes = checkText(original.toString());
				if (mistakes.size() > 1)
					errors.add(String
							.format("Error in the test: there may be at most one problem per line (at line %d).",
									lineNumber));
				else if (mistakes.size() == 0)
					errors.add(String
							.format("False negative: expected 1 problem; got 0 (at line %d).",
									lineNumber));
				else {
					Mistake mistake = mistakes.get(0);
					if (mistake.getRuleIdentifier().trim().equals(id.trim())) {
						for (String suggestion : mistake.getSuggestions())
							obtainedSuggestions.add(new StringBuilder(l)
									.replace(mistake.getStart(),
											mistake.getEnd(), suggestion)
									.toString());
						Collections.sort(expectedSuggestions);
						Collections.sort(obtainedSuggestions);
						int i = 0, j = 0;
						while (i < expectedSuggestions.size()
								&& j < obtainedSuggestions.size()) {
							int cmp = expectedSuggestions.get(i).compareTo(
									obtainedSuggestions.get(j));
							if (cmp < 0) {
								errors.add(String
										.format("Did not obtain the expected suggestion: '%s' (at line %d).",
												expectedSuggestions.get(i++),
												lineNumber));
							} else if (cmp > 0) {
								errors.add(String
										.format("Did not expect the obtainted suggestion: '%s' (at line %d).",
												obtainedSuggestions.get(j++),
												lineNumber));
							} else {
								i++;
								j++;
							}
						}
						while (i < expectedSuggestions.size())
							errors.add(String
									.format("Did not obtain the expected suggestion: '%s' (at line %d).",
											expectedSuggestions.get(i++),
											lineNumber));
						while (j < obtainedSuggestions.size())
							errors.add(String
									.format("Did not expect the obtainted suggestion: '%s' (at line %d).",
											obtainedSuggestions.get(j++),
											lineNumber));
					} else {
						errors.add(String
								.format("Unexpected problem with id '%s' (at line %d).",
										mistake.getRuleIdentifier(), lineNumber));
					}
				}

			} else {
				// we expected no problem, but got some
				int n = checkText(l.toString()).size();
				if (n > 0)
					errors.add(String.format(
							"Expected 0 problem, got %d (at line %d).", n,
							lineNumber));
			}
		}
		return errors;
	}

	private String test(String filename) throws IOException {
		List<String> suggestionList = testSuggestions("cogroo/ruta/Tests/"
				+ filename);
		String[] suggestions = suggestionList.toArray(new String[suggestionList
				.size()]);
		return StringUtils.join(suggestions, "\n");
	}

	@Test
	public void testCheckAnexos() throws IOException {
		assertEquals("", test("Anexos.txt"));
	}

	@Test
	public void testCheckColocacaoPronominal() throws IOException {
		assertEquals("", test("ColocacaoPronominal.txt"));
	}

	@Test
	public void testCheckConcordanciaAdjSub() throws IOException {
		assertEquals("", test("ConcordanciaAdjSub.txt"));
	}

	private List<Mistake> checkText(String text) {
		CheckDocument document = new CheckDocument(text);
		cogroo.analyze(document);

		return document.getMistakes();
	}

}
