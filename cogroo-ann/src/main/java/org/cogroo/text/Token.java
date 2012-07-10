package org.cogroo.text;

import org.cogroo.config.Analyzers;

/**
 * The <code>Token</code> interface is responsible for obtaining each component
 * of a token
 */
public interface Token {

  public int getStart();
  
  public int getEnd();

  public String[] getLemmas();

  public String getLexeme();

  public String getPOSTag();

  public String getFeatures();
  
  public String getChunkTag();

  public void setFeatures(String features);

  public void setLemmas(String[] lemmas);

  public void setLexeme(String lexeme);

  public void setPOSTag(String tag);

  public void setBoundaries(int start, int end);
  
  public void setChunkTag(String string);

  public void addContext(Analyzers contractionFinder, String value);

  public Object getAdditionalContext(Analyzers analyzers);

}