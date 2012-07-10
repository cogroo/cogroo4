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
package org.cogroo.interpreters;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.cogroo.tools.checker.rules.model.TagMask.Class;

import org.cogroo.interpreters.FlorestaTagInterpreter;
import org.cogroo.interpreters.TagInterpreterI;
import org.junit.Test;

public class FlorestaTagInterpreterTest {

  private static Map<String, Class> table = new HashMap<String, Class>();
  private TagInterpreterI ti = new FlorestaTagInterpreter();

  static {
    table.put("-", Class.PUNCTUATION_MARK);
    table.put("--", Class.PUNCTUATION_MARK);
    table.put(",", Class.PUNCTUATION_MARK);
    table.put(";", Class.PUNCTUATION_MARK);
    table.put(":", Class.PUNCTUATION_MARK);
    table.put("!", Class.PUNCTUATION_MARK);
    table.put("?", Class.PUNCTUATION_MARK);
    table.put(".", Class.PUNCTUATION_MARK);
    table.put("...", Class.PUNCTUATION_MARK);
    table.put("'", Class.PUNCTUATION_MARK);
    // table.put("«", Class.PUNCTUATION_MARK);
    // table.put("»", Class.PUNCTUATION_MARK);
    table.put("(", Class.PUNCTUATION_MARK);
    table.put(")", Class.PUNCTUATION_MARK);
    table.put("[", Class.PUNCTUATION_MARK);
    table.put("]", Class.PUNCTUATION_MARK);
    table.put("/", Class.PUNCTUATION_MARK);
    table.put("adj", Class.ADJECTIVE);
    table.put("adv", Class.ADVERB);
    table.put("art", Class.ARTICLE);
    table.put("conj-c", Class.COORDINATING_CONJUNCTION);
    table.put("conj-s", Class.SUBORDINATING_CONJUNCTION);
    table.put("ec", Class.PREFIX);
    table.put("intj", Class.INTERJECTION);
    table.put("n", Class.NOUN);
    table.put("n-adj", Class.NOUN_ADJECTIVE);
    table.put("n:", Class.NOUN);
    table.put("np", Class.NOUN);
    table.put("num", Class.NUMERAL);
    table.put("pp", Class.PREPOSITION);
    table.put("pron", Class.PRONOUN); // don't happen, added only for
                                      // compatibility
    table.put("pron-det", Class.PRONOUN);
    table.put("pron-indp", Class.PRONOUN);
    table.put("pron-pers", Class.PERSONAL_PRONOUN);
    table.put("prop", Class.PROPER_NOUN);
    table.put("prp", Class.PREPOSITION);
    table.put("v-fin", Class.FINITIVE_VERB);
    table.put("v-ger", Class.GERUND_VERB);
    table.put("v-inf", Class.INFINITIVE_VERB);
    table.put("v-pcp", Class.PARTICIPLE_VERB);
    table.put("vp", Class.INFINITIVE_VERB);

  }

  @Test
  public void testParseMorphologicalTag() {

    // class
    for (String tag : table.keySet()) {
      if (table.get(tag) != null)
        assertEquals("Failed to parse class tag: " + tag, table.get(tag), ti
            .parseMorphologicalTag(tag).getClazzE());
    }
  }

  @Test
  public void testSerializeTag() {
    Set<Class> classes = new HashSet<Class>(table.values());
    for (Class classTag : classes) {
      if (!classTag.equals(Class.PUNCTUATION_MARK)) {
        String value = ti.serialize(classTag);
        assertEquals("Failed to parse class tag: " + classTag, classTag,
            table.get(value));
      }
    }
  }

}
