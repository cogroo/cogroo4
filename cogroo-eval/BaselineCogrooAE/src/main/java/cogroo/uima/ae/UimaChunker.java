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
package cogroo.uima.ae;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import br.usp.pcs.lta.cogroo.entity.Chunk;
import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.entity.Token;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.ChunkCogroo;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.ChunkTag;
import br.usp.pcs.lta.cogroo.tag.TagInterpreterI;
import br.usp.pcs.lta.cogroo.tools.ProcessingEngine;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.ChunkFunction;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Class;
import cogroo.ExpandedSentence;
import cogroo.uima.interpreters.FlorestaTagInterpreter;

public class UimaChunker extends AnnotationService implements ProcessingEngine {

  private Type tokenType;
  private Type sentenceType;

  private Feature postagFeature;

  private Type chunkType;
  private Feature chunktagFeature;
  // private Feature chunkheadFeature;

  private TagInterpreterI floresta = new FlorestaTagInterpreter();

  protected static final Logger LOGGER = Logger.getLogger(UimaChunker.class);

  private TagMask noum = new TagMask();
  private TagMask verb = new TagMask();

  public UimaChunker() throws AnnotationServiceException {
    super("UIMAChunker");
    noum.setClazz(Class.NOUN);
    verb.setClazz(Class.VERB);
  }

  public void process(Sentence text) {

    ExpandedSentence extSentence = new ExpandedSentence(text);

    // ************************************
    // Add text to the CAS
    // ************************************
    updateCas(extSentence, cas);
    // ************************************
    // Analyze text
    // ************************************
    try {
      ae.process(cas);
    } catch (Exception e) {
      throw new RuntimeException("Error processing a text.", e);
    }

    // ************************************
    // Extract the result using annotated CAS
    // ************************************

    List<Token> tokens = text.getTokens();

    FSIterator<Annotation> iterator = cas.getAnnotationIndex(chunkType)
        .iterator();
    List<Chunk> chunks = new ArrayList<Chunk>();
    int lastToken = 0;
    while (iterator.hasNext()) {
      Annotation a = iterator.next();
      String uimatag = a.getStringValue(chunktagFeature);
      if (uimatag.equals("NP") || uimatag.equals("VP")) {
        int start = -1;
        List<Token> chunkTokens = new ArrayList<Token>();
        // will find the region
        for (int i = lastToken; i < tokens.size(); i++) {
          Token t = tokens.get(i);
          boolean found = false;
          // boolean isHead = false;
          boolean isBoundary = false;
          if (a.getBegin() == extSentence.getTokenSpan(i).getStart()) {
            found = true;
            start = i;
            isBoundary = true;
          } else if (a.getEnd() == extSentence.getTokenSpan(i).getEnd()) {
            found = true;
            lastToken = i + 1;
          } else if (start >= 0) {
            found = true;
          }
          if (found) {
            /*
             * if(mt == null) { if(uimatag.equals("NP") &&
             * t.getMorphologicalTag().match(noum) || uimatag.equals("VP") &&
             * t.getMorphologicalTag().match(verb)) { isHead = true; mt =
             * t.getMorphologicalTag(); } }
             */
            ChunkTag tag = create(uimatag, isBoundary, false);
            t.setChunkTag(tag);
            chunkTokens.add(t);
            if (a.getEnd() == extSentence.getTokenSpan(i).getEnd()) {
              break;
            }
          }
        }
        if (chunkTokens.size() > 0) {
          ChunkCogroo c = new ChunkCogroo(chunkTokens, start);
          for (Token t : chunkTokens) {
            t.setChunk(c);
          }
          chunks.add(c);
        } else {
          System.out.println("blah");
        }

      }
    }
    for (int j = 0; j < tokens.size(); j++) {
      Token t = tokens.get(j);
      if (t.getChunk() == null) {
        List<Token> cl = new ArrayList<Token>(1);
        cl.add(t);
        ChunkTag ct = new ChunkTag();
        ct.setChunkFunction(ChunkFunction.OTHER);
        t.setChunkTag(ct);
        ChunkCogroo c = new ChunkCogroo(cl, j);
        c.setMorphologicalTag(t.getMorphologicalTag());
        t.setChunk(c);
        chunks.add(c);
      }
    }

    text.setTokens(tokens);
    text.setChunks(chunks);
    cas.reset();

  }

  private ChunkTag create(String uimatag, boolean isBoundary, boolean isHead) {
    if (isBoundary) {
      uimatag = "B-" + uimatag;
    } else {
      uimatag = "I-" + uimatag;
    }
    if (isHead) {
      uimatag = "*" + uimatag;
    } else if (uimatag.equals("B-VP")) {
      uimatag = "*" + uimatag;
    }
    return floresta.parseChunkTag(uimatag);
  }

  @Override
  protected void initTypes(TypeSystem typeSystem) {
    sentenceType = cas.getTypeSystem().getType("opennlp.uima.Sentence");

    tokenType = cas.getTypeSystem().getType("opennlp.uima.Token");
    postagFeature = tokenType.getFeatureByBaseName("pos");

    chunkType = cas.getTypeSystem().getType("opennlp.uima.Chunk");
    chunktagFeature = chunkType.getFeatureByBaseName("type");
    // chunkheadFeature = chunkType.getFeatureByBaseName("head");
  }

  private void updateCas(ExpandedSentence sentence, JCas cas) {

    cas.reset();
    cas.setDocumentText(sentence.getExtendedSentence());

    AnnotationFS sentenceAnnotation = cas.getCas().createAnnotation(
        sentenceType,
        sentence.getSent().getOffset(),
        sentence.getSent().getOffset()
            + sentence.getExtendedSentence().length());

    cas.getIndexRepository().addFS(sentenceAnnotation);

    for (int i = 0; i < sentence.getSent().getTokens().size(); i++) {
      Token t = sentence.getSent().getTokens().get(i);
      AnnotationFS tokenAnnotation = cas.getCas().createAnnotation(tokenType,
          sentence.getTokenSpan(i).getStart(),
          sentence.getTokenSpan(i).getEnd());
      br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Class c = t
          .getMorphologicalTag().getClazzE();
      String tag;
      if (c != null) {
        if (t.getMorphologicalTag().getClazzE().equals(Class.VERB)) {
          tag = floresta.serialize(t.getMorphologicalTag().getFinitenessE());
        } else {
          tag = floresta.serialize(t.getMorphologicalTag().getClazzE());
        }
      } else {
        tag = t.getLexeme();
      }
      if (tag == null || tag.isEmpty()) {
        throw new RuntimeException("tag was empty!");
      }
      tokenAnnotation.setStringValue(postagFeature, tag);
      cas.getIndexRepository().addFS(tokenAnnotation);
    }
  }

}
