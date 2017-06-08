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

import opennlp.tools.util.TokenTag;


public class TokenTagUtil {

  public static void extract(TokenTag[] wt, String[] word, String[] tag, String[] chunks) {
    for (int i = 0; i < wt.length; i++) {
      word[i] = wt[i].getToken();
      if(wt[i].getAddtionalData() == null || wt[i].getAddtionalData().length == 0) {
        String t = wt[i].getTag();
        int bar = t.indexOf("|");

        tag[i] = t.substring(0, bar);
        chunks[i] = t.substring(bar+1);
      } else {
        tag[i] = wt[i].getTag();
        chunks[i] = wt[i].getAddtionalData()[0];
      }
    }
  }


}
