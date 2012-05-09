package br.ccsl.cogroo.text;

import java.util.List;

import com.google.common.base.Objects;

import opennlp.tools.util.Span;

public class Sentence {
  
  private Span span;

  private List<Token> tokens;
  
  
  public Sentence(Span span) {
    this(span, null);
  }
  
  public Sentence(Span span, List<Token> tokens) {
    this.span = span;
    this.tokens = tokens;
  }
  
  public String getCoveredSentence(String text) {
    return span.getCoveredText(text).toString();
  }

  public Span getSpan() {
    return span;
  }

  public void setSpan(Span span) {
    this.span = span;
  }

  public List<Token> getTokens() {
    return tokens;
  }

  public void setTokens(List<Token> tokens) {
    this.tokens = tokens;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Sentence) {
      Sentence that = (Sentence) obj;
          return Objects.equal(this.tokens, that.tokens)
          && Objects.equal(this.span, that.span);
    }
    return false;
  }
  
  @Override
  public String toString() {

    return Objects.toStringHelper(this).add("span", span).add("tk", tokens)
        .toString();
  }
  
  @Override
  public int hashCode() {
    return Objects.hashCode(span, tokens);
  }
  
  
  
}
