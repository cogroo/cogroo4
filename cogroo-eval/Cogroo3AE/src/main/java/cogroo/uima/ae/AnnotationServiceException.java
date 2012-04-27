package cogroo.uima.ae;

public class AnnotationServiceException extends Exception {

  private static final long serialVersionUID = 1L;

  public AnnotationServiceException(String msg, Exception inner) {
    super(msg, inner);
  }

  public AnnotationServiceException(String msg) {
    super(msg);
  }
}
