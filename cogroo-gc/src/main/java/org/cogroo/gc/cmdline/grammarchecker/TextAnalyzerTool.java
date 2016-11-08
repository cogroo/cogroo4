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
package org.cogroo.gc.cmdline.grammarchecker;

import java.util.Locale;
import java.util.Scanner;

import org.cogroo.analyzer.Analyzer;
import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.analyzer.InitializationException;
import org.cogroo.checker.CheckDocument;
import org.cogroo.util.StringsUtil;
import org.cogroo.util.TextUtils;

import opennlp.tools.cmdline.BasicCmdLineTool;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.TerminateToolException;

public class TextAnalyzerTool extends BasicCmdLineTool {

  interface Params extends LanguageCountryParams {
  }

  public String getShortDescription() {
    return "analyzes a text document";
  }

  public String getHelp() {
    return getBasicHelp(Params.class);
  }

  public void run(String[] args) {
    Params params = validateAndParseParams(args, Params.class);

    String lang = params.getLang();
    CmdLineUtil.checkLanguageCode(lang);
    
    String country = params.getCountry();
    if(StringsUtil.isNullOrEmpty(country)) {
      throw new TerminateToolException(1, "Country cannot be empty. Example country: BR");
    }

    long start = System.nanoTime();

    ComponentFactory factory;
    try {
      factory = ComponentFactory.create(new Locale(lang, country));
    } catch(InitializationException e) {
      e.printStackTrace();
      throw new TerminateToolException(1, "Could not find configuration for "
          + lang + ". Only " + new Locale("pt", "BR") + " might be supported for now.");
    }
    Analyzer cogroo = factory.createPipe();

    System.out.println("Loading time ["
        + ((System.nanoTime() - start) / 1000000) + "ms]");
    
    Scanner kb = new Scanner(System.in);
    System.out.print("Enter the sentence or 'q' to quit: ");
    String input = kb.nextLine();

    while (!input.equals("q")) {

      CheckDocument document = new CheckDocument();
      document.setText(input);
      cogroo.analyze(document);
      
      System.out.println(TextUtils.nicePrint(document));
      

      System.out.print("Enter the sentence or 'q' to quit: ");
      input = kb.nextLine();
    }
    
  }

}
