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
import java.util.List;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.util.InvalidFormatException;

import org.apache.log4j.Logger;
import org.cogroo.analyzer.AnalyzerI;
import org.cogroo.entities.Mistake;
import org.cogroo.interpreters.FlorestaTagInterpreter;
import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.tools.checker.Checker;
import org.cogroo.tools.checker.CheckerComposite;
import org.cogroo.tools.checker.RuleDefinitionI;
import org.cogroo.tools.checker.SentenceAdapter;
import org.cogroo.tools.checker.TypedChecker;
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
import org.cogroo.tools.checker.rules.util.MistakeComparator;


public class GrammarCheckerAnalyzer implements AnalyzerI {
  
  private static final Logger LOGGER = Logger.getLogger(GrammarCheckerAnalyzer.class);

  private CheckerComposite checkers;

  private TagDictionary td;

  private boolean allowOverlap;

  private SentenceAdapter sentenceAdapter;

  private TypedCheckerComposite typedCheckers;
  
  private static final MistakeComparator MISTAKE_COMPARATOR = new MistakeComparator();

  /**
   * Creates an analyzer that will call the available checker. Today it is hard
   * coded, but it is in the TODO list that we should make it more flexible,
   * specially because of other languages.
   * 
   * <p>
   * We have two different types of checkers: {@link TypedChecker}s are the one
   * that uses the classes from XSD (package checker.rules.model), that should
   * be deprecated in the future. They are:
   * </p>
   * <ul>
   *    <li>{@link RulesApplier} (rules from XML file)</li>
   *    <li>{@link PunctuationChecker}</li>
   *    <li>{@link RepetitionChecker}</li>
   *    <li>{@link SpaceChecker}</li>
   *    <li>{@link WordCombinationChecker} (beta)</li>
   * </ul>
   * 
   * Also we should have checker that deals with basic Document structure, but today we don't have any yet.
   *  
   * @throws IllegalArgumentException
   * @throws IOException
   */
  public GrammarCheckerAnalyzer() throws IllegalArgumentException, IOException {
    this(false, null);
  }
  
  public GrammarCheckerAnalyzer(boolean allowOverlap, long[] activeXmlRules) throws IllegalArgumentException, IOException {
    
    // initialize resources...
    // today we load the tag dictionary this way, but in the future it should be
    // shared the rules and the models.
    td = new TagDictionary(new FSALexicalDictionary(), false,
        new FlorestaTagInterpreter());
    
    
    sentenceAdapter = new SentenceAdapter(td);
    
    //*************************************************************************
    // Create typed checkers
    //*************************************************************************
    List<TypedChecker> typedCheckersList = new ArrayList<TypedChecker>();
    
    // add the rules applier, from XSD
    typedCheckersList.add(createRulesApplierChecker(activeXmlRules));

    // create other typed checkers
    
    // how to get the abbreviation dictionary? 
    //typedCheckersList.add(new SpaceChecker(loadAbbDict()));
    //typedCheckersList.add(new PunctuationChecker());
    //typedCheckersList.add(new RepetitionChecker());
    
    typedCheckers = new TypedCheckerComposite(typedCheckersList, false);

    // all non typed checkers will be added to this:
    List<Checker> checkerList = new ArrayList<Checker>();
//    checkerList.add(new WordCombinationChecker());
    

    this.checkers = new CheckerComposite(checkerList, false);
   
    
    this.allowOverlap = allowOverlap;
    
    if(LOGGER.isDebugEnabled()) {
      LOGGER.debug("Created following rules:");
      int count = 0;
      for (RuleDefinitionI def : this.typedCheckers.getRulesDefinition()) {
        LOGGER.debug(count++ + ": " + def.getId());
      }
      for (RuleDefinitionI def : this.checkers.getRulesDefinition()) {
        LOGGER.debug(count++ + ": " + def.getId());
      }
    }
  }

  private Dictionary loadAbbDict() throws InvalidFormatException, IOException {
    Dictionary abbDict = new Dictionary(this.getClass().getResourceAsStream("/dictionaries/pt_br/abbr.xml"));
    return abbDict;
  }

  private TypedChecker createRulesApplierChecker(long[] activeRules) {
    // Create XML rules applier
    RulesProvider xmlProvider = new RulesProvider(RulesXmlAccess.getInstance(),
        false);
    RulesTreesBuilder rtb = new RulesTreesBuilder(xmlProvider, activeRules);
    RulesTreesAccess rta = new RulesTreesFromScratchAccess(rtb);
    RulesTreesProvider rtp = new RulesTreesProvider(rta, false);

    return new RulesApplier(rtp, td);
  }

  public void analyze(Document document) {
    if (document instanceof CheckDocument) {
      List<Mistake> mistakes = new ArrayList<Mistake>();
      List<Sentence> sentences = document.getSentences();
      List<org.cogroo.entities.Sentence> typedSentences = new ArrayList<org.cogroo.entities.Sentence>(sentences.size());
      for (Sentence sentence : sentences) {
        mistakes.addAll(this.checkers.check(sentence));
        
        org.cogroo.entities.Sentence typedSentence = this.sentenceAdapter.asTypedSentence(sentence);
        typedSentences.add(typedSentence);
        
        mistakes.addAll(this.typedCheckers.check(typedSentence));
      }
      ((CheckDocument) document).setSentencesLegacy(typedSentences);
      Collections.sort(mistakes, MISTAKE_COMPARATOR);
      
      if(this.allowOverlap == false)
        mistakes = filterOverlap(document, mistakes);
      
      ((CheckDocument) document).setMistakes(mistakes);
    } else {
      throw new IllegalArgumentException("An instance of "
          + CheckDocument.class + " was expected.");
    }
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

  public void ignoreRule(String ruleIdentifier) {
    this.checkers.ignore(ruleIdentifier);
  }

  
  public void resetIgnoredRules(){
    this.checkers.resetIgnored();
  }
  
}
