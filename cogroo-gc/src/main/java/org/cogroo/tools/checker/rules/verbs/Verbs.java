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
import java.util.List;
import java.util.regex.Pattern;

public class Verbs {

  private List<VerbPlusPreps> verbsList;
  
  private static final Pattern PREP_LINE = Pattern.compile("\\w+:");
  
  public Verbs() {
    parseConfiguration();
  }

  public static void main(String[] args) {
    Verbs verb = new Verbs();
  }

  public void parseConfiguration() {
     InputStream input = Verbs.class.getClassLoader().getResourceAsStream(
     "rules/regencia/verbs.txt");

    verbsList = new ArrayList<VerbPlusPreps>();
    VerbPlusPreps vp = null;

    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(input,
          "UTF-8"));

      while (reader.ready()) {
        String line = reader.readLine();

        if (line.length() > 0) {
          if (line.charAt(0) == '#') {
            vp = new VerbPlusPreps();
            vp.setVerb(line.substring(1));
            verbsList.add(vp);

          } else if (PREP_LINE.matcher(line).matches()) {
            Prep prep = new Prep();
            String[] words = line.split(":\\s?", 3);
            prep.setPreposition(words[0].substring(1));
            prep.setMeaning(words[1]);
            prep.setObjects(words[2]);
            vp.addPreps(prep);
          }
        }
      }

    } catch (UnsupportedEncodingException e) {
      // Shouldn't happen because every system contains the utf-8 encode.
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public VerbPlusPreps getVerb(String verb) {

    int low = 0;
    int high = verbsList.size() - 1;
    int mid;

    while (low <= high) {
      mid = (low + high) / 2;

      if (verbsList.get(mid).getVerb().compareTo(verb) < 0)
        low = mid + 1;
      else if (verbsList.get(mid).getVerb().compareTo(verb) > 0)
        high = mid - 1;
      else
        return verbsList.get(mid);
    }

    return null;

  }
}
