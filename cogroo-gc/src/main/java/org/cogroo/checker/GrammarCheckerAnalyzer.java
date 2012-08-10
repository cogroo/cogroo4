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
import java.util.List;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.util.InvalidFormatException;

import org.cogroo.analyzer.AnalyzerI;
import org.cogroo.entities.Mistake;
import org.cogroo.interpreters.FlorestaTagInterpreter;
import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.tools.checker.Checker;
import org.cogroo.tools.checker.CheckerComposite;
import org.cogroo.tools.checker.TypedChecker;
import org.cogroo.tools.checker.TypedCheckerAdapter;
import org.cogroo.tools.checker.TypedCheckerComposite;
import org.cogroo.tools.checker.checkers.PunctuationChecker;
import org.cogroo.tools.checker.checkers.RepetitionChecker;
import org.cogroo.tools.checker.checkers.SpaceChecker;
import org.cogroo.tools.checker.checkers.WordCombinationChecker;
import org.cogroo.tools.checker.rules.applier.RulesApplier;
import org.cogroo.tools.checker.rules.applier.RulesProvider;
import org.cogroo.tools.checker.rules.applier.RulesTreesAccess;
import org.cogroo.tools.checker.rules.applier.RulesTreesBuilder;
import org.cogroo.tools.checker.rules.applier.RulesTreesFromScratchAccess;
import org.cogroo.tools.checker.rules.applier.RulesTreesProvider;
import org.cogroo.tools.checker.rules.applier.RulesXmlAccess;
import org.cogroo.tools.checker.rules.dictionary.FSALexicalDictionary;
import org.cogroo.tools.checker.rules.dictionary.TagDictionary;

public class GrammarCheckerAnalyzer implements AnalyzerI {

	private CheckerComposite checkers;

	private TagDictionary td;

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
	 * <li>{@link WordCombinationChecker} (beta)</li>
	 * </ul>
	 * 
	 * Also we should have checker that deals with basic Document structure, but
	 * today we don't have any yet.
	 * 
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public GrammarCheckerAnalyzer() throws IllegalArgumentException,
			IOException {

		// initialize resources...
		// today we load the tag dictionary this way, but in the future it
		// should be
		// shared the rules and the models.
		td = new TagDictionary(new FSALexicalDictionary(), false,
				new FlorestaTagInterpreter());

		// *************************************************************************
		// Create typed checkers
		// *************************************************************************
		List<TypedChecker> typedCheckersList = new ArrayList<TypedChecker>();

		// add the rules applier, from XSD
		typedCheckersList.add(createRulesApplierChecker());

		// create other typed checkers

		// how to get the abbreviation dictionary?
		typedCheckersList.add(new SpaceChecker(loadAbbDict()));

		typedCheckersList.add(new PunctuationChecker());
		typedCheckersList.add(new RepetitionChecker());

		// create the typed composite and adapter
		// we need to pass in the tag dictionary because of the adapter, that
		// needs
		// to manipulate the tags
		TypedCheckerAdapter adaptedComposite = new TypedCheckerAdapter(
				new TypedCheckerComposite(typedCheckersList, false), td);

		// all checkers will be added to this:
		List<Checker> checkerList = new ArrayList<Checker>();

		// finally:
		checkerList.add(adaptedComposite);
		checkerList.add(new WordCombinationChecker());

		// now we can create other checkers...

		this.checkers = new CheckerComposite(checkerList, false);
	}

	private Dictionary loadAbbDict() throws InvalidFormatException, IOException {
		Dictionary abbDict = new Dictionary(this.getClass()
				.getResourceAsStream("/dictionaries/pt_br/abbr.xml"));
		return abbDict;
	}

	private TypedChecker createRulesApplierChecker() {
		// Create XML rules applier
		RulesProvider xmlProvider = new RulesProvider(
				RulesXmlAccess.getInstance(), false);
		RulesTreesBuilder rtb = new RulesTreesBuilder(xmlProvider);
		RulesTreesAccess rta = new RulesTreesFromScratchAccess(rtb);
		RulesTreesProvider rtp = new RulesTreesProvider(rta, false);

		return new RulesApplier(rtp, td);
	}

	public void analyze(Document document) {
		if (document instanceof CheckDocument) {
			List<Mistake> mistakes = new ArrayList<Mistake>();
			List<Sentence> sentences = document.getSentences();
			List<org.cogroo.entities.Sentence> legacySentences = new ArrayList<org.cogroo.entities.Sentence>();
			for (Sentence sentence : sentences) {
				mistakes.addAll(this.checkers.check(sentence));
			}
			((CheckDocument) document).setMistakes(mistakes);
			((CheckDocument) document).setSentencesLegacy(legacySentences);
		} else {
			throw new IllegalArgumentException("An instance of "
					+ CheckDocument.class + " was expected.");
		}
	}

	public void ignoreRule(String ruleIdentifier) {
		this.checkers.ignore(ruleIdentifier);
	}

	public void resetIgnoredRules() {
		this.checkers.resetIgnored();
	}

}
