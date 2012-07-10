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
package org.cogroo.tools.checker.rules.util;

import java.io.File;

import org.apache.log4j.Logger;
import org.cogroo.tools.checker.rules.applier.RulesProvider;
import org.cogroo.tools.checker.rules.applier.RulesTreesBuilder;
import org.cogroo.tools.checker.rules.applier.RulesTreesFromScratchAccess;
import org.cogroo.tools.checker.rules.applier.RulesTreesSerializedAccess;
import org.cogroo.tools.checker.rules.applier.RulesXmlAccess;


/**
 * Loads the rules from XML and persist to a binary file. (faster)
 * 
 * @author Marcelo Suzumura
 */
public class RulesTreesSerializer {
  
  protected static final Logger LOGGER = Logger
      .getLogger(RulesTreesSerializer.class);

  public static void serialize() {
    RulesProvider xmlProvider = new RulesProvider(RulesXmlAccess.getInstance(), false);
    
    RulesTreesFromScratchAccess IN = new RulesTreesFromScratchAccess(new RulesTreesBuilder(xmlProvider));
    
    RulesTreesSerializedAccess OUT = new RulesTreesSerializedAccess("rules.serialized");
    
    OUT.persist(IN.getTrees());
  }
  
  public static void serializeIfAbsent() {
	  if(!RulesProperties.isReadFromSerialized()) {
		  LOGGER.info("Will not create serialized rules file because will use the XML version only.");
		  return;
	  }
	  
    File xml = new File(RulesProperties.getRulesFile());
    File bin = new File(RulesProperties.getSerializedTreesFile());
    
    if(xml.exists()) {
      if(!bin.exists() || RulesProperties.REREAD_FROM_SERIALIZED) {
        serialize();
      }      
    } else {
      LOGGER.warn("Failed to create binary rules file because the xml file is missing.");
    }
    
    if(!bin.exists()) {
      LOGGER.warn("Failed to create binary rules file.");
    }
    
  }

  public static void main(String[] args) {
    serialize();
  }

}
