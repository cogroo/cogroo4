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
