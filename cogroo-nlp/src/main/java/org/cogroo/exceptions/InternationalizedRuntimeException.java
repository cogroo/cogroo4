/**
 * Copyright (C) 2012 cogroo <cogroo@cogroo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cogroo.exceptions;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class InternationalizedRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 6486810457574147193L;

  private String resourceBundleName;

  private String messageKey;

  private Object[] arguments;

  private Throwable cause;

  public InternationalizedRuntimeException() {
    this(null, null, null, null);
  }

  public InternationalizedRuntimeException(Throwable aCause) {
    this(null, null, null, aCause);
  }

  public InternationalizedRuntimeException(String aResourceBundleName,
      String aMessageKey, Object[] aArguments) {
    this(aResourceBundleName, aMessageKey, aArguments, null);
  }

  public InternationalizedRuntimeException(String aResourceBundleName,
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
      ResourceBundle bundle = ResourceBundle.getBundle(getResourceBundleName(),
          aLocale);
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

  public synchronized Throwable initCause(Throwable aCause) {
    cause = aCause;
    return this;
  }

}
