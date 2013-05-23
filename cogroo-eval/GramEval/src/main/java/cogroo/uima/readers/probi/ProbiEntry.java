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
package cogroo.uima.readers.probi;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProbiEntry {
  private boolean containsError;
  private String category;
  private String sentence;

  public ProbiEntry(String cat, String sentence) {
    super();
    this.containsError = true;
    this.category = cat;
    this.sentence = sentence;
  }

  public ProbiEntry(String sentence) {
    super();
    this.containsError = false;
    this.sentence = sentence;
  }

  public boolean isContainsError() {
    return containsError;
  }

  public String getCategory() {
    return category;
  }

  public String getSentence() {
    return sentence;
  }

  private static Pattern noErrorPattern = Pattern.compile("^([^\\|]+)$");
  private static Pattern errorPattern = Pattern
      .compile("^(\\w\\w\\w)\\|([^\\|]+)$");

  public static ProbiEntry fromString(String string) {
    // no error
    Matcher noError = noErrorPattern.matcher(string);
    if (noError.matches()) {
      return new ProbiEntry(noError.group(1));
    }

    Matcher error = errorPattern.matcher(string);
    if (error.matches()) {
      return new ProbiEntry(error.group(1), error.group(2));
    }

    throw new RuntimeException("Should not get here!");
  }

  @Override
  public String toString() {
    if (!isContainsError()) {
      return " S: " + getSentence();
    }
    StringBuilder s = new StringBuilder("*S: ");
    s.append(getCategory() + " " + getSentence());

    return s.toString();
  }
}
