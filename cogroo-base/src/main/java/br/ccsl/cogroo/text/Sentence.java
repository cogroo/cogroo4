package br.ccsl.cogroo.text;

import java.util.List;

import opennlp.tools.util.Span;

public interface Sentence {

  /**
   * @return the <code>String</code> of the sentence
   */
  public String getText();

  public Span getSpan();

  public void setSpan(Span span);

  public List<Token> getTokens();

  public void setTokens(List<Token> tokens);
  
  public List<Chunk> getChunks();
  
  public void setChunks(List<Chunk> chunks);
  
  public List<SyntacticChunk> getSyntacticChunks();

  public void setSyntacticChunks(List<SyntacticChunk> syntacticChunks);

}