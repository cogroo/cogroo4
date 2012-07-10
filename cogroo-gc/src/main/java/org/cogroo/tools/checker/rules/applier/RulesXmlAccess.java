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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.cogroo.tools.checker.rules.exception.RulesException;
import org.xml.sax.SAXException;

import org.cogroo.tools.checker.rules.model.ObjectFactory;
import org.cogroo.tools.checker.rules.model.Rule;
import org.cogroo.tools.checker.rules.model.Rules;

import com.google.common.io.Closeables;

/**
 * Class that provides access to the rules read from a xml file.
 */
public class RulesXmlAccess implements RulesAccess {

//  private static final Logger LOGGER = Logger.getLogger(RulesXmlAccess.class);
  private static RulesXmlAccess instance;

  private RulesXmlAccess() {
  }
  
  /**
   * Instantiates the singleton and recovers all rules from the xml file.
   */
  private RulesXmlAccess(URL xmlFile, URL schemaFile) {
    this.xmlFile = xmlFile;
    this.schemaName = schemaFile;
    this.loadSchema();
  }
  
  public static synchronized RulesAccess getInstance() {
    if(instance == null) {
      URL xml = RulesXmlAccess.class.getClassLoader().getResource("rules/rules.xml");
      URL xsd = RulesXmlAccess.class.getClassLoader().getResource("rules/schema/rules.xsd");
      instance = new RulesXmlAccess(xml, xsd);
    }
    return instance;
  }

  private URL schemaName;

  private Schema schema;

  private URL xmlFile;

  private void loadSchema() {
    if (this.schema == null) {
      InputStream in = null;
      try {
        in = this.schemaName.openStream();
        StreamSource ss = new StreamSource(in);
        SchemaFactory sf = SchemaFactory
            .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        this.schema = sf.newSchema(ss);
      } catch (SAXException saxe) {
        this.schema = null;
        throw new RulesException("Could not read file " + this.schemaName, saxe);
      } catch (IOException e) {
        throw new RulesException("Could not open schema " + this.schemaName, e);
      } finally {
        Closeables.closeQuietly(in);
      }
    }
  }

  /**
   * Checks the xml against the xsd.
   */
  public void validate() {
    Validator validator = this.schema.newValidator();
    InputStream in = null;
    try {
      in = this.xmlFile.openStream();
      validator.validate(new StreamSource(in));
    } catch (SAXException e) {
      throw new RulesException("Rules file does not conform to the schema", e);
    } catch (IOException e) {
      throw new RulesException("Could not read file " + this.xmlFile, e);
    } finally {
      Closeables.closeQuietly(in);
    }
  }

  /**
   * Gets a rule by its id.
   * 
   * @param id
   *          the id of the rule
   * @param rereadRules
   *          states if the rules must be read from the file or to reuse the
   *          in-memory representation, if it already exists
   * @return the rule
   */
  public Rule getRule(int id) {
    Rule returnRule = null;
    for (Rule rule : this.getRules().getRule()) {
      if (rule.getId() == id) {
        returnRule = rule;
        break;
      }
    }
    return returnRule;
  }

  public Rules getRules() {
    Rules rules = null;
    InputStream in = null;
    try {
      in = xmlFile.openStream();
      rules = unmarshal(in);
    } catch (JAXBException e) {
      throw new RulesException("Invalid rules file: " + this.xmlFile, e);
    } catch (FileNotFoundException e) {
      throw new RulesException("Could not find rules file: " + this.xmlFile, e);
    } catch (IOException e) {
      throw new RulesException("Could not open  file: " + this.xmlFile, e);
    } finally {
      Closeables.closeQuietly(in);
    }
    return rules;
  }

  private Rules unmarshal(InputStream inputStream) throws JAXBException {
    String packageName = Rules.class.getPackage().getName();
    JAXBContext jc = JAXBContext.newInstance(packageName, ObjectFactory.class.getClassLoader());
    Unmarshaller u = jc.createUnmarshaller();
    loadSchema();
    u.setSchema(this.schema);

    return (Rules) u.unmarshal(inputStream);
  }

  public void persist(Rules newRules) {
    throw new UnsupportedOperationException(
        "Method not implemented: RulesXMLAccess.persist");
  }

}
