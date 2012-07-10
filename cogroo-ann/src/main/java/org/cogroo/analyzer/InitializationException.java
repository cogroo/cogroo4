package org.cogroo.analyzer;

/**
 * The <code>InitializationException</code> class is responsible for throwing
 * the exceptions, while opening files and locating streams, and then for showing its corresponding error messages.
 * 
 */
public class InitializationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public InitializationException(String message, Throwable throwable) {
    super(message, throwable);
  }

  public InitializationException(String message) {
    super(message);
  }
}
