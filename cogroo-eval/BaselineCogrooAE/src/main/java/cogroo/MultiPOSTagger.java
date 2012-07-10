/**
 * Copyright (C) 2012 cogroo <cogroo@cogroo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cogroo;

import org.apache.log4j.Logger;

import br.usp.pcs.lta.cogroo.configuration.RuntimeConfigurationI;
import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.tools.ProcessingEngine;
import cogroo.uima.ae.AnnotationServiceException;
import cogroo.uima.ae.UimaFeaturizer;
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
        this.featurizer = new UimaFeaturizer();
      } catch (AnnotationServiceException e) {
        throw new RuntimeException(e);
      }
    } else {
      LOGGER.info("Loading std pos tagger");
      this.posTagger = config.getPOSTagger();
    }
  }

  public void process(Sentence text) {
    this.posTagger.process(text);
    if(featurizer != null)
      this.featurizer.process(text);
  }

}
