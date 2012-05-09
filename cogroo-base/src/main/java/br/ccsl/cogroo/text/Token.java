package br.ccsl.cogroo.text;

import opennlp.tools.util.Span;

public interface Token {

  public Span getSpan();

  public String getLemma();

  public String getLexeme();
  
  public String getPOSTag();

}