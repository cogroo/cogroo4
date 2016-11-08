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

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.analyzer.InitializationException;
import org.cogroo.checker.GrammarChecker;
import org.cogroo.tools.checker.RuleDefinition;
import org.cogroo.tools.checker.rules.model.Example;
import org.cogroo.util.StringsUtil;

import opennlp.tools.cmdline.ArgumentParser.OptionalParameter;
import opennlp.tools.cmdline.ArgumentParser.ParameterDescription;
import opennlp.tools.cmdline.BasicCmdLineTool;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.TerminateToolException;

public class ExamplesTool extends BasicCmdLineTool {

  interface Params extends LanguageCountryParams {
    @ParameterDescription(valueName = "value", description = "if true prints correct examples instead of incorrect.")
    @OptionalParameter(defaultValue = "false")
    Boolean getCorrect();
  }

  public String getShortDescription() {
    return "prints examples from the working rules";
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
      cogroo = new GrammarChecker(factory.createPipe());
    } catch(IOException e) {
      e.printStackTrace();
      throw new TerminateToolException(1, "Could not create pipeline!");
    }

    printExamples(cogroo.getRuleDefinitions(), params.getCorrect());
    
  }

  private static void printExamples(Collection<RuleDefinition> rulesDefinition, boolean correct) {
    
    for (RuleDefinition def : rulesDefinition) {
      for (Example ex : def.getExamples()) {
        if(correct) {
          System.out.println(ex.getCorrect());
        } else {
          System.out.println(ex.getIncorrect());
        } 
      }
    }
  }
  
}
