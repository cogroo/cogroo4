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
package org.cogroo.tools.featurizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.FilterObjectStream;
import opennlp.tools.util.ObjectStream;

/**
 * Parses a featurizer training data.
 */
public class FeatureSampleStream extends
    FilterObjectStream<String, FeatureSample> {

  /**
   * Initializes the current instance.
   * 
   * @param samples
   *          a plain text line stream
   */
  public FeatureSampleStream(ObjectStream<String> samples) {
    super(samples);
  }

  public FeatureSample read() throws IOException {

    List<String> toks = new ArrayList<String>();
    List<String> lemmas = new ArrayList<String>();
    List<String> tags = new ArrayList<String>();
    List<String> preds = new ArrayList<String>();

    for (String line = samples.read(); line != null && !line.equals(""); line = samples
        .read()) {
      String[] parts = line.split(" ");
      if (parts.length != 3) {
        System.err.println("Skipping corrupt line: " + line);
      } else {
        toks.add(parts[0]);
        lemmas.add(parts[0]); // no lemma info for now
        tags.add(parts[1]);
        preds.add(parts[2]);
        
      }
    }

    if (toks.size() > 0) {
      return new FeatureSample(toks, lemmas, tags, preds);
    } else {
      return null;
    }
  }
}
