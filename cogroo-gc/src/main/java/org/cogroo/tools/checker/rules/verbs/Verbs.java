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
package org.cogroo.tools.checker.rules.verbs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.cogroo.analyzer.InitializationException;

/**
 * The <code>Verbs</code> class reads a verb's list as input and turns it into a
 * <tt>List</tt>
 * 
 */
public class Verbs {

  /**
   * Structure that stores from an input list verbs, its acceptable prepositions
   * and in which case to use them
   */
  private final Map<String, VerbPlusPreps> verbsMap;

  private static final Pattern PREP_LINE = Pattern.compile("^\\w+:.*");

  public Verbs() {
    verbsMap = Collections.unmodifiableMap(parseConfiguration());
  }

  public Map<String, VerbPlusPreps> parseConfiguration() {
    InputStream input = Verbs.class.getClassLoader().getResourceAsStream(
        "rules/regencia/verbs.txt");

    Map<String, VerbPlusPreps> map = new HashMap<String, VerbPlusPreps>();

    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(input,
          "UTF-8"));

      List<Prep> preps = null;
      String verb = null;

      while (reader.ready()) {
        String line = reader.readLine();

        if (line.length() > 0) {

          if (line.charAt(0) == '#') {

            if (preps != null) {
              map.put(verb, new VerbPlusPreps(preps));
              verb = null;
              preps = null;
            }

            preps = new ArrayList<Prep>();
            verb = line.substring(1).trim();

          } else if (PREP_LINE.matcher(line).matches()) {
            Prep prep = new Prep();
            String[] words = line.split(":\\s?", 3);
            if (words != null) {
              prep.setPreposition(words[0]);
              if (words.length > 1) {
                prep.setMeaning(words[1]);
                if (words.length > 2)
                  prep.setObjects(words[2]);
              }
            }
            preps.add(prep);
          }
        }
      }
      return map;

    } catch (UnsupportedEncodingException e) {
      // Shouldn't happen because every system contains the utf-8 encode.
      throw new InitializationException(
          "Enconding problem while reading the verbs.txt file", e);
    } catch (IOException e) {
      throw new InitializationException("Could not read the verbs.txt file", e);
    }
  }

  public VerbPlusPreps getVerb(String verb) {
    return this.verbsMap.get(verb);
  }

}
