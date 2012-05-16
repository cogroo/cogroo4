package br.ccsl.cogroo.analyzer;

public class InitializationException extends RuntimeException {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public InitializationException(String message, Throwable throwable) {
    super(message, throwable);
  }
  
  public InitializationException(String message) {
    super(message);
  }
}
