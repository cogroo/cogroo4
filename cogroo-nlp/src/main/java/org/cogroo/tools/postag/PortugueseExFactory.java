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
package org.cogroo.tools.postag;

import opennlp.model.AbstractModel;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.postag.POSContextGenerator;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.postag.TagDictionary;
import opennlp.tools.util.InvalidFormatException;

public class PortugueseExFactory extends PortugueseFactory {

  public PortugueseExFactory() {
    super();
  }
  
  public PortugueseExFactory(Dictionary ngramDictionary,
      TagDictionary posDictionary) {
    super(ngramDictionary, posDictionary);
  }

  @Override
  public POSContextGenerator getPOSContextGenerator(int cacheSize) {
    return new PortugueseExtPOSContextGenerator(cacheSize, getDictionary());
  }

  @Override
  public POSContextGenerator getPOSContextGenerator() {
    return new PortugueseExtPOSContextGenerator(getDictionary());
  }
  
  @Override
  protected void validatePOSDictionary(POSDictionary posDict,
      AbstractModel posModel) throws InvalidFormatException {
  }
  
}
