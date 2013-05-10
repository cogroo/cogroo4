package org.cogroo.tools.checker.rules.applier;

import org.cogroo.entities.Token;

public class NullToken extends Token {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static final NullToken instance = new NullToken();
  
  public void setLexeme(String lexeme) {
    // do nothing
  }
  
  private NullToken() {
    
  }
  
  public static Token instance() {
    return instance;
  }

  @Override
  public String toString() {
    return "NULL";
  }
}
