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
package org.cogroo.cmdline.featurizer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.cogroo.cmdline.featurizer.FeaturizerEvaluatorTool.EvalToolParams;
import org.cogroo.tools.featurizer.FeatureSample;
import org.cogroo.tools.featurizer.FeaturizerEvaluationMonitor;
import org.cogroo.tools.featurizer.FeaturizerEvaluator;
import org.cogroo.tools.featurizer.FeaturizerME;
import org.cogroo.tools.featurizer.FeaturizerModel;

import opennlp.tools.cmdline.AbstractEvaluatorTool;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.TerminateToolException;
import opennlp.tools.cmdline.params.DetailedFMeasureEvaluatorParams;
import opennlp.tools.cmdline.params.EvaluatorParams;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.eval.EvaluationMonitor;

public final class FeaturizerEvaluatorTool extends
    AbstractEvaluatorTool<FeatureSample, EvalToolParams> {

  interface EvalToolParams extends EvaluatorParams,
      DetailedFMeasureEvaluatorParams {
  }

  public FeaturizerEvaluatorTool() {
    super(FeatureSample.class, EvalToolParams.class);
  }

  public String getShortDescription() {
    return "Measures the performance of the Chunker model with the reference data";
  }

  public void run(String format, String[] args) {
    super.run(format, args);

    FeaturizerModel model = new FeaturizerModelLoader().load(params.getModel());

    List<EvaluationMonitor<FeatureSample>> listeners = new LinkedList<EvaluationMonitor<FeatureSample>>();
    if (params.getMisclassified()) {
      listeners.add(new FeaturizerEvaluationErrorListener());
    }

    FeaturizerEvaluator evaluator = new FeaturizerEvaluator(new FeaturizerME(
        model, FeaturizerME.DEFAULT_BEAM_SIZE),
        listeners.toArray(new FeaturizerEvaluationMonitor[listeners.size()]));

    final PerformanceMonitor monitor = new PerformanceMonitor("sent");

    ObjectStream<FeatureSample> measuredSampleStream = new ObjectStream<FeatureSample>() {

      public FeatureSample read() throws IOException {
        monitor.incrementCounter();
        return sampleStream.read();
      }

      public void reset() throws IOException {
        sampleStream.reset();
      }

      public void close() throws IOException {
        sampleStream.close();
      }
    };

    monitor.startAndPrintThroughput();

    try {
      evaluator.evaluate(measuredSampleStream);
    } catch (IOException e) {
      System.err.println("failed");
      throw new TerminateToolException(-1, "IO error while reading test data: "
          + e.getMessage());
    } finally {
      try {
        measuredSampleStream.close();
      } catch (IOException e) {
        // sorry that this can fail
      }
    }

    monitor.stopAndPrintFinalResult();

    System.out.println();

    System.out.println(evaluator.getWordAccuracy());
  }
}
