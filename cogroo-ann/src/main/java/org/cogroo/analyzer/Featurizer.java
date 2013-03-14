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
package org.cogroo.analyzer;

import java.util.List;

import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.Token;
import org.cogroo.tools.featurizer.FeaturizerME;
import org.cogroo.util.TextUtils;


public class Featurizer implements Analyzer {
  private FeaturizerME featurizer;

  public Featurizer(FeaturizerME featurizer) {
    this.featurizer = featurizer;
  }

  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();

    for (Sentence sentence : sentences) {
      List<Token> tokens = sentence.getTokens();

      String[] tags = new String[tokens.size()];

      for (int i = 0; i < tokens.size(); i++)
        tags[i] = tokens.get(i).getPOSTag();

      String[] features;

      synchronized (this.featurizer) {
        features = featurizer.featurize(TextUtils.tokensToString(tokens), tags);
      }

      for (int i = 0; i < features.length; i++)
        tokens.get(i).setFeatures(features[i]);

    }
  }
}
