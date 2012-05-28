package br.ccsl.cogroo.tools.postag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.postag.DefaultPOSContextGenerator;

public class PortuguesePOSContextGenerator extends DefaultPOSContextGenerator {

  public PortuguesePOSContextGenerator(Dictionary dict) {
    super(dict);
  }

  public PortuguesePOSContextGenerator(int cacheSize, Dictionary dict) {
    super(cacheSize, dict);
  }

  public String[] getContext(int index, String[] sequence,
      String[] priorDecisions, Object[] additionalContext) {
    String[] context = super.getContext(index, sequence, priorDecisions,
        additionalContext);
    if (additionalContext != null && additionalContext.length > 0) {
      String[][] ac = (String[][]) additionalContext;
        List<String> modContext = new ArrayList<String>(Arrays.asList(context));
        for (int i = 0; i < ac.length; i++) {
          if (ac[i][index] != null) {
            modContext.add("ac_" + i + "=" + ac[i][index]);
          }
        context = modContext.toArray(new String[modContext.size()]);
      }
    }

    return context;
  }
}
