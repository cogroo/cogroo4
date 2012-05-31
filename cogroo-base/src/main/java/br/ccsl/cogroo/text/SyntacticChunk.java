package br.ccsl.cogroo.text;

public interface SyntacticChunk {

  public String getTag();
  
  public void setTag(String tag);
  
  public int getStart();
  
  public int getEnd();
  
}
