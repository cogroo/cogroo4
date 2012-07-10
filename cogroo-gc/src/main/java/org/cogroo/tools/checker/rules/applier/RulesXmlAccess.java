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
