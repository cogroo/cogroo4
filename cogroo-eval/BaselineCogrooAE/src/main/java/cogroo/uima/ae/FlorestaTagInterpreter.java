package cogroo.uima.ae;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import opennlp.tools.util.Cache;

import org.apache.log4j.Logger;

import br.usp.pcs.lta.cogroo.entity.impl.runtime.ChunkTag;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.MorphologicalTag;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.SyntacticTag;
import br.usp.pcs.lta.cogroo.tag.TagInterpreterI;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Case;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.ChunkFunction;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Class;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Finiteness;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Gender;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Mood;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Number;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Person;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Punctuation;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.SyntacticFunction;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Tense;

public class FlorestaTagInterpreter implements TagInterpreterI {

  private static final Map<Enum<?>, String> ENUM_MTAG_PARTS;
  private static final Map<String, List<Enum<?>>> MTAG_PARTS_ENUM;

  private static final Map<ChunkFunction, String> ENUM_CTAG_PARTS;
  private static final Map<String, List<ChunkFunction>> CTAG_PARTS_ENUM;

  private static final Map<SyntacticFunction, String> ENUM_STAG_PARTS;
  private static final Map<String, List<SyntacticFunction>> STAG_PARTS_ENUM;

  protected static final Logger LOGGER = Logger
      .getLogger(FlorestaTagInterpreter.class);

  private static final String SEP = "=";

  static {
    /* ********************************
     * Chunk /* *******************************
     */
    Map<ChunkFunction, String> cenumElements = new HashMap<ChunkFunction, String>(
        7);

    cenumElements.put(ChunkFunction.OTHER, "O");

    cenumElements.put(ChunkFunction.BOUNDARY_NOUN_PHRASE, "B-NP");
    cenumElements.put(ChunkFunction.BOUNDARY_NOUN_PHRASE_MAIN, "*B-NP");
    cenumElements.put(ChunkFunction.INTERMEDIARY_NOUN_PHRASE, "I-NP");
    cenumElements.put(ChunkFunction.INTERMEDIARY_NOUN_PHRASE_MAIN, "*I-NP");

    cenumElements.put(ChunkFunction.BOUNDARY_VERB_PHRASE_MAIN, "*B-VP");
    cenumElements.put(ChunkFunction.INTERMEDIARY_VERB_PHRASE, "I-VP");

    ENUM_CTAG_PARTS = Collections.unmodifiableMap(cenumElements);
    Set<ChunkFunction> k = ENUM_CTAG_PARTS.keySet();

    Map<String, List<ChunkFunction>> stringCElements = new HashMap<String, List<ChunkFunction>>(
        7);

    for (ChunkFunction tagE : k) {
      ArrayList<ChunkFunction> values = new ArrayList<ChunkFunction>();
      values.add(tagE);
      stringCElements.put(ENUM_CTAG_PARTS.get(tagE),
          Collections.unmodifiableList(values));
    }

    CTAG_PARTS_ENUM = Collections.unmodifiableMap(stringCElements);

    /* ********************************
     * Syntactic /* *******************************
     */
    Map<SyntacticFunction, String> senumElements = new HashMap<SyntacticFunction, String>(
        3);

    senumElements.put(SyntacticFunction.NONE, "-");
    senumElements.put(SyntacticFunction.SUBJECT, "SUBJ");
    senumElements.put(SyntacticFunction.VERB, "VERB");

    ENUM_STAG_PARTS = Collections.unmodifiableMap(senumElements);
    Set<SyntacticFunction> k1 = ENUM_STAG_PARTS.keySet();

    Map<String, List<SyntacticFunction>> stringSElements = new HashMap<String, List<SyntacticFunction>>(
        3);

    for (SyntacticFunction tagE : k1) {
      ArrayList<SyntacticFunction> values = new ArrayList<SyntacticFunction>();
      values.add(tagE);
      stringSElements.put(ENUM_STAG_PARTS.get(tagE),
          Collections.unmodifiableList(values));
    }

    STAG_PARTS_ENUM = Collections.unmodifiableMap(stringSElements);

    /* ********************************
     * Morphologic /* *******************************
     */
    Map<Enum<?>, String> menumElements = new HashMap<Enum<?>, String>();

    /* Class */
    menumElements.put(Class.NOUN, "n");
    menumElements.put(Class.PROPER_NOUN, "prop");
    menumElements.put(Class.SPECIFIER, "pron-indp");// ?
    menumElements.put(Class.DETERMINER, "det");// collision
    // menumElements.put(Class.ARTICLE, "art");//collision
    // menumElements.put(Class.DETERMINER_PRONOUN, "pron-det");//collision
    menumElements.put(Class.PERSONAL_PRONOUN, "pron-pers");
    menumElements.put(Class.PREPOSITION, "prp");
    menumElements.put(Class.ADJECTIVE, "adj");
    menumElements.put(Class.ADVERB, "adv");
    // menumElements.put(Class.VERB, "v-"); //? v-*
    menumElements.put(Class.NUMERAL, "num");
    menumElements.put(Class.SUBORDINATING_CONJUNCTION, "conj-s");
    menumElements.put(Class.COORDINATING_CONJUNCTION, "conj-c");
    menumElements.put(Class.INTERJECTION, "intj");
    menumElements.put(Class.HYPHEN_SEPARATED_PREFIX, "ec");
    menumElements.put(Class.PUNCTUATION_MARK, "pnt"); // ?
    menumElements.put(Class.UNIT, "uni");// ?
    /* Gender */
    menumElements.put(Gender.MALE, "M");
    menumElements.put(Gender.FEMALE, "F");
    menumElements.put(Gender.NEUTRAL, "M/F");
    /* Number */
    menumElements.put(Number.SINGULAR, "S");
    menumElements.put(Number.PLURAL, "P");
    menumElements.put(Number.NEUTRAL, "S/P");
    /* Case */
    menumElements.put(Case.ACCUSATIVE, "ACC");
    menumElements.put(Case.DATIVE, "DAT");
    menumElements.put(Case.NOMINATIVE, "NOM");
    menumElements.put(Case.PREPOSITIVE, "PIV");
    menumElements.put(Case.ACCUSATIVE_DATIVE, "ACC/DAT");
    menumElements.put(Case.NOMINATIVE_PREPOSITIVE, "NOM/PIV");
    /* Person */
    menumElements.put(Person.FIRST, "1");
    menumElements.put(Person.SECOND, "2");
    menumElements.put(Person.THIRD, "3");
    // enumElements.put(Person.FIRST, "1S");
    // enumElements.put(Person.FIRST, "1P");
    // enumElements.put(Person.SECOND, "2S");
    // enumElements.put(Person.SECOND, "2P");
    // enumElements.put(Person.THIRD, "3S");
    // enumElements.put(Person.THIRD, "3P");
    menumElements.put(Person.FIRST_THIRD, "1/3S");
    // enumElements.put(Person.THIRD, "3S/P");
    menumElements.put(Person.NONE_FIRST_THIRD, "0/1/3S");
    /* Tense */
    menumElements.put(Tense.PRESENT, "PR");
    menumElements.put(Tense.PRETERITO_IMPERFEITO, "IMPF");
    menumElements.put(Tense.PRETERITO_PERFEITO, "PS");
    menumElements.put(Tense.PRETERITO_MAIS_QUE_PERFEITO, "MQP");
    menumElements.put(Tense.FUTURE, "FUT");
    menumElements.put(Tense.CONDITIONAL, "COND");
    menumElements.put(Tense.PRETERITO_PERFEITO_MAIS_QUE_PERFEITO, "PS/MQP");
    /* Mood */
    menumElements.put(Mood.INDICATIVE, "IND");
    menumElements.put(Mood.SUBJUNCTIVE, "SUBJ");
    menumElements.put(Mood.IMPERATIVE, "IMP");
    /* Finiteness */
    menumElements.put(Finiteness.FINITE, "v-fin");
    menumElements.put(Finiteness.INFINITIVE, "v-inf");
    menumElements.put(Finiteness.PARTICIPLE, "v-pcp");
    menumElements.put(Finiteness.GERUND, "v-ger");
    /* Punctuation */
    menumElements.put(Punctuation.ABS, "ABS");
    menumElements.put(Punctuation.NSEP, "NSEP");
    menumElements.put(Punctuation.BIN, "BIN");
    menumElements.put(Punctuation.REL, "REL");

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

    // enumElements.put(Person.FIRST, "1S");
    // enumElements.put(Person.FIRST, "1P");
    // enumElements.put(Person.SECOND, "2S");
    // enumElements.put(Person.SECOND, "2P");
    // enumElements.put(Person.THIRD, "3S");
    // enumElements.put(Person.THIRD, "3P");
    // * enumElements.put(Person.FIRST_THIRD, "1/3S");
    // enumElements.put(Person.THIRD, "3S/P");
    // * enumElements.put(Person.NONE_FIRST_THIRD, "0/1/3S");

    ArrayList<Enum<?>> _1S = new ArrayList<Enum<?>>();
    _1S.add(Person.FIRST);
    _1S.add(Number.SINGULAR);
    stringMElements.put("1S", Collections.unmodifiableList(_1S));

    ArrayList<Enum<?>> _1P = new ArrayList<Enum<?>>();
    _1P.add(Person.FIRST);
    _1P.add(Number.PLURAL);
    stringMElements.put("1P", Collections.unmodifiableList(_1P));

    ArrayList<Enum<?>> _2S = new ArrayList<Enum<?>>();
    _2S.add(Person.SECOND);
    _2S.add(Number.SINGULAR);
    stringMElements.put("2S", Collections.unmodifiableList(_2S));

    ArrayList<Enum<?>> _2P = new ArrayList<Enum<?>>();
    _2P.add(Person.SECOND);
    _2P.add(Number.PLURAL);
    stringMElements.put("2P", Collections.unmodifiableList(_2P));

    ArrayList<Enum<?>> _3S = new ArrayList<Enum<?>>();
    _3S.add(Person.THIRD);
    _3S.add(Number.SINGULAR);
    stringMElements.put("3S", Collections.unmodifiableList(_3S));

    ArrayList<Enum<?>> _3P = new ArrayList<Enum<?>>();
    _3P.add(Person.THIRD);
    _3P.add(Number.PLURAL);
    stringMElements.put("3P", Collections.unmodifiableList(_3P));

    ArrayList<Enum<?>> _13S = new ArrayList<Enum<?>>();
    _13S.add(Person.FIRST_THIRD);
    _13S.add(Number.SINGULAR);
    stringMElements.put("1/3S", Collections.unmodifiableList(_13S));

    ArrayList<Enum<?>> _3SP = new ArrayList<Enum<?>>();
    _3SP.add(Person.THIRD);
    _3SP.add(Number.NEUTRAL);
    stringMElements.put("3S/P", Collections.unmodifiableList(_3SP));

    ArrayList<Enum<?>> _013S = new ArrayList<Enum<?>>();
    _013S.add(Person.NONE_FIRST_THIRD);
    _013S.add(Number.SINGULAR);
    stringMElements.put("0/1/3S", Collections.unmodifiableList(_013S));

    /* weird things */
    ArrayList<Enum<?>> hifen = new ArrayList<Enum<?>>();
    hifen.add(Class.PUNCTUATION_MARK);
    stringMElements.put(".", Collections.unmodifiableList(hifen));
    hifen.add(Punctuation.REL);
    stringMElements.put("$--", Collections.unmodifiableList(hifen));

    ArrayList<Enum<?>> ap = new ArrayList<Enum<?>>();
    ap.add(Class.PUNCTUATION_MARK);
    ap.add(Punctuation.BIN);
    stringMElements.put("$`", Collections.unmodifiableList(ap));
    stringMElements.put("$´", Collections.unmodifiableList(ap));

    ArrayList<Enum<?>> others = new ArrayList<Enum<?>>();
    others.add(Class.PUNCTUATION_MARK);
    others.add(Punctuation.NSEP);
    stringMElements.put("$+", Collections.unmodifiableList(others));
    stringMElements.put("$±", Collections.unmodifiableList(others));
    stringMElements.put("$=", Collections.unmodifiableList(others));
    stringMElements.put("$$", Collections.unmodifiableList(others));
    stringMElements.put("$\\", Collections.unmodifiableList(others));

    ArrayList<Enum<?>> pp = new ArrayList<Enum<?>>();
    pp.add(Class.PREPOSITION);
    stringMElements.put("PP", Collections.unmodifiableList(pp));

    MTAG_PARTS_ENUM = Collections.unmodifiableMap(stringMElements);

  }

  public FlorestaTagInterpreter() {

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
            m.setTense((Tense) t);
          } else if (t instanceof Mood) {
            m.setMood((Mood) t);
          } else if (t instanceof Finiteness) {
            m.setClazz(Class.VERB);
            m.setFiniteness((Finiteness) t);
          } else if (t instanceof Punctuation) {
            m.setPunctuation((Punctuation) t);
          }
        }
      } else {
        if ("v-pcp".equals(tag)) {
          m.setClazz(Class.VERB);
          m.setFiniteness(Finiteness.PARTICIPLE);
        } else if ("v-inf".equals(tag)) {
          m.setClazz(Class.VERB);
          m.setFiniteness(Finiteness.INFINITIVE);
        } else if ("v-ger".equals(tag)) {
          m.setClazz(Class.VERB);
          m.setFiniteness(Finiteness.GERUND);
        } else if ("v-fin".equals(tag)) {
          m.setClazz(Class.VERB);
          m.setFiniteness(Finiteness.FINITE);
        } else if ("n-adj".equals(tag)) {
          m.setClazz(Class.ADJECTIVE);
        } else if (tag.length() == 1 || "--".equals(tag) || "...".equals(tag)) {
          m.setClazz(Class.PUNCTUATION_MARK);
        } else if ("n:".equals(tag)) {
          m.setClazz(Class.NOUN);
        } else if ("intj".equals(tag)) {
          m.setClazz(Class.INTERJECTION);
        } else if ("pp".equals(tag)) {
          m.setClazz(Class.PREPOSITION);
        } else if ("np".equals(tag)) {
          m.setClazz(Class.NOUN);
        } else if ("vp".equals(tag)) {
          m.setClazz(Class.VERB);
        } else if (tag.contains("<") || "P.vp".equals(tag) || "GER".equals(tag)) {
          // garbage
        } else {
          System.out.println(tag);
        }
      }

    }

    if (m.toString() == null || m.toString().length() == 0) {
      LOGGER.error("Invalid MorphologicalTag: " + tagString);
    }

    synchronized (cache) {
      if (!cache.containsKey(tagString)) {
        cache.put(tagString, m.clone());
      }
    }

    return m;
  }

  public ChunkTag parseChunkTag(String tagString) {
    ChunkTag ct = new ChunkTag();
    // ct.setChunkFunction(ChunkFunction.valueOf(tagString));
    List<ChunkFunction> tag = CTAG_PARTS_ENUM.get(tagString);
    if (tag != null && tag.size() != 0) {
      ChunkFunction en = tag.get(0);
      ct.setChunkFunction(en);
    } else {
      ct.setChunkFunction(ChunkFunction.OTHER);
      LOGGER.error("Invalid ChunkTag: " + tagString);
    }
    return ct;
  }

  public SyntacticTag parseSyntacticTag(String tagString) {
    SyntacticTag st = new SyntacticTag();
    // ct.setChunkFunction(ChunkFunction.valueOf(tagString));
    List<SyntacticFunction> tag = STAG_PARTS_ENUM.get(tagString);
    if (tag != null && tag.size() != 0) {
      SyntacticFunction en = tag.get(0);
      st.setSyntacticFunction(en);
    } else {
      st.setSyntacticFunction(SyntacticFunction.NONE);
      LOGGER.error("Invalid ChunkTag: " + tagString);
    }
    return st;
  }

  public String serialize(MorphologicalTag tag) {
    StringBuilder res = new StringBuilder();
    if (tag.getClazzE() != null) {
      if (tag.getClazzE().equals(Class.VERB)) {
        if (tag.getFinitenessE().equals(Finiteness.FINITE)) {
          res.append("v-fin" + SEP);
        } else if (tag.getFinitenessE().equals(Finiteness.GERUND)) {
          res.append("v-ger" + SEP);
        } else if (tag.getFinitenessE().equals(Finiteness.INFINITIVE)) {
          res.append("v-inf" + SEP);
        } else if (tag.getFinitenessE().equals(Finiteness.PARTICIPLE)) {
          res.append("v-pcp" + SEP);
        } else {
          throw new RuntimeException("Missing finiteness");
        }
      } else {
        res.append(serializer(tag.getClazzE()) + SEP);
      }
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

  public String serialize(Finiteness tag) {
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

  private String serializer(SyntacticFunction value) {
    if (ENUM_STAG_PARTS.containsKey(value)) {
      return ENUM_STAG_PARTS.get(value);
    }
    return "";
  }

  private String serializer(ChunkFunction value) {
    if (ENUM_CTAG_PARTS.containsKey(value)) {
      return ENUM_CTAG_PARTS.get(value);
    }
    return "";
  }

  // public static void main(String[] args) throws IOException {
  // ADPOSSampleStream sampleStream = new ADPOSSampleStream(
  // CmdLineUtil.openInFile(new
  // File("/Users/wcolen/Documents/wrks/corpus/FlorestaVirgem/FlorestaVirgem_CF_3.0_ad.txt")),
  // "ISO-8859-1");
  // FlorestaTagInterpreter inter = new FlorestaTagInterpreter();
  // POSSample sample = sampleStream.read();
  // while(sample != null) {
  // for(int i = 0; i < sample.getTags().length; i++) {
  // String ori = sample.getTags()[i];
  // String con = inter.serialize(inter.parseMorphologicalTag(ori));
  // if(con == null) {
  // System.err.println("null: " + ori);
  // } else if(!con.equals(ori) && !con.contains("pnt") &&
  // !ori.contains("n-adj")) {
  // System.out.println(ori + " > " + con);
  // }
  // }
  // sample = sampleStream.read();
  // }
  // }
}
