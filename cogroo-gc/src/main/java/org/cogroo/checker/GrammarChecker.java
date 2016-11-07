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
package org.cogroo.checker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;
import org.cogroo.analyzer.Analyzer;
import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.entities.Mistake;
import org.cogroo.interpreters.FlorestaTagInterpreter;
import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.tools.checker.Checker;
import org.cogroo.tools.checker.CheckerComposite;
import org.cogroo.tools.checker.RuleDefinition;
import org.cogroo.tools.checker.SentenceAdapter;
import org.cogroo.tools.checker.TypedChecker;
import org.cogroo.tools.checker.TypedCheckerComposite;
import org.cogroo.tools.checker.checkers.GovernmentChecker;
import org.cogroo.tools.checker.checkers.ParonymChecker;
import org.cogroo.tools.checker.checkers.PunctuationChecker;
import org.cogroo.tools.checker.checkers.RepetitionChecker;
import org.cogroo.tools.checker.checkers.SpaceChecker;
import org.cogroo.tools.checker.rules.applier.RulesApplier;
import org.cogroo.tools.checker.rules.applier.RulesProvider;
import org.cogroo.tools.checker.rules.applier.RulesTreesAccess;
import org.cogroo.tools.checker.rules.applier.RulesTreesBuilder;
import org.cogroo.tools.checker.rules.applier.RulesTreesFromScratchAccess;
import org.cogroo.tools.checker.rules.applier.RulesTreesProvider;
import org.cogroo.tools.checker.rules.applier.RulesXmlAccess;
import org.cogroo.tools.checker.rules.dictionary.FSALexicalDictionary;
import org.cogroo.tools.checker.rules.dictionary.TagDictionary;
import org.cogroo.tools.checker.rules.model.Example;
import org.cogroo.tools.checker.rules.util.MistakeComparator;
import org.cogroo.tools.checker.rules.validator.RulePostValidatorProvider;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.util.InvalidFormatException;

public class GrammarChecker implements CheckAnalyzer {

	private static final Logger LOGGER = Logger.getLogger(GrammarChecker.class);

	private final CheckerComposite checkers;

	private final TagDictionary td;

	private boolean allowOverlap;

	private final SentenceAdapter sentenceAdapter;

	private final TypedCheckerComposite typedCheckers;

	private final Analyzer pipe;

	private final RulePostValidatorProvider validator = new RulePostValidatorProvider();

	private static final MistakeComparator MISTAKE_COMPARATOR = new MistakeComparator();

	/**
	 * Creates an analyzer that will call the available checker. Today it is
	 * hard coded, but it is in the TODO list that we should make it more
	 * flexible, specially because of other languages.
	 * 
	 * <p>
	 * We have two different types of checkers: {@link TypedChecker}s are the
	 * one that uses the classes from XSD (package checker.rules.model), that
	 * should be deprecated in the future. They are:
	 * </p>
	 * <ul>
	 * <li>{@link RulesApplier} (rules from XML file)</li>
	 * <li>{@link PunctuationChecker}</li>
	 * <li>{@link RepetitionChecker}</li>
	 * <li>{@link SpaceChecker}</li>
	 * <li>{@link GovernmentChecker} (beta)</li>
	 * </ul>
	 * 
	 * Also we should have checker that deals with basic Document structure, but
	 * today we don't have any yet.
	 * 
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public GrammarChecker(Analyzer pipe) throws IllegalArgumentException,
			IOException {
		this(pipe, false, null);
	}

	public GrammarChecker(Analyzer pipe, boolean allowOverlap,
			long[] activeXmlRules) throws IllegalArgumentException, IOException {

		this.pipe = pipe;
		// initialize resources...
		// today we load the tag dictionary this way, but in the future it
		// should be
		// shared the rules and the models.
		td = new TagDictionary(new FSALexicalDictionary(), false,
				new FlorestaTagInterpreter());

		sentenceAdapter = new SentenceAdapter(td);

		// *************************************************************************
		// Create typed checkers
		// *************************************************************************
		List<TypedChecker> typedCheckersList = new ArrayList<TypedChecker>();

		// add the rules applier, from XSD
		// Removido para LabXP 2015
		// typedCheckersList.add(createRulesApplierChecker(activeXmlRules));

		// create other typed checkers

		// how to get the abbreviation dictionary?
		typedCheckersList.add(new SpaceChecker(loadAbbDict()));
		typedCheckersList.add(new PunctuationChecker());
		typedCheckersList.add(new RepetitionChecker());
//		typedCheckersList.add(new UIMAChecker(td));

		typedCheckers = new TypedCheckerComposite(typedCheckersList, false);

		// all non typed checkers will be added to this:
		List<Checker> checkerList = new ArrayList<Checker>();

		checkerList.add(new GovernmentChecker());
		checkerList.add(new ParonymChecker(this.pipe));

		this.checkers = new CheckerComposite(checkerList, false);

		this.allowOverlap = allowOverlap;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Created following rules:");
			int count = 0;
			for (RuleDefinition def : this.typedCheckers.getRulesDefinition()) {
				LOGGER.debug(count++ + ": " + def.getId());
			}
			for (RuleDefinition def : this.checkers.getRulesDefinition()) {
				LOGGER.debug(count++ + ": " + def.getId());
			}
		}
	}

	public GrammarChecker(Analyzer pipe, String serializedRule)
			throws IllegalArgumentException, IOException {
		this.pipe = pipe;
		// initialize resources...
		// today we load the tag dictionary this way, but in the future it
		// should be
		// shared the rules and the models.
		td = new TagDictionary(new FSALexicalDictionary(), false,
				new FlorestaTagInterpreter());

		sentenceAdapter = new SentenceAdapter(td);

		// *************************************************************************
		// Create typed checkers
		// *************************************************************************
		List<TypedChecker> typedCheckersList = new ArrayList<TypedChecker>();

		// add the rules applier, from XSD
		typedCheckersList.add(createSingletonRuleChecker(serializedRule));

		typedCheckers = new TypedCheckerComposite(typedCheckersList, false);

		// all non typed checkers will be added to this:
		List<Checker> checkerList = new ArrayList<Checker>();

		this.checkers = new CheckerComposite(checkerList, false);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Created following rules:");
			int count = 0;
			for (RuleDefinition def : this.typedCheckers.getRulesDefinition()) {
				LOGGER.debug(count++ + ": " + def.getId());
			}
			for (RuleDefinition def : this.checkers.getRulesDefinition()) {
				LOGGER.debug(count++ + ": " + def.getId());
			}
		}
	}

	public Set<RuleDefinition> getRuleDefinitions() {
		Set<RuleDefinition> ruleDefinitions = new HashSet<RuleDefinition>();

		ruleDefinitions.addAll(this.typedCheckers.getRulesDefinition());
		ruleDefinitions.addAll(this.checkers.getRulesDefinition());

		return ruleDefinitions;
	}

	private Dictionary loadAbbDict() throws InvalidFormatException, IOException {
		Dictionary abbDict = new Dictionary(this.getClass()
				.getResourceAsStream("/dictionaries/pt_br/abbr.xml"));
		return abbDict;
	}

	private TypedChecker createRulesApplierChecker(long[] activeRules) {
		// Create XML rules applier
		RulesProvider xmlProvider = new RulesProvider(
				RulesXmlAccess.getInstance(), false);
		RulesTreesBuilder rtb = new RulesTreesBuilder(xmlProvider, activeRules);
		RulesTreesAccess rta = new RulesTreesFromScratchAccess(rtb);
		RulesTreesProvider rtp = new RulesTreesProvider(rta, false);

		return new RulesApplier(rtp, td);
	}

	private TypedChecker createSingletonRuleChecker(String serializedRule) {
		// Create XML rules applier
		RulesProvider xmlProvider = new RulesProvider(
				RulesXmlAccess.getInstance(serializedRule), true);
		RulesTreesBuilder rtb = new RulesTreesBuilder(xmlProvider, null);
		RulesTreesAccess rta = new RulesTreesFromScratchAccess(rtb);
		RulesTreesProvider rtp = new RulesTreesProvider(rta, false);

		return new RulesApplier(rtp, td);
	}

	public void analyze(CheckDocument document, boolean filterInvalidSuggestions) {

		pipe.analyze(document);

		List<Mistake> mistakes = new ArrayList<Mistake>();
		List<Sentence> sentences = document.getSentences();
		List<org.cogroo.entities.Sentence> typedSentences = new ArrayList<org.cogroo.entities.Sentence>(
				sentences.size());
		for (Sentence sentence : sentences) {
			mistakes.addAll(this.checkers.check(sentence));

			org.cogroo.entities.Sentence typedSentence = this.sentenceAdapter
					.asTypedSentence(sentence, document.getText());
			typedSentences.add(typedSentence);

			mistakes.addAll(this.typedCheckers.check(typedSentence));
		}
		document.setSentencesLegacy(typedSentences);
		Collections.sort(mistakes, MISTAKE_COMPARATOR);

		mistakes = filterInvalid(document, mistakes);

		if (this.allowOverlap == false) {
			mistakes = filterOverlap(document, mistakes);
		}

		if (filterInvalidSuggestions) {
			filterWrongSuggestions(document, mistakes);
		}

		document.setMistakes(mistakes);
	}

	@Override
	public void analyze(CheckDocument document) {
		this.analyze(document, true);
	}

	private List<Mistake> filterInvalid(CheckDocument document,
			List<Mistake> mistakes) {
		List<Mistake> filtered = new ArrayList<Mistake>();
		for (Mistake mistake : mistakes) {
			if (validator.isValid(mistake, document)) {
				filtered.add(mistake);
			}
		}
		return filtered;
	}

	private List<Mistake> filterOverlap(Document doc, List<Mistake> mistakes) {
		boolean[] occupied = new boolean[doc.getText().length()];

		List<Mistake> mistakesNoOverlap = new ArrayList<Mistake>();
		boolean overlap = false;
		for (Mistake mistake : mistakes) {
			overlap = false;
			for (int i = mistake.getStart(); i < mistake.getEnd(); i++) {
				if (occupied[i]) {
					overlap = true;
				}
			}
			if (!overlap) {
				for (int i = mistake.getStart(); i < mistake.getEnd(); i++) {
					occupied[i] = true;
				}
				mistakesNoOverlap.add(mistake);
			}
		}
		return mistakesNoOverlap;
	}

	private void filterWrongSuggestions(Document document,
			List<Mistake> mistakes) {
		String documentText = document.getText();

		for (Mistake mistake : mistakes) {

			List<String> rightSuggestions = new ArrayList<String>();

			for (String suggestion : mistake.getSuggestions()) {

				String alternativeText = documentText.substring(0,
						mistake.getStart())
						+ suggestion + documentText.substring(mistake.getEnd());

				CheckDocument alternative = new CheckDocument(alternativeText);
				this.analyze(alternative, false);

				if (alternative.getMistakes().size() == 0) { // No errors in
																// suggestion
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("\n****** Filtering suggestions **********: "
								+ alternativeText + "   (OK!)\n");
					}
					rightSuggestions.add(suggestion);
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("\n****** Filtering suggestions **********: "
								+ alternativeText + "   (WRONG!)\n");
					}
				}

				if (rightSuggestions.size() > 0) {
					mistake.setSuggestions(rightSuggestions
							.toArray(new String[rightSuggestions.size()]));
				}
			}
		}
	}

	public void ignoreRule(String ruleIdentifier) {
		this.checkers.ignore(ruleIdentifier);
	}

	public void resetIgnoredRules() {
		this.checkers.resetIgnored();
	}

	private static void printExamples(List<RuleDefinition> rulesDefinition) {

		for (RuleDefinition def : rulesDefinition) {
			for (Example ex : def.getExamples()) {
				System.out.println(ex.getIncorrect());
				// System.out.println(def.getCategory());
			}
		}
	}

	/**
	 * @param args
	 *            the language to be used, "pt_BR" by default
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static void main(String[] args) throws IllegalArgumentException,
			IOException {

		long start = System.nanoTime();

		if (args.length != 1) {
			System.err.println("Language is missing! usage: CLI pt_br");
			return;
		}

		ComponentFactory factory = ComponentFactory.create(new Locale("pt",
				"BR"));

		long[] rules = { 129 };

		// GrammarChecker cogroo = new GrammarChecker(factory.createPipe(),
		// false, rules);
		GrammarChecker cogroo = new GrammarChecker(factory.createPipe());

		System.out.println("Loading time ["
				+ ((System.nanoTime() - start) / 1000000) + "ms]");
		Scanner kb = new Scanner(System.in);
		System.out
				.print("Enter the sentence, q to quit, 0 for the default, or 1 to print the examples: ");
		String input = kb.nextLine();

		while (!input.equals("q")) {
			if (input.equals("0")) {
				input = "Foi ferido por uma balas perdidas.";
			} else if (input.equals("1")) {
				printExamples(new ArrayList<RuleDefinition>(
						cogroo.getRuleDefinitions()));
			}

			CheckDocument document = new CheckDocument();
			document.setText(input);
			cogroo.analyze(document);

			System.out.println(document);

			System.out.print("Enter the sentence: ");
			input = kb.nextLine();
		}
	}

}
