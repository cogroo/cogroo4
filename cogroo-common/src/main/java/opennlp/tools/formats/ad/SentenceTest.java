package opennlp.tools.formats.ad;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;



public class SentenceTest {
  public static void main(String[] a) throws Exception {
    ADTokenSampleStreamFactory factory = new ADTokenSampleStreamFactory(
        ADTokenSampleStreamFactory.Parameters.class);
    
    File dict = new File("/Users/wcolen/Documents/wrks/opennlp/opennlp/opennlp-tools/lang/pt/tokenizer/pt-detokenizer.xml");
    File data = new File("/Users/wcolen/Documents/wrks/corpus/Bosque/Bosque_CF_8.0.ad.txt");
    String[] args = {"-data", data.getCanonicalPath(), "-encoding", "ISO-8859-1", "-lang", "pt", "-detokenizer", dict.getCanonicalPath()};
    ObjectStream<TokenSample> tokenSampleStream = factory.create(args);

    TokenSample sample = tokenSampleStream.read();
    BufferedWriter fromNameSample = new BufferedWriter(new FileWriter("fromNameSample.txt"));
    while (sample != null) {
      fromNameSample.append(sample.getText().toLowerCase() + "\n" );
      sample = tokenSampleStream.read();
    }
    fromNameSample.close();
    
    
    
    FileInputStream sampleDataIn = new FileInputStream(data);

    ObjectStream<SentenceSample> sampleStream = new ADSentenceSampleStream(
        new PlainTextByLineStream(sampleDataIn.getChannel(), "ISO-8859-1"), true);
    
    SentenceSample sentSample = sampleStream.read();
    BufferedWriter fromSentence = new BufferedWriter(new FileWriter("fromSentence.txt"));
    while (sentSample != null) {
      String[] sentences = Span.spansToStrings(sentSample.getSentences(), sentSample.getDocument());
      for (String string : sentences) {
        fromSentence.append(string.toLowerCase() + "\n");
      }
      sentSample = sampleStream.read();
    }
    fromSentence.close();
  }
}
