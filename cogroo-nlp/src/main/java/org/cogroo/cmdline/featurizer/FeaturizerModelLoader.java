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
package org.cogroo.cmdline.featurizer;

import java.io.IOException;
import java.io.InputStream;

import org.cogroo.tools.featurizer.FeaturizerModel;

import opennlp.tools.cmdline.ModelLoader;

/**
 * Loads a Featurizer Model for the command line tools.
 * <p>
 * <b>Note:</b> Do not use this class, internal use only!
 */
public class FeaturizerModelLoader extends ModelLoader<FeaturizerModel> {

  public FeaturizerModelLoader() {
    super("Featurizer");
  }

  @Override
  protected FeaturizerModel loadModel(InputStream modelIn) throws IOException {
    return new FeaturizerModel(modelIn);
  }

}
