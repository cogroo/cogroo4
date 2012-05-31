package br.ccsl.cogroo.text;


public interface Chunk {

  public String getTag();
  
  public void setTag(String tag);
  
  public int getStart();
  
  public int getEnd();
  
  public void setBoundaries(int start, int end);
  
  public void setHeadIndex(int index);
}
