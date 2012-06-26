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
import br.ccsl.cogroo.entities.impl.MorphologicalTag;
import br.ccsl.cogroo.entities.impl.TokenCogroo;
import br.ccsl.cogroo.interpreters.FlorestaTagInterpreter;
import br.ccsl.cogroo.interpreters.TagInterpreterI;
import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.Token;
import br.ccsl.cogroo.tools.checker.Checker;
import br.ccsl.cogroo.tools.checker.CheckerComposite;
import br.ccsl.cogroo.tools.checker.Merger;
import br.ccsl.cogroo.tools.checker.checkers.PunctuationChecker;
import br.ccsl.cogroo.tools.checker.checkers.WordCombinationChecker;
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

public class GrammarCheckerAnalyzer implements AnalyzerI {

  private static final Logger LOGGER = Logger.getLogger(RulesApplier.class);

  private CheckerComposite checker;
  private TagInterpreterI ti = new FlorestaTagInterpreter();

  private TagDictionary td;

  public GrammarCheckerAnalyzer() throws IllegalArgumentException, IOException {
    RulesProvider xmlProvider = new RulesProvider(RulesXmlAccess.getInstance(),
        false);
    td = new TagDictionary(new FSALexicalDictionary(), false,
        new FlorestaTagInterpreter());

    RulesTreesBuilder rtb = new RulesTreesBuilder(xmlProvider);
    RulesTreesAccess rta = new RulesTreesFromScratchAccess(rtb);
    RulesTreesProvider rtp = new RulesTreesProvider(rta, false);
    Checker rulesApplier = new RulesApplier(rtp, td);
    
    List<Checker> checkers = new ArrayList<Checker>();
    checkers.add(new WordCombinationChecker());
    checkers.add(rulesApplier);
    checkers.add(new PunctuationChecker());
    
    checker = new CheckerComposite(checkers);

  }

  public void analyze(Document document) {
    if (document instanceof CheckDocument) {
      List<Mistake> mistakes = new ArrayList<Mistake>();
      List<Sentence> sentences = document.getSentences();
      List<br.ccsl.cogroo.entities.Sentence> legacySentences = new ArrayList<br.ccsl.cogroo.entities.Sentence>();
      for (Sentence sentence : sentences) {
        br.ccsl.cogroo.entities.Sentence legacy = asTypedSentence(sentence);
        legacySentences.add(legacy);
        mistakes.addAll(this.checker.check(legacy));
      }
      ((CheckDocument) document).setMistakes(mistakes);
      ((CheckDocument) document).setSentencesLegacy(legacySentences);
    } else {
      throw new IllegalArgumentException("An instance of "
          + CheckDocument.class + " was expected.");
    }
  }

  private br.ccsl.cogroo.entities.Sentence asTypedSentence(Sentence sentence) {
    br.ccsl.cogroo.entities.Sentence typedSentence = new br.ccsl.cogroo.entities.Sentence();
    typedSentence.setSentence(sentence.getText());
    typedSentence.setOffset(sentence.getStart());
    typedSentence.setSpan(new Span(sentence.getStart(), sentence.getEnd()));

    List<br.ccsl.cogroo.entities.Token> typedTokenList = new ArrayList<br.ccsl.cogroo.entities.Token>();
    for (Token token : sentence.getTokens()) {
      br.ccsl.cogroo.entities.Token typedToken = new TokenCogroo(
          new Span(token.getStart(), token.getEnd()));

      typedToken.setLexeme(token.getLexeme());
      typedToken.setMorphologicalTag(createMorphologicalTag(token));
      setPrimitiveAndGeneralize(typedToken, td);

      typedTokenList.add(typedToken);
    }

    typedSentence.setChunks(Collections.<Chunk> emptyList());
    typedSentence.setSyntacticChunks(Collections.<SyntacticChunk> emptyList());

    typedSentence.setTokens(Collections.unmodifiableList(typedTokenList));

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Typede sentence: " + typedSentence);
    }

    return typedSentence;
  }

  private MorphologicalTag createMorphologicalTag(Token token) {
    String tag;
    if ("-".equals(token.getFeatures()))
      tag = token.getPOSTag();
    else
      tag = token.getPOSTag() + "=" + token.getFeatures();
    return ti.parseMorphologicalTag(tag);
  }

  public static void setPrimitiveAndGeneralize(
      br.ccsl.cogroo.entities.Token tok, CogrooTagDictionary dict) {
    Merger.generalizePOSTags(tok.getMorphologicalTag(),
        dict.getTags(tok.getLexeme(), false));

    // tokens.get(i).setMorphologicalTag(mt);
    // Gets the primitive of the token.
    String[] primitives = dict.getPrimitive(tok.getLexeme(),
        tok.getMorphologicalTag(), true);
    if (primitives == null) {
      primitives = dict.getPrimitive(tok.getLexeme().toLowerCase(),
          tok.getMorphologicalTag(), true);
    }
    if (primitives == null) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Missing lemma for: " + tok);
      }
      tok.setPrimitive(tok.getLexeme());
    } else {
      tok.setPrimitive(primitives[0]);
    }
  }
}
