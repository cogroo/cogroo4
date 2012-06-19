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

package br.ccsl.cogroo.tools.checker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import br.ccsl.cogroo.entities.Mistake;
import br.ccsl.cogroo.entities.Sentence;
import br.ccsl.cogroo.tools.checker.rules.util.MistakeComparator;

public class CheckerComposite implements Checker {

  private final static String ID_PREFIX = "";
  private SortedSet<Checker> mChildCheckers;
  private boolean mAllowOverlaps;
  protected static final Logger LOGGER = Logger
      .getLogger(CheckerComposite.class);

  private static final MistakeComparator MISTAKE_COMPARATOR = new MistakeComparator();

  public CheckerComposite(List<Checker> aChildCheckers) {
    this(aChildCheckers, false);
  }

  public CheckerComposite(List<Checker> aChildCheckers, boolean aAllowOverlaps) {
    SortedSet<Checker> children = new TreeSet<Checker>(
        new Comparator<Checker>() {
          public int compare(Checker o1, Checker o2) {
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

  public List<Mistake> check(Sentence sentence) {
    List<Mistake> mistakes = new LinkedList<Mistake>();

    boolean[] occupied = new boolean[sentence.getSentence().length()];

    for (Checker child : mChildCheckers) {
      List<Mistake> mistakesFromChild = child.check(sentence);
      mistakes.addAll(addFilteredMistakes(mistakesFromChild, occupied,
          sentence.getOffset()));
    }

    Collections.sort(mistakes, MISTAKE_COMPARATOR);

    return mistakes;
  }

  private List<Mistake> addFilteredMistakes(List<Mistake> mistakes,
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
    for (Checker checker : mChildCheckers) {
      if (id.startsWith(checker.getIdPrefix())) {
        checker.ignore(id);
        break;
      }
    }
  }

  public void resetIgnored() {
    for (Checker checker : mChildCheckers) {
      checker.resetIgnored();
    }
  }

  public int getPriority() {
    return 0;
  }

  public List<RuleDefinitionI> getRulesDefinition() {
    List<RuleDefinitionI> definitions = new LinkedList<RuleDefinitionI>();
    for (Checker d : mChildCheckers) {
      definitions.addAll(d.getRulesDefinition());
    }
    return definitions;
  }

}
