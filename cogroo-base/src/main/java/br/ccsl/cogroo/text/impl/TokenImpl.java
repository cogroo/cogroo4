package br.ccsl.cogroo.text.impl;

import java.util.List;

import br.ccsl.cogroo.text.Token;

import com.google.common.base.Objects;

import opennlp.tools.util.Span;

public class TokenImpl implements Token {
  
  private Span span;
  private String lexeme;
  private String lemma;
  private String tag;
  
  public TokenImpl(Span span, String lexeme) {
    this(span, lexeme, null, null);
  }
  
  public TokenImpl(Span span, String lexeme, String lemma, String tag) {
    this.span = span;
    this.lexeme = lexeme;
    this.lemma = lemma;
    this.tag = tag;
  }

  /* (non-Javadoc)
   * @see br.ccsl.cogroo.TokenI#getSpan()
   */
  public Span getSpan() {
    return span;
  }
  
  public void setSpan(Span span) {
    this.span = span;
  }
  
  /* (non-Javadoc)
   * @see br.ccsl.cogroo.TokenI#getLemma()
   */
  public String getLemma() {
    return lemma;
  }
  
  public void setLemma(String lemma) {
    this.lemma = lemma;
  }
  
  /* (non-Javadoc)
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
  
  public String[] lexemesToString (List<Token> tokens) {
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
