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

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

import opennlp.tools.cmdline.ArgumentParser.OptionalParameter;
import opennlp.tools.cmdline.ArgumentParser.ParameterDescription;
import opennlp.tools.cmdline.BasicCmdLineTool;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.TerminateToolException;

import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.analyzer.InitializationException;
import org.cogroo.checker.CheckDocument;
import org.cogroo.checker.GrammarChecker;
import org.cogroo.util.TextUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;

public class GrammarCheckerTool extends BasicCmdLineTool {

  interface Params extends LanguageCountryParams {
    @ParameterDescription(valueName = "show", description = "if true will show text analysis.")
    @OptionalParameter(defaultValue = "false")
    Boolean getShowAnalysis();
    
    @ParameterDescription(valueName = "rulesXml", description = "if set will use a custom rules file")
    @OptionalParameter
    File getRulesFile();
  }

  public String getShortDescription() {
    return "checks a text for grammar errors";
  }

  public String getHelp() {
    return getBasicHelp(Params.class);
  }

  public void run(String[] args) {
    Params params = validateAndParseParams(args, Params.class);

    String lang = params.getLang();
    CmdLineUtil.checkLanguageCode(lang);
    
    String country = params.getCountry();
    if(Strings.isNullOrEmpty(country)) {
      throw new TerminateToolException(1, "Country cannot be empty. Example country: BR");
    }
    
    File rulesFile = params.getRulesFile();
    if(rulesFile != null) {
      CmdLineUtil.checkInputFile("Rules file", rulesFile);
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
    GrammarChecker cogroo;
    try {
      if(rulesFile == null) {
        cogroo = new GrammarChecker(factory.createPipe());
      } else {
        String serializedRules = Files.toString(rulesFile, Charsets.UTF_8);
        cogroo = new GrammarChecker(factory.createPipe(), serializedRules);
      }
    } catch(IOException e) {
      e.printStackTrace();
      throw new TerminateToolException(1, "Could not create pipeline!");
    }

    System.out.println("Loading time ["
        + ((System.nanoTime() - start) / 1000000) + "ms]");
    
    Scanner kb = new Scanner(System.in);
    System.out.print("Enter the sentence or 'q' to quit: ");
    String input = kb.nextLine();

    while (!input.equals("q")) {

      CheckDocument document = new CheckDocument();
      document.setText(input);
      cogroo.analyze(document);
      
      if(params.getShowAnalysis()) {
        System.out.println(TextUtils.nicePrint(document));
      }
      
      System.out.println(document.getMistakesAsString());

      System.out.print("Enter the sentence or 'q' to quit: ");
      input = kb.nextLine();
    }
    
  }

}
