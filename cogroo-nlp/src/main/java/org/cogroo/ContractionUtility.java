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
package org.cogroo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import opennlp.tools.formats.ad.PortugueseContractionUtility;

public class ContractionUtility extends PortugueseContractionUtility {

  private static final Map<String, String[]> REVERSE_CONTRACTIONS;

  static {
    Map<String, String[]> reverse = new HashMap<String, String[]>(
        CONTRACTIONS.size());
    for (String expanded : CONTRACTIONS.keySet()) {
      reverse.put(CONTRACTIONS.get(expanded), expanded.split("\\+"));
    }
    REVERSE_CONTRACTIONS = Collections.unmodifiableMap(reverse);
  }

  public static String[] expand(String contraction) {
    String lowercase = contraction.toLowerCase();
    if (REVERSE_CONTRACTIONS.containsKey(lowercase)) {
      return REVERSE_CONTRACTIONS.get(lowercase);
    }
    return null;
  }

  public static Set<String> getContractionSet() {
    return REVERSE_CONTRACTIONS.keySet();
  }
}
