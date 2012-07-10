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
package org.cogroo.tools.tokenizer;

import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.tokenize.TokenContextGenerator;
import opennlp.tools.tokenize.TokenizerFactory;
import opennlp.tools.util.model.ArtifactProvider;

public class PortugueseTokenizerFactory extends TokenizerFactory {

  public PortugueseTokenizerFactory(ArtifactProvider artifactProvider) {
    super(artifactProvider);
  }

  public PortugueseTokenizerFactory(String languageCode,
      Dictionary abbreviationDictionary, boolean useAlphaNumericOptimization,
      Pattern alphaNumericPattern) {
    super(languageCode, abbreviationDictionary, useAlphaNumericOptimization,
        alphaNumericPattern);
  }

  @Override
  public TokenContextGenerator getContextGenerator() {
    Dictionary dic = this.getAbbreviationDictionary();
    Set<String> abbSet;
    if (dic != null) {
      abbSet = dic.asStringSet();
    } else {
      abbSet = Collections.emptySet();
    }

    TokenContextGenerator cg = new PortugueseTokenContextGenerator(abbSet);
    return cg;
  }

}
