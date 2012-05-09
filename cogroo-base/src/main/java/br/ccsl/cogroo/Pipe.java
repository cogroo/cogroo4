package br.ccsl.cogroo;

import java.io.FileNotFoundException;
import java.util.Scanner;

import org.apache.log4j.Logger;

import br.ccsl.cogroo.analyzer.Analyzer;
import br.ccsl.cogroo.analyzer.ContractionFinder;
import br.ccsl.cogroo.analyzer.NameFinder;
import br.ccsl.cogroo.analyzer.SentenceDetector;
import br.ccsl.cogroo.analyzer.POSTagger;
import br.ccsl.cogroo.analyzer.Tokenizer;
import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.util.TextUtils;

public class Pipe {

  protected static final Logger LOGGER = Logger.getLogger(Pipe.class);
  private Analyzer  sentenceDetector, 
                    tokenizer, 
                    tagger, 
                    names, 
                    contractions;

  public Pipe() throws FileNotFoundException {

    sentenceDetector = new SentenceDetector();
    tokenizer = new Tokenizer();
    tagger = new POSTagger();
    names = new NameFinder();
    contractions = new ContractionFinder();

  }

  public void analyze(String text) {
    Document document = new Document();
    document.setText(text);

    sentenceDetector.analyze(document);
    tokenizer.analyze(document);
    names.analyze(document);
    contractions.analyze(document);
    tagger.analyze(document);

    System.out.println(TextUtils.nicePrint(document));
  }

  /**
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {

    long start = System.nanoTime();
    Pipe pipe = new Pipe();

    System.out.println("Loading time ["
        + ((System.nanoTime() - start) / 1000000) + "ms]");
    Scanner kb = new Scanner(System.in);
    System.out.print("Enter the sentence: ");
    String input = kb.nextLine();
    while (!input.equals("q")) {
      if (input.equals("0")) {
        input = "Fomos levados à crer que os menino são burro de doer. As menina chegaram.";
      }

      pipe.analyze(input);

      System.out.print("Enter the sentence: ");
      input = kb.nextLine();
    }
  }
}
