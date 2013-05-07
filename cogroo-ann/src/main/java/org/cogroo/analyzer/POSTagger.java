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
package org.cogroo.analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.Span;

import org.apache.log4j.Logger;
import org.cogroo.config.Analyzers;
import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.Token;
import org.cogroo.tools.postag.GenderUtil;
import org.cogroo.util.EntityUtils;
import org.cogroo.util.TextUtils;


/**
 * The <code>POSTagger</code> class analyzes each token of a sentence and
 * classifies it grammatically.
 * 
 */
public class POSTagger implements Analyzer {
  private static final Logger LOGGER = Logger.getLogger(POSTagger.class);
  private POSTaggerME tagger;

  public POSTagger(POSTaggerME tagger) {
    this.tagger = tagger;
  }

  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();

    for (Sentence sentence : sentences) {
      List<Token> tokens = sentence.getTokens();
      String[] tags;
      
      double[] probs;
      String[][] ac = TextUtils.additionalContext(tokens,
          Arrays.asList(Analyzers.CONTRACTION_FINDER, Analyzers.NAME_FINDER));
      String[] toks = TextUtils.tokensToString(sentence.getTokens());
      synchronized (this.tagger) {
        tags = tagger.tag(toks, ac);
        probs = tagger.probs();
      }
      
      double finalProb = computeFinalProb(probs);
      
      sentence.setTokensProb(finalProb);
      
      if (LOGGER.isDebugEnabled()) {
        StringBuilder sb = new StringBuilder("Probabilidades do tagger:\n");
        for (int i = 0; i < toks.length; i++) {
          sb.append("[").append(toks[i]).append("_").append(tags[i])
              .append(" ").append(probs[i]).append("] ");
        }
        LOGGER.debug(sb.toString());
        
        LOGGER.debug("Soma dos logs das probabilidades: " + finalProb);
      }
      
      tags = GenderUtil.removeGender(tags);
      
      for (int i = 0; i < tags.length; i++) {
        tokens.get(i).setPOSTag(tags[i]);
        tokens.get(i).setPOSTagProb(probs[i]);
      }
      
      EntityUtils.groupTokens(sentence.getText(), tokens, createSpanList(toTokensArray(tokens), toTagsArray(tokens)));
      
      mergeHyphenedWords(sentence);
    }
  }
  
  private double computeFinalProb(double[] probs) {
    double finalProb = 0;
    if(true) {
      for (double prob : probs) {
        finalProb += Math.log(prob);
      }      
    } else {
      for (double prob : probs) {
        finalProb += prob;
      } 
    } 
    if(probs.length > 0) {
      finalProb = finalProb / probs.length;
    }
    return finalProb;
  }

  private String[] toTokensArray(List<Token> tokens) {
    String[] arr = new String[tokens.size()];
    for (int i = 0; i < tokens.size(); i++) {
      arr[i] = tokens.get(i).getLexeme();
    }
    return arr;
  }

  private String[] toTagsArray(List<Token> tokens) {
    String[] arr = new String[tokens.size()];
    for (int i = 0; i < tokens.size(); i++) {
      arr[i] = tokens.get(i).getPOSTag();
    }
    return arr;
  }

  // this is from opennlp
  public static List<Span> createSpanList(String[] toks, String[] tags) {

    // initialize with the list maximum size
    List<Span> phrases = new ArrayList<Span>(toks.length); 
    String startTag = "";
    int startIndex = 0;
    boolean foundPhrase = false;

    for (int ci = 0, cn = tags.length; ci < cn; ci++) {
      String pred = tags[ci];
      if(!tags[ci].startsWith("B-") && !tags[ci].startsWith("I-")) {
        pred = "O";
      }
      if (pred.startsWith("B-")
          || (!pred.equals("I-" + startTag) && !pred.equals("O"))) { // start
        if (foundPhrase) { // handle the last
          phrases.add(new Span(startIndex, ci, startTag));
        }
        startIndex = ci;
        startTag = pred.substring(2);
        foundPhrase = true;
      } else if (pred.equals("I-" + startTag)) { // middle
        // do nothing
      } else if (foundPhrase) {// end
        phrases.add(new Span(startIndex, ci, startTag));
        foundPhrase = false;
        startTag = "";
      }
    }
    if (foundPhrase) { // leftover
      phrases.add(new Span(startIndex, tags.length, startTag));
    }

    return phrases;
  }
  
  private void mergeHyphenedWords(Sentence sentence) {
    List<Token> tokens = sentence.getTokens();
    // look for "-", check if it makes contact with the other hyphens
    boolean restart = true;
    int start = 1;
    while (restart) {
      restart = false;
      for (int i = start; i < tokens.size() - 1 && !restart; i++) {
        if ("-".equals(tokens.get(i).getLexeme())) {
          if (!hasCharacterBetween(tokens.get(i - 1), tokens.get(i))
              && !hasCharacterBetween(tokens.get(i), tokens.get(i + 1))) {
            Token a = tokens.get(i - 1);
            Token b = tokens.get(i + 1);
            if (b.getPOSTag().startsWith("pron-")) {
              // remove the "-"
              b.setBoundaries(b.getStart() - 1, b.getEnd());
              b.setLexeme("-" + b.getLexeme());
              tokens.remove(i);
              restart = true;
              start = i + 1;
            } else {
              // merge the terms
              String res = merge(a.getPOSTag(), b.getPOSTag());
              if(res != null) {
                String lexeme = a.getLexeme() + "-" + b.getLexeme();
                b.setLexeme(lexeme);
                b.setPOSTag(res);
                b.setBoundaries(a.getStart(), b.getEnd());
                tokens.remove(i);
                tokens.remove(i - 1);
                start = i;
                restart = true;
              }
            }
          }
        }
      }
    }
  }
  
  private String merge(String a, String b) {
    // http://www.soportugues.com.br/secoes/morf/morf28.php
    
    if (isNoun(a) || isNoun(b)) {
      return "n";
    } else if (isNoun(a) && isAdjective(b)) {
      return "n";
    } else if (isVerb(a) && isNoun(b)) {
      return "n";
    } else if (isAdjective(a) && isAdjective(b)) {
      return "n";
    } else if ("prep".equals(b) || "art".equals(b)) {
      return a;
    } else if (isVerb(a) && "adv".equals(b)) {
      return "n";
    } else if (isNoun(b)) {
      return "n";
    } else if(a.equals(b)){
      return a;
    }
    return null;
  }

  private boolean isVerb(String a) {
    return a.startsWith("v-");
  }

  private boolean isNoun(String b) {
    return "n".equals(b) || "n-adj".equals(b);
  }

  private boolean isAdjective(String b) {
    return "adj".equals(b) || "n-adj".equals(b);
  }

  private boolean hasCharacterBetween(Token a, Token b) {
    int aEnd = a.getEnd();
    int bStart = b.getStart();
    if (aEnd == bStart) {
      return false;
    }
    return true;
  }
}
