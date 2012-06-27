package br.ccsl.cogroo.checker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import opennlp.tools.util.Span;

import org.apache.log4j.Logger;

import br.ccsl.cogroo.analyzer.AnalyzerI;
import br.ccsl.cogroo.entities.Chunk;
import br.ccsl.cogroo.entities.Mistake;
import br.ccsl.cogroo.entities.SyntacticChunk;
import br.ccsl.cogroo.entities.impl.ChunkCogroo;
import br.ccsl.cogroo.entities.impl.ChunkTag;
import br.ccsl.cogroo.entities.impl.MorphologicalTag;
import br.ccsl.cogroo.entities.impl.TokenCogroo;
import br.ccsl.cogroo.interpreters.FlorestaTagInterpreter;
import br.ccsl.cogroo.interpreters.TagInterpreterI;
import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.Token;
import br.ccsl.cogroo.tools.checker.Checker;
import br.ccsl.cogroo.tools.checker.CheckerComposite;
import br.ccsl.cogroo.tools.checker.TypedChecker;
import br.ccsl.cogroo.tools.checker.GenericCheckerComposite;
import br.ccsl.cogroo.tools.checker.Merger;
import br.ccsl.cogroo.tools.checker.TypedCheckerAdapter;
import br.ccsl.cogroo.tools.checker.TypedCheckerComposite;
import br.ccsl.cogroo.tools.checker.checkers.PunctuationChecker;
import br.ccsl.cogroo.tools.checker.checkers.SpaceChecker;
import br.ccsl.cogroo.tools.checker.rules.applier.RulesApplier;
import br.ccsl.cogroo.tools.checker.rules.applier.RulesProvider;
import br.ccsl.cogroo.tools.checker.rules.applier.RulesTreesAccess;
import br.ccsl.cogroo.tools.checker.rules.applier.RulesTreesBuilder;
import br.ccsl.cogroo.tools.checker.rules.applier.RulesTreesFromScratchAccess;
import br.ccsl.cogroo.tools.checker.rules.applier.RulesTreesProvider;
import br.ccsl.cogroo.tools.checker.rules.applier.RulesXmlAccess;
import br.ccsl.cogroo.tools.checker.rules.dictionary.CogrooTagDictionary;
import br.ccsl.cogroo.tools.checker.rules.dictionary.FSALexicalDictionary;
import br.ccsl.cogroo.tools.checker.rules.dictionary.TagDictionary;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.ChunkFunction;

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
    TypedCheckerAdapter adaptedComposite = new TypedCheckerAdapter(new TypedCheckerComposite(typedCheckers, false), td);
    
    // finally:
    checkerList.add(adaptedComposite);
    
    // now we can create other checkers...
    
    this.checkers = new CheckerComposite(checkerList, false);
  }

  public void analyze(Document document) {
    if (document instanceof CheckDocument) {
      List<Mistake> mistakes = new ArrayList<Mistake>();
      List<Sentence> sentences = document.getSentences();
      List<br.ccsl.cogroo.entities.Sentence> legacySentences = new ArrayList<br.ccsl.cogroo.entities.Sentence>();
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


}
