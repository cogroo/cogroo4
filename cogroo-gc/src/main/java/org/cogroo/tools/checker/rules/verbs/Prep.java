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

import java.util.ArrayList;
import java.util.List;

public class Prep {

  private String preposition;

  private String meaning;
  
  private List<String> objects;

  public String getPreposition() {
    return preposition;
  }

  public void setPreposition(String preposition) {
    this.preposition = preposition;
  }

  public String getMeaning() {
    return meaning;
  }

  public void setMeaning(String meaning) {
    this.meaning = meaning;
  }

  public List<String> getObjects() {
    return objects;
  }

  public void setObjects(String list) {
    String[] words = list.split(",\\s?");
    List<String> obj = new ArrayList<String>();
    for (String string : words) {
      obj.add(string);
    }
    this.objects = obj;
  }

}
