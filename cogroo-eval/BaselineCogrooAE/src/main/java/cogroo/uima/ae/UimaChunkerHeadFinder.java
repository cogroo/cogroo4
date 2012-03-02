package cogroo.uima.ae;

import java.util.List;

import opennlp.tools.util.Span;

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
import br.usp.pcs.lta.cogroo.entity.impl.runtime.ChunkTag;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.SyntacticTag;
import br.usp.pcs.lta.cogroo.tag.TagInterpreterI;
import br.usp.pcs.lta.cogroo.tools.ProcessingEngine;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.ChunkFunction;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Class;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.SyntacticFunction;
import cogroo.ExpandedSentence;

public class UimaChunkerHeadFinder extends AnnotationService implements
    ProcessingEngine {

  private Type tokenType;
  private Type sentenceType;

  private Feature postagFeature;

  private Type chunkType;
  private Feature chunktagFeature;
  

  private TagInterpreterI floresta = new FlorestaTagInterpreter();
  
  protected static final Logger LOGGER = Logger
      .getLogger(UimaChunkerHeadFinder.class);


  public UimaChunkerHeadFinder() throws AnnotationServiceException {
    super(
        "/Users/wcolen/Documents/wrks/_REPO/UIMAChunkerHeadFinder/UIMAChunkerHeadFinder_pear.xml");
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

    //List<Token> tokens = text.getTokens();

    List<Chunk> chunks = text.getChunks();
    
    FSIterator<Annotation> iterator = cas.getAnnotationIndex(chunkType)
        .iterator();
    
    // tenho criar um por tag...

    int lastToken = 0;
    while (iterator.hasNext()) {
      Annotation a = iterator.next();
      boolean isHead = false;
      String uimatag = a.getStringValue(chunktagFeature);
      if(uimatag != null && uimatag.equals("H")) {
        isHead = true;
      }
      
      Span s = new Span(a.getBegin(), a.getEnd());
      
      for (int i = 0; i < text.getTokens().size(); i++) {
        Token token = text.getTokens().get(i);
        if(s.intersects(extSentence.getTokenSpan(i))) {
          token.setChunkTag(create(token.getChunkTag(), isHead));
          if(isHead) {
            token.getChunk().setMorphologicalTag(token.getMorphologicalTag());
          }
          break;
          //boolean isSubjOrMainVerb = st.match(SUBJ) || st.match(MV);
          
          /*if ( isSubjOrMainVerb ) {
              token.getChunk().setSyntacticTag(st);
          } else if(token.getChunk().getSyntacticTag() == null) {
              SyntacticTag none = new SyntacticTag();
              none.setSyntacticFunction(SyntacticFunction.NONE);
              token.getChunk().setSyntacticTag(none);
          }*/
        }
      }
      

    }

    for (int j = 0; j < text.getChunks().size(); j++) {
      Chunk c = text.getChunks().get(j);
      if(c.getMorphologicalTag() == null) {
        if(c.getTokens().size() > 0) {
          c.setMorphologicalTag(c.getTokens().get(0).getMorphologicalTag());
        } else {
          System.out.println("dude");
        }
      }
    }
    
    cas.reset();

  }
  
  final static ChunkTag BOUNDARY_NOUN_PHRASE_MAIN = new ChunkTag();
  final static ChunkTag BOUNDARY_VERB_PHRASE_MAIN = new ChunkTag();
  final static ChunkTag INTERMEDIARY_NOUN_PHRASE_MAIN = new ChunkTag();
  
  static {
    BOUNDARY_NOUN_PHRASE_MAIN.setChunkFunction(ChunkFunction.BOUNDARY_NOUN_PHRASE_MAIN);
    BOUNDARY_VERB_PHRASE_MAIN.setChunkFunction(ChunkFunction.BOUNDARY_VERB_PHRASE_MAIN);
    INTERMEDIARY_NOUN_PHRASE_MAIN.setChunkFunction(ChunkFunction.INTERMEDIARY_NOUN_PHRASE_MAIN);
  }

  private ChunkTag create(ChunkTag chunkTag, boolean isHead) {
    if(isHead) {
      if(chunkTag != null) {
        if(ChunkFunction.BOUNDARY_NOUN_PHRASE.equals(chunkTag.getChunkFunction())) {
          return BOUNDARY_NOUN_PHRASE_MAIN;
        } /*else if(ChunkFunction.BOUNDARY_VERB_PHRASE_MAIN.equals(chunkTag.getChunkFunction())) {
          return BOUNDARY_VERB_PHRASE_MAIN;
        }*/ else if(ChunkFunction.INTERMEDIARY_NOUN_PHRASE.equals(chunkTag.getChunkFunction())) {
          return INTERMEDIARY_NOUN_PHRASE_MAIN;
        } else {
          //throw new IllegalArgumentException(chunkTag + " whithout main equivalent");
        }
      }
    }
    return chunkTag;
  }

  private SyntacticTag create(String uimatag) {
    SyntacticTag s = new SyntacticTag();
    if("SUBJ".equals(uimatag)) {
      s.setSyntacticFunction(SyntacticFunction.SUBJECT);
    } else if("P".equals(uimatag)) {
      s.setSyntacticFunction(SyntacticFunction.VERB);
    } else {
      s.setSyntacticFunction(SyntacticFunction.NONE);
    }
    return s;
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

    AnnotationFS sentenceAnnotation = cas.getCas().createAnnotation(sentenceType,
        sentence.getSent().getOffset(),
        sentence.getSent().getOffset() + sentence.getExtendedSentence().length());

    cas.getIndexRepository().addFS(sentenceAnnotation);

    for (int i = 0; i < sentence.getSent().getTokens().size(); i++) {
      Token t = sentence.getSent().getTokens().get(i);
      AnnotationFS tokenAnnotation = cas.getCas().createAnnotation(tokenType,
          sentence.getTokenSpan(i).getStart()/* + sentence.getOffset() */,
          sentence.getTokenSpan(i).getEnd()/* + sentence.getOffset() */);
      br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Class c = t
          .getMorphologicalTag().getClazzE();
      String tag;
      if (c != null) {
        if(t.getMorphologicalTag().getClazzE().equals(Class.VERB)) {
          tag = floresta.serialize(t.getMorphologicalTag().getFinitenessE());
        } else {
          tag = floresta.serialize(t.getMorphologicalTag().getClazzE());
        }
      } else {
        tag = t.getLexeme();
      }
      String chunk = floresta.serialize(t.getChunkTag());
      if(tag == null || tag.isEmpty()) {
        throw new RuntimeException("tag was empty!");
      }
      if(chunk == null || chunk.isEmpty()) {
        throw new RuntimeException("chunk was empty!");
      }
      tokenAnnotation.setStringValue(postagFeature, tag + "|" + chunk.replace("*", ""));
      cas.getIndexRepository().addFS(tokenAnnotation);
    }
  }

}
