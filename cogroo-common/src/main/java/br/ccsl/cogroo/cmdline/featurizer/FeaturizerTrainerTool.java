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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import opennlp.tools.cmdline.AbstractTrainerTool;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.TerminateToolException;
import opennlp.tools.cmdline.params.TrainingToolParams;
import opennlp.tools.postag.ExtendedPOSDictionary;
import opennlp.tools.util.model.ModelUtil;
import br.ccsl.cogroo.cmdline.featurizer.FeaturizerTrainerTool.TrainerToolParams;
import br.ccsl.cogroo.tools.featurizer.DefaultFeaturizerContextGenerator;
import br.ccsl.cogroo.tools.featurizer.FeatureSample;
import br.ccsl.cogroo.tools.featurizer.FeaturizerME;
import br.ccsl.cogroo.tools.featurizer.FeaturizerModel;

public class FeaturizerTrainerTool extends
    AbstractTrainerTool<FeatureSample, TrainerToolParams> {

  interface TrainerToolParams extends TrainingParams, TrainingToolParams {
  }

  public FeaturizerTrainerTool() {
    super(FeatureSample.class, TrainerToolParams.class);
  }

  public String getName() {
    return "FeaturizerTrainerME";
  }

  public String getShortDescription() {
    return "trainer for the learnable Featurizer";
  }

  public void run(String format, String[] args) {
    super.run(format, args);

    mlParams = CmdLineUtil.loadTrainingParameters(params.getParams(), false);
    if (mlParams == null) {
      mlParams = ModelUtil.createTrainingParameters(params.getIterations(),
          params.getCutoff());
    }

    File modelOutFile = params.getModel();
    CmdLineUtil.checkOutputFile("sentence detector model", modelOutFile);

    FeaturizerModel model;
    try {
      ExtendedPOSDictionary tagdict = null;
      if (params.getDict() != null) {
        tagdict = ExtendedPOSDictionary.create(new FileInputStream(params
            .getDict()));
      }

      model = FeaturizerME.train(factory.getLang(), sampleStream,
          new DefaultFeaturizerContextGenerator(), mlParams, tagdict);

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

    CmdLineUtil.writeModel("Featurizer", modelOutFile, model);
  }
}
