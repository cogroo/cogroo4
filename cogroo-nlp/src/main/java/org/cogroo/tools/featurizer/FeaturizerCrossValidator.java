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

import java.io.IOException;

import org.cogroo.dictionary.FeatureDictionaryI;

import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.CrossValidationPartitioner;
import opennlp.tools.util.eval.Mean;

public class FeaturizerCrossValidator {

  private final String languageCode;
  private final TrainingParameters params;

  private Mean wordAccuracy = new Mean();
  private FeaturizerEvaluationMonitor[] listeners;
  private FeatureDictionaryI posDict;
  private String factoryClassName;
  private FeaturizerFactory factory;

  public FeaturizerCrossValidator(String languageCode,
      TrainingParameters params, FeatureDictionaryI dict,
      String factoryClass, FeaturizerEvaluationMonitor... listeners) {

    this.languageCode = languageCode;
    this.params = params;
    this.listeners = listeners;
    this.posDict = dict;
    this.factoryClassName = factoryClass;
  }

  /**
   * Starts the evaluation.
   * 
   * @param samples
   *          the data to train and test
   * @param nFolds
   *          number of folds
   * 
   * @throws IOException
   */
  public void evaluate(ObjectStream<FeatureSample> samples, int nFolds)
      throws IOException, InvalidFormatException, IOException {
    CrossValidationPartitioner<FeatureSample> partitioner = new CrossValidationPartitioner<FeatureSample>(
        samples, nFolds);

    while (partitioner.hasNext()) {

      CrossValidationPartitioner.TrainingSampleStream<FeatureSample> trainingSampleStream = partitioner
          .next();
      
      if (this.factory == null) {
        this.factory = FeaturizerFactory.create(this.factoryClassName, posDict);
      }

      FeaturizerModel model = FeaturizerME.train(languageCode,
          trainingSampleStream, this.params, factory);

      // do testing
      FeaturizerEvaluator evaluator = new FeaturizerEvaluator(new FeaturizerME(
          model, FeaturizerME.DEFAULT_BEAM_SIZE), listeners);

      evaluator.evaluate(trainingSampleStream.getTestSampleStream());

      wordAccuracy.add(evaluator.getWordAccuracy(), evaluator.getWordCount());
    }
  }

  /**
   * Retrieves the accuracy for all iterations.
   * 
   * @return the word accuracy
   */
  public double getWordAccuracy() {
    return wordAccuracy.mean();
  }

  /**
   * Retrieves the number of words which where validated over all iterations.
   * The result is the amount of folds multiplied by the total number of words.
   * 
   * @return the word count
   */
  public long getWordCount() {
    return wordAccuracy.count();
  }
}
