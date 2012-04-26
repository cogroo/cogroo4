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
import java.nio.charset.Charset;

import opennlp.tools.chunker.ChunkSample;
import opennlp.tools.cmdline.ArgumentParser;
import opennlp.tools.cmdline.ArgumentParser.OptionalParameter;
import opennlp.tools.cmdline.ArgumentParser.ParameterDescription;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.StreamFactoryRegistry;
import opennlp.tools.formats.LanguageSampleStreamFactory;
import opennlp.tools.util.ObjectStream;

/**
 * A Factory to create a Arvores Deitadas ChunkStream from the command line
 * utility.
 * <p>
 * <b>Note:</b> Do not use this class, internal use only!
 */
public class ADChunkBasedShallowParserSampleStreamFactory extends
    LanguageSampleStreamFactory<ChunkSample> {

  interface Parameters {
    @ParameterDescription(valueName = "charsetName", description = "encoding for reading and writing text, if absent the system default is used.")
    Charset getEncoding();

    @ParameterDescription(valueName = "sampleData", description = "data to be used, usually a file name.")
    File getData();

    @ParameterDescription(valueName = "language", description = "language which is being processed.")
    String getLang();

    @ParameterDescription(valueName = "includePOS", description = "true if to include POS Tags. default is true")
    @OptionalParameter(defaultValue = "true")
    Boolean getIsIncludePOSTags();

    @ParameterDescription(valueName = "commaSepFunctTags", description = "comma separated functional tags")
    @OptionalParameter
    String getFunctTags();

    @ParameterDescription(valueName = "start", description = "index of first sentence")
    @OptionalParameter
    Integer getStart();

    @ParameterDescription(valueName = "end", description = "index of last sentence")
    @OptionalParameter
    Integer getEnd();
    
    @ParameterDescription(valueName = "cgTags", description = "use CG tags instead of floresta")
    @OptionalParameter(defaultValue = "false")
    Boolean getUseCGTags();
    
    @ParameterDescription(valueName = "expandME", description = "expand multiword expressions")
    @OptionalParameter(defaultValue = "false")
    Boolean getExpandME();
  }

  public static void registerFactory() {
    StreamFactoryRegistry.registerFactory(ChunkSample.class, "adshallowparser",
        new ADChunkBasedShallowParserSampleStreamFactory(Parameters.class));
  }

  protected <P> ADChunkBasedShallowParserSampleStreamFactory(Class<P> params) {
    super(params);
  }

  public ObjectStream<ChunkSample> create(String[] args) {

    Parameters params = ArgumentParser.parse(args, Parameters.class);

    language = params.getLang();

    Charset encoding = params.getEncoding();

    ADChunkBasedShallowParserSampleStream sampleStream = new ADChunkBasedShallowParserSampleStream(
        CmdLineUtil.openInFile(params.getData()), encoding.name(),
        params.getFunctTags(), params.getIsIncludePOSTags(), params.getUseCGTags(), params.getExpandME());

    if (params.getStart() != null && params.getStart() > -1) {
      sampleStream.setStart(params.getStart());
    }

    if (params.getEnd() != null && params.getEnd() > -1) {
      sampleStream.setEnd(params.getEnd());
    }

    return sampleStream;
  }
}
