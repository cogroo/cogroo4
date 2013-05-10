package org.cogroo.tools.checker.rules.applier;

import java.util.Collections;

import org.cogroo.entities.Chunk;
import org.cogroo.entities.SyntacticChunk;

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

}
