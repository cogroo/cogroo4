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
package org.cogroo.formats;

import java.io.FileInputStream;

import opennlp.tools.cmdline.ArgumentParser;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.StreamFactoryRegistry;
import opennlp.tools.cmdline.params.BasicFormatParams;
import opennlp.tools.formats.AbstractSampleStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

import org.cogroo.tools.featurizer.FeatureSample;
import org.cogroo.tools.featurizer.FeatureSampleStream;

/**
 * Factory producing OpenNLP {@link FeatureSampleStream}s.
 */
public class FeatureSampleStreamFactory extends
    AbstractSampleStreamFactory<FeatureSample> {

  interface Parameters extends BasicFormatParams {
  }

  public static void registerFactory() {
    StreamFactoryRegistry.registerFactory(FeatureSample.class,
        StreamFactoryRegistry.DEFAULT_FORMAT, new FeatureSampleStreamFactory(
            Parameters.class));
  }

  protected <P> FeatureSampleStreamFactory(Class<P> params) {
    super(params);
  }

  public ObjectStream<FeatureSample> create(String[] args) {
    Parameters params = ArgumentParser.parse(args, Parameters.class);

    CmdLineUtil.checkInputFile("Data", params.getData());
    FileInputStream sampleDataIn = CmdLineUtil.openInFile(params.getData());

    ObjectStream<String> lineStream = new PlainTextByLineStream(
        sampleDataIn.getChannel(), params.getEncoding());

    return new FeatureSampleStream(lineStream);
  }
}