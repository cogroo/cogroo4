package org.cogroo.tools.checker.rules.applier;

import java.util.Collections;
import java.util.List;

import org.cogroo.entities.Chunk;
import org.cogroo.entities.SyntacticChunk;
import org.cogroo.entities.Token;

public class NullSyntacticChunk extends SyntacticChunk {

  private static final NullSyntacticChunk instance = new NullSyntacticChunk();
  
  private NullSyntacticChunk() {
    super(Collections.<Chunk>emptyList());
  }
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  public static SyntacticChunk instance() {
    return instance;
  }
  
  @Override
  public String toString() {
    return "NULL";
  }
  
  @Override
  public List<Token> getTokens() {
    return Collections.singletonList(NullToken.instance());
  }

}
