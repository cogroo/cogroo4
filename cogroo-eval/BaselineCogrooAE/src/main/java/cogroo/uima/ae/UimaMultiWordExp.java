package cogroo.uima.ae;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import opennlp.tools.util.Span;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.entity.Token;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.TokenCogroo;
import br.usp.pcs.lta.cogroo.tools.ProcessingEngine;

public class UimaMultiWordExp extends AnnotationService implements
    ProcessingEngine {

  private Type tokenType;
  private Type sentenceType;
  private Type personType;
  private Type expType;

  public UimaMultiWordExp() throws AnnotationServiceException {
    super("UIMAMultiWordExp");

  }

  public void process(Sentence text) {
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

    FSIterator<Annotation> personIterator = cas.getAnnotationIndex(personType)
        .iterator();
    FSIterator<Annotation> expIterator = cas.getAnnotationIndex(expType)
        .iterator();
    List<Span> names = new ArrayList<Span>();
    List<Span> exp = new ArrayList<Span>();

    List<Token> tokens = new ArrayList<Token>();
    while (personIterator.hasNext()) {
      Annotation a = personIterator.next();
      Span s = new Span(a.getBegin(), a.getEnd());
      names.add(s);
    }

    while (expIterator.hasNext()) {
      Annotation a = expIterator.next();
      Span s = new Span(a.getBegin(), a.getEnd());
      exp.add(s);
    }
    List<Span> merged = merge(exp, names);

    text.setTokens(groupTokens(text.getTokens(), merged));

    cas.reset();

  }

  @Override
  protected void initTypes(TypeSystem typeSystem) {
    sentenceType = cas.getTypeSystem().getType("opennlp.uima.Sentence");
    tokenType = cas.getTypeSystem().getType("opennlp.uima.Token");
    personType = cas.getTypeSystem().getType("opennlp.uima.Person");
    expType = cas.getTypeSystem().getType("opennlp.uima.Exp");
  }

  private void updateCas(Sentence sentence, JCas cas) {
    cas.reset();
    cas.setDocumentText(sentence.getSentence());

    AnnotationFS a = cas.getCas().createAnnotation(sentenceType,
        sentence.getOffset(),
        sentence.getOffset() + sentence.getSentence().length());

    cas.getIndexRepository().addFS(a);

    for (Token t : sentence.getTokens()) {
      a = cas.getCas().createAnnotation(tokenType,
          t.getSpan().getStart() + sentence.getOffset(),
          t.getSpan().getEnd() + sentence.getOffset());

      cas.getIndexRepository().addFS(a);
    }
  }

  private static List<Token> groupTokens(List<Token> toks, List<Span> spans) {
    if (spans == null || spans.size() == 0) {
      return toks;
    }
    List<Token> grouped = new ArrayList<Token>(toks);
    int lastTokVisited = 0;
    List<Integer> toMerge = new ArrayList<Integer>();
    for (int i = 0; i < spans.size(); i++) {
      Span s = spans.get(i);
      boolean canStop = false;
      for (int j = lastTokVisited; j < toks.size(); j++) {
        Token t = toks.get(j);
        if (s.intersects(t.getSpan())) {
          toMerge.add(j);
          canStop = true;
        } else if (canStop) {
          lastTokVisited = j;
          break;
        }
      }
    }
    mergeTokens(grouped, toMerge);

    return grouped;
  }

  private static void mergeTokens(List<Token> grouped, List<Integer> toMerge) {
    if (toMerge.size() > 0) {
      StringBuilder sb = new StringBuilder();
      int s = grouped.get(toMerge.get(0)).getSpan().getStart();
      int e = grouped.get(toMerge.get(toMerge.size() - 1)).getSpan().getEnd();
      for (int i = 0; i < toMerge.size(); i++) {
        int index = toMerge.get(i);
        sb.append(grouped.get(index).getLexeme() + "_");
      }
      String lexeme = sb.substring(0, sb.length() - 1);
      for (int i = toMerge.size() - 1; i > 0; i--) {
        grouped.remove(toMerge.get(i).intValue());
      }
      grouped.set(toMerge.get(0).intValue(), new TokenCogroo(lexeme, new Span(
          s, e)));
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
