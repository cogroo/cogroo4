package cogroo;

import org.apache.log4j.Logger;

import br.usp.pcs.lta.cogroo.configuration.RuntimeConfigurationI;
import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.tools.ProcessingEngine;
import br.usp.pcs.lta.cogroo.tools.pretagger.contraction.Contraction;
import cogroo.uima.ae.AnnotationServiceException;
import cogroo.uima.ae.UimaContraction;
import cogroo.uima.ae.UimaMultiWordExp;
import cogroo.uima.ae.UimaPOSTagger;

public class MultiPOSTagger implements ProcessingEngine {

  private ProcessingEngine posTagger;
  private ProcessingEngine featurizer;
  protected static final Logger LOGGER = Logger.getLogger(MultiPOSTagger.class);

  public MultiPOSTagger(RuntimeConfigurationI config) {
    if (MultiCogrooSettings.TAGGER) {
      try {
        LOGGER.info("Loading *NEW* posTagger and featurizer");
        this.posTagger = new UimaPOSTagger();
        
      } catch (AnnotationServiceException e) {
        throw new RuntimeException(e);
      }
    } else {
      LOGGER.info("Loading std pos tagger");
      this.posTagger = config.getPOSTagger();
    }
    
    if (MultiCogrooSettings.CON) {
      try {
        LOGGER.info("Loading *NEW* CON");
        this.featurizer = new UimaContraction();
      } catch (AnnotationServiceException e) {
        throw new RuntimeException(e);
      }
    } else {
      LOGGER.info("Loading std CON");
      this.featurizer = new Contraction();
    }
  }

  public void process(Sentence text) {
    this.posTagger.process(text);
//    this.featurizer.process(text);
  }

}
