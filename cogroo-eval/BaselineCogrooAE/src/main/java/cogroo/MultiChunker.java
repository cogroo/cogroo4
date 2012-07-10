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
import cogroo.uima.ae.UimaChunker;
import cogroo.uima.ae.UimaChunkerHeadFinder;

public class MultiChunker implements ProcessingEngine {

  private ProcessingEngine chunker;
  private ProcessingEngine chunkerHeadFinder;

  protected static final Logger LOGGER = Logger.getLogger(MultiChunker.class);

  public MultiChunker(RuntimeConfigurationI config) {
    if (MultiCogrooSettings.CHUNKER) {
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
    if (chunkerHeadFinder != null) {
      this.chunkerHeadFinder.process(text);
    }
  }

}
