package br.ccsl.cogroo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import opennlp.tools.formats.ad.PortugueseContractionUtility;

public class ContractionUtility extends PortugueseContractionUtility {

  private static final Map<String, String[]> REVERSE_CONTRACTIONS;
  
  static {
    Map<String,String[]> reverse = new HashMap<String, String[]>(CONTRACTIONS.size());
    for (String expanded : CONTRACTIONS.keySet()) {
      reverse.put(CONTRACTIONS.get(expanded), expanded.split("\\+"));
    }
    REVERSE_CONTRACTIONS = Collections.unmodifiableMap(reverse);
  }
  
  public static String[] expand(String contraction) {
    String lowercase = contraction.toLowerCase();
    if (REVERSE_CONTRACTIONS.containsKey(lowercase)) {
      return REVERSE_CONTRACTIONS.get(lowercase);
    }
    return null;
  }
  
  public static Set<String> getContractionSet() {
    return REVERSE_CONTRACTIONS.keySet();
  }
}
