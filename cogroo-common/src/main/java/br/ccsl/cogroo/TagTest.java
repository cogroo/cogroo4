package br.ccsl.cogroo;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.SortedSet;
import java.util.TreeSet;

import opennlp.tools.postag.POSSample;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import br.ccsl.cogroo.formats.ad.ADExPOSSampleStream;

public class TagTest {

  public static void main(String[] args) throws Exception {

    InputStream in = new FileInputStream(
        "/Users/wcolen/Documents/wrks/corpus/Bosque/Bosque_CF_8.0.ad.txt");
        //"/Users/wcolen/Documents/wrks/corpus/FlorestaVirgem/FlorestaVirgem_CF_3.0_ad.txt");

    String encoding = "ISO-8859-1";

    ObjectStream<POSSample> sampleStream = new ADExPOSSampleStream(
        new PlainTextByLineStream(new InputStreamReader(in, encoding)), false,
        true, false);
    
    SortedSet<String> tags = new TreeSet<String>();
    POSSample sample = sampleStream.read();
    while(sample != null) {
      for (String tag : sample.getTags()) {
        tags.add(tag);
      }
      sample = sampleStream.read();
    }
    
    for (String string : tags) {
      System.out.println(string);
    }
  }

}
