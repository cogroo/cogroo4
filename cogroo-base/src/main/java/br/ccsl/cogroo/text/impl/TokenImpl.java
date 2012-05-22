package br.ccsl.cogroo.text.impl;

import java.awt.Component;
import java.util.ArrayList;
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
  private String lemma;

  /** Is the morphological tag of the <code>lemma</code> */
  private String tag;
  
  private Map<Analyzers, Object> additionalContext = new HashMap<Analyzers, Object>();
  
  public TokenImpl(Span span, String lexeme) {
    this(span, lexeme, null, null);
  }

  public TokenImpl(Span span, String lexeme, String lemma, String tag) {
    this.span = span;
    this.lexeme = lexeme;
    this.lemma = lemma;
    this.tag = tag;
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
  public String getLemma() {
    return lemma;
  }

  public void setLemma(String lemma) {
    this.lemma = lemma;
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
  
  public void addContext (Analyzers analyzer, Object object) {
    additionalContext.put(analyzer, object);
  }
  
  public Object getAdditionalContext(Analyzers analyzer) {
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
          && Objects.equal(this.lemma, that.lemma)
          && Objects.equal(this.span, that.span);
    }
    return false;
  }

  @Override
  public String toString() {

    return Objects.toStringHelper(this).add("lxm", lexeme).add("lm", lemma)
    // .add("span", span)
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(lexeme, lemma, span);
  }
}
