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

import java.util.Collection;
import java.util.List;

import br.ccsl.cogroo.entities.Mistake;

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
