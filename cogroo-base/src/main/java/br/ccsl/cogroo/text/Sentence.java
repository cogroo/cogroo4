package br.ccsl.cogroo.text;

import java.util.List;

import opennlp.tools.util.Span;

public interface Sentence {

  /**
   * @return the <code>String</code> of the sentence
   */
  public abstract String getText();

  public abstract Span getSpan();

  public abstract void setSpan(Span span);

  public abstract List<Token> getTokens();

  public abstract void setTokens(List<Token> tokens);

}