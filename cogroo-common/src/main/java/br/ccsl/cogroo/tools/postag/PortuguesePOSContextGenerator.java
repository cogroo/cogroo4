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
      if (ac[index] != null) {
        List<String> modContext = new ArrayList<String>(Arrays.asList(context));
        for (int i = 0; i < ac[index].length; i++) {
          if (ac[index][i] != null) {
            modContext.add("ac=" + ac[index][i]);
//            System.out.println(sequence[index] + " : " + ac[index][i]);
          }
        }
        context = modContext.toArray(new String[modContext.size()]);
      }
    }

    return context;
  }
}
