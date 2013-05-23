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
