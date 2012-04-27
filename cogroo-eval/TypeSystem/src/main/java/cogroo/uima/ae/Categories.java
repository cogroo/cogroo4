package cogroo.uima.ae;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Categories {

  private static final Map<String, String> RULES_AND_CATEGORIES;
  private static final Map<String, String> CATEGORIES_DESCRIPTION;

  private static Set<String> implCat;
  static {
    Map<String, String> elems = new HashMap<String, String>();
    elems.put("xml:1", "cra");
    elems.put("xml:2", "cra");
    elems.put("xml:3", "cra");
    elems.put("xml:4", "cra");
    elems.put("xml:5", "cra");
    elems.put("xml:6", "cra");
    elems.put("xml:7", "cra");
    elems.put("xml:8", "cra");
    elems.put("xml:9", "cra");
    elems.put("xml:10", "cra");
    elems.put("xml:11", "cra");
    elems.put("xml:12", "cra");
    elems.put("xml:13", "cra");
    elems.put("xml:14", "cra");
    elems.put("xml:15", "cra");
    elems.put("xml:16", "cra");
    elems.put("xml:17", "con");
    elems.put("xml:18", "con");
    elems.put("xml:19", "con");
    elems.put("xml:20", "con");
    elems.put("xml:21", "con");
    elems.put("xml:22", "con");
    elems.put("xml:23", "con");
    elems.put("xml:24", "con");
    elems.put("xml:25", "ali");// em anexo
    elems.put("xml:26", "ali");
    elems.put("xml:27", "ali");
    elems.put("xml:28", "con"); // cond em anexo os documentos (no word nao tem)
    elems.put("xml:29", "con");
    elems.put("xml:30", "con");
    elems.put("xml:31", "con");
    elems.put("xml:32", "con");
    elems.put("xml:33", "con");
    elems.put("xml:34", "con");
    elems.put("xml:35", "con");
    elems.put("xml:36", "con");
    elems.put("xml:37", "con");
    elems.put("xml:38", "adv");
    elems.put("xml:39", "adv");
    elems.put("xml:40", "con");
    elems.put("xml:41", "con");// meio
    elems.put("xml:42", "cov");
    elems.put("xml:43", "cov|reg|ver");
    elems.put("xml:44", "cov");
    elems.put("xml:45", "cov");
    elems.put("xml:46", "aha");
    elems.put("xml:47", "aha");
    elems.put("xml:48", "cov");
    elems.put("xml:49", "cov");
    elems.put("xml:50", "cov");
    elems.put("xml:51", "cov");
    elems.put("xml:52", "cop|pro");
    elems.put("xml:53", "pro");
    elems.put("xml:54", "pro");
    elems.put("xml:55", "pro");
    elems.put("xml:56", "pro");
    elems.put("xml:57", "mal");
    elems.put("xml:58", "mal");
    elems.put("xml:59", "ali");// red
    elems.put("xml:60", "cov");
    elems.put("xml:61", "cop|pro");
    elems.put("xml:62", "cop");
    elems.put("xml:63", "cop");
    elems.put("xml:64", "cop");
    elems.put("xml:65", "cop|pro");
    elems.put("xml:66", "cop");
    elems.put("xml:67", "cop");
    elems.put("xml:68", "cop");
    elems.put("xml:69", "cop");
    elems.put("xml:70", "cop");
    elems.put("xml:71", "cop");
    elems.put("xml:72", "cop");
    elems.put("xml:73", "cop");
    elems.put("xml:74", "cop");
    elems.put("xml:75", "cmt");
    elems.put("xml:76", "cmt");
    elems.put("xml:77", "cmt");
    elems.put("xml:78", "cra");
    elems.put("xml:79", "cra");
    elems.put("xml:80", "cra");
    elems.put("xml:81", "cra");
    elems.put("xml:82", "cra");
    elems.put("xml:83", "con");
    elems.put("xml:84", "cra");
    elems.put("xml:85", "cra");
    elems.put("xml:86", "reg");
    elems.put("xml:87", "cra");
    elems.put("xml:88", "ren");
    elems.put("xml:89", "cra");
    elems.put("xml:90", "reg");
    elems.put("xml:91", "cra");
    elems.put("xml:92", "adv|con");
    elems.put("xml:93", "cra");
    elems.put("xml:94", "cra");
    elems.put("xml:95", "con");
    elems.put("xml:96", "reg");
    elems.put("xml:97", "reg");
    elems.put("xml:98", "reg");
    elems.put("xml:99", "reg");
    elems.put("xml:100", "reg");
    elems.put("xml:101", "reg");
    elems.put("xml:102", "reg");
    elems.put("xml:103", "con");
    elems.put("xml:104", "con");
    elems.put("xml:105", "con");
    elems.put("xml:106", "cjc|det|lex");
    elems.put("xml:107", "reg");
    elems.put("xml:108", "cra");
    elems.put("xml:109", "reg");
    elems.put("xml:110", "ren");
    elems.put("xml:111", "ptn");
    elems.put("xml:112", "ptn");
    elems.put("xml:113", "ptn");
    elems.put("xml:114", "con");
    elems.put("xml:115", "con");
    // elems.put("xml:116", null);
    elems.put("xml:117", "cov");
    elems.put("xml:118", "cov");
    elems.put("xml:119", "cov");
    elems.put("xml:120", "cov");
    elems.put("xml:121", "ger");
    elems.put("xml:122", "sem");
    elems.put("xml:123", "sem");
    elems.put("xml:124", "con");

    elems.put("space:EXTRA_BETWEEN_WORDS", "esp");
    elems.put("space:EXTRA_BEFORE_RIGHT_PUNCT", "esp");
    elems.put("space:EXTRA_AFTER_LEFT_PUNCT", "esp");
    elems.put("space:MISSING_SPACE_AFTER_PUNCT", "esp");

    RULES_AND_CATEGORIES = Collections.unmodifiableMap(elems);

    String[] allCatArr = { "abr", "ace", "adj", "adv", "aha", "ali", "arc",
        "bde", "cap", "cjc", "cli", "cmt", "con", "cop", "cov", "cra", "det",
        "esp", "est", "ger", "lex", "mal", "mec", "mor", "neo", "nol", "num",
        "ond", "ort", "par", "ple", "pre", "pro", "prq", "ptn", "ptp", "reg",
        "ren", "rep", "res", "sem", "ver" };

    Set<String> allCat = Collections.unmodifiableSet(new HashSet<String>(Arrays
        .asList(allCatArr)));

    implCat = Collections.unmodifiableSet(new HashSet<String>(elems.values()));

    Map<String, String> cat = new HashMap<String, String>();
    cat.put("abr", "USO DE SIGLAS");
    cat.put("ace", "ACENTUAÇÃO GRÁFICA");
    cat.put("adj", "USO DE ADJETIVOS");
    cat.put("adv", "USO DE ADVÉRBIOS");
    cat.put("aha", "USO DE HÁ/A");
    cat.put("ali", "OUTROS PROBLEMAS");
    cat.put("arc", "USO DE ARCAÍSMOS");
    cat.put("bde", "BALANCEAMENTO DE DELIMITADORES");
    cat.put("cap", "USO DE LETRAS MAIÚSCULAS");
    cat.put("cjc", "USO DE CONJUNÇÕES");
    cat.put("cli", "USO DE CLICHÊ");
    cat.put("cmt", "CONCORDÂNCIA ENTRE MODOS E TEMPOS VERBAIS");
    cat.put("con", "CONCORDÂNCIA NOMINAL");
    cat.put("cop", "COLOCAÇÃO PRONOMINAL");
    cat.put("cov", "CONCORDÂNCIA VERBAL");
    cat.put("cra", "USO DE CRASE");
    cat.put("det", "USO DE ARTIGOS E DETERMINANTES");
    cat.put("esp", "USO DE ESPAÇOS");
    cat.put("est", "USO DE ESTRANGEIRISMOS");
    cat.put("ger", "USO DE GERÚNDIO");
    cat.put("lex", "INADEQUAÇÃO LEXICAL");
    cat.put("mal", "USO DE MAU/MAL");
    cat.put("mec", "PROBLEMAS MECÂNICOS");
    cat.put("mor", "MORFOLOGIA");
    cat.put("neo", "USO DE NEOLOGISMOS");
    cat.put("nol", "USO DE NOTAÇÕES LÉXICAS");
    cat.put("num", "USO E GRAFIA DOS NUMERAIS");
    cat.put("ond", "USO DE ONDE/AONDE");
    cat.put("ort", "ORTOGRAFIA");
    cat.put("par", "USO DE PARÔNIMOS");
    cat.put("ple", "USO DE PLEBEÍSMOS");
    cat.put("pre", "USO DE PREPOSIÇÕES");
    cat.put("pro", "USO DE PRONOMES");
    cat.put("prq", "USO DE POR QUE");
    cat.put("ptn", "PONTUAÇÃO");
    cat.put("ptp", "USO DO PARTICÍPIO");
    cat.put("reg", "REGÊNCIA VERBAL");
    cat.put("ren", "REGÊNCIA NOMINAL");
    cat.put("rep", "REPETIÇÃO EXCESSIVA DE PALAVRAS");
    cat.put("res", "REPETIÇÃO DE SÍMBOLOS");
    cat.put("sem", "PLEONASMO VICIOSO");
    cat.put("ver", "USO DOS VERBOS");

    CATEGORIES_DESCRIPTION = Collections.unmodifiableMap(cat);

  }

  public static String getCategoryDescription(String cat) {
    return CATEGORIES_DESCRIPTION.get(cat);
  }

  public static String getCat(String rule) {
    return RULES_AND_CATEGORIES.get(rule);
  }

  public static boolean isCategoryImplemented(String cat) {
    return implCat.contains(cat);
  }

  public static Set<String> getCategories() {
    return Collections.unmodifiableSet(implCat);
  }

  public static void printCategoriesByRules() {
    Map<String, List<Integer>> m = new HashMap<String, List<Integer>>();

    for (String rule : RULES_AND_CATEGORIES.keySet()) {
      String cat = RULES_AND_CATEGORIES.get(rule);
      if (cat == null) {
        cat = "???";
      }
      if (!m.containsKey(cat)) {
        m.put(cat, new ArrayList<Integer>());
      }
      m.get(cat).add(new Integer(rule));
    }

    SortedSet<String> sortedCat = new TreeSet<String>();
    sortedCat.addAll(m.keySet());
    for (String cat : sortedCat) {
      SortedSet<Integer> sortedRule = new TreeSet<Integer>();
      sortedRule.addAll(m.get(cat));
      System.out.print(cat + ",");
      for (Integer rule : sortedRule) {
        System.out.print(rule + "; ");
      }
      System.out.println();
    }
    System.out.println();
    System.out.println();
    String[] allCat = { "abr", "ace", "adj", "adv", "aha", "ali", "arc", "bde",
        "cap", "cjc", "cli", "cmt", "con", "cop", "cov", "cra", "det", "esp",
        "est", "ger", "lex", "mal", "mec", "mor", "neo", "nol", "num", "ond",
        "ort", "par", "ple", "pre", "pro", "prq", "ptn", "ptp", "reg", "ren",
        "rep", "res", "sem", "ver" };

    for (String c : allCat) {
      if (!m.containsKey(c)) {
        System.out.print(c + ", ");
      }
    }
  }

  public static Set<String> getRules() {
    return Collections.unmodifiableSet(RULES_AND_CATEGORIES.keySet());
  }

}
