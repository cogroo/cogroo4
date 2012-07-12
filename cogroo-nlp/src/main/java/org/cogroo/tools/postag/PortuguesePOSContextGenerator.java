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
package org.cogroo.tools.postag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.postag.DefaultPOSContextGenerator;

public class PortuguesePOSContextGenerator extends DefaultPOSContextGenerator {

  public PortuguesePOSContextGenerator(Dictionary dict) {
    this(0, dict);
  }

  public PortuguesePOSContextGenerator(int cacheSize, Dictionary dict) {
    super(cacheSize, dict);
  }

  protected void getContext(final int index, String[] sequence,
      String[] priorDecisions, Object[] additionalContext,
      List<String> modContext) {

    if (additionalContext != null && additionalContext.length > 0) {
      String[][] ac = (String[][]) additionalContext;

      for (int i = 0; i < ac.length; i++) {
        if (ac[i][index] != null) {
          modContext.add("ac_" + i + "=" + ac[i][index]);
        }

      }
    }

  }

  public String[] getContext(final int index, String[] sequence,
      String[] priorDecisions, Object[] additionalContext) {
    String[] context = super.getContext(index, sequence, priorDecisions,
        additionalContext);

    List<String> modContext = new ArrayList<String>(Arrays.asList(context));

    getContext(index, sequence, priorDecisions, additionalContext, modContext);

    context = modContext.toArray(new String[modContext.size()]);

    return context;
  }
}
