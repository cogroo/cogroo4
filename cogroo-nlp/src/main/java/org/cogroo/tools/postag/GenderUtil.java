package org.cogroo.tools.postag;

import java.util.Arrays;

public class GenderUtil {

  public static String removeGender(String pos) {
    if(pos.startsWith("art")) {
      pos = "art";
    } else if("nm".equals(pos) || "nf".equals(pos) || "nn".equals(pos)) {
      pos = "n";
    }
    
    return pos;
  }
  
  public static String[] removeGender(String[] pos) {
    if(pos == null) return null;
    String[] copy = Arrays.copyOf(pos, pos.length);
    for (int i = 0; i < copy.length; i++) {
      copy[i] = removeGender(copy[i]);
    }
    return copy;
  }

}
