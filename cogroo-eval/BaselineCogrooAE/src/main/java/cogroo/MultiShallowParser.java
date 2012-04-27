package cogroo;

import org.apache.log4j.Logger;

import br.usp.pcs.lta.cogroo.configuration.RuntimeConfigurationI;
import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.tools.ProcessingEngine;
import cogroo.uima.ae.AnnotationServiceException;
import cogroo.uima.ae.UimaShallowParser;

public class MultiShallowParser implements ProcessingEngine {

  private ProcessingEngine chunker;
  protected static final Logger LOGGER = Logger
      .getLogger(MultiShallowParser.class);

  public MultiShallowParser(RuntimeConfigurationI config) {
    if (MultiCogrooSettings.SP) {
      try {
        LOGGER.info("Loading *NEW* shallow parser");
        this.chunker = new UimaShallowParser();
      } catch (AnnotationServiceException e) {
        throw new RuntimeException(e);
      }
    } else {
      LOGGER.info("Loading std shallow parser");
      this.chunker = config.getShallowParser();
    }
  }

  public void process(Sentence text) {
    this.chunker.process(text);
  }

}
