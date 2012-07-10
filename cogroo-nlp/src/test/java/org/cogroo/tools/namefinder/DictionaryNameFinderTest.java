package org.cogroo.tools.namefinder;

import java.io.File;
import java.io.FileInputStream;

import org.cogroo.formats.ad.ADContractionNameSampleStream;

import opennlp.tools.cmdline.namefind.NameEvaluationErrorListener;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.namefind.DictionaryNameFinder;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.TokenNameFinderEvaluator;
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
    FileInputStream sampleDataIn = new FileInputStream(new File("/Users/wcolen/Documents/wrks/corpus/Bosque/Bosque_CF_8.0.ad.txt"));
    
    return new ADContractionNameSampleStream(new PlainTextByLineStream(
        sampleDataIn.getChannel(), "ISO-8859-1"), null);
  }

  private static Dictionary createDictionary() throws Exception {
    FileInputStream sampleDataIn = new FileInputStream(new File("/Users/wcolen/Documents/wrks/cogroo4/cogroo4/cogroo-dict/target/contractionRes/cont.dictionary"));
    return new Dictionary(sampleDataIn);
  }

}
