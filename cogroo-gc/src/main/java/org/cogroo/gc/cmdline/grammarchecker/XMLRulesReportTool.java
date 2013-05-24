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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import opennlp.tools.cmdline.ArgumentParser.ParameterDescription;
import opennlp.tools.cmdline.BasicCmdLineTool;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.TerminateToolException;

import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.analyzer.InitializationException;
import org.cogroo.checker.GrammarChecker;
import org.cogroo.tools.checker.rules.CogrooHtml;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

public class XMLRulesReportTool extends BasicCmdLineTool {

  interface Params extends LanguageCountryParams {
    @ParameterDescription(valueName = "htmlFile", description = "output html file.")
    File getOutputFile();
  }

  public String getShortDescription() {
    return "creates a HTML report for the XML rules";
  }

  public String getHelp() {
    return getBasicHelp(Params.class);
  }

  public void run(String[] args) {
    Params params = validateAndParseParams(args, Params.class);

    String lang = params.getLang();
    CmdLineUtil.checkLanguageCode(lang);
    
    File outFile = params.getOutputFile();
    CmdLineUtil.checkOutputFile("report file", outFile);
    
    String country = params.getCountry();
    if(Strings.isNullOrEmpty(country)) {
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

    try {
      CogrooHtml report = new CogrooHtml(outFile, cogroo);
      report.evaluate();
      
      File jsFile = new File(outFile.getParentFile(), "overlib.js");
      if(!jsFile.exists()) {
        InputStream is = this.getClass().getResourceAsStream("/org/cogroo/gc/htmlreport/overlib.js");
        OutputStream os = new FileOutputStream(jsFile);
        ByteStreams.copy(is, os);
        Closeables.closeQuietly(os);
        Closeables.closeQuietly(is);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new TerminateToolException(1, "Failure during report build.");
    }
    
  }

}
