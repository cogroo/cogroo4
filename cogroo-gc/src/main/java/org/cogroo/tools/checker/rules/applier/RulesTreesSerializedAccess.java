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

package org.cogroo.tools.checker.rules.applier;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import org.apache.log4j.Logger;
import org.cogroo.tools.checker.rules.util.RulesTreesSerializer;


/**
 * Read the rules from a serialized file (faster)
 * 
 * @author Marcelo Suzumura
 */
public class RulesTreesSerializedAccess implements RulesTreesAccess {

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger
      .getLogger(RulesTreesSerializedAccess.class);

  public RulesTreesSerializedAccess(String serializedRulesFile) {
    this.serializedRulesFile = serializedRulesFile;
  }

  private RulesTrees rulesTrees;
  private String serializedRulesFile;

  public void persist(RulesTrees newRulesTrees) {
    if (newRulesTrees == null) {
      throw new IllegalArgumentException();
    }
    long start = System.nanoTime();
    try {
      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
          serializedRulesFile));
      out.writeObject(newRulesTrees);
      LOGGER.info("Rules trees serialized in " + (System.nanoTime() - start)
          / 1000000 + "ms");
    } catch (IOException e) {
      LOGGER.warn("Could not serialize rules trees");
    }
  }

  public RulesTrees getTrees() {
    if (this.rulesTrees == null) {
      LOGGER.info("Reading from serialized rules trees");
      long start = System.nanoTime();
      try {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(
            serializedRulesFile));
        this.rulesTrees = (RulesTrees) in.readObject();
        LOGGER.info("Rules trees read in " + (System.nanoTime() - start)
            / 1000000 + "ms");
      } catch (StreamCorruptedException e) {
        LOGGER.info("Failed to read rules. Will create it from scratch");
        RulesTreesSerializer.serialize();
        try {
          ObjectInputStream in = new ObjectInputStream(new FileInputStream(
              serializedRulesFile));
          this.rulesTrees = (RulesTrees) in.readObject();
          LOGGER.info("Rules trees read in " + (System.nanoTime() - start)
              / 1000000 + "ms");
        } catch (Exception e1) {
          // sorry this can fail...
        }
      } catch (IOException e) {
        LOGGER.warn("Could not read the serialized rules trees.", e);
      } catch (ClassNotFoundException e) {
        LOGGER.warn("Could not read the serialized rules trees.", e);
      }
    }
    return this.rulesTrees;
  }

}
