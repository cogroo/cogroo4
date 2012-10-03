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
package org.cogroo.tools.featurizer;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import opennlp.tools.util.featuregen.StringPattern;
import opennlp.tools.util.featuregen.TokenClassFeatureGenerator;

/**
 * A context generator for the Featurizer.
 */
public class DefaultFeaturizerContextGenerator implements
    FeaturizerContextGenerator {

  protected final String SE = "*SE*";
  protected final String SB = "*SB*";
  private static final int PREFIX_LENGTH = 4;
  private static final int SUFFIX_LENGTH = 4;

  private TokenClassFeatureGenerator tokenClassFeatureGenerator = new TokenClassFeatureGenerator();
  
  // TODO: this is language dependent!
  private NumberFormat nf = NumberFormat.getInstance(new Locale("pt"));
  
  
  private boolean isWiderContext;
  private boolean isSuffixFeats;
  private boolean isHiphenedFeats;
  private boolean isNumberFeats;
  private boolean isClassFeatures;
  
  /**
   * Default is shnc
   * @param flags
   */
  public DefaultFeaturizerContextGenerator(String flags) {
    this.isWiderContext = flags.contains("w");
    this.isSuffixFeats = flags.contains("s");
    this.isHiphenedFeats = flags.contains("h");
    this.isNumberFeats = flags.contains("n");
    this.isClassFeatures = flags.contains("c");
  }

  protected static String[] getPrefixes(String lex) {
    String[] prefs = new String[PREFIX_LENGTH];
    for (int li = 0, ll = PREFIX_LENGTH; li < ll; li++) {
      prefs[li] = lex.substring(0, Math.min(li + 1, lex.length()));
    }
    return prefs;
  }

  protected static String[] getSuffixes(String lex) {
    String[] suffs = new String[SUFFIX_LENGTH];
    for (int li = 0, ll = SUFFIX_LENGTH; li < ll; li++) {
      suffs[li] = lex.substring(Math.max(lex.length() - li - 1, 0));
    }
    return suffs;
  }

  public String[] getContext(int index, WordTag[] sequence,
      String[] priorDecisions, Object[] additionalContext) {
    String[] w = new String[sequence.length];
    String[] t = new String[sequence.length];
    WordTag.extract(sequence, w, t);
    return getContext(index, w, t, priorDecisions);
  }

  /**
   * Returns the context for making a pos tag decision at the specified token
   * index given the specified tokens and previous tags.
   * 
   * @param i
   *          The index of the token for which the context is provided.
   * @param toks
   *          The tokens in the sentence.
   * @param tags
   *          pos-tags
   * @param preds
   *          The tags assigned to the previous words in the sentence.
   * @return The context for making a pos tag decision at the specified token
   *         index given the specified tokens and previous tags.
   */
  public String[] getContext(int i, String[] toks, String[] tags, String[] preds) {

    List<String> e = new ArrayList<String>();

    if(isWiderContext)
      createWindowFeats(i, toks, tags, preds, e);
    else
      create3WindowFeats(i, toks, tags, preds, e);
    
    if(i > 0)
      wrappWindowFeatures("prev_", i-1, toks, tags, preds, e);
    
    wrappWindowFeatures("", i, toks, tags, preds, e);
    
    if(i < toks.length - 1)
      wrappWindowFeatures("nxt_", i+1, toks, tags, preds, e);

    String[] context = e.toArray(new String[e.size()]);

    return context;
  }

  private void wrappWindowFeatures(String prefix, int i, String[] toks,
      String[] tags, String[] preds, List<String> e) {
    String lex = toks[i];
    List<String> features = new ArrayList<String>();
    
    if(isClassFeatures)
      tokenClassFeatureGenerator.createFeatures(features, toks, i, preds);
    
    if(isNumberFeats)
      createNumberFeats(i, toks, features);
    
    boolean suffixesCollected = false;
    
    if(isHiphenedFeats) {
      if(lex.length() >= 3) {
        if (lex.contains("_")) {
          createGroupSuffixex("us_", lex, features);
          suffixesCollected = true;
        }
        if (lex.contains("-")) {
          createGroupSuffixex("hf_", lex, features);
          suffixesCollected = true;
        }
      }
    }
    
    if(!suffixesCollected && isSuffixFeats) {
      createSuffixFeats(i, toks, tags, preds, features);
    }
    
    for (String f : features) {
      e.add(prefix + f);
    }
    
  }

  private static final Pattern UNDERLINE_PATTERN = Pattern.compile("[_-]");

  private void createGroupSuffixex(String pre, String lex, List<String> e) {
    String[] parts = UNDERLINE_PATTERN.split(lex);

    if (parts.length < 2) // this is handled already
      return;

    for (int i = 0; i < parts.length; i++) {
      e.add(pre + "up_" + i + "=" + parts[i]);
      String prefix = pre + "prsf_" + i + "=";
      String[] suffixes = getSuffixes(parts[i]);
      for (String suf : suffixes) {
        e.add(prefix + suf);
      }
    }
  }
  
  private void createNumberFeats(int i, String[] toks, List<String> e) {
    String lex = toks[i];
    // numbers would benefit from this
    StringPattern sp = StringPattern.recognize(lex);
    if (sp.containsDigit() && !sp.containsLetters()) {
      // TODO: make it generic !! this is only for Portuguese!
      String num = lex; // we need only the decimal separator
      try {
        Number number = nf.parse(num);
        if (number != null) {
          Double value = Math.abs(number.doubleValue());
          if (value >= 2) {
            e.add("num=h2");
          } else if (value >= 1) {
            e.add("num=h1");
          } else if (value > 0) {
            e.add("num=h0");
          } else {
            e.add("num=zero");
          }
        } else {
          e.add("numNull");
        }
      } catch (ParseException e1) {
        // nothing to do...
//        System.err.println("failed to parse num: " + num);
        e.add("notNum");
      }
    }
  }

  private void createSuffixFeats(int i, String[] toks, String[] tags,
      String[] preds, List<String> e) {

    String lex = toks[i];
    // do some basic suffix analysis
    String[] suffs = getSuffixes(lex);
    for (int j = 0; j < suffs.length; j++) {
      e.add("suf=" + suffs[j]);
    }

    String[] prefs = getPrefixes(lex);
    for (int j = 0; j < prefs.length; j++) {
      e.add("pre=" + prefs[j]);
    }
    // see if the word has any special characters
    if (lex.indexOf('-') != -1) {
      e.add("h");
    }

  }

  // 0.9674293472168595
  private void createWindowFeats(int i, String[] toks, String[] tags,
      String[] preds, List<String> feats) {

    // Words in a 5-word window
    String w_2, w_1, w0, w1, w2;

    // Tags in a 5-word window
    String t_2, t_1, t0, t1, t2;

    // Previous predictions
    String p_2, p_1;

    w_2 = w_1 = w0 = w1 = w2 = null;
    t_2 = t_1 = t0 = t1 = t2 = null;
    p_1 = p_2 = null;

    if (i < 2) {
      w_2 = "w_2=bos";
      t_2 = "t_2=bos";
      p_2 = "p_2=bos";
    } else {
      w_2 = "w_2=" + toks[i - 2];
      t_2 = "t_2=" + tags[i - 2];
      p_2 = "p_2" + preds[i - 2];
    }

    if (i < 1) {
      w_1 = "w_1=bos";
      t_1 = "t_1=bos";
      p_1 = "p_1=bos";
    } else {
      w_1 = "w_1=" + toks[i - 1];
      t_1 = "t_1=" + tags[i - 1];
      p_1 = "p_1=" + preds[i - 1];
    }

    w0 = "w0=" + toks[i];
    t0 = "t0=" + tags[i];

    if (i + 1 >= toks.length) {
      w1 = "w1=eos";
      t1 = "t1=eos";
    } else {
      w1 = "w1=" + toks[i + 1];
      t1 = "t1=" + tags[i + 1];
    }

    if (i + 2 >= toks.length) {
      w2 = "w2=eos";
      t2 = "t2=eos";
    } else {
      w2 = "w2=" + toks[i + 2];
      t2 = "t2=" + tags[i + 2];
    }

    String[] features = new String[] {
        // add word features
        w_2, w_1, w0, w1,
        w2,
        w_1 + w0,
        w0 + w1,

        // add tag features
        t_2, t_1, t0, t1, t2, t_2 + t_1, t_1 + t0, t0 + t1, t1 + t2,
        t_2 + t_1 + t0,
        t_1 + t0 + t1,
        t0 + t1 + t2,

        // add pred tags
        p_2,
        p_1,
        p_2 + p_1,

        // add pred and tag
        p_1 + t_2, p_1 + t_1, p_1 + t0, p_1 + t1, p_1 + t2, p_1 + t_2 + t_1,
        p_1 + t_1 + t0, p_1 + t0 + t1, p_1 + t1 + t2, p_1 + t_2 + t_1 + t0,
        p_1 + t_1 + t0 + t1, p_1 + t0 + t1 + t2,

        // add pred and word
        p_1 + w_2, p_1 + w_1, p_1 + w0, p_1 + w1, p_1 + w2, p_1 + w_1 + w0,
        p_1 + w0 + w1 };

    feats.addAll(Arrays.asList(features));
  }

  //0.9670307770871996
  private void create3WindowFeats(int i, String[] toks, String[] tags,
      String[] preds, List<String> feats) {

    // Words in a 5-word window
    String w_1, w0, w1;

    // Tags in a 5-word window
    String t_1, t0, t1;

    // Previous predictions
    String p_2, p_1;

    w0 = w1 = null;
    t_1 = t0 = t1 = null;
    p_1 = p_2 = null;

    if (i < 2) {
      p_2 = "p_2=bos";
    } else {
      p_2 = "p_2" + preds[i - 2];
    }

    if (i < 1) {
      w_1 = "w_1=bos";
      t_1 = "t_1=bos";
      p_1 = "p_1=bos";
    } else {
      w_1 = "w_1=" + toks[i - 1];
      t_1 = "t_1=" + tags[i - 1];
      p_1 = "p_1=" + preds[i - 1];
    }

    w0 = "w0=" + toks[i];
    t0 = "t0=" + tags[i];

    if (i + 1 >= toks.length) {
      w1 = "w1=eos";
      t1 = "t1=eos";
    } else {
      w1 = "w1=" + toks[i + 1];
      t1 = "t1=" + tags[i + 1];
    }

    String[] features = new String[] {
        // add word features
        w_1, w0, w1,
        w_1 + w0,
        w0 + w1,

        // add tag features
        t_1, t0, t1, 
        t_1 + t0,
        t0 + t1,
        t_1 + t0 + t1,

        // add pred tags
        p_2,
        p_1,
        p_2 + p_1,

        // add pred and tag
        p_1 + t_1, p_1 + t0, p_1 + t1,
        p_1 + t_1 + t0, p_1 + t0 + t1,
        p_1 + t_1 + t0 + t1,

        // add pred and word
        p_1 + w_1, p_1 + w0, p_1 + w1, p_1 + w_1 + w0,
        p_1 + w0 + w1 };

    feats.addAll(Arrays.asList(features));
  }


}
