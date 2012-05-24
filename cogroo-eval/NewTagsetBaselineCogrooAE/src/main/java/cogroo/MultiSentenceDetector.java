package cogroo;

import java.util.List;

import org.apache.log4j.Logger;

import br.usp.pcs.lta.cogroo.configuration.RuntimeConfigurationI;
import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.tools.sentencedetector.SentenceDetectorI;
import cogroo.uima.ae.AnnotationServiceException;
import cogroo.uima.ae.UimaSentenceDetector;

public class MultiSentenceDetector implements SentenceDetectorI {

  private SentenceDetectorI sd;
  protected static final Logger LOGGER = Logger
      .getLogger(MultiSentenceDetector.class);

  public MultiSentenceDetector(RuntimeConfigurationI config) {
    if (MultiCogrooSettings.SENT) {
      try {
        LOGGER.info("Loading *NEW* sentence detector");
        this.sd = new UimaSentenceDetector();
      } catch (AnnotationServiceException e) {
        throw new RuntimeException(e);
      }
    } else {
      LOGGER.info("Loading std sentence detector");
      this.sd = config.getSentenceDetector();
    }
  }

  public List<Sentence> process(String text) {
    return this.sd.process(text);
  }

}
