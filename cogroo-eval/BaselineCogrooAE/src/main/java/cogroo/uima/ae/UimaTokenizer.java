package cogroo.uima.ae;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.util.Span;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import cogroo.MultiCogrooSettings;

import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.entity.Token;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.MorphologicalTag;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.TokenCogroo;
import br.usp.pcs.lta.cogroo.tools.ProcessingEngine;

public class UimaTokenizer extends AnnotationService implements
    ProcessingEngine {

  private Type tokenType;
  private Type sentenceType;
  
  private static final Set<String> PRONOMES_OBLIQUOS_ATONOS;
  
  static {
    String[] arr = { "me", "te", "se", "o", "a", "lhe", "nos", "vos", "os",
        "as", "lhes", "lo" };
    PRONOMES_OBLIQUOS_ATONOS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(arr)));
  }
  

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
    boolean foundHyphen = false;
    while(iterator.hasNext()) {
      Annotation  a = iterator.next();
      String tokStr = a.getCoveredText();
      Span tokSpan = new Span(a.getBegin(), a.getEnd());
      
/*    if(!MultiCogrooSettings.PRE) {
        if(tokStr != null && foundHyphen && PRONOMES_OBLIQUOS_ATONOS.contains(tokStr.toLowerCase())) {
//          System.out.println("found pronome obliquo");
          foundHyphen = false;
          tokStr = '-' + tokStr;
          tokSpan = new Span(tokSpan.getStart() - 1, tokSpan.getEnd());
//          System.out.println("tokStr: " + tokStr);
//          System.out.println("tokSpan: " + tokSpan);
          
          TokenCogroo lt = (TokenCogroo)tokens.get(tokens.size()-1);
          lt.setLexeme(lt.getLexeme().substring(0, lt.getLexeme().length() - 1));
          lt.setSpan(new Span(lt.getSpan().getStart(), lt.getSpan().getEnd() -1));
//          System.out.println("replace tok: " + lt);
          tokens.set(tokens.size()-1,lt);
        } else if(foundHyphen) {
          foundHyphen = false;
//          System.out.println("reset foundHyphen");
        } else if(tokStr.endsWith("-")) {
//          System.out.println("found hyphen: " + tokStr);
          foundHyphen = true;
        }       
      }*/
      
      TokenCogroo t = new TokenCogroo(tokStr, tokSpan);
      tokens.add(t);
    }
    
    if(!MultiCogrooSettings.PRE) {
      boolean restart = true;
      int start = 1;
      while(restart) {
        restart = false;
        for(int i = start; i < tokens.size() - 1 && !restart; i++) {
          if("-".equals(tokens.get(i).getLexeme())) {
            if(!hasCharacterBetween(tokens.get(i-1), tokens.get(i)) && !hasCharacterBetween(tokens.get(i), tokens.get(i+1))) {
              Token a = tokens.get(i-1);
              Token b = tokens.get(i+1);
              if(PRONOMES_OBLIQUOS_ATONOS.contains(b.getLexeme().toLowerCase())) {
                // remove the "-"
                b.setSpan(new Span(b.getSpan().getStart() - 1, b.getSpan().getEnd()));
                b.setLexeme("-" + b.getLexeme());
                tokens.remove(i);
                restart = true;
                start = i+1;
              }
            }
          }
        }      
      }
    }

    text.setTokens(tokens);

    cas.reset();

  }
  
  private boolean hasCharacterBetween(Token a, Token b) {
    int aEnd = a.getSpan().getEnd();
    int bStart = b.getSpan().getStart();
    if(aEnd == bStart) {
      return false;
    }
    return true;
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
