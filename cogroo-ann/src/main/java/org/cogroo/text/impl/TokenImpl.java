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
package org.cogroo.text.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.util.Span;

import org.cogroo.config.Analyzers;
import org.cogroo.text.Token;


import com.google.common.base.Objects;

/**
 * The <code>TokenImpl</code> class represents a token, which is a word, its
 * lemma, its morphological posTag and the position of it in the sentence.
 */
public class TokenImpl implements Token {

  /** Is the position of the token in the sentence */
  private Span span;
  
  /** Is the actual word of the token */
  private String lexeme;
  
  /** Is the primitive form of the <code>lemma</code> */
  private String[] lemmas;

  /** Is the morphological posTag of the <code>lemma</code> */
  private String posTag;
  
  private String features;
  
  private String chunkTag;
  
  private String syntacticTag;
  
  private Map<Analyzers, String> additionalContext = new HashMap<Analyzers, String>();

  private double posTagProb;

  private boolean isChunkHead;
  
  public TokenImpl(int start, int end, String lexeme) {
    this(start, end, lexeme, null, null, null);
  }

  public TokenImpl(int start, int end, String lexeme, String[] lemmas, String tag, String features) {
    this.span = new Span(start, end);
    this.lexeme = lexeme;
    this.lemmas = lemmas;
    this.posTag = tag;
    this.features = features;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.cogroo.TokenI#getLemma()
   */
  public String[] getLemmas() {
    return lemmas;
  }

  public void setLemmas(String[] lemmas) {
    this.lemmas = lemmas;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.cogroo.TokenI#getLexeme()
   */
  public String getLexeme() {
    return lexeme;
  }

  public void setLexeme(String lexeme) {
    this.lexeme = lexeme;
  }

  public String getPOSTag() {
    return posTag;
  }

  public void setPOSTag(String tag) {
    this.posTag = tag;
  }
  
  public void setFeatures(String features) {
    this.features = features;
  }
  
  public String getFeatures() {
    return features;
  }
  
  public String getChunkTag() {
    return chunkTag;
  }
  
  public void setChunkTag(String chunkTag) {
    this.chunkTag = chunkTag;
  }
  
  public void setSyntacticTag(String tag) {
    this.syntacticTag = tag;
  }
  
  public String getSyntacticTag() {
    return this.syntacticTag;
  }
  
  public void addContext (Analyzers analyzer, String value) {
    additionalContext.put(analyzer, value);
  }
  
  public String getAdditionalContext(Analyzers analyzer) {
    return additionalContext.get(analyzer);
  }
  
/**
 * 
 * @param tokens the list of each token of a sentence
 * @return the <code>String</code> list of <code>lexemes</code>
 */
  public String[] lexemesToString(List<Token> tokens) {
    String[] lexemes = new String[tokens.size()];

    for (int i = 0; i < tokens.size(); i++) {
      lexemes[i] = tokens.get(i).getLexeme();
    }

    return lexemes;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof TokenImpl) {
      TokenImpl that = (TokenImpl) obj;
      return Objects.equal(this.lexeme, that.lexeme)
          && Objects.equal(this.lemmas, that.lemmas)
          && Objects.equal(this.span, that.span);
    }
    return false;
  }

  @Override
  public String toString() {

    return Objects.toStringHelper(this).add("lxm", lexeme).add("lm", Arrays.toString(lemmas)).add("posTag", posTag).add("feat", features)
    // .add("span", span)
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(lexeme, lemmas, span);
  }

  public int getStart() {
    return span.getStart();
  }

  public int getEnd() {
    return span.getEnd();
  }

  public void setBoundaries(int start, int end) {
    span = new Span(start, end);
  }

  @Override
  public double getPOSTagProb() {
    return posTagProb;
  }

  @Override
  public void setPOSTagProb(double prob) {
    posTagProb = prob;
  }

  @Override
  public boolean isChunkHead() {
    return this.isChunkHead;
  }

  @Override
  public void isChunkHead(boolean ch) {
    this.isChunkHead = ch;
  }

}