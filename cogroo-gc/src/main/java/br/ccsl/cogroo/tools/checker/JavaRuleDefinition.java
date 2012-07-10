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

import java.util.List;

import br.ccsl.cogroo.tools.checker.rules.model.Example;

/**
 * Implementation of RuleDefinitionI to be used in Java based rules. This is
 * used to display human readable information about the rules in places like
 * Cogroo Comunidade
 */
public class JavaRuleDefinition implements RuleDefinitionI {

  private final String id;
  private final String category;
  private final String group;
  private final String description;
  private final String message;
  private final String shortMessage;
  private final List<Example> examples;

  /**
   * Creates a new Java rule definition
   * 
   * @param id
   *          prefixed identifier
   * @param category
   *          rule category
   * @param group
   *          rule group
   * @param description
   *          a description
   * @param message
   *          long error message generated by this rule
   * @param shortMessage
   *          short error message generated by this rule
   * @param examples
   *          examples of this errors catch by this rule
   */
  public JavaRuleDefinition(String id, String category, String group,
      String description, String message, String shortMessage,
      List<Example> examples) {
    super();
    this.id = id;
    this.category = category;
    this.group = group;
    this.description = description;
    this.message = message;
    this.shortMessage = shortMessage;
    this.examples = examples;
  }

  public String getId() {
    return id;
  }

  public String getCategory() {
    return category;
  }

  public String getGroup() {
    return group;
  }

  public String getDescription() {
    return description;
  }

  public String getMessage() {
    return message;
  }

  public String getShortMessage() {
    return shortMessage;
  }

  public List<Example> getExamples() {
    return examples;
  }

  /**
   * Will always return RuleType.JAVA
   * 
   * @see org.cogroo.tools.checker.RuleDefinitionI#getRuleType()
   */
  public RuleType getRuleType() {
    return RuleType.JAVA;
  }

  /**
   * Will allways return false
   * 
   * @see org.cogroo.tools.checker.RuleDefinitionI#isXMLBased()
   */
  public boolean isXMLBased() {
    return false;
  }

}
