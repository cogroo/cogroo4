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

package org.cogroo.cmdline.chunker2;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.cogroo.cmdline.chunker2.Chunker2CrossValidatorTool.CVToolParams;
import org.cogroo.tools.chunker2.ChunkSample;
import org.cogroo.tools.chunker2.ChunkerCrossValidator;
import org.cogroo.tools.chunker2.ChunkerEvaluationMonitor;
import org.cogroo.tools.chunker2.ChunkerFactory;

import opennlp.tools.cmdline.AbstractCrossValidatorTool;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.TerminateToolException;
import opennlp.tools.cmdline.params.CVParams;
import opennlp.tools.cmdline.params.DetailedFMeasureEvaluatorParams;
import opennlp.tools.util.eval.EvaluationMonitor;
import opennlp.tools.util.eval.FMeasure;
import opennlp.tools.util.model.ModelUtil;


public final class Chunker2CrossValidatorTool
    extends AbstractCrossValidatorTool<ChunkSample, CVToolParams> {
  
  interface CVToolParams extends TrainingParams, CVParams, DetailedFMeasureEvaluatorParams {
  }

  public Chunker2CrossValidatorTool() {
    super(ChunkSample.class, CVToolParams.class);
  }

  public String getShortDescription() {
    return "K-fold cross validator for the chunker";
  }
  
  public void run(String format, String[] args) {
    super.run(format, args);

    mlParams = CmdLineUtil.loadTrainingParameters(params.getParams(), false);
    if (mlParams == null) {
      mlParams = ModelUtil.createDefaultTrainingParameters();
    }

    List<EvaluationMonitor<ChunkSample>> listeners = new LinkedList<EvaluationMonitor<ChunkSample>>();
    ChunkerDetailedFMeasureListener detailedFMeasureListener = null;
    ChunkerDetailedFMeasureSizeListener detailedFMeasureForSizeListener = null;
    if (params.getMisclassified()) {
      listeners.add(new ChunkEvaluationErrorListener());
    }
    if (params.getDetailedF()) {
      detailedFMeasureListener = new ChunkerDetailedFMeasureListener();
      listeners.add(detailedFMeasureListener);

      detailedFMeasureForSizeListener = new ChunkerDetailedFMeasureSizeListener();
      listeners.add(detailedFMeasureForSizeListener);
    }

    ChunkerCrossValidator validator;

    try {
      ChunkerFactory chunkerFactory = ChunkerFactory.create(params.getFactory());


      validator = new ChunkerCrossValidator(params.getLang(), mlParams,
          chunkerFactory,
          listeners.toArray(new ChunkerEvaluationMonitor[listeners.size()]));
      validator.evaluate(sampleStream, params.getFolds());
    }
    catch (IOException e) {
      throw new TerminateToolException(-1, "IO error while reading training data or indexing data: " +
          e.getMessage(), e);
    }
    finally {
      try {
        sampleStream.close();
      } catch (IOException e) {
        // sorry that this can fail
      }
    }
    
    if (detailedFMeasureListener == null) {
      FMeasure result = validator.getFMeasure();
      System.out.println(result.toString());
    } else {
      System.out.println(detailedFMeasureListener.toString());
      System.out.println();
      System.out.println(detailedFMeasureForSizeListener.toString());
    }
  }
}
