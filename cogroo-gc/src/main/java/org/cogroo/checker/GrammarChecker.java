package org.cogroo.checker;

import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.analyzer.Pipe;


/**
 * 
 */
public class GrammarChecker {
  /**
   * @param args
   *          the language to be used, "pt_BR" by default
   * @throws IOException
   * @throws IllegalArgumentException
   */
  public static void main(String[] args) throws IllegalArgumentException,
      IOException {

    long start = System.nanoTime();

    if (args.length != 1) {
      System.err.println("Language is missing! usage: CLI pt_br");
      return;
    }

    ComponentFactory factory = ComponentFactory.create(new Locale("pt", "BR"));

    Pipe pipe = (Pipe) factory.createPipe();
    pipe.add(new GrammarCheckerAnalyzer());

    System.out.println("Loading time ["
        + ((System.nanoTime() - start) / 1000000) + "ms]");
    Scanner kb = new Scanner(System.in);
    System.out.print("Enter the sentence: ");
    String input = kb.nextLine();

    while (!input.equals("q")) {
      if (input.equals("0")) {
        input = "O casaco feios ficou pronto. Os casacos bonito ficaram prontos.";
      }

      CheckDocument document = new CheckDocument();
      document.setText(input);
      pipe.analyze(document);
      
      System.out.println(document);

      System.out.print("Enter the sentence: ");
      input = kb.nextLine();
    }
  }
}
