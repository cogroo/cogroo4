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
package org.cogroo.tools.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import opennlp.tools.util.Span;

import org.apache.log4j.Logger;
import org.cogroo.entities.Chunk;
import org.cogroo.entities.Mistake;
import org.cogroo.entities.SyntacticChunk;
import org.cogroo.entities.impl.ChunkCogroo;
import org.cogroo.entities.impl.ChunkTag;
import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.entities.impl.SyntacticTag;
import org.cogroo.entities.impl.TokenCogroo;
import org.cogroo.interpreters.FlorestaTagInterpreter;
import org.cogroo.interpreters.TagInterpreterI;
import org.cogroo.text.Sentence;
import org.cogroo.text.Token;
import org.cogroo.tools.checker.rules.dictionary.CogrooTagDictionary;
import org.cogroo.tools.checker.rules.dictionary.TagDictionary;
import org.cogroo.tools.checker.rules.model.TagMask;
import org.cogroo.tools.checker.rules.model.TagMask.ChunkFunction;
import org.cogroo.tools.checker.rules.model.TagMask.SyntacticFunction;

public class TypedCheckerAdapter implements Checker {
  
  private TypedChecker inner;
  private TagDictionary td;
  private TagInterpreterI ti = new FlorestaTagInterpreter();
  private static final Logger LOGGER = Logger.getLogger(TypedCheckerAdapter.class);
  private ChunkerConverter chunkerConverter;
  private SyntacticChunkConverter syntacticChunkerConverter;
  
  public TypedCheckerAdapter(TypedChecker inner, TagDictionary td) {
    this.inner = inner;
    this.td = td;
    this.chunkerConverter = new ChunkerConverter(ti);
    this.syntacticChunkerConverter = new SyntacticChunkConverter(ti);
  }

  public String getIdPrefix() {
    return inner.getIdPrefix();
  }

  public List<Mistake> check(Sentence sentence) {
    org.cogroo.entities.Sentence typed = asTypedSentence(sentence);
    return inner.check(typed);
  }

  public void ignore(String id) {
    inner.ignore(id);
  }

  public void resetIgnored() {
    inner.resetIgnored();
  }

  public int getPriority() {
    return inner.getPriority();
  }

  public Collection<RuleDefinitionI> getRulesDefinition() {
    return inner.getRulesDefinition();
  }
  
  private org.cogroo.entities.Sentence asTypedSentence(Sentence sentence) {
    org.cogroo.entities.Sentence typedSentence = new org.cogroo.entities.Sentence();
    typedSentence.setSentence(sentence.getText());
    typedSentence.setOffset(sentence.getStart());
    typedSentence.setSpan(new Span(sentence.getStart(), sentence.getEnd()));

    List<org.cogroo.entities.Token> typedTokenList = new ArrayList<org.cogroo.entities.Token>();
    for (Token token : sentence.getTokens()) {
      org.cogroo.entities.Token typedToken = new TokenCogroo(new Span(
          token.getStart(), token.getEnd()));

      typedToken.setLexeme(token.getLexeme());
      typedToken.setMorphologicalTag(createMorphologicalTag(token));
      setPrimitiveAndGeneralize(typedToken, td);

      typedTokenList.add(typedToken);
    }

    typedSentence.setTokens(Collections.unmodifiableList(typedTokenList));
    
    chunkerConverter.convertChunks(sentence, typedSentence);
    syntacticChunkerConverter.convertChunks(sentence, typedSentence);
    

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Typed sentence: ");
      if (LOGGER.isDebugEnabled()) {
        StringBuilder trace = new StringBuilder();
        trace.append("Show tree [" + typedSentence.getSentence()
                + "]: \n");
        List<org.cogroo.entities.Token> tokens = typedSentence.getTokens();
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
      org.cogroo.entities.Token tok, CogrooTagDictionary dict) {
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
  
  private static class SyntacticChunkConverter {

    private final TagInterpreterI corpusTagInterpreter;

    public SyntacticChunkConverter(TagInterpreterI corpusTagInterpreter) {
      this.corpusTagInterpreter = corpusTagInterpreter;
    }

    public void convertChunks(Sentence sentence,
        org.cogroo.entities.Sentence typedSentence) {

      int lastToken = 0;
      List<SyntacticChunk> typedSyntacticChunks = new ArrayList<SyntacticChunk>();
      List<org.cogroo.entities.Token> typedTokens = typedSentence.getTokens();
      for (org.cogroo.text.SyntacticChunk syntacticChunk : sentence
          .getSyntacticChunks()) {
        int start = syntacticChunk.getStart();
        int end = syntacticChunk.getEnd();

        for (int i = lastToken; i < start; i++) {
          typedSyntacticChunks
              .add(createNoneSyntacticChunk(typedTokens.get(i)));
        }
        lastToken = end;

        List<Chunk> typedChunks = new ArrayList<Chunk>();
        // search for the chunk...
        for (int i = start; i < end; i++) {
          Chunk tc = typedTokens.get(i).getChunk();
          if (typedChunks.size() == 0
              || !typedChunks.get(typedChunks.size() - 1).equals(tc))
            typedChunks.add(tc);
        }
        SyntacticChunk typedSyntacticChunk = new SyntacticChunk(typedChunks);
        typedSyntacticChunk.setSyntacticTag(corpusTagInterpreter
            .parseSyntacticTag(syntacticChunk.getTag()));

        for (int i = start; i < end; i++) {
          typedTokens.get(i).setSyntacticChunk(typedSyntacticChunk);
        }

        typedSyntacticChunks.add(typedSyntacticChunk);
      }

      // leftovers
      for (int i = lastToken; i < typedTokens.size(); i++) {
        typedSyntacticChunks.add(createNoneSyntacticChunk(typedTokens.get(i)));
      }

      typedSentence.setSyntacticChunks(Collections
          .unmodifiableList(typedSyntacticChunks));

    }

    private SyntacticChunk createNoneSyntacticChunk(
        org.cogroo.entities.Token token) {
      SyntacticChunk noneTypedSyntacticChunk = new SyntacticChunk(
          Collections.singletonList(token.getChunk()));
      SyntacticTag st = new SyntacticTag();
      st.setSyntacticFunction(SyntacticFunction.NONE);
      noneTypedSyntacticChunk.setSyntacticTag(st);
      token.setSyntacticChunk(noneTypedSyntacticChunk);
      return noneTypedSyntacticChunk;
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

    public void convertChunks(Sentence sentence, org.cogroo.entities.Sentence typedSentence) {
      
      for (int i = 0; i < sentence.getTokens().size(); i++) {
        Token textToken = sentence.getTokens().get(i);
        org.cogroo.entities.Token typedToken = typedSentence.getTokens().get(i);
        
        ChunkTag tag = corpusTagInterpreter.parseChunkTag(textToken.getChunkTag());
        
        typedToken.setChunkTag(tag);
      }
      
      List<Chunk> chunks = new ArrayList<Chunk>(sentence.getChunks().size());
      for (org.cogroo.text.Chunk textChunk : sentence.getChunks()) {
        int head = 0;
        if(textChunk.getHeadIndex() != -1) {
          head = textChunk.getHeadIndex();
        }
        MorphologicalTag tag = typedSentence.getTokens().get(head).getMorphologicalTag().clone();

        List<org.cogroo.entities.Token> tokens = new ArrayList<org.cogroo.entities.Token>();
        for (int i = textChunk.getStart(); i < textChunk.getEnd(); i++) {
          tokens.add(typedSentence.getTokens().get(i));
        }
        
        Chunk typedChunk = new ChunkCogroo(tokens, textChunk.getStart());
        
        for (org.cogroo.entities.Token token : tokens) {
          token.setChunk(typedChunk);
        }
        
        typedChunk.setMorphologicalTag(tag);
        chunks.add(typedChunk);
      }
      
      for (org.cogroo.entities.Token token : typedSentence.getTokens()) {
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
