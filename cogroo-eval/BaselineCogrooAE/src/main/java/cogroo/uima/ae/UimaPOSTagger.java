package cogroo.uima.ae;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import opennlp.tools.util.Span;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.entity.Token;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.MorphologicalTag;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.TokenCogroo;
import br.usp.pcs.lta.cogroo.tools.ProcessingEngine;

public class UimaPOSTagger extends AnnotationService implements
    ProcessingEngine {

  private Type tokenType;
  private Type sentenceType;
  private Feature posFeature;
  private FlorestaTagInterpreter it = new FlorestaTagInterpreter();

  public UimaPOSTagger() throws AnnotationServiceException {
    super("UimaPOSTagger");

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

    FSIterator<Annotation> tokenIterator = cas.getAnnotationIndex(tokenType)
        .iterator();

    int index = 0;
    List<Token> tokens = text.getTokens();
    while (tokenIterator.hasNext()) {
      Annotation a = tokenIterator.next();
      String tag = a.getFeatureValueAsString(posFeature);
      tokens.get(index).setOriginalPOSTag(tag);
      tokens.get(index).setMorphologicalTag(toMorphologicalTag(tag));
      index++;
    }

    cas.reset();
  }

  private MorphologicalTag toMorphologicalTag(String tag) {
    
    return it.parseMorphologicalTag(tag);
  }

  @Override
  protected void initTypes(TypeSystem typeSystem) {
    sentenceType = cas.getTypeSystem().getType("opennlp.uima.Sentence");
    tokenType = cas.getTypeSystem().getType("opennlp.uima.Token");
    posFeature = tokenType.getFeatureByBaseName("pos");
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
}
