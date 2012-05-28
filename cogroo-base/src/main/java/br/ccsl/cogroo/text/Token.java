package br.ccsl.cogroo.text;

import opennlp.tools.util.Span;

/**
 * The <code>Token</code> interface is responsible for obtaining each component
 * of a token
 */
public interface Token {

  public Span getSpan();

  public String[] getLemmas();

  public String getLexeme();

  public String getPOSTag();
  
  public String getFeatures();

}