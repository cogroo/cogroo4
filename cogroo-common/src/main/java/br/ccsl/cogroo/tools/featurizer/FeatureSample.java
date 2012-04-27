/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.ccsl.cogroo.tools.featurizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class for holding features for a single unit of text.
 */
public class FeatureSample {

  private final List<String> sentence;
  private final List<String> tags;
  private final List<String> feats;

  /**
   * Initializes the current instance.
   * 
   * @param sentence
   *          training sentence
   * @param tags
   *          POS Tags for the sentence
   * @param feats
   *          Feature tags
   */
  public FeatureSample(String[] sentence, String[] tags, String[] feats) {

    validateArguments(sentence.length, tags.length, feats.length);

    this.sentence = Collections.unmodifiableList(new ArrayList<String>(Arrays
        .asList(sentence)));
    this.tags = Collections.unmodifiableList(new ArrayList<String>(Arrays
        .asList(tags)));
    this.feats = Collections.unmodifiableList(new ArrayList<String>(Arrays
        .asList(feats)));
  }

  /**
   * Initializes the current instance.
   * 
   * @param sentence
   *          training sentence
   * @param tags
   *          POS Tags for the sentence
   * @param feats
   *          Feature tags
   */
  public FeatureSample(List<String> sentence, List<String> tags,
      List<String> feats) {

    validateArguments(sentence.size(), tags.size(), feats.size());

    this.sentence = Collections.unmodifiableList(new ArrayList<String>(
        (sentence)));
    this.tags = Collections.unmodifiableList(new ArrayList<String>((tags)));
    this.feats = Collections.unmodifiableList(new ArrayList<String>((feats)));
  }

  /** Gets the training sentence */
  public String[] getSentence() {
    return sentence.toArray(new String[sentence.size()]);
  }

  /** Gets the POS Tags for the sentence */
  public String[] getTags() {
    return tags.toArray(new String[tags.size()]);
  }

  /** Gets the feature tags */
  public String[] getFeatures() {
    return feats.toArray(new String[feats.size()]);
  }

  private static void validateArguments(int sentenceSize, int tagsSize,
      int featsSize) throws IllegalArgumentException {
    if (sentenceSize != tagsSize || tagsSize != featsSize)
      throw new IllegalArgumentException(
          "All arrays must have the same length!");
  }

  @Override
  public String toString() {

    StringBuilder featsString = new StringBuilder();

    for (int ci = 0; ci < feats.size(); ci++) {
      featsString.append(sentence.get(ci)).append(" ").append(tags.get(ci))
          .append(" ").append(feats.get(ci)).append("\n");
    }
    return featsString.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj instanceof FeatureSample) {
      FeatureSample a = (FeatureSample) obj;

      return Arrays.equals(getSentence(), a.getSentence())
          && Arrays.equals(getTags(), a.getTags())
          && Arrays.equals(getFeatures(), a.getFeatures());
    } else {
      return false;
    }
  }

}
