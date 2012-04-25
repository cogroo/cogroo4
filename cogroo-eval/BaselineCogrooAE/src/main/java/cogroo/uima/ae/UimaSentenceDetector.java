package cogroo.uima.ae;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.Span;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.jcas.tcas.Annotation;

import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.tools.sentencedetector.SentenceDetectorI;

public class UimaSentenceDetector extends AnnotationService implements
    SentenceDetectorI {

  private Type sentenceType;
  

  public UimaSentenceDetector() throws AnnotationServiceException{
    super("UIMASentenceDetector");

  }

  public List<Sentence> process(String text) {
    // ************************************
    // Add text to the CAS
    // ************************************
    cas.setDocumentText(text);
    List<Sentence> sentences = new ArrayList<Sentence>();
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
    
    FSIterator<Annotation> iterator = cas.getAnnotationIndex(sentenceType).iterator();
    while(iterator.hasNext()) {
      Annotation  a = iterator.next();
      Sentence s = new Sentence();
      s.setSpan(new Span(a.getBegin(), a.getEnd()));
      s.setSentence(a.getCoveredText());
      s.setOffset(a.getBegin());
      sentences.add(s);
    }

    cas.reset();

    return sentences;
  }

  @Override
  protected void initTypes(TypeSystem typeSystem) {
    sentenceType = cas.getTypeSystem().getType("opennlp.uima.Sentence");    
  }
  
  public static void main(String[] args) throws AnnotationServiceException {
    UimaSentenceDetector sd = new UimaSentenceDetector();
    System.out.println(sd.process("O sr. José chegou. Vamos sair."));
  }


}
