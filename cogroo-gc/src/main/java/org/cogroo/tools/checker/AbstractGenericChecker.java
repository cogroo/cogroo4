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

    return new MistakeImpl(ruleDefinition.getId(), getPriority(), ruleDefinition.getMessage(),
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
