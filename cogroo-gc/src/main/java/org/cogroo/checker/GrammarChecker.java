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
package org.cogroo.checker;

import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

import org.cogroo.analyzer.ComponentFactory;


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

    //long[] rules = {127};
//    pipe.add(new GrammarCheckerAnalyzer(true, rules));
    
    GrammarCheckerAnalyzer cogroo = new GrammarCheckerAnalyzer(factory.createPipe());

    System.out.println("Loading time ["
        + ((System.nanoTime() - start) / 1000000) + "ms]");
    Scanner kb = new Scanner(System.in);
    System.out.print("Enter the sentence: ");
    String input = kb.nextLine();

    while (!input.equals("q")) {
      if (input.equals("0")) {
        input = "Foi ferido por uma balas perdidas.";
      }

      CheckDocument document = new CheckDocument();
      document.setText(input);
      cogroo.analyze(document);
      
      System.out.println(document);

      System.out.print("Enter the sentence: ");
      input = kb.nextLine();
    }
  }
}
