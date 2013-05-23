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
package org.cogroo.cmdline.dictionary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import opennlp.tools.cmdline.BasicCmdLineTool;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.TerminateToolException;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.util.StringList;

import org.cogroo.ContractionUtility;

public class AbbreviationDictionaryBuilderTool extends BasicCmdLineTool {

  interface Params extends AbbreviationDictionaryBuilderParams {
  }

  public String getShortDescription() {
    return "builds a new dictionary";
  }

  public String getHelp() {
    return getBasicHelp(Params.class);
  }

  public void run(String[] args) {
    Params params = validateAndParseParams(args, Params.class);

    File dictOutFile = params.getOutputFile();

    CmdLineUtil.checkOutputFile("dictionary output file", dictOutFile);

    OutputStream out = null;
    try {
      out = new FileOutputStream(dictOutFile);

      Dictionary dict = create(ContractionUtility.getContractionSet());
      dict.serialize(out);

    } catch (IOException e) {
      throw new TerminateToolException(-1,
          "IO error while reading training data or indexing data: "
              + e.getMessage());
    } finally {
      try {
        out.close();
      } catch (IOException e) {
        // sorry that this can fail
      }
    }

  }

  private Dictionary create(Set<String> contractionSet) {
    Dictionary d = new Dictionary(false);
    for (String contraction : contractionSet) {
      d.put(new StringList(contraction));
    }
    return d;
  }

}
