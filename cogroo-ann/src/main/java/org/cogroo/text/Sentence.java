package org.cogroo.text;

import java.util.List;

import org.cogroo.text.tree.Node;


public interface Sentence {

  /**
   * @return the <code>String</code> of the sentence
   */
  public String getText();

  public int getStart();
  
  public int getEnd();
  
  public void setBoundaries(int start, int end);

  public List<Token> getTokens();

  public void setTokens(List<Token> tokens);
  
  public List<Chunk> getChunks();
  
  public void setChunks(List<Chunk> chunks);
  
  public List<SyntacticChunk> getSyntacticChunks();

  public void setSyntacticChunks(List<SyntacticChunk> syntacticChunks);
  
  public Node asTree ();
}