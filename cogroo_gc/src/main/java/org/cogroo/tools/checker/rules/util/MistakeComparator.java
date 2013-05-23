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
package org.cogroo.tools.checker.rules.util;

import java.util.Comparator;

import opennlp.tools.util.Span;

import org.cogroo.entities.Mistake;

/**
 * Utility class to compare {@link Mistake} objects
 */
public class MistakeComparator implements Comparator<Mistake> {

  public int compare(Mistake m1, Mistake m2) {

    // First we check if they overlap. If they don't we simply use the start position
    Span a = new Span(m1.getStart(), m1.getEnd());
    Span b = new Span(m2.getStart(), m2.getEnd());
    if(!a.intersects(b)) {
      return a.compareTo(b);
    }
    
    // they intersect, so we should sort using the priority. The higher the
    // number, the higher th priority
    if(m1.getRulePriority() > m2.getRulePriority()) {
      return -1;
    } else if(m1.getRulePriority() < m2.getRulePriority()) {
      return 1;
    } else {
      // equal priority! so we try to use the rule id
      if(m2.getRuleIdentifier().startsWith("xml:")) {
        Integer id1 = new Integer(m1.getRuleIdentifier().substring(4));
        Integer id2 = new Integer(m2.getRuleIdentifier().substring(4));
        
        return id1.compareTo(id2);
      }
      return m1.getRuleIdentifier().compareTo(m2.getRuleIdentifier());
    }
  }

}
