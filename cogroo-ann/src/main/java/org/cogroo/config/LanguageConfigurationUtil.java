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
package org.cogroo.config;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.cogroo.analyzer.InitializationException;
import org.cogroo.config.LanguageConfiguration;
import org.cogroo.config.ObjectFactory;


import com.google.common.io.Closeables;

/**
 * The <code>LanguageConfigurationUtil</code> class is responsible for
 * identifying the language to be used.
 * 
 */
public class LanguageConfigurationUtil {

  /**
   * 
   * @param locale
   *          contains the language to be used
   * @throws InitializationException
   * @return the file .xml converted to Java, it contains which analyzers to add
   *         into the pipe according to the language to be used
   */
  public static LanguageConfiguration get(Locale locale) {

    String file = generateName(locale);

    InputStream in = LanguageConfigurationUtil.class.getClassLoader()
        .getResourceAsStream(file);

    if (in != null) {
      LanguageConfiguration lc = get(in);
      Closeables.closeQuietly(in);
      return lc;
    } else
      throw new InitializationException(
          "Couldn't locate configuration for locale: " + locale
              + " The expected file was: " + file);
  }

  /**
   * Creates a {@link LanguageConfiguration} from a {@link InputStream}, which
   * remains opened.
   * 
   * @param configuration
   *          the input stream
   * @return a {@link LanguageConfiguration}
   */
  public static LanguageConfiguration get(InputStream configuration) {
    try {
      return unmarshal(configuration);
    } catch (JAXBException e) {
      throw new InitializationException("Invalid configuration file.", e);
    }
  }

  /**
   * Generates the name of the file to be used according to the language chosen.
   * 
   * @param locale
   *          contains the language that will be used
   * @return the <code>String</code> which names the .xml file to be used
   *         according to the language
   */
  private static String generateName(Locale locale) {
    StringBuilder str = new StringBuilder();
    str.append("models_").append(locale.getLanguage());

    if (locale.getCountry() != null && !locale.getCountry().isEmpty())
      str.append("_").append(locale.getCountry());

    str.append(".xml");

    return str.toString();
  }

  private static LanguageConfiguration unmarshal(InputStream inputStream)
      throws JAXBException {
    String packageName = LanguageConfiguration.class.getPackage().getName();
    ClassLoader cl = ObjectFactory.class.getClassLoader();
    JAXBContext jc = JAXBContext.newInstance(packageName, cl);
    Unmarshaller u = jc.createUnmarshaller();
    return (LanguageConfiguration) u.unmarshal(inputStream);
  }

  public static void main(String[] args) throws FileNotFoundException,
      JAXBException {
    LanguageConfiguration lc = LanguageConfigurationUtil
        .get(new Locale("pt_BR"));

    System.out.println(lc.getLocale());

    System.out.println(lc.getModel().get(0).getType());
    System.out.println(lc.getModel().get(0).getValue());

    System.out.println(lc.getModel().get(1).getType());
    System.out.println(lc.getModel().get(1).getValue());
  }
}
