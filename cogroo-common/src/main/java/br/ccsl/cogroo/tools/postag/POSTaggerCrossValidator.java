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

package br.ccsl.cogroo.tools.postag;

import java.io.IOException;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.postag.MyPOSDictionary;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.postag.POSEvaluator;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerEvaluationMonitor;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.CrossValidationPartitioner;
import opennlp.tools.util.eval.CrossValidationPartitioner.TrainingSampleStream;
import opennlp.tools.util.eval.Mean;

public class POSTaggerCrossValidator {

  private final String languageCode;
  
  private final TrainingParameters params;
  
  private POSDictionary tagDictionary;
  private Dictionary ngramDictionary;
  private Integer ngramCutoff;

  private Mean wordAccuracy = new Mean();
  private POSTaggerEvaluationMonitor[] listeners;

  private boolean extendDictionary;
  
  public POSTaggerCrossValidator(String languageCode,
      TrainingParameters trainParam, POSDictionary tagDictionary,
      Integer ngramCutoff, boolean extendDictionary, POSTaggerEvaluationMonitor... listeners) {
    this.languageCode = languageCode;
    this.params = trainParam;
    this.tagDictionary = tagDictionary;
    this.ngramDictionary = null;
    this.ngramCutoff = ngramCutoff;
    this.listeners = listeners;
    this.extendDictionary = extendDictionary;
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
  public void evaluate(ObjectStream<POSSample> samples, int nFolds) throws IOException {
    
    CrossValidationPartitioner<POSSample> partitioner = new CrossValidationPartitioner<POSSample>(
        samples, nFolds);

    while (partitioner.hasNext()) {

      CrossValidationPartitioner.TrainingSampleStream<POSSample> trainingSampleStream = partitioner
          .next();
      
      Dictionary ngramDict = null;
      if (this.ngramDictionary == null) {
        if(this.ngramCutoff != null) {
          System.err.print("Building ngram dictionary ... ");
          ngramDict = POSTaggerME.buildNGramDictionary(trainingSampleStream,
              this.ngramCutoff);
          trainingSampleStream.reset();
          System.err.println("done");
        }
      } else {
        ngramDict = this.ngramDictionary;
      }
      
      POSDictionary dict = this.tagDictionary;
      
      if(dict != null && this.extendDictionary) {
        // need to clone the dictionary and populate the copy...
        System.err.print("Extending tag dictionary ... ");
        MyPOSDictionary extDict = MyPOSDictionary.createCopy(this.tagDictionary);
        populateDict(extDict, trainingSampleStream);
        trainingSampleStream.reset();
        System.err.print("done");
        dict = extDict;
      }
      
      PortugueseFactory f = new PortugueseFactory(ngramDict, dict);

      POSModel model = POSTaggerME.train(languageCode, trainingSampleStream, params, f,
          dict, ngramDict);

      POSEvaluator evaluator = new POSEvaluator(new POSTaggerME(model), listeners);
      
      evaluator.evaluate(trainingSampleStream.getTestSampleStream());

      wordAccuracy.add(evaluator.getWordAccuracy(), evaluator.getWordCount());
    }
  }
  
  private void populateDict(MyPOSDictionary extDict,
      TrainingSampleStream<POSSample> trainingSampleStream) throws IOException {
    
    POSSample sample = trainingSampleStream.read();
    while(sample != null) {
      //System.err.println("# process new sentence: " + sample.toString());
      for(int i = 0; i < sample.getSentence().length; i++) {
        extDict.addTag(sample.getSentence()[i], sample.getTags()[i]);
      }
      sample = trainingSampleStream.read();
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
   * Retrieves the number of words which where validated
   * over all iterations. The result is the amount of folds
   * multiplied by the total number of words.
   * 
   * @return the word count
   */
  public long getWordCount() {
    return wordAccuracy.count();
  }
}
