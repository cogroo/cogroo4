package org.cogroo.tools.headfinder;

import opennlp.tools.chunker.ChunkerContextGenerator;
import opennlp.tools.util.TokenTag;

public class HeadFinderContextGenerator implements ChunkerContextGenerator {

  @Override
  public String[] getContext(int index, TokenTag[] sequence,
      String[] priorDecisions, Object[] additionalContext) {
    return getContext(index, sequence, priorDecisions);
  }

  public String[] getContext(int index, TokenTag[] sequence, String[] priorDecisions) {
    String[] toks = new String[sequence.length];
    String[] tags = new String[sequence.length];
    String[] chunks = new String[sequence.length];
    
    for (int i = 0; i < sequence.length; i++) {
      toks[i] = sequence[i].getToken();
      String t = sequence[i].getTag();
      int bar = t.indexOf("|");
      
      tags[i] = t.substring(0, bar);
      chunks[i] = t.substring(bar+1);
    }
    
    return getContext(index, toks, tags, chunks, priorDecisions);
  }

  @Override
  public String[] getContext(int i, String[] toks, String[] tags, String[] preds) {
    return getContext(i, TokenTag.create(toks, tags), preds);
  }

  public String[] getContext(int i, String[] toks, String[] tags, String[] chks, String[] preds) {
    // Words in a 5-word window
    String w_2, w_1, w0, w1, w2;

    // Tags in a 5-word window
    String t_2, t_1, t0, t1, t2;

    // Chunks in a 5-word window
    String c_2, c_1, c0, c1, c2;

    // Previous predictions
    String p_2, p_1;

    if (i < 2) {
      w_2 = "w_2=bos";
      t_2 = "t_2=bos";
      c_2 = "c_2=bos";
      p_2 = "p_2=bos";
    }
    else {
      w_2 = "w_2=" + toks[i - 2];
      t_2 = "t_2=" + tags[i - 2];
      c_2 = "c_2=" + chks[i - 2];
      p_2 = "p_2" + preds[i - 2];
    }

    if (i < 1) {
      w_1 = "w_1=bos";
      t_1 = "t_1=bos";
      c_1 = "c_1=bos";
      p_1 = "p_1=bos";
    }
    else {
      w_1 = "w_1=" + toks[i - 1];
      t_1 = "t_1=" + tags[i - 1];
      c_1 = "c_1=" + chks[i - 1];
      p_1 = "p_1=" + preds[i - 1];
    }

    w0 = "w0=" + toks[i];
    t0 = "t0=" + tags[i];
    c0 = "c0=" + chks[i];

    if (i + 1 >= toks.length) {
      w1 = "w1=eos";
      t1 = "t1=eos";
      c1 = "c1=eos";
    }
    else {
      w1 = "w1=" + toks[i + 1];
      t1 = "t1=" + tags[i + 1];
      c1 = "c1=" + chks[i + 1];
    }

    if (i + 2 >= toks.length) {
      w2 = "w2=eos";
      t2 = "t2=eos";
      c2 = "c2=eos";
    }
    else {
      w2 = "w2=" + toks[i + 2];
      t2 = "t2=" + tags[i + 2];
      c2 = "c2=" + chks[i + 2];
    }

    String[] features = new String[] {
        //add word features
        w_2,
        w_1,
        w0,
        w1,
        w2,
        w_1 + w0,
        w0 + w1,

        //add tag features
        t_2,
        t_1,
        t0,
        t1,
        t2,
        t_2 + t_1,
        t_1 + t0,
        t0 + t1,
        t1 + t2,
        t_2 + t_1 + t0,
        t_1 + t0 + t1,
        t0 + t1 + t2,
        
      //add chks features
        c_2,
        c_1,
        c0,
        c1,
        c2,
        c_2 + c_1,
        c_1 + c0,
        c0 + c1,
        c1 + c2,
        c_2 + c_1 + c0,
        c_1 + c0 + c1,
        c0 + c1 + c2,

        //add pred tags
        p_2,
        p_1,
        p_2 + p_1,

        //add pred and tag
        p_1 + t_2,
        p_1 + t_1,
        p_1 + t0,
        p_1 + t1,
        p_1 + t2,
        p_1 + t_2 + t_1,
        p_1 + t_1 + t0,
        p_1 + t0 + t1,
        p_1 + t1 + t2,
        p_1 + t_2 + t_1 + t0,
        p_1 + t_1 + t0 + t1,
        p_1 + t0 + t1 + t2,

        //add pred and chunk
        p_1 + c_2,
        p_1 + c_1,
        p_1 + c0,
        p_1 + c1,
        p_1 + c2,
        p_1 + c_2 + c_1,
        p_1 + c_1 + c0,
        p_1 + c0 + c1,
        p_1 + c1 + c2,
        p_1 + c_2 + c_1 + c0,
        p_1 + c_1 + c0 + c1,
        p_1 + c0 + c1 + c2,

        //add pred and word
        p_1 + w_2,
        p_1 + w_1,
        p_1 + w0,
        p_1 + w1,
        p_1 + w2,
        p_1 + w_1 + w0,
        p_1 + w0 + w1,
        
        // 
        t_2 + c_2,
        t_1 + c_1,
        t0 + c0,
        t1 + c1,
        t2 + c2
    };

    return features;
  }
}
