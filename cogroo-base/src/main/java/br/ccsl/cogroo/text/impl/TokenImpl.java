package br.ccsl.cogroo.text.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ccsl.cogroo.config.Analyzers;
import br.ccsl.cogroo.text.Token;

import com.google.common.base.Objects;

import opennlp.tools.util.Span;

/**
 * The <code>TokenImpl</code> class represents a token, which is a word, its
 * lemma, its morphological tag and the position of it in the sentence.
 */
public class TokenImpl implements Token {

  /** Is the position of the token in the sentence */
  private Span span;
  
  /** Is the actual word of the token */
  private String lexeme;
  
  /** Is the primitive form of the <code>lemma</code> */
  private String[] lemmas;

  /** Is the morphological tag of the <code>lemma</code> */
  private String tag;
  
  private String features;
  
  private String chunkTag;
  
  private Map<Analyzers, String> additionalContext = new HashMap<Analyzers, String>();
  
  public TokenImpl(Span span, String lexeme) {
    this(span, lexeme, null, null, null);
  }

  public TokenImpl(Span span, String lexeme, String[] lemmas, String tag, String features) {
    this.span = span;
    this.lexeme = lexeme;
    this.lemmas = lemmas;
    this.tag = tag;
    this.features = features;
  }

  /*
   * (non-Javadoc)
   * 
   * @see br.ccsl.cogroo.TokenI#getSpan()
   */
  public Span getSpan() {
    return span;
  }

  public void setSpan(Span span) {
    this.span = span;
  }

  /*
   * (non-Javadoc)
   * 
   * @see br.ccsl.cogroo.TokenI#getLemma()
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
   * @see br.ccsl.cogroo.TokenI#getLexeme()
   */
  public String getLexeme() {
    return lexeme;
  }

  public void setLexeme(String lexeme) {
    this.lexeme = lexeme;
  }

  public String getPOSTag() {
    return tag;
  }

  public void setPOSTag(String tag) {
    this.tag = tag;
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

    return Objects.toStringHelper(this).add("lxm", lexeme).add("lm", Arrays.toString(lemmas)).add("tag", tag).add("feat", features)
    // .add("span", span)
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(lexeme, lemmas, span);
  }

}