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

package br.ccsl.cogroo.formats.ad;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Set;

import opennlp.tools.cmdline.ArgumentParser;
import opennlp.tools.cmdline.ArgumentParser.ParameterDescription;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.StreamFactoryRegistry;
import opennlp.tools.formats.LanguageSampleStreamFactory;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

/**
 * A Factory to create a Arvores Deitadas NameSampleStream from the command line
 * utility. This outcomes the contractions.
 * 
 * <b>Note:</b> Do not use this class, internal use only!
 */
public class ADContractionNameSampleStreamFactory extends
    LanguageSampleStreamFactory<NameSample> {

  interface Parameters {
    @ParameterDescription(valueName = "charsetName", description = "encoding for reading and writing text, if absent the system default is used.")
    Charset getEncoding();

    @ParameterDescription(valueName = "sampleData", description = "data to be used, usually a file name.")
    File getData();

    @ParameterDescription(valueName = "language", description = "language which is being processed.")
    String getLang();
  }

  public static void registerFactory() {
    StreamFactoryRegistry.registerFactory(NameSample.class, "adcon",
        new ADContractionNameSampleStreamFactory(Parameters.class));
  }

  protected <P> ADContractionNameSampleStreamFactory(Class<P> params) {
    super(params);
  }

  public ObjectStream<NameSample> create(String[] args) {

    Parameters params = ArgumentParser.parse(args, Parameters.class);

    language = params.getLang();

    Set<String> tagSet = null;

    FileInputStream sampleDataIn = CmdLineUtil.openInFile(params.getData());

    ObjectStream<String> lineStream = new PlainTextByLineStream(
        sampleDataIn.getChannel(), params.getEncoding());


    return new ADContractionNameSampleStream(lineStream, tagSet);
  }
}
