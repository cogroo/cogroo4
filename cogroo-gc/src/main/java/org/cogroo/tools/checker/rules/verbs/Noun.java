package org.cogroo.tools.checker.rules.verbs;

public class Noun {

  private String noun;
  private int span;

  public Noun(String noun, int span) {
    this.noun = noun;
    this.span = span;
  }
  
  public String getNoun() {
    return noun;
  }
  
  public int getSpan() {
    return span;
  }

}
