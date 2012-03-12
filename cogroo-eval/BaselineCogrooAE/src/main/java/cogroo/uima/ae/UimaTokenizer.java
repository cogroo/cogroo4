package cogroo.uima.ae;

import java.util.ArrayList;
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

public class UimaTokenizer extends AnnotationService implements
    ProcessingEngine {

  private Type tokenType;
  private Type sentenceType;
  

  public UimaTokenizer() throws AnnotationServiceException{
    super("UIMATokenizer");

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
    
    FSIterator<Annotation> iterator = cas.getAnnotationIndex(tokenType).iterator();
    List<Token> tokens = new ArrayList<Token>();
    while(iterator.hasNext()) {
      Annotation  a = iterator.next();
      TokenCogroo t = new TokenCogroo(a.getCoveredText(), new Span(a.getBegin(), a.getEnd()));
      tokens.add(t);
    }
    
    text.setTokens(tokens);

    cas.reset();

  }

  @Override
  protected void initTypes(TypeSystem typeSystem) {
    sentenceType = cas.getTypeSystem().getType("opennlp.uima.Sentence");  
    tokenType = cas.getTypeSystem().getType("opennlp.uima.Token");    
  }
  
  private void updateCas(Sentence sentence, JCas cas) {
    cas.reset();
    cas.setDocumentText(sentence.getSentence());
    
    AnnotationFS a = cas.getCas().createAnnotation(sentenceType,
        sentence.getOffset(),
        sentence.getOffset() + sentence.getSentence().length());

    cas.getIndexRepository().addFS(a);
  }


}
