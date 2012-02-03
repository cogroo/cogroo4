package br.ccsl.cogroo.interpreters;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Case;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Class;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Gender;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Mood;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Number;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Person;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Tense;

public class JspellTagInterpreterTest {
  
  private static Map<String, Class> classTable = new HashMap<String, Class>();
  private static Map<String, Number> numTable = new HashMap<String, Number>();
  private static Map<String, Gender> genTable = new HashMap<String, Gender>();
  private static Map<String, Person> persTable = new HashMap<String, Person>();
  private static Map<String, Tense> tenseTable = new HashMap<String, Tense>();
  private static Map<String, Case> caseTable = new HashMap<String, Case>();
  private static Map<String, Mood> moodTable = new HashMap<String, Mood>();
  
  private JspellTagInterpreter ti = new JspellTagInterpreter();
  
  static {
    classTable.put("CAT:adj", Class.ADJECTIVE);
    classTable.put("CAT:adj;GR=dim", Class.ADJECTIVE);
    classTable.put("CAT:adj;GR=sup", Class.ADJECTIVE);
    classTable.put("CAT:adv", Class.ADVERB);
    classTable.put("CAT:art", Class.ARTICLE);
    classTable.put("CAT:card", Class.NUMERAL);
    classTable.put("CAT:conj-c", Class.COORDINATING_CONJUNCTION);
    classTable.put("CAT:conj-s", Class.SUBORDINATING_CONJUNCTION);
    classTable.put("CAT:in", Class.INTERJECTION);
    classTable.put("CAT:nc", Class.NOUN);
    classTable.put("CAT:a_nc", Class.NOUN_ADJECTIVE);
    classTable.put("CAT:nord", Class.NUMERAL);
    classTable.put("CAT:np", Class.PROPER_NOUN);
    classTable.put("CAT:pdem", Class.PRONOUN);
    classTable.put("CAT:pind", Class.PRONOUN);
    classTable.put("CAT:pint", Class.PRONOUN);
    classTable.put("CAT:ppes", Class.PERSONAL_PRONOUN);
    classTable.put("CAT:ppos", Class.PRONOUN);
    classTable.put("CAT:pref", Class.PRONOUN);
    classTable.put("CAT:prel", Class.PRONOUN);
    classTable.put("CAT:prep", Class.PREPOSITION);
    classTable.put("CAT:punct", Class.PUNCTUATION_MARK);
    classTable.put("CAT:v|T:c", Class.FINITIVE_VERB);
    classTable.put("CAT:v|T:f", Class.FINITIVE_VERB);
    classTable.put("CAT:v|T:fc", Class.FINITIVE_VERB);
    classTable.put("CAT:v|T:g", Class.GERUND_VERB);
    classTable.put("CAT:v|T:i", Class.FINITIVE_VERB);
    classTable.put("CAT:v|T:inf", Class.INFINITIVE_VERB);
    classTable.put("CAT:v|T:ip", Class.INFINITIVE_VERB);
    classTable.put("CAT:v|T:p", Class.FINITIVE_VERB);
    classTable.put("T:pc|CAT:v|", Class.FINITIVE_VERB);
    classTable.put("T:pi|CAT:v|", Class.FINITIVE_VERB);
    classTable.put("T:pic|CAT:v|", Class.FINITIVE_VERB);
    classTable.put("T:pmp|CAT:v|", Class.FINITIVE_VERB);
    classTable.put("T:pp|CAT:v|", Class.FINITIVE_VERB);
    classTable.put("T:ppa|CAT:v|", Class.PARTICIPLE_VERB);
    classTable.put("CAT:v", Class.FINITIVE_VERB);
    classTable.put("CAT:pref", Class.PREFIX);
    classTable.put("CAT:a_nc|T:inf|", Class.NOUN_ADJECTIVE);
    
    // gender
    genTable.put("G:2", Gender.NEUTRAL);
    genTable.put("G:_", Gender.NEUTRAL);
    genTable.put("G:f", Gender.FEMALE);
    genTable.put("G:m", Gender.MALE);
    genTable.put("G:n", Gender.NEUTRAL);

    // number
    numTable.put("N:_", Number.NEUTRAL);
    numTable.put("N:n", Number.NEUTRAL);
    numTable.put("N:p", Number.PLURAL);
    numTable.put("N:s", Number.SINGULAR);
    numTable.put("DN:p", Number.PLURAL);
    numTable.put("DN:s", Number.SINGULAR);

    // person
    persTable.put("P:1", Person.FIRST);
    persTable.put("P:1_3", Person.FIRST_THIRD);
    persTable.put("P:2", Person.SECOND);
    persTable.put("P:3", Person.THIRD);
    persTable.put("AP:1", Person.FIRST);
    persTable.put("AP:2", Person.SECOND);
    persTable.put("AP:3", Person.THIRD);
    persTable.put("DP:3", Person.THIRD);
    
    // tense
    // subjuntive == conjuntive
    
    tenseTable.put("T:c", Tense.CONDITIONAL);
    tenseTable.put("T:f", Tense.FUTURE);
     tenseTable.put("T:fc", Tense.FUTURE); //subjuntive
    // tenseTable.put("T:g", Tense.CONDITIONAL); //gerundive
    //tenseTable.put("T:i", Tense.CONDITIONAL); //imperative
    //tenseTable.put("T:inf", Tense.CONDITIONAL); // infinitive 
    //tenseTable.put("T:ip", Tense.CONDITIONAL); // infinitive 
    tenseTable.put("T:p", Tense.PRESENT);
    tenseTable.put("T:pc", Tense.PRESENT); //subjuntive
    tenseTable.put("T:pi", Tense.PRETERITO_IMPERFEITO);
    tenseTable.put("T:pic", Tense.PRETERITO_IMPERFEITO); //subjuntive
    tenseTable.put("T:pmp", Tense.PRETERITO_MAIS_QUE_PERFEITO);
    tenseTable.put("T:pp", Tense.PRETERITO_PERFEITO);
    //tenseTable.put("T:ppa", Tense.CONDITIONAL); //participio passado

    // Case
    caseTable.put("C:a", Case.ACCUSATIVE);
    caseTable.put("C:d", Case.DATIVE);
    caseTable.put("C:g", Case.PREPOSITIVE);
    caseTable.put("C:n", Case.NOMINATIVE);
    
    // Mood
    moodTable.put("T:i", Mood.IMPERATIVE);
    moodTable.put("T:pic", Mood.SUBJUNCTIVE);
    moodTable.put("T:pc", Mood.SUBJUNCTIVE);
    moodTable.put("T:fc", Mood.SUBJUNCTIVE);
    moodTable.put("T:f", Mood.INDICATIVE);
  }
  

  @Test
  public void testParseClassTag() {
    
    // class
    for (String tag : classTable.keySet()) {
      assertEquals("Failed to parse class tag: " + tag, classTable.get(tag), ti.parseMorphologicalTag(tag).getClazzE());
    }
    
    assertNull(ti.parseMorphologicalTag("CAT:cp"));
    assertNull(ti.parseMorphologicalTag("CAT:pass"));
  }
  
  @Test
  public void testParseNumberTag() {
    
    for (String tag : numTable.keySet()) {
      assertEquals("Failed to parse num tag: " + tag, numTable.get(tag), ti.parseMorphologicalTag(tag).getNumberE());
    }
    
  }
  
  @Test
  public void testParseGenderTag() {
    
    for (String tag : genTable.keySet()) {
      assertEquals("Failed to parse gen tag: " + tag, genTable.get(tag), ti.parseMorphologicalTag(tag).getGenderE());
    }
    
  }
  
  @Test
  public void testParsePersTag() {
    
    for (String tag : persTable.keySet()) {
      assertEquals("Failed to parse pers tag: " + tag, persTable.get(tag), ti.parseMorphologicalTag(tag).getPersonE());
    }
    
  }
  
  @Test
  public void testParseTenseTag() {
    
    for (String tag : tenseTable.keySet()) {
      assertEquals("Failed to parse tense tag: " + tag, tenseTable.get(tag), ti.parseMorphologicalTag(tag).getTense());
    }
    
  }
  
  @Test
  public void testCaseTenseTag() {
    
    for (String tag : caseTable.keySet()) {
      assertEquals("Failed to parse case tag: " + tag, caseTable.get(tag), ti.parseMorphologicalTag(tag).getCase());
    }
    
  }

  @Test
  public void testParseMoodTag() {
    
    for (String tag : moodTable.keySet()) {
      assertEquals("Failed to parse mood tag: " + tag, moodTable.get(tag), ti.parseMorphologicalTag(tag).getMood());
    }
    
  }
}
