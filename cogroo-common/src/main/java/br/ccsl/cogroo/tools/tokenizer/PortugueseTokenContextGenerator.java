package br.ccsl.cogroo.tools.tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.tokenize.DefaultTokenContextGenerator;

public class PortugueseTokenContextGenerator extends DefaultTokenContextGenerator {
  
  private static final Pattern itemPattern = Pattern.compile("^\\p{Nd}[\\.)]$");
//  private static final Pattern hyphenPattern = Pattern.compile("\\p{L}-\\p{L}");
  
  // pronomes obliquuos átonos
  private static final Set<String> PRONOMES_OBLIQUOS_ATONOS;
  
  static {
    String[] arr = { "me", "te", "se", "o", "a", "lhe", "nos", "vos", "os",
        "as", "lhes" };
    PRONOMES_OBLIQUOS_ATONOS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(arr)));
  }
  
  
  // me, te, se, o, a, lhe, nos, vos, os, as, lhes
  
  public PortugueseTokenContextGenerator(Set<String> inducedAbbreviations) {
    super(inducedAbbreviations);
  }
  
  @Override
  protected List<String> createContext(String sentence, int index) {
    List<String> preds = super.createContext(sentence, index);
    
    String prefix = sentence.substring(0, index);
    String suffix = sentence.substring(index);
    
    if(prefix.endsWith("-")) {
      if(!createPronoumFeats("s_pro=", suffix, preds)) {
          createHyphenFeats("s_hyph=", prefix, suffix, preds);    
      }
      
    }
    
    if(sentence.length() == 2) {
      char current = sentence.charAt(index);
      char prev = sentence.charAt(0);
      if(current=='.') {
        if(Character.isLetter(prev) && Character.isUpperCase(prev)) {
          preds.add("abbname");
        }
      }
      if( (current=='.' || current==')') && itemPattern.matcher(sentence).matches()) {
        preds.add("item");
      }
    }

    /*String prefix = sentence.substring(0, index);
    
    if(index > 2 && sentence.charAt(index-1) == '-' && sentence.length() > index) {
      String sub = sentence.substring(index-2, index+1);
      if(hyphenPattern.matcher(sub).matches()) {
        preds.add("hyphened");
        
        String preHyphen = prefix.substring(0,index-1);
        if(preHyphen.contains("-")) {
          preds.add("preHy");
        }
      }
    }
    
    if(sentence.charAt(index) == ')' && sentence.length() > 2 && Character.isDigit(sentence.charAt(index-1))) {
      preds.add("pard");
    }*/
    
    return preds;
  }
  
  private static final String VOWELS = "aeiouáãâàéêíóõôúüAEIOUÁÃÂÀÉÊÍÓÕÔÚÜ";
  private static final String CONSONANTS = "bcçdfghjklmnpqrstvwxyzBCÇDFGHJKLMNPQRSTVWXYZ";
  
  private static final Pattern VOWELS_PATTERN = Pattern.compile("[" + VOWELS + "]");
  private static final Pattern CONSONANTS_PATTERN = Pattern.compile("[" + CONSONANTS + "]");
  private static final Pattern RULE_5_PATTERN = Pattern.compile("[mnMN][hmnHMN"+VOWELS+"]");
  private static final Pattern RULE_8_PATTERN = Pattern.compile("[rbRB"+VOWELS+"][hH]");
  
  private static final Set<String> PREFIXOS_HYPHEN_OBRIGATORIO;
  private static final Set<String> PREFIXOS_HYPHEN_TONICOS;
  
  static {
    String[] pho = {"ex-", "sota-", "soto-", "vice-"};
    PREFIXOS_HYPHEN_OBRIGATORIO = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(pho)));
    
    String[] pht = {"pré-", "pós-", "pró-"};
    PREFIXOS_HYPHEN_TONICOS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(pht)));
  }
  
  private void createHyphenFeats(String key, String prefix, String suffix,
      List<String> outPreds) {
    if(suffix.length() > 1 && prefix.length() > 1) {
      List<String> preds = new ArrayList<String>();
      // features based on the AO
      String lastPrefixLetter = Character.toString(prefix.charAt(prefix.length() - 2));
      String firtSuffixLetter = Character.toString(suffix.charAt(0));
      
      // Rule 1: vowelA - vowelA
      if(lastPrefixLetter.equals(firtSuffixLetter) && VOWELS_PATTERN.matcher(firtSuffixLetter).matches()) {
        preds.add(key+"r1");
      }
      
      // Rule 3: consonantA-consonantA
      if(lastPrefixLetter.equals(firtSuffixLetter) && CONSONANTS_PATTERN.matcher(firtSuffixLetter).matches()) {
        preds.add(key+"r3");
      }
      
      // Rule 5: [mn]-[VOWEL + hmn] 
      if(RULE_5_PATTERN.matcher(lastPrefixLetter + firtSuffixLetter).matches()) {
        preds.add(key+"r5");
      }
      
      // Rule 6: ex, sota, soto, vice
      if(PREFIXOS_HYPHEN_OBRIGATORIO.contains(prefix.toLowerCase())) {
        preds.add(key+"r6");
      }
      
      // Rule 7: pré, pós, pró
      if(PREFIXOS_HYPHEN_TONICOS.contains(prefix.toLowerCase())) {
        preds.add(key+"r7");
      }
      
      // Rule 8: [rbVowels][h]
      if(RULE_8_PATTERN.matcher(lastPrefixLetter + firtSuffixLetter).matches()) {
        preds.add(key+"r8");
      }
      
      if(preds.size() > 0) {
        outPreds.add(key+"hyp");
        outPreds.addAll(preds);
      }
    }
    
  }

  private boolean createPronoumFeats(String key, String token,
      List<String> preds) {
    token = removeTrailingSymbols(token).toLowerCase();
    if(PRONOMES_OBLIQUOS_ATONOS.contains(token)) {
      preds.add(key+"p");
      return true;
    }
    return false;
  }
  
  private static final Pattern REMOVE_TRAILING_SYMBOLS = Pattern.compile("^([\\p{L}]+)[^\\p{L}]*$");
  private String removeTrailingSymbols(String token) {
    Matcher matcher = REMOVE_TRAILING_SYMBOLS.matcher(token);
    if(matcher.matches()) {
      return matcher.group(1);
    }
    return token;
  }

  @Override
  protected void addCharPreds(String key, char c, List<String> preds) {
    super.addCharPreds(key, c, preds);
    
    if (c==':' || c==',' || c==';') {
      preds.add(key + "_sep");
    } else
    if (c=='»' || c=='«') {
      preds.add(key + "_quote");
    }
  }
}
