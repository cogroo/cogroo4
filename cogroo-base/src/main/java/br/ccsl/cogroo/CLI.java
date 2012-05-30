package br.ccsl.cogroo;

import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;

import br.ccsl.cogroo.analyzer.AnalyzerI;
import br.ccsl.cogroo.analyzer.ComponentFactory;
import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.impl.DocumentImpl;
import br.ccsl.cogroo.util.TextUtils;

/**
 * 
 */
public class CLI {
  /**
   * @param args
   *          the language to be used, "pt_BR" by default
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {

    long start = System.nanoTime();

    if (args.length != 1) {
      System.err.println("Language is missing! usage: CLI pt_br");
      return;
    }

    ComponentFactory factory = ComponentFactory.create(new Locale("pt", "BR"));

    AnalyzerI pipe = factory.createPipe();

    System.out.println("Loading time ["
        + ((System.nanoTime() - start) / 1000000) + "ms]");
    Scanner kb = new Scanner(System.in);
    System.out.print("Enter the sentence: ");
    String input = kb.nextLine();

    while (!input.equals("q")) {
      if (input.equals("0")) {
        input = "Fomos levados à crer que os menino são burro de doer. As menina chegaram.";
      }

      Document document = new DocumentImpl();
      document.setText(input);
      pipe.analyze(document);
      
      System.out.println(TextUtils.nicePrint(document));
      
      System.out.print("Enter the sentence: ");
      input = kb.nextLine();
    }
  }
}
