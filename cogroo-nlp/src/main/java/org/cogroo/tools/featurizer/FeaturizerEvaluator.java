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

package org.cogroo.tools.featurizer;

import opennlp.tools.util.eval.Evaluator;
import opennlp.tools.util.eval.Mean;

/**
 * The {@link FeaturizerEvaluator} measures the performance of the given
 * {@link Featurizer} with the provided reference {@link FeatureSample}s.
 * 
 * @see Evaluator
 * @see Featurizer
 * @see FeatureSample
 */
public class FeaturizerEvaluator extends Evaluator<FeatureSample> {

  private Mean wordAccuracy = new Mean();

  /**
   * The {@link Featurizer} used to create the predicted {@link FeatureSample}
   * objects.
   */
  private Featurizer featurizer;

  /**
   * Initializes the current instance with the given {@link Featurizer}.
   * 
   * @param featurizer
   *          the {@link Featurizer} to evaluate.
   * @param listeners
   *          evaluation listeners
   */
  public FeaturizerEvaluator(Featurizer featurizer,
      FeaturizerEvaluationMonitor... listeners) {
    super(listeners);
    this.featurizer = featurizer;
  }

  /**
   * Evaluates the given reference {@link FeatureSample} object.
   * 
   * This is done by finding the phrases with the {@link Featurizer} in the
   * sentence from the reference {@link FeatureSample}. The found phrases are
   * then used to calculate and update the scores.
   * 
   * @param reference
   *          the reference {@link FeatureSample}.
   * 
   * @return the predicted sample
   */
  @Override
  protected FeatureSample processSample(FeatureSample reference) {
    String[] predictedFeatures = featurizer.featurize(reference.getSentence(),
        reference.getTags());
    String[] referenceTags = reference.getFeatures();

    for (int i = 0; i < referenceTags.length; i++) {
      if (referenceTags[i].equals(predictedFeatures[i])) {
        wordAccuracy.add(1);
      } else {
        wordAccuracy.add(0);
      }
    }

    FeatureSample result = new FeatureSample(reference.getSentence(), reference.getLemmas(),
        reference.getTags(), predictedFeatures);

    return result;
  }

  /**
   * Retrieves the word accuracy.
   * 
   * This is defined as: word accuracy = correctly detected tags / total words
   * 
   * @return the word accuracy
   */
  public double getWordAccuracy() {
    return wordAccuracy.mean();
  }

  /**
   * Retrieves the total number of words considered in the evaluation.
   * 
   * @return the word count
   */
  public long getWordCount() {
    return wordAccuracy.count();
  }
}
