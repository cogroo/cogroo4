/**
 * Copyright (C) 2008 CoGrOO Team (cogroo AT gmail DOT com)
 * 
 * CoGrOO Team (cogroo AT gmail DOT com)
 * LTA, PCS (Computer and Digital Systems Engineering Department),
 * Escola Politécnica da Universidade de São Paulo
 * Av. Prof. Luciano Gualberto, trav. 3, n. 380
 * CEP 05508-900 - São Paulo - SP - BRAZIL
 * 
 * http://cogroo.sourceforge.net/
 * 
 * This file is part of CoGrOO.
 * 
 * CoGrOO is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public as published by 
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CoGrOO is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with CoGrOO. If not, see <http://www.gnu.org/licenses/>.
 */

package org.cogroo.tools.checker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.cogroo.entities.Mistake;
import org.cogroo.tools.checker.rules.util.MistakeComparator;


public abstract class GenericCheckerComposite<T> implements GenericChecker<T> {

  private final static String ID_PREFIX = "";
  protected SortedSet<GenericChecker<T>> mChildCheckers;
  private boolean mAllowOverlaps;
  protected static final Logger LOGGER = Logger
      .getLogger(GenericCheckerComposite.class);

  protected static final MistakeComparator MISTAKE_COMPARATOR = new MistakeComparator();

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

  protected List<Mistake> addFilteredMistakes(List<Mistake> mistakes,
      boolean[] occupied, final int offset) {
    if (mAllowOverlaps) {
      return mistakes;
    }
    List<Mistake> mistakesNoOverlap = new ArrayList<Mistake>();
    boolean overlap = false;
    for (Mistake mistake : mistakes) {
      overlap = false;
      for (int i = mistake.getStart(); i < mistake.getEnd(); i++) {
        if (occupied[i - offset]) {
          overlap = true;
        }
      }
      if (!overlap) {
        for (int i = mistake.getStart(); i < mistake.getEnd(); i++) {
          occupied[i - offset] = true;
        }
        mistakesNoOverlap.add(mistake);
      }
    }
    return mistakesNoOverlap;
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

  public List<RuleDefinitionI> getRulesDefinition() {
    List<RuleDefinitionI> definitions = new LinkedList<RuleDefinitionI>();
    for (GenericChecker<T> d : mChildCheckers) {
      definitions.addAll(d.getRulesDefinition());
    }
    return definitions;
  }

}
