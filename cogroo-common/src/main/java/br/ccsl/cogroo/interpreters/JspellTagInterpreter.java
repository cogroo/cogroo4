package br.ccsl.cogroo.interpreters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import opennlp.tools.util.Cache;

import org.apache.log4j.Logger;

import br.ccsl.cogroo.entities.impl.ChunkTag;
import br.ccsl.cogroo.entities.impl.MorphologicalTag;
import br.ccsl.cogroo.entities.impl.SyntacticTag;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Case;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.ChunkFunction;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Class;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Gender;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Mood;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Number;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Person;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Punctuation;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.SyntacticFunction;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Tense;

public class JspellTagInterpreter implements TagInterpreterI {

  private static final Map<Enum<?>, String> ENUM_MTAG_PARTS;
  private static final Map<String, List<Enum<?>>> MTAG_PARTS_ENUM;

  protected static final Logger LOGGER = Logger
      .getLogger(JspellTagInterpreter.class);

  private static final String SEP = "\\|";
  private static final List<String> MOOD_INDICATIVE;
  static {

    /* ********************************
     * Morphologic******************************
     */
    Map<Enum<?>, String> menumElements = new HashMap<Enum<?>, String>();

    /* Class */
    menumElements.put(Class.NOUN, "CAT:nc");
    menumElements.put(Class.NOUN_ADJECTIVE, "CAT:a_nc");
    menumElements.put(Class.PROPER_NOUN, "CAT:np");
    menumElements.put(Class.ARTICLE, "CAT:art");
    menumElements.put(Class.PREPOSITION, "CAT:prep");
    menumElements.put(Class.ADJECTIVE, "CAT:adj");
    menumElements.put(Class.ADVERB, "CAT:adv");
    menumElements.put(Class.SUBORDINATING_CONJUNCTION, "CAT:conj-s");
    menumElements.put(Class.COORDINATING_CONJUNCTION, "CAT:conj-c");
    menumElements.put(Class.INTERJECTION, "CAT:in");
    menumElements.put(Class.PREFIX, "CAT:pref");
    
    menumElements.put(Class.PERSONAL_PRONOUN, "CAT:ppes");

    /* Gender */
    menumElements.put(Gender.MALE, "G:m");
    menumElements.put(Gender.FEMALE, "G:f");
    menumElements.put(Gender.NEUTRAL, "G:n");

    /* Number */
    menumElements.put(Number.SINGULAR, "N:s");
    menumElements.put(Number.PLURAL, "N:p");
    menumElements.put(Number.NEUTRAL, "N:n");

    /* Case */
    menumElements.put(Case.ACCUSATIVE, "C:a");
    menumElements.put(Case.DATIVE, "C:d");
    menumElements.put(Case.NOMINATIVE, "C:n");
    menumElements.put(Case.PREPOSITIVE, "C:g");
    // menumElements.put(Case.ACCUSATIVE_DATIVE, "ACC/DAT");
    // menumElements.put(Case.NOMINATIVE_PREPOSITIVE, "NOM/PIV");

    /* Person */
    menumElements.put(Person.FIRST, "P:1");
    menumElements.put(Person.SECOND, "P:2");
    menumElements.put(Person.THIRD, "P:3");
    menumElements.put(Person.FIRST_THIRD, "P:1_3");

    /* Tense */
    menumElements.put(Tense.PRESENT, "T:p");
    menumElements.put(Tense.PRETERITO_IMPERFEITO, "T:pi");
    menumElements.put(Tense.PRETERITO_PERFEITO, "T:pp");
    menumElements.put(Tense.PRETERITO_MAIS_QUE_PERFEITO, "T:pmp");
    menumElements.put(Tense.FUTURE, "T:f");
    menumElements.put(Tense.CONDITIONAL, "T:c");
    // menumElements.put(Tense.PRETERITO_PERFEITO_MAIS_QUE_PERFEITO, "T:pmp");

    /* Mood */

    // menumElements.put(Mood.INDICATIVE, "IND");
    // menumElements.put(Mood.SUBJUNCTIVE, "SUBJ");
    menumElements.put(Mood.IMPERATIVE, "T:i");

    /* Punctuation */
    // menumElements.put(Punctuation.ABS, "ABS");
    // menumElements.put(Punctuation.NSEP, "NSEP");
    // menumElements.put(Punctuation.BIN, "BIN");
    // menumElements.put(Punctuation.REL, "REL");

    ENUM_MTAG_PARTS = Collections.unmodifiableMap(menumElements);

    Set<Enum<?>> k2 = ENUM_MTAG_PARTS.keySet();

    Map<String, List<Enum<?>>> stringMElements = new HashMap<String, List<Enum<?>>>(
        60);

    for (Enum<?> tagE : k2) {
      ArrayList<Enum<?>> values = new ArrayList<Enum<?>>();
      values.add(tagE);
      stringMElements.put(ENUM_MTAG_PARTS.get(tagE),
          Collections.unmodifiableList(values));
    }

    // gender
    ArrayList<Enum<?>> _Gn = new ArrayList<Enum<?>>();
    _Gn.add(Gender.NEUTRAL);
    stringMElements.put("G:2", Collections.unmodifiableList(_Gn));
    stringMElements.put("G:_", Collections.unmodifiableList(_Gn));

    // number
    ArrayList<Enum<?>> _Nn = new ArrayList<Enum<?>>();
    _Nn.add(Number.NEUTRAL);
    stringMElements.put("N:_", Collections.unmodifiableList(_Nn));

    ArrayList<Enum<?>> _Np = new ArrayList<Enum<?>>();
    _Np.add(Number.PLURAL);
    stringMElements.put("DN:p", Collections.unmodifiableList(_Np));

    ArrayList<Enum<?>> _Ns = new ArrayList<Enum<?>>();
    _Ns.add(Number.SINGULAR);
    stringMElements.put("DN:s", Collections.unmodifiableList(_Ns));

    // person
    ArrayList<Enum<?>> _P1 = new ArrayList<Enum<?>>();
    _P1.add(Person.FIRST);
    stringMElements.put("AP:1", Collections.unmodifiableList(_P1));

    ArrayList<Enum<?>> _P2 = new ArrayList<Enum<?>>();
    _P2.add(Person.SECOND);
    stringMElements.put("AP:2", Collections.unmodifiableList(_P2));

    ArrayList<Enum<?>> _P3 = new ArrayList<Enum<?>>();
    _P3.add(Person.THIRD);
    stringMElements.put("AP:3", Collections.unmodifiableList(_P3));
    stringMElements.put("DP:3", Collections.unmodifiableList(_P3));

    // Tense
    ArrayList<Enum<?>> _Tfc = new ArrayList<Enum<?>>();
    _Tfc.add(Tense.FUTURE);
    _Tfc.add(Mood.SUBJUNCTIVE);
    stringMElements.put("T:fc", Collections.unmodifiableList(_Tfc));

    ArrayList<Enum<?>> _Tpc = new ArrayList<Enum<?>>();
    _Tpc.add(Tense.PRESENT);
    _Tpc.add(Mood.SUBJUNCTIVE);
    stringMElements.put("T:pc", Collections.unmodifiableList(_Tpc));

    ArrayList<Enum<?>> _Tpic = new ArrayList<Enum<?>>();
    _Tpic.add(Tense.PRETERITO_IMPERFEITO);
    _Tpic.add(Mood.SUBJUNCTIVE);
    stringMElements.put("T:pic", Collections.unmodifiableList(_Tpic));

    // indicative we to using software
    String[] ind = { "T:f", "T:p", "T:pi", "T:pmp", "T:pp" };
    MOOD_INDICATIVE = Collections.unmodifiableList(Arrays.asList(ind));

    MTAG_PARTS_ENUM = Collections.unmodifiableMap(stringMElements);

  }

  public JspellTagInterpreter() {

  }

  // private final Map<String, MorphologicalTag> cache = new HashMap<String,
  // MorphologicalTag>();
  private final Cache cache = new Cache(200);

  public MorphologicalTag parseMorphologicalTag(String tagString) {

    if (tagString == null) {
      return null;
    }

    synchronized (cache) {
      if (cache.containsKey(tagString)) {
        return ((MorphologicalTag) cache.get(tagString)).clone();
      }
    }

    MorphologicalTag m = new MorphologicalTag();

    String[] tags = tagString.split(SEP);
    for (String tag : tags) {
      if (MTAG_PARTS_ENUM.containsKey(tag)) {
        List<Enum<?>> tagE = MTAG_PARTS_ENUM.get(tag);
        for (Enum<?> t : tagE) {
          if (t instanceof Class) {
            m.setClazz((Class) t);
          } else if (t instanceof Gender) {
            m.setGender((Gender) t);
          } else if (t instanceof Number) {
            m.setNumber((Number) t);
          } else if (t instanceof Case) {
            m.setCase((Case) t);
          } else if (t instanceof Person) {
            m.setPerson((Person) t);
          } else if (t instanceof Tense) {
            if (MOOD_INDICATIVE.contains(tag)) {
              m.setMood(Mood.INDICATIVE);
            }
            m.setTense((Tense) t);
          } else if (t instanceof Mood) {
            m.setMood((Mood) t);
          } else if (t instanceof Punctuation) {
            m.setPunctuation((Punctuation) t);
          }
        }
      } else {
        if (tag.startsWith("CAT:")) {
          if (tag.startsWith("CAT:punct")) {
            m.setClazz(Class.PUNCTUATION_MARK);
          } else if ("CAT:v".equals(tag) && m.getClazzE() == null) {
            m.setClazz(Class.FINITIVE_VERB);
          } else if ("CAT:ppos".equals(tag) || "CAT:pind".equals(tag)
              || "CAT:pdem".equals(tag) || "CAT:pint".equals(tag)
              || "CAT:prel".equals(tag)) {
            m.setClazz(Class.PRONOUN);
          } else if ("CAT:card".equals(tag) || "CAT:nord".equals(tag)) {
            m.setClazz(Class.NUMERAL);
          } else if (tag.startsWith("CAT:adj")) {
            m.setClazz(Class.ADJECTIVE);
          } else if ("CAT:cp".equals(tag) || "CAT:pass".equals(tag)) {
            return null; // ignore this tag
          }
        } else if (tag.startsWith("T:")) {
          if (MOOD_INDICATIVE.contains(tag)) {
            m.setMood(Mood.INDICATIVE);
          }
          if ((Class.FINITIVE_VERB.equals(m.getClazzE()) || m.getClazzE() == null)) {
            if ("T:inf".equals(tag) || "T:ip".equals(tag)) {
              m.setClazz(Class.INFINITIVE_VERB);
            } else if ("T:ppa".equals(tag)) {
              m.setClazz(Class.PARTICIPLE_VERB);
            } else if ("T:g".equals(tag)) {
              m.setClazz(Class.GERUND_VERB);
            }
          }
        }

        else if (tag.length() == 1 || "--".equals(tag) || "...".equals(tag)) {
          m.setClazz(Class.PUNCTUATION_MARK);
        } else if ("n:".equals(tag)) {
          m.setClazz(Class.NOUN);
        } else if ("intj".equals(tag)) {
          m.setClazz(Class.INTERJECTION);
        } else if ("pp".equals(tag)) {
          m.setClazz(Class.PREPOSITION);
        } else if ("np".equals(tag)) {
          m.setClazz(Class.NOUN);
        } else {
          System.out.println(tag);
        }
      }

    }

    if (m.toString() == null || m.toString().length() == 0) {
      LOGGER.error("Invalid MorphologicalTag: " + tagString);
    }

    // post process
    if (m.getGenderE() == null && m.getNumberE() != null) {
      if (Class.NOUN.equals(m.getClazzE())
          || Class.NOUN_ADJECTIVE.equals(m.getClazzE())
          || Class.NUMERAL.equals(m.getClazzE())) {
        m.setGender(Gender.NEUTRAL);
      } else if (Class.PROPER_NOUN.equals(m.getClazzE())) {
        m.setGender(Gender.MALE);
      }
    }

    if (m.getNumberE() == null && m.getGenderE() != null) {
      if (Class.NOUN.equals(m.getClazzE())
          || Class.NOUN_ADJECTIVE.equals(m.getClazzE())
          || Class.NUMERAL.equals(m.getClazzE())) {
        m.setNumber(Number.NEUTRAL);
      } else if (Class.PROPER_NOUN.equals(m.getClazzE())) {
        m.setNumber(Number.SINGULAR);
      }
    }

    if (m.getTense() != null && Class.NOUN.equals(m.getClazzE())) {
      m.setClazz(Class.INFINITIVE_VERB);
    }
    
    if(m == null || m.getClazzE() == null) {
      LOGGER.warn("something wrong with tag: " + tagString);
    }
    removeInvalidFeatures(m);

    synchronized (cache) {
      if (!cache.containsKey(tagString)) {
        cache.put(tagString, m.clone());
      }
    }

    return m;
  }

  private void removeInvalidFeatures(MorphologicalTag m) {
    if(m != null && m.getClazzE() != null) {
      switch (m.getClazzE()) {
      case ADVERB:
        m.setCase(null);
        m.setGender(null);
        m.setMood(null);
        m.setNumber(null);
        m.setPerson(null);
        m.setPunctuation(null);
        m.setTense(null);
        break;
      case NOUN:
      case PRONOUN:
        m.setPerson(null);
        m.setMood(null);
        m.setCase(null);
        break;
      case FINITIVE_VERB:
        m.setGender(null);
        break;
      case INFINITIVE_VERB:
        m.setCase(null);
        m.setGender(null);
        m.setMood(null);
        m.setPunctuation(null);
        m.setTense(null);
        
      default:
        break;
      }      
    }
  }

  public ChunkTag parseChunkTag(String tagString) {
    return null;
  }

  public SyntacticTag parseSyntacticTag(String tagString) {
    return null;
  }

  public String serialize(MorphologicalTag tag) {
    StringBuilder res = new StringBuilder();
    if (tag.getClazzE() != null) {
      res.append(serializer(tag.getClazzE()) + SEP);
    }

    if (tag.getGenderE() != null) {
      res.append(serializer(tag.getGenderE()) + SEP);
    }

    if (tag.getTense() != null) {
      res.append(serializer(tag.getTense()) + SEP);
    }

    if (tag.getMood() != null && tag.getTense() == null) {
      res.append(serializer(tag.getMood()) + SEP);
    }

    if (tag.getPersonE() != null && tag.getNumberE() != null) {
      String s = serializer(tag.getPersonE());

      if (!(s.contains("S") || s.contains("P"))) {
        res.append(s + serializer(tag.getNumberE()) + SEP);
      } else {
        res.append(s + SEP);
      }
    } else if (tag.getNumberE() != null) {
      res.append(serializer(tag.getNumberE()) + SEP);
    }

    if (tag.getCase() != null) {
      res.append(serializer(tag.getCase()) + SEP);
    }

    if (tag.getMood() != null && tag.getTense() != null) {
      res.append(serializer(tag.getMood()) + SEP);
    }

    // if(tag.getFinitenessE() != null) {
    // res.append(serializer(tag.getFinitenessE()) + SEP);
    // }

    if (tag.getPunctuation() != null) {
      res.append(serializer(tag.getPunctuation()) + SEP);
    }

    if (res.length() == 0) {
      LOGGER.error("Unable to serialize MorphologicalTag: " + tag);
    }

    if (res.length() > 1) {
      return res.substring(0, res.length() - 1);
    } else {
      return null;
    }
  }

  public String serialize(ChunkTag tag) {
    String value = serializer(tag.getChunkFunction());
    return value;
  }

  public String serialize(SyntacticTag tag) {
    String value = serializer(tag.getSyntacticFunction());
    return value;
  }

  public String serialize(SyntacticFunction tag) {
    return serializer(tag);
  }

  public String serialize(ChunkFunction tag) {
    return serializer(tag);
  }

  public String serialize(Class tag) {
    return serializer(tag);
  }

  public String serialize(Gender tag) {
    return serializer(tag);
  }

  public String serialize(Number tag) {
    return serializer(tag);
  }

  public String serialize(Case tag) {
    return serializer(tag);
  }

  public String serialize(Person tag) {
    return serializer(tag);
  }

  public String serialize(Tense tag) {
    return serializer(tag);
  }

  public String serialize(Mood tag) {
    return serializer(tag);
  }

  public String serialize(Punctuation tag) {
    return serializer(tag);
  }

  private String serializer(Enum<?> value) {
    if (ENUM_MTAG_PARTS.containsKey(value)) {
      return ENUM_MTAG_PARTS.get(value);
    }
    return "";
  }
}
