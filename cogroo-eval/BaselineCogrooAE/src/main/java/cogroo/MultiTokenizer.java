package cogroo;

import org.apache.log4j.Logger;

import br.usp.pcs.lta.cogroo.configuration.RuntimeConfigurationI;
import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.tools.ProcessingEngine;
import cogroo.uima.ae.AnnotationServiceException;
import cogroo.uima.ae.UimaTokenizer;

public class MultiTokenizer implements ProcessingEngine {
  protected static final Logger LOGGER = Logger.getLogger(MultiTokenizer.class);

  private ProcessingEngine tok;

  public MultiTokenizer(RuntimeConfigurationI config) {
    if(MultiCogrooSettings.TOK) {
      try {
        LOGGER.info("Loading *NEW* tokenizer");
        this.tok = new UimaTokenizer();
      } catch (AnnotationServiceException e) {
        throw new RuntimeException(e);
      }
    } else {
      LOGGER.info("Loading std tokenizer");
      this.tok = config.getTokenizer();
    }
  }

  public void process(Sentence text) {
    this.tok.process(text);
  }

}
