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
import br.ccsl.cogroo.tools.checker.TypedChecker;
import br.ccsl.cogroo.tools.checker.CheckerComposite;
import br.ccsl.cogroo.tools.checker.Merger;
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

  private CheckerComposite checker;
  private TagInterpreterI ti = new FlorestaTagInterpreter();

  private TagDictionary td;

  private ChunkerConverter chunkerConverter;

  public GrammarCheckerAnalyzer() throws IllegalArgumentException, IOException {
    RulesProvider xmlProvider = new RulesProvider(RulesXmlAccess.getInstance(),
        false);
    td = new TagDictionary(new FSALexicalDictionary(), false,
        new FlorestaTagInterpreter());

    RulesTreesBuilder rtb = new RulesTreesBuilder(xmlProvider);
    RulesTreesAccess rta = new RulesTreesFromScratchAccess(rtb);
    RulesTreesProvider rtp = new RulesTreesProvider(rta, false);
    TypedChecker rulesApplier = new RulesApplier(rtp, td);

    checker = new CheckerComposite(Collections.singletonList(rulesApplier));
    
    chunkerConverter = new ChunkerConverter(ti);

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
      br.ccsl.cogroo.entities.Token typedToken = new TokenCogroo(new Span(
          token.getStart(), token.getEnd()));

      typedToken.setLexeme(token.getLexeme());
      typedToken.setMorphologicalTag(createMorphologicalTag(token));
      setPrimitiveAndGeneralize(typedToken, td);

      typedTokenList.add(typedToken);
    }

    // typedSentence.setChunks(Collections.<Chunk> emptyList());

    typedSentence.setSyntacticChunks(Collections.<SyntacticChunk> emptyList());

    typedSentence.setTokens(Collections.unmodifiableList(typedTokenList));
    
    chunkerConverter.convertChunks(sentence, typedSentence);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Typed sentence: ");
      if (LOGGER.isDebugEnabled()) {
        StringBuilder trace = new StringBuilder();
        trace.append("Show tree [" + typedSentence.getSentence()
                + "]: \n");
        List<br.ccsl.cogroo.entities.Token> tokens = typedSentence.getTokens();
        for (int i = 0; i < tokens.size(); i++) {
            trace.append("\t["
                    + tokens.get(i).getSyntacticTag() + "]["
                    + tokens.get(i).getChunkTag() + "] (ck: "
                    + tokens.get(i).getChunk().getMorphologicalTag() + ") "
                    + tokens.get(i) + " --> {"
                    + tokens.get(i).getPrimitive() + "}_"
                    + tokens.get(i).getMorphologicalTag()
                    + "\n");
        }
        LOGGER.debug(trace.toString());
    }
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

  private static class ChunkerConverter {
    private final TagMask CHUNK_OTHER;
    private final TagMask CHUNK_BOUNDARY_NOUN_PHRASE;
    private final TagMask CHUNK_BOUNDARY_NOUN_PHRASE_MAIN;
    private final TagMask CHUNK_INTERMEDIARY_NOUN_PHRASE_MAIN;
    private final TagMask CHUNK_BOUNDARY_VERB_PHRASE_MAIN;
    private final TagMask CHUNK_INTERMEDIARY_VERB_PHRASE;
    private final TagInterpreterI corpusTagInterpreter;

    public ChunkerConverter(TagInterpreterI corpusTagInterpreter) {
      this.corpusTagInterpreter = corpusTagInterpreter;

      CHUNK_OTHER = new TagMask();
      CHUNK_OTHER.setChunkFunction(ChunkFunction.OTHER);

      CHUNK_BOUNDARY_NOUN_PHRASE = new TagMask();
      CHUNK_BOUNDARY_NOUN_PHRASE
          .setChunkFunction(ChunkFunction.BOUNDARY_NOUN_PHRASE);

      CHUNK_BOUNDARY_NOUN_PHRASE_MAIN = new TagMask();
      CHUNK_BOUNDARY_NOUN_PHRASE_MAIN
          .setChunkFunction(ChunkFunction.BOUNDARY_NOUN_PHRASE_MAIN);

      CHUNK_INTERMEDIARY_NOUN_PHRASE_MAIN = new TagMask();
      CHUNK_INTERMEDIARY_NOUN_PHRASE_MAIN
          .setChunkFunction(ChunkFunction.INTERMEDIARY_NOUN_PHRASE);

      CHUNK_BOUNDARY_VERB_PHRASE_MAIN = new TagMask();
      CHUNK_BOUNDARY_VERB_PHRASE_MAIN
          .setChunkFunction(ChunkFunction.BOUNDARY_VERB_PHRASE_MAIN);

      CHUNK_INTERMEDIARY_VERB_PHRASE = new TagMask();
      CHUNK_INTERMEDIARY_VERB_PHRASE
          .setChunkFunction(ChunkFunction.INTERMEDIARY_VERB_PHRASE);
    }

    public void convertChunks(Sentence sentence, br.ccsl.cogroo.entities.Sentence typedSentence) {
      
      for (int i = 0; i < sentence.getTokens().size(); i++) {
        Token textToken = sentence.getTokens().get(i);
        br.ccsl.cogroo.entities.Token typedToken = typedSentence.getTokens().get(i);
        
        ChunkTag tag = corpusTagInterpreter.parseChunkTag(textToken.getChunkTag());
        
        typedToken.setChunkTag(tag);
      }
      
      List<Chunk> chunks = new ArrayList<Chunk>(sentence.getChunks().size());
      for (br.ccsl.cogroo.text.Chunk textChunk : sentence.getChunks()) {
        int head = 0;
        if(textChunk.getHeadIndex() != -1) {
          head = textChunk.getHeadIndex();
        }
        MorphologicalTag tag = typedSentence.getTokens().get(head).getMorphologicalTag().clone();

        List<br.ccsl.cogroo.entities.Token> tokens = new ArrayList<br.ccsl.cogroo.entities.Token>();
        for (int i = textChunk.getStart(); i < textChunk.getEnd(); i++) {
          tokens.add(typedSentence.getTokens().get(i));
        }
        
        Chunk typedChunk = new ChunkCogroo(tokens, textChunk.getStart());
        
        for (br.ccsl.cogroo.entities.Token token : tokens) {
          token.setChunk(typedChunk);
        }
        
        typedChunk.setMorphologicalTag(tag);
        chunks.add(typedChunk);
      }
      
      for (br.ccsl.cogroo.entities.Token token : typedSentence.getTokens()) {
        if(token.getChunk() == null) {
          Chunk c = new ChunkCogroo(Collections.singletonList(token), 0);
          c.setMorphologicalTag(token.getMorphologicalTag().clone());
          token.setChunk(c);
        }
      }
      
      typedSentence.setChunks(chunks);
    }
  }
}
