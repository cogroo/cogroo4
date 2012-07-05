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

package br.ccsl.cogroo.cmdline.featurizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import opennlp.tools.cmdline.AbstractCrossValidatorTool;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.TerminateToolException;
import opennlp.tools.cmdline.params.CVParams;
import opennlp.tools.cmdline.params.DetailedFMeasureEvaluatorParams;
import opennlp.tools.postag.ExtendedPOSDictionary;
import opennlp.tools.util.eval.EvaluationMonitor;
import opennlp.tools.util.model.ModelUtil;
import br.ccsl.cogroo.cmdline.featurizer.FeaturizerCrossValidatorTool.CVToolParams;
import br.ccsl.cogroo.dictionary.FeatureDictionaryI;
import br.ccsl.cogroo.tools.featurizer.FeatureSample;
import br.ccsl.cogroo.tools.featurizer.FeaturizerCrossValidator;
import br.ccsl.cogroo.tools.featurizer.FeaturizerEvaluationMonitor;

public final class FeaturizerCrossValidatorTool extends
    AbstractCrossValidatorTool<FeatureSample, CVToolParams> {

  interface CVToolParams extends TrainingParams, CVParams,
      DetailedFMeasureEvaluatorParams {
  }

  public FeaturizerCrossValidatorTool() {
    super(FeatureSample.class, CVToolParams.class);
  }

  public String getShortDescription() {
    return "K-fold cross validator for the featurizer";
  }

  public void run(String format, String[] args) {
    super.run(format, args);

    mlParams = CmdLineUtil.loadTrainingParameters(params.getParams(), false);
    if (mlParams == null) {
      mlParams = ModelUtil.createTrainingParameters(params.getIterations(),
          params.getCutoff());
    }

    List<EvaluationMonitor<FeatureSample>> listeners = new LinkedList<EvaluationMonitor<FeatureSample>>();
    if (params.getMisclassified()) {
      listeners.add(new FeaturizerEvaluationErrorListener());
    }

    FeaturizerCrossValidator validator;
    try {
      FeatureDictionaryI tagdict = null;
      if (params.getDict() != null) {
        long start = System.nanoTime();
        tagdict = ExtendedPOSDictionary.create(new FileInputStream(params
            .getDict()));
        System.out.println("ExtendedPOSDictionary loaded in "
            + (System.nanoTime() - start) / 1000000 + "ms");
      }

      String factoryName = params.getFactory();

      validator = new FeaturizerCrossValidator(factory.getLang(), mlParams,
          tagdict, factoryName,
          listeners.toArray(new FeaturizerEvaluationMonitor[listeners.size()]));

      validator.evaluate(sampleStream, params.getFolds());
    } catch (IOException e) {
      throw new TerminateToolException(-1,
          "IO error while reading training data or indexing data: "
              + e.getMessage());
    } finally {
      try {
        sampleStream.close();
      } catch (IOException e) {
        // sorry that this can fail
      }
    }

    System.out.println();

    System.out.println("Accuracy: " + validator.getWordAccuracy());
  }
}
