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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.cogroo.entities.Mistake;
import org.cogroo.entities.impl.MistakeImpl;

import org.cogroo.tools.checker.rules.model.Example;

public abstract class AbstractGenericChecker<T> implements GenericChecker<T> {

  protected static final Logger LOGGER = Logger
      .getLogger(AbstractGenericChecker.class);

  private Set<String> ignored = Collections
      .synchronizedSet(new HashSet<String>());

  private Map<String, RuleDefinitionI> definitions = new HashMap<String, RuleDefinitionI>();

  protected boolean isCheckRule(String ruleId) {
    return !ignored.contains(ruleId);
  }

  protected static Example createExample(String incorrect, String correct) {
    Example e = new Example();
    e.setIncorrect(incorrect);
    e.setCorrect(correct);
    return e;
  }

  protected Mistake createMistake(String ruleID, String[] suggestions,
      int start, int end, String text) {
    RuleDefinitionI ruleDefinition = getRuleDefinition(ruleID);

    return new MistakeImpl(ruleDefinition.getId(), ruleDefinition.getMessage(),
        ruleDefinition.getShortMessage(), suggestions, start, end,
        ruleDefinition.getExamples(), text);
  }

  public void ignore(String id) {
    ignored.add(id);
  }

  public void resetIgnored() {
    ignored.clear();
  }

  public AbstractGenericChecker add(RuleDefinitionI ruleDefinition) {
    this.definitions.put(ruleDefinition.getId(), ruleDefinition);
    return this;
  }

  public RuleDefinitionI getRuleDefinition(String ruleID) {
    RuleDefinitionI ruleDefinition = definitions.get(ruleID);
    if (ruleDefinition == null) {
      LOGGER.fatal("Unknow rule ID: " + ruleID);
      List<Example> empty = Collections.emptyList();
      ruleDefinition = new JavaRuleDefinition("-", "-", "-", "-", "-", "-",
          empty);
    }
    return ruleDefinition;
  }

  public Collection<RuleDefinitionI> getRulesDefinition() {
    if (definitions.isEmpty()) {
      LOGGER
          .fatal("Rules were not defined properly! Please define the rules using the add method of AbstractChecker.");
    }
    return definitions.values();
  }

}
