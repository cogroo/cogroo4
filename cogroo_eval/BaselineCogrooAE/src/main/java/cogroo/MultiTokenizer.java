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
import cogroo.uima.ae.UimaTokenizer;

public class MultiTokenizer implements ProcessingEngine {
  protected static final Logger LOGGER = Logger.getLogger(MultiTokenizer.class);

  private ProcessingEngine tok;

  public MultiTokenizer(RuntimeConfigurationI config) {
    if (MultiCogrooSettings.TOK) {
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
