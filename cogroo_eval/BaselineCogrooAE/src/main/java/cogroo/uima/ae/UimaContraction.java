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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import opennlp.tools.util.Span;

import org.apache.log4j.Logger;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.entity.Token;
import br.usp.pcs.lta.cogroo.tools.ProcessingEngine;
import br.usp.pcs.lta.cogroo.tools.pretagger.contraction.Contraction;

public class UimaContraction extends AnnotationService implements
    ProcessingEngine {

  private Type tokenType;
  private Type sentenceType;
  private Type contractionType;

  protected static final Logger LOGGER = Logger
      .getLogger(UimaContraction.class);

  public UimaContraction() throws AnnotationServiceException {
    super("UIMAContraction");

  }

  public void process(Sentence text) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(">>> preTag()");
      StringBuilder trace = new StringBuilder("preTag tokens: ");
      for (Token token : text.getTokens()) {
        trace.append("[" + token.getLexeme() + ";" + token.getSpan().toString()
            + "]");
      }
      LOGGER.debug(trace.toString());
    }

    // ************************************
    // Add text to the CAS
    // ************************************
    updateCas(text, cas);
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

    FSIterator<Annotation> personIterator = cas.getAnnotationIndex(
        contractionType).iterator();
    List<Span> contractions = new ArrayList<Span>();

    List<Token> tokens = new ArrayList<Token>();
    while (personIterator.hasNext()) {
      Annotation a = personIterator.next();
      Span s = new Span(a.getBegin(), a.getEnd());
      contractions.add(s);
    }

    text.setTokens(ungroupTokens(text.getTokens(), contractions));

    cas.reset();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("<<< preTag()");
      StringBuilder trace = new StringBuilder("preTag result: ");
      for (Token token : text.getTokens()) {
        trace.append("[" + token.getLexeme() + ";" + token.getSpan().toString()
            + "]");
      }
      LOGGER.debug(trace.toString());
    }

  }

  @Override
  protected void initTypes(TypeSystem typeSystem) {
    sentenceType = cas.getTypeSystem().getType("opennlp.uima.Sentence");
    tokenType = cas.getTypeSystem().getType("opennlp.uima.Token");
    contractionType = cas.getTypeSystem().getType("opennlp.uima.Contraction");
  }

  private void updateCas(Sentence sentence, JCas cas) {
    cas.reset();
    cas.setDocumentText(sentence.getSentence());

    AnnotationFS a = cas.getCas().createAnnotation(sentenceType,
        sentence.getOffset(),
        sentence.getOffset() + sentence.getSentence().length());

    cas.getIndexRepository().addFS(a);

    for (Token t : sentence.getTokens()) {
      a = cas.getCas().createAnnotation(tokenType, t.getSpan().getStart()/*
                                                                          * +
                                                                          * sentence
                                                                          * .
                                                                          * getOffset
                                                                          * ()
                                                                          */,
          t.getSpan().getEnd()/* + sentence.getOffset() */);

      cas.getIndexRepository().addFS(a);
    }
  }

  private static List<Token> ungroupTokens(List<Token> toks, List<Span> spans) {
    if (spans == null || spans.size() == 0) {
      return toks;
    }
    List<Token> grouped = new ArrayList<Token>(toks);
    int lastTokVisited = 0;
    List<Integer> toSplit = new ArrayList<Integer>();
    for (int i = 0; i < spans.size(); i++) {
      Span s = spans.get(i);
      boolean canStop = false;
      for (int j = lastTokVisited; j < toks.size(); j++) {
        Token t = toks.get(j);
        if (s.intersects(t.getSpan())) {
          toSplit.add(j);
          canStop = true;
        } else if (canStop) {
          lastTokVisited = j;
          break;
        }
      }
    }

    return mergeTokens(grouped, toSplit);
  }

  private static List<Token> mergeTokens(List<Token> grouped,
      List<Integer> toSplit) {
    if (toSplit.size() > 0) {
      List<Token> tokens = new ArrayList<Token>();

      int index = 0;
      for (int i = 0; i < grouped.size(); i++) {
        if (index < toSplit.size() && toSplit.get(index).equals(i)) {
          Token[] ts = Contraction.separate(grouped.get(i));
          tokens.addAll(Arrays.asList(ts));
          index++;
        } else {
          tokens.add(grouped.get(i));
        }
      }

      return tokens;
    } else {
      return grouped;
    }

  }

  private static List<Span> merge(List<Span> first, List<Span> second) {
    List<Span> merged = new ArrayList<Span>(first.size() + second.size());
    // add all of the first
    merged.addAll(first);

    for (Span s : second) {
      boolean addS = true;
      for (Span f : first) {
        if (s.intersects(f)) {
          addS = false;
          break;
        }
      }
      if (addS) {
        merged.add(s);
      }
    }
    Collections.<Span> sort(merged);
    return merged;
  }
}
