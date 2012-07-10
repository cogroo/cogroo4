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
import java.util.List;

import org.cogroo.entities.Mistake;


/**
 * Interface to implement checkers. Checkers should be added to
 * CheckerComposite.
 */
public interface GenericChecker<T> {

  /**
   * Each checker should have IDs with a fixed prefix so CheckerComposite can
   * route the ignore message to the correct checker.
   * 
   * @return the id prefix
   */
  public String getIdPrefix();

  /**
   * Check the sentence for errors.
   * 
   * @param sentence
   *          the sentence
   * @return errors
   */
  public List<Mistake> check(T sentence);

  /**
   * The checker should ignore the rule with the given id. IDs of a checker have
   * a fixed prefix.
   * 
   * @param id
   *          the rule id to be ignored
   */
  public void ignore(String id);

  /**
   * Reset all ignored rules
   */
  public void resetIgnored();

  /**
   * Priority of this checker. The higher is the number the higher is its
   * priority
   * 
   * @return the priority of this checker
   */
  public int getPriority();

  /**
   * Returns the descriptions of the rules implemented by this checker.
   * 
   * @return a list of descriptions
   */
  public Collection<RuleDefinitionI> getRulesDefinition();

}
