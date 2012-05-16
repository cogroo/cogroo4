package br.ccsl.cogroo.analyzer;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.apache.log4j.Logger;

import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.util.TextUtils;

public class Pipe implements Analyzer {

  protected static final Logger LOGGER = Logger.getLogger(Pipe.class);
  private List<Analyzer> mChildAnalyzers = new ArrayList<Analyzer>();

  public void add(Analyzer aAnalyzer) {
    mChildAnalyzers.add(aAnalyzer);
  }

  public void analyze(Document document) {

    for (Analyzer analyzer : mChildAnalyzers) {
      analyzer.analyze(document);
    }

    System.out.println(TextUtils.nicePrint(document));
  }

  /**
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {
    long start = System.nanoTime();
    Pipe pipe = new Pipe();
    OpenNLPComponentFactory factory = OpenNLPComponentFactory.create(new Locale("pt_BR"));
    
    pipe.add(factory.createSentenceDetector());
    pipe.add(factory.createTokenizer());
    pipe.add(factory.createNameFinder());
    pipe.add(factory.createContractionFinder());
    pipe.add(factory.createPOSTagger());

    System.out.println("Loading time ["
        + ((System.nanoTime() - start) / 1000000) + "ms]");
    Scanner kb = new Scanner(System.in);
    System.out.print("Enter the sentence: ");
    String input = kb.nextLine();
    while (!input.equals("q")) {
      if (input.equals("0")) {
        input = "Fomos levados à crer que os menino são burro de doer. As menina chegaram.";
      }

      Document document = new Document();
      document.setText(input);
      pipe.analyze(document);

      System.out.print("Enter the sentence: ");
      input = kb.nextLine();
    }
  }
}
