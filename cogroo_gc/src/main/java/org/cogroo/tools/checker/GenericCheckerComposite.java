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

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;


public abstract class GenericCheckerComposite<T> implements GenericChecker<T> {

  private final static String ID_PREFIX = "";
  protected SortedSet<GenericChecker<T>> mChildCheckers;
  private boolean mAllowOverlaps;
  protected static final Logger LOGGER = Logger
      .getLogger(GenericCheckerComposite.class);

  public GenericCheckerComposite(List<GenericChecker<T>> aChildCheckers) {
    this(aChildCheckers, false);
  }

  public GenericCheckerComposite(List<GenericChecker<T>> aChildCheckers, boolean aAllowOverlaps) {
    SortedSet<GenericChecker<T>> children = new TreeSet<GenericChecker<T>>(
        new Comparator<GenericChecker<T>>() {
          public int compare(GenericChecker<T> o1, GenericChecker<T> o2) {
            if (o1.equals(o2))
              return 0;

            if (o2.getPriority() - o1.getPriority() == 0) {
              return 1;
            }
            return o2.getPriority() - o1.getPriority();
          }
        });
    children.addAll(aChildCheckers);
    mChildCheckers = Collections.unmodifiableSortedSet(children);
    mAllowOverlaps = aAllowOverlaps;
  }

  public String getIdPrefix() {
    return ID_PREFIX;
  }

  public void ignore(String id) {
    for (GenericChecker<T> checker : mChildCheckers) {
      if (id.startsWith(checker.getIdPrefix())) {
        checker.ignore(id);
        break;
      }
    }
  }

  public void resetIgnored() {
    for (GenericChecker<T> checker : mChildCheckers) {
      checker.resetIgnored();
    }
  }

  public int getPriority() {
    return 0;
  }

  public List<RuleDefinition> getRulesDefinition() {
    List<RuleDefinition> definitions = new LinkedList<RuleDefinition>();
    for (GenericChecker<T> d : mChildCheckers) {
      definitions.addAll(d.getRulesDefinition());
    }
    return definitions;
  }

}
