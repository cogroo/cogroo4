/**
 * Copyright (C) 2008 CoGrOO Team (cogroo AT gmail DOT com)
 * 
 * CoGrOO Team (cogroo AT gmail DOT com)
 * LTA, PCS (Computer and Digital Systems Engineering Department),
 * Escola Polit�cnica da Universidade de S�o Paulo
 * Av. Prof. Luciano Gualberto, trav. 3, n. 380
 * CEP 05508-900 - S�o Paulo - SP - BRAZIL
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

package br.ccsl.cogroo.tools.checker.rules.util;

import java.io.File;

import org.apache.log4j.Logger;

import br.ccsl.cogroo.tools.checker.rules.applier.RulesProvider;
import br.ccsl.cogroo.tools.checker.rules.applier.RulesTreesBuilder;
import br.ccsl.cogroo.tools.checker.rules.applier.RulesTreesFromScratchAccess;
import br.ccsl.cogroo.tools.checker.rules.applier.RulesTreesSerializedAccess;
import br.ccsl.cogroo.tools.checker.rules.applier.RulesXmlAccess;

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
