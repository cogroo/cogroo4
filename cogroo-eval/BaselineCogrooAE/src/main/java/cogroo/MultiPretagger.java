package cogroo;

import org.apache.log4j.Logger;

import br.usp.pcs.lta.cogroo.configuration.RuntimeConfigurationI;
import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.tools.ProcessingEngine;
import br.usp.pcs.lta.cogroo.tools.pretagger.contraction.Contraction;
import cogroo.uima.ae.AnnotationServiceException;
import cogroo.uima.ae.UimaContraction;
import cogroo.uima.ae.UimaMultiWordExp;

public class MultiPretagger implements ProcessingEngine {

  private ProcessingEngine me;
  private ProcessingEngine con;
  protected static final Logger LOGGER = Logger.getLogger(MultiPretagger.class);

  public MultiPretagger(RuntimeConfigurationI config) {
    if (MultiCogrooSettings.PRE) {
      try {
        LOGGER.info("Loading *NEW* PreTagger");
        this.me = new UimaMultiWordExp();
        this.con = new UimaContraction();
      } catch (AnnotationServiceException e) {
        throw new RuntimeException(e);
      }
    } else {
      LOGGER.info("Loading std PreTagger");
      this.con = new Contraction();
      this.me = config.getNameFinder();
    }
  }

  public void process(Sentence text) {
    this.me.process(text);
    this.con.process(text);
  }

}
