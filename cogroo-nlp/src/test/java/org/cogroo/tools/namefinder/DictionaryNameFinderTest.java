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
package org.cogroo.tools.namefinder;

import java.io.File;
import java.io.FileInputStream;

import org.cogroo.formats.ad.ADContractionNameSampleStream;

import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.namefind.NameEvaluationErrorListener;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.namefind.DictionaryNameFinder;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.TokenNameFinderEvaluator;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class DictionaryNameFinderTest {
  
  public static void main(String[] args) throws Exception {
    DictionaryNameFinder nameFinder = new DictionaryNameFinder(
        createDictionary());
    TokenNameFinderEvaluator evaluator = new TokenNameFinderEvaluator(
        nameFinder, new NameEvaluationErrorListener());
    ObjectStream<NameSample> sample = createSample();

    evaluator.evaluate(sample);
    sample.close();

    System.out.println(evaluator.getFMeasure());
  }

  private static ObjectStream<NameSample> createSample() throws Exception {
    InputStreamFactory sampleDataIn = CmdLineUtil.createInputStreamFactory(new File("/Users/wcolen/Documents/wrks/corpus/Bosque/Bosque_CF_8.0.ad.txt"));

    return new ADContractionNameSampleStream(new PlainTextByLineStream(
        sampleDataIn, "ISO-8859-1"), null);
  }

  private static Dictionary createDictionary() throws Exception {
    FileInputStream sampleDataIn = new FileInputStream(new File("/Users/wcolen/Documents/wrks/cogroo4/cogroo4/cogroo-dict/target/contractionRes/cont.dictionary"));
    return new Dictionary(sampleDataIn);
  }

}
