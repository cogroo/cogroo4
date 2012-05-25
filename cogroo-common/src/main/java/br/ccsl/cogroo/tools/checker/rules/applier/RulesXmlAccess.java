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

package br.ccsl.cogroo.tools.checker.rules.applier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import br.ccsl.cogroo.tools.checker.rules.exception.RulesException;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Rule;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Rules;

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
  private RulesXmlAccess(String xmlFile, String schemaFile) {
    this.xmlFile = xmlFile;
    this.schemaName = schemaFile;
    this.loadSchema();
  }
  
  public static synchronized RulesAccess getInstance() {
    if(instance == null) {
      String xml = RulesXmlAccess.class.getClassLoader().getResource("rules/rules.xml").getFile();
      String xsd = RulesXmlAccess.class.getClassLoader().getResource("rules/schema/rules.xsd").getFile();
      instance = new RulesXmlAccess(xml, xsd);
    }
    return instance;
  }

  private String schemaName;

  private Schema schema;

  private String xmlFile;

  private void loadSchema() {
    if (this.schema == null) {
      StreamSource ss = new StreamSource(this.schemaName);
      SchemaFactory sf = SchemaFactory
          .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      try {
        this.schema = sf.newSchema(ss);
      } catch (SAXException saxe) {
        this.schema = null;
        throw new RulesException("Could not read file " + this.schemaName);
      }
    }
  }

  /**
   * Checks the xml against the xsd.
   */
  public void validate() {
    Validator validator = this.schema.newValidator();
    try {
      validator.validate(new StreamSource(this.xmlFile));
    } catch (SAXException e) {
      throw new RulesException("Rules file does not conform to the schema");
    } catch (IOException e) {
      throw new RulesException("Could not read file " + this.xmlFile);
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

    File file = new File(this.xmlFile);
    try {
      InputStream in;
      in = new FileInputStream(file);
      return unmarshal(in);
    } catch (JAXBException e) {
      throw new RulesException("Invalid rules file: " + this.xmlFile, e);
    } catch (FileNotFoundException e) {
      throw new RulesException("Could not find rules file: " + this.xmlFile, e);
    }
  }

  private Rules unmarshal(InputStream inputStream) throws JAXBException {
    String packageName = Rules.class.getPackage().getName();
    JAXBContext jc = JAXBContext.newInstance(packageName);
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
