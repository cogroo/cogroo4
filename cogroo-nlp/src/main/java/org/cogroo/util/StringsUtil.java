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

package org.cogroo.util;

public class StringsUtil {
  public static final boolean isNullOrEmpty(String str) {
    if (str == null || str.isEmpty()) {
      return true;
    }
    return false;
  }
  
  public static final String nullToEmpty(String str) {
    if(str == null) {
      return "";
    }
    return str;
  }

  public static String join(String[] lemmas, String sep) {
    if(lemmas == null) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < lemmas.length - 1; i++) {
      sb.append(lemmas[i]).append(sep);
    }
    sb.append(lemmas[lemmas.length-1]);
    
    return sb.toString();
  }
}
