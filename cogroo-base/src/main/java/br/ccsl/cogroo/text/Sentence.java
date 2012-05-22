package br.ccsl.cogroo.text;

import java.util.List;

import com.google.common.base.Objects;

import opennlp.tools.util.Span;

/**
 * The <code>Sentence</code> class contains the position of the sentence in the
 * text and the list of word in it.
 */
public class Sentence {

  /** the position of the sentence in the text */
  private Span span;

  /** the list every token in the sentence */
  private List<Token> tokens;

  public Sentence(Span span) {
    this(span, null);
  }

  public Sentence(Span span, List<Token> tokens) {
    this.span = span;
    this.tokens = tokens;
  }

  /**
   * @return the <code>String</code> of the sentence
   */
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
