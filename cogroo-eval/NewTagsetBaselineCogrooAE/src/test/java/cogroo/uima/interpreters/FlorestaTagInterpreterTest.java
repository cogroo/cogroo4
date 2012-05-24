package cogroo.uima.interpreters;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import br.usp.pcs.lta.cogroo.entity.impl.runtime.MorphologicalTag;
import br.usp.pcs.lta.cogroo.tag.TagInterpreterI;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Class;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Finiteness;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Punctuation;

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
    table.put("«", Class.PUNCTUATION_MARK);
    table.put("»", Class.PUNCTUATION_MARK);
    table.put("(", Class.PUNCTUATION_MARK);
    table.put(")", Class.PUNCTUATION_MARK);
    table.put("[", Class.PUNCTUATION_MARK);
    table.put("]", Class.PUNCTUATION_MARK);
    table.put("/", Class.PUNCTUATION_MARK);
    table.put("adj", Class.ADJECTIVE);
    table.put("adv", Class.ADVERB);
    table.put("art", Class.DETERMINER);
    table.put("conj-c", Class.COORDINATING_CONJUNCTION);
    table.put("conj-s", Class.SUBORDINATING_CONJUNCTION);
    table.put("ec", Class.HYPHEN_SEPARATED_PREFIX);
    table.put("intj", Class.INTERJECTION);
    table.put("n", Class.NOUN);
    table.put("n-adj", Class.ADJECTIVE);
    table.put("n:", Class.NOUN);
    table.put("np", Class.NOUN);
    table.put("num", Class.NUMERAL);
    table.put("pp", Class.PREPOSITION);
    //table.put("pron", Class.PRONOUN); // don't happen, added only for
                                      // compatibility
    table.put("pron-det", Class.DETERMINER);
    table.put("pron-indp", Class.SPECIFIER);
    table.put("pron-pers", Class.PERSONAL_PRONOUN);
    table.put("prop", Class.PROPER_NOUN);
    table.put("prp", Class.PREPOSITION);
    table.put("v-fin", Class.VERB);
    table.put("v-ger", Class.VERB);
    table.put("v-inf", Class.VERB);
    table.put("v-pcp", Class.VERB);
    table.put("vp", Class.VERB);

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
      if (!classTag.equals(Class.PUNCTUATION_MARK) && !classTag.equals(Class.VERB)) {
        String value = ti.serialize(classTag);
        assertEquals("Failed to parse class tag: " + classTag, classTag,
            table.get(value));
      }
    }
  }
  
  @Test
  public void testPunctuation() {
    
    MorphologicalTag mt = ti.parseMorphologicalTag(".");
    assertEquals(Class.PUNCTUATION_MARK, mt.getClazzE());
    assertEquals(Punctuation.ABS, mt.getPunctuation());
    
    mt = ti.parseMorphologicalTag("!");
    assertEquals(Class.PUNCTUATION_MARK, mt.getClazzE());
    assertEquals(Punctuation.ABS, mt.getPunctuation());
    
    mt = ti.parseMorphologicalTag("?");
    assertEquals(Class.PUNCTUATION_MARK, mt.getClazzE());
    assertEquals(Punctuation.ABS, mt.getPunctuation());
    
    mt = ti.parseMorphologicalTag(",");
    assertEquals(Class.PUNCTUATION_MARK, mt.getClazzE());
    assertEquals(Punctuation.NSEP, mt.getPunctuation());

    mt = ti.parseMorphologicalTag(";");
    assertEquals(Class.PUNCTUATION_MARK, mt.getClazzE());
    assertEquals(Punctuation.REL, mt.getPunctuation());
    
    mt = ti.parseMorphologicalTag("(");
    assertEquals(Class.PUNCTUATION_MARK, mt.getClazzE());
    assertEquals(Punctuation.BIN, mt.getPunctuation());
    
    mt = ti.parseMorphologicalTag("--");
    assertEquals(Class.PUNCTUATION_MARK, mt.getClazzE());
    assertEquals(Punctuation.BIN, mt.getPunctuation());
    
    mt = ti.parseMorphologicalTag("...");
    assertEquals(Class.PUNCTUATION_MARK, mt.getClazzE());
    assertEquals(Punctuation.REL, mt.getPunctuation());
    
    mt = ti.parseMorphologicalTag("«");
    assertEquals(Class.PUNCTUATION_MARK, mt.getClazzE());
    assertEquals(Punctuation.NSEP, mt.getPunctuation());

    
    mt = ti.parseMorphologicalTag("v-pcp");
    assertEquals(Class.VERB, mt.getClazzE());
    assertEquals(Finiteness.PARTICIPLE, mt.getFinitenessE());   

    mt = ti.parseMorphologicalTag("v-inf");
    assertEquals(Class.VERB, mt.getClazzE());
    assertEquals(Finiteness.INFINITIVE, mt.getFinitenessE());
    
    mt = ti.parseMorphologicalTag("v-ger");
    assertEquals(Class.VERB, mt.getClazzE());
    assertEquals(Finiteness.GERUND, mt.getFinitenessE());    
    
    mt = ti.parseMorphologicalTag("v-fin");
    assertEquals(Class.VERB, mt.getClazzE());
    assertEquals(Finiteness.FINITE, mt.getFinitenessE());
    
    mt = ti.parseMorphologicalTag("n-adj");
    assertEquals(Class.ADJECTIVE, mt.getClazzE());

    
    mt = ti.parseMorphologicalTag("intj");
    assertEquals(Class.INTERJECTION, mt.getClazzE());
  }

}
