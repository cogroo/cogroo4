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

package br.ccsl.cogroo.gc.cmdline.dictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.cmdline.AbstractBasicCmdLineTool;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.TerminateToolException;
import opennlp.tools.postag.ExtendedPOSDictionary;
import opennlp.tools.postag.MyPOSDictionary;
import br.ccsl.cogroo.formats.ad.ADFeaturizerSampleStream;
import br.ccsl.cogroo.interpreters.FlorestaTagInterpreter;
import br.ccsl.cogroo.interpreters.JspellTagInterpreter;
import br.ccsl.cogroo.tools.featurizer.FeatureSample;

public class POSDictionaryBuilderTool extends AbstractBasicCmdLineTool {

  interface Params extends POSDictionaryBuilderParams {
  }

  public String getShortDescription() {
    return "builds a new POS Tag dictionary";
  }

  public String getHelp() {
    return getBasicHelp(Params.class);
  }

  public void run(String[] args) {
    Params params = validateAndParseParams(args, Params.class);

    File dictInFile = params.getInputFile();
    File dictOutFile = params.getOutputFile();
    File corpusFile = params.getCorpus();
    Charset encoding = params.getEncoding();

    CmdLineUtil.checkInputFile("dictionary input file", dictInFile);
    CmdLineUtil.checkOutputFile("dictionary output file", dictOutFile);
    CmdLineUtil.checkInputFile("corpus input file", corpusFile);

    InputStreamReader in = null;
    OutputStream out = null;
    try {

      // load corpus tags

      ADFeaturizerSampleStream sentenceStream = new ADFeaturizerSampleStream(
          new FileInputStream(corpusFile), "ISO-8859-1", false);
      Set<String> knownFeats = new HashSet<String>();
      Set<String> knownPostags = new HashSet<String>();
      FeatureSample sample = sentenceStream.read();
      while (sample != null) {
        Collections.addAll(knownFeats, sample.getFeatures());
        Collections.addAll(knownPostags, sample.getTags());
        sample = sentenceStream.read();
      }

      in = new InputStreamReader(new FileInputStream(dictInFile), encoding);
      out = new FileOutputStream(dictOutFile);

      ExtendedPOSDictionary dict = MyPOSDictionary.parseOneEntryPerLine(in,
          new JspellTagInterpreter(), new FlorestaTagInterpreter(), knownFeats,
          knownPostags, params.getAllowInvalidFeats());

      dict.serialize(out);

    } catch (IOException e) {
      throw new TerminateToolException(-1,
          "IO error while reading training data or indexing data: "
              + e.getMessage());
    } finally {
      try {
        in.close();
        out.close();
      } catch (IOException e) {
        // sorry that this can fail
      }
    }

  }

}
