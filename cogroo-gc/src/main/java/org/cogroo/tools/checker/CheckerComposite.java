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
package org.cogroo.tools.checker;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.cogroo.entities.Mistake;
import org.cogroo.text.Sentence;


public class CheckerComposite extends GenericCheckerComposite<Sentence> implements Checker {

  public CheckerComposite(List<Checker> aChildCheckers,
      boolean aAllowOverlaps) {
    super(convert(aChildCheckers), aAllowOverlaps);
  }

  private static List<GenericChecker<Sentence>> convert(
      List<Checker> aChildCheckers) {
    List<GenericChecker<Sentence>> converted = new ArrayList<GenericChecker<Sentence>>(aChildCheckers.size());
    for (GenericChecker<Sentence> a : aChildCheckers) {
      converted.add(a);
    }
    return converted;
  }

  public List<Mistake> check(Sentence sentence) {
    List<Mistake> mistakes = new LinkedList<Mistake>();

    for (GenericChecker<Sentence> child : mChildCheckers) {
      List<Mistake> mistakesFromChild = child.check(sentence);
      mistakes.addAll(mistakesFromChild);
    }

    return mistakes;
  }

}
