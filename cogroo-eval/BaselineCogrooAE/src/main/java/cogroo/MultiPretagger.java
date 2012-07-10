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
import br.usp.pcs.lta.cogroo.tools.pretagger.contraction.Contraction;
import cogroo.uima.ae.AnnotationServiceException;
import cogroo.uima.ae.UimaContraction;
import cogroo.uima.ae.UimaMultiWordExp;

public class MultiPretagger implements ProcessingEngine {

  private ProcessingEngine me;
  private ProcessingEngine con;
  protected static final Logger LOGGER = Logger.getLogger(MultiPretagger.class);

  public MultiPretagger(RuntimeConfigurationI config) {
    if (MultiCogrooSettings.PROP) {
      try {
        LOGGER.info("Loading *NEW* NF");
        this.me = new UimaMultiWordExp();
      } catch (AnnotationServiceException e) {
        throw new RuntimeException(e);
      }
    } else {
      LOGGER.info("Loading std NF");
      this.me = config.getNameFinder();
    }
    
    if (MultiCogrooSettings.CON) {
      try {
        LOGGER.info("Loading *NEW* CON");
        this.con = new UimaContraction();
      } catch (AnnotationServiceException e) {
        throw new RuntimeException(e);
      }
    } else {
      LOGGER.info("Loading std CON");
      this.con = new Contraction();
    }
  }

  public void process(Sentence text) {
    this.me.process(text);
    this.con.process(text);
  }

}
