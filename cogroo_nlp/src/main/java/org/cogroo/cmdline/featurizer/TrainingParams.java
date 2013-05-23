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

import java.io.File;

import opennlp.tools.cmdline.ArgumentParser.OptionalParameter;
import opennlp.tools.cmdline.ArgumentParser.ParameterDescription;
import opennlp.tools.cmdline.params.BasicTrainingParams;

/**
 * TrainingParams for Featurizer.
 * 
 * Note: Do not use this class, internal use only!
 */
interface TrainingParams extends BasicTrainingParams {

  @ParameterDescription(valueName = "dictionaryPath", description = "The XML tag dictionary file")
  @OptionalParameter
  File getDict();
  
  @ParameterDescription(valueName = "factoryName", description = "A sub-class of POSTaggerFactory where to get implementation and resources.")
  @OptionalParameter
  String getFactory();
  
  @ParameterDescription(valueName = "cgFlags", description = "The Context Generator flags.")
  @OptionalParameter
  String getCGFlags();
}
