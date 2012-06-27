package br.ccsl.cogroo.text;

import java.util.List;


public interface Chunk {

  public String getTag();
  
  public void setTag(String tag);
  
  public List<Token> getTokens();
  
  public int getStart();
  
  public int getEnd();
  
  public void setBoundaries(int start, int end);
  
  public void setHeadIndex(int index);
  
  public int getHeadIndex();
}
