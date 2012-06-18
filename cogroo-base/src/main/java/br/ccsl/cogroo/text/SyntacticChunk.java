package br.ccsl.cogroo.text;

import java.util.List;

public interface SyntacticChunk {

  public String getTag();
  
  public void setTag(String tag);
  
  public int getStart();
  
  public int getEnd();

  public List<Token> getTokens();
  
}
