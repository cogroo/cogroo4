package org.cogroo.exceptions;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class InternationalizedException extends Exception {

  private static final long serialVersionUID = 1218000142275850583L;

  private String resourceBundleName;

  private String messageKey;

  private Object[] arguments;

  private Throwable cause;

  public InternationalizedException() {
    this(null, null, null, null);
  }

  public InternationalizedException(Throwable aCause) {
    this(null, null, null, aCause);
  }

  public InternationalizedException(String aResourceBundleName,
      String aMessageKey, Object[] aArguments) {
    this(aResourceBundleName, aMessageKey, aArguments, null);
  }

  public InternationalizedException(String aResourceBundleName,
      String aMessageKey, Object[] aArguments, Throwable aCause) {
    super();
    cause = aCause;
    resourceBundleName = aResourceBundleName;
    messageKey = aMessageKey;
    arguments = aArguments;
    // if null message and mCause is Internationalized exception, "promote"
    // message
    if (resourceBundleName == null && messageKey == null) {
      if (cause instanceof InternationalizedException) {
        resourceBundleName = ((InternationalizedException) cause)
            .getResourceBundleName();
        messageKey = ((InternationalizedException) cause).getMessageKey();
        arguments = ((InternationalizedException) cause).getArguments();
      } else if (cause instanceof InternationalizedRuntimeException) {
        resourceBundleName = ((InternationalizedRuntimeException) cause)
            .getResourceBundleName();
        messageKey = ((InternationalizedRuntimeException) cause)
            .getMessageKey();
        arguments = ((InternationalizedRuntimeException) cause).getArguments();
      }
    }
  }

  public String getResourceBundleName() {
    return resourceBundleName;
  }

  public String getMessageKey() {
    return messageKey;
  }

  public Object[] getArguments() {
    if (arguments == null)
      return new Object[0];

    Object[] result = new Object[arguments.length];
    System.arraycopy(arguments, 0, result, 0, arguments.length);
    return result;
  }

  public String getMessage() {
    return getLocalizedMessage(Locale.ENGLISH);
  }

  public String getLocalizedMessage() {
    return getLocalizedMessage(Locale.getDefault());
  }

  public String getLocalizedMessage(Locale aLocale) {
    // check for null message
    if (getMessageKey() == null)
      return null;

    try {
      // locate the resource bundle for this exception's messages
      // turn over the classloader of the current object explicitly, so that the
      // message resolving also works for derived exception classes
      ResourceBundle bundle = ResourceBundle.getBundle(getResourceBundleName(),
          aLocale, this.getClass().getClassLoader());
      // retrieve the message from the resource bundle
      String message = bundle.getString(getMessageKey());
      // if arguments exist, use MessageFormat to include them
      if (getArguments().length > 0) {
        MessageFormat fmt = new MessageFormat(message);
        fmt.setLocale(aLocale);
        return fmt.format(getArguments());
      } else
        return message;
    } catch (Exception e) {
      return "EXCEPTION MESSAGE LOCALIZATION FAILED: " + e.toString();
    }
  }

  public Throwable getCause() {
    return cause;
  }

  public boolean hasMessageKey(String messageKey) {
    if (messageKey.equals(this.getMessageKey())) {
      return true;
    }
    Throwable cause = getCause();
    if (cause != null && cause instanceof InternationalizedException) {
      return ((InternationalizedException) cause).hasMessageKey(messageKey);
    }
    return false;
  }

  public synchronized Throwable initCause(Throwable aCause) {
    cause = aCause;
    return this;
  }

}
