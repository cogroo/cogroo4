package cogroo;

import org.apache.log4j.Logger;

import br.usp.pcs.lta.cogroo.configuration.RuntimeConfigurationI;
import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.tools.ProcessingEngine;
import cogroo.uima.ae.AnnotationServiceException;
import cogroo.uima.ae.UimaChunker;
import cogroo.uima.ae.UimaChunkerHeadFinder;

public class MultiChunker implements ProcessingEngine {
  
  private ProcessingEngine chunker;
  private ProcessingEngine chunkerHeadFinder;
  
  protected static final Logger LOGGER = Logger.getLogger(MultiChunker.class);

  public MultiChunker(RuntimeConfigurationI config) {
    if(MultiCogrooSettings.CHUNKER) {
      try {
        LOGGER.info("Loading *NEW* Chunker");
        this.chunker = new UimaChunker();
        this.chunkerHeadFinder = new UimaChunkerHeadFinder();
      } catch (AnnotationServiceException e) {
        throw new RuntimeException(e);
      }
    } else {
      LOGGER.info("Loading std Chunker");
      this.chunker = config.getChunker();
    }
  }

  public void process(Sentence text) {
    this.chunker.process(text);
    if(chunkerHeadFinder != null) {
      this.chunkerHeadFinder.process(text);
    }
  }

}
