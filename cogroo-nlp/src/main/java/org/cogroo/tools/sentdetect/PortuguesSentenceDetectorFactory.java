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
package org.cogroo.tools.sentdetect;

import java.util.Collections;
import java.util.Set;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.sentdetect.SDContextGenerator;
import opennlp.tools.sentdetect.SentenceDetectorFactory;
import opennlp.tools.sentdetect.lang.Factory;
import opennlp.tools.util.model.ArtifactProvider;

public class PortuguesSentenceDetectorFactory extends SentenceDetectorFactory {

  private char[] eos;

  public PortuguesSentenceDetectorFactory(ArtifactProvider artifactProvider) {
    super(artifactProvider);
    this.eos = super.getEOSCharacters();
  }

  public PortuguesSentenceDetectorFactory(String languageCode,
      boolean useTokenEnd, Dictionary abbreviationDictionary,
      char[] eosCharacters) {
    super(languageCode, useTokenEnd, abbreviationDictionary, eosCharacters);
    if (eosCharacters != null) {
      this.eos = eosCharacters;
    } else {
      this.eos = Factory.ptEosCharacters;
    }
  }

  public PortuguesSentenceDetectorFactory() {
  }

  @Override
  public char[] getEOSCharacters() {
    return eos;
  }

  @Override
  public SDContextGenerator getSDContextGenerator() {
    Set<String> abb = null;
    Dictionary abbDic = getAbbreviationDictionary();
    if (abbDic != null) {
      abb = abbDic.asStringSet();
    } else {
      abb = Collections.emptySet();
    }
    return new PortugueseSDContextGenerator(abb, getEOSCharacters());
  }
}
