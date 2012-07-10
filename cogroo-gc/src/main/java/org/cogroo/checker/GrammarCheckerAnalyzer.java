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

import org.apache.log4j.Logger;
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

  private static final Logger LOGGER = Logger.getLogger(RulesApplier.class);

  private CheckerComposite checkers;

  private TagDictionary td;

  public GrammarCheckerAnalyzer() throws IllegalArgumentException, IOException {
    // all checkers will be added to this:
    List<Checker> checkerList = new ArrayList<Checker>();

    // create typed checkers
    List<TypedChecker> typedCheckers = new ArrayList<TypedChecker>();

    // Create XML rules applier
    RulesProvider xmlProvider = new RulesProvider(RulesXmlAccess.getInstance(),
        false);
    td = new TagDictionary(new FSALexicalDictionary(), false,
        new FlorestaTagInterpreter());
    RulesTreesBuilder rtb = new RulesTreesBuilder(xmlProvider);
    RulesTreesAccess rta = new RulesTreesFromScratchAccess(rtb);
    RulesTreesProvider rtp = new RulesTreesProvider(rta, false);

    typedCheckers.add(new RulesApplier(rtp, td));

    // create other typed checkers
    // typedCheckers.add(new SpaceChecker(dic));
    typedCheckers.add(new PunctuationChecker());

    // create the typed composite and adapter
    TypedCheckerAdapter adaptedComposite = new TypedCheckerAdapter(
        new TypedCheckerComposite(typedCheckers, false), td);

    // finally:
    checkerList.add(adaptedComposite);

    // now we can create other checkers...

    this.checkers = new CheckerComposite(checkerList, false);
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

  
  public void resetIgnoredRules(){
    this.checkers.resetIgnored();
  }
  
}
