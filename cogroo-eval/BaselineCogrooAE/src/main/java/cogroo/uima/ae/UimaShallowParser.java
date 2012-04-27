package cogroo.uima.ae;

import java.util.ArrayList;
import java.util.Collections;
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
import br.usp.pcs.lta.cogroo.entity.SyntacticChunk;
import br.usp.pcs.lta.cogroo.entity.Token;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.ChunkTag;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.SyntacticTag;
import br.usp.pcs.lta.cogroo.tag.TagInterpreterI;
import br.usp.pcs.lta.cogroo.tools.ProcessingEngine;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Class;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.SyntacticFunction;
import cogroo.ExpandedSentence;

public class UimaShallowParser extends AnnotationService implements
    ProcessingEngine {

  private Type tokenType;
  private Type sentenceType;

  private Feature postagFeature;

  private Type chunkType;
  private Feature chunktagFeature;
  // private Feature chunkheadFeature;

  private final SyntacticTag SUBJ;
  private final SyntacticTag MV;
  private final SyntacticTag NONE;

  private TagInterpreterI floresta = new FlorestaTagInterpreter();

  protected static final Logger LOGGER = Logger
      .getLogger(UimaShallowParser.class);

  public UimaShallowParser() throws AnnotationServiceException {
    super("UIMAShallowParser");

    SUBJ = new SyntacticTag();
    SUBJ.setSyntacticFunction(SyntacticFunction.SUBJECT);

    MV = new SyntacticTag();
    MV.setSyntacticFunction(SyntacticFunction.VERB);

    NONE = new SyntacticTag();
    NONE.setSyntacticFunction(SyntacticFunction.NONE);
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

    // List<Token> tokens = text.getTokens();

    FSIterator<Annotation> iterator = cas.getAnnotationIndex(chunkType)
        .iterator();

    // tenho criar um por tag...

    List<SyntacticChunk> sentenceSyntacticChunks = new ArrayList<SyntacticChunk>();
    while (iterator.hasNext()) {
      Annotation a = iterator.next();
      String uimatag = a.getStringValue(chunktagFeature);
      SyntacticTag st = create(uimatag);
      Span s = new Span(a.getBegin(), a.getEnd());

      ArrayList<Chunk> childChunks = new ArrayList<Chunk>();
      SyntacticChunk syntacticChunk = new SyntacticChunk(childChunks);
      syntacticChunk.setSyntacticTag(st);

      for (int i = 0; i < text.getTokens().size(); i++) {
        Token token = text.getTokens().get(i);
        if (s.intersects(extSentence.getTokenSpan(i))) {

          // checks if it is the same chunk
          if (childChunks.size() > 0) {
            if (!token.getChunk().equals(
                childChunks.get(childChunks.size() - 1))) {
              childChunks.add(token.getChunk());
            }
          } else {
            childChunks.add(token.getChunk());
          }
          token.setSyntacticChunk(syntacticChunk);
        }
      }
      sentenceSyntacticChunks.add(syntacticChunk);
    }

    for (Token token : text.getTokens()) {
      if (token.getSyntacticTag() == null) {
        SyntacticChunk sc = new SyntacticChunk(Collections.singletonList(token
            .getChunk()));
        sc.setSyntacticTag(none);
        token.setSyntacticChunk(sc);
        sentenceSyntacticChunks.add(sc);
      }
    }

    // for (Chunk chunk : text.getChunks()) {
    // if(chunk.getSyntacticTag() == null) {
    // chunk.setSyntacticTag(none);
    // }
    // }

    text.setSyntacticChunks(sentenceSyntacticChunks);

    cas.reset();

  }

  static SyntacticTag none;

  static {
    none = new SyntacticTag();
    none.setSyntacticFunction(SyntacticFunction.NONE);
  }

  private boolean isHead(ChunkTag chunkTag) {
    // TODO Auto-generated method stub
    return false;
  }

  private SyntacticTag create(String uimatag) {
    SyntacticTag s = new SyntacticTag();
    if ("SUBJ".equals(uimatag)) {
      s.setSyntacticFunction(SyntacticFunction.SUBJECT);
    } else if ("P".equals(uimatag)) {
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
      String chunk = floresta.serialize(t.getChunkTag());
      if (tag == null || tag.isEmpty()) {
        throw new RuntimeException("tag was empty!");
      }
      if (chunk == null || chunk.isEmpty()) {
        throw new RuntimeException("chunk was empty!");
      }
      tokenAnnotation.setStringValue(postagFeature,
          tag + "|" + chunk.replace("*", ""));
      cas.getIndexRepository().addFS(tokenAnnotation);
    }
  }

}
