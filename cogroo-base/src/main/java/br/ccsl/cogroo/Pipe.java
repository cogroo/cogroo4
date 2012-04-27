package br.ccsl.cogroo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.google.common.io.Closeables;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

public class Pipe {

  protected static final Logger LOGGER = Logger.getLogger(Pipe.class);
  private SentenceDetectorME sentenceDetector;

  public Pipe() throws FileNotFoundException {
    InputStream modelIn = new FileInputStream("models/pt-sent.bin");

    try {
      SentenceModel model = new SentenceModel(modelIn);
      sentenceDetector = new SentenceDetectorME(model);
    } catch (IOException e) {
      LOGGER.fatal("Couldn't load sentence model!", e);
    } finally {
      Closeables.closeQuietly(modelIn);
    }

  }

  public Span[] analyze(String text) {
    Span sentences[] = sentenceDetector.sentPosDetect(text);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Found sentences: "
          + Arrays.toString(Span.spansToStrings(sentences, text)));
    }

    return sentences;
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
