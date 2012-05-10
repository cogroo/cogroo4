package cogroo;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.util.Span;
import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.entity.Token;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.MorphologicalTag;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.TokenCogroo;
import br.usp.pcs.lta.cogroo.tools.ProcessingEngine;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Class;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Gender;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Number;
import cogroo.uima.ae.FlorestaTagInterpreter;

public class PostPOSTagger implements ProcessingEngine {

  // pronomes obliquuos átonos
  private static final Set<String> PRONOMES_OBLIQUOS_ATONOS;
  private FlorestaTagInterpreter it = new FlorestaTagInterpreter();

  static {
    String[] arr = { "me", "te", "se", "o", "a", "lhe", "nos", "vos", "os",
        "as", "lhes" };
    PRONOMES_OBLIQUOS_ATONOS = Collections.unmodifiableSet(new HashSet<String>(
        Arrays.asList(arr)));
  }

  private static final Set<String> PREFIXOS_HYPHENS;

  static {
    String[] pho = { "ex", "sota", "soto", "vice", "pré", "pós", "pró",
        "extra", "contra", "auto", "neo", "semi", "ultra", "supra", "intra" };
    PREFIXOS_HYPHENS = Collections.unmodifiableSet(new HashSet<String>(Arrays
        .asList(pho)));
  }
  
  private MorphologicalTag toMorphologicalTag(String tag) {
    return it.parseMorphologicalTag(tag);
  }

  public void process(Sentence sentence) {
    for (Token t : sentence.getTokens()) {
      t.setMorphologicalTag(toMorphologicalTag(t.getOriginalPOSTag()));
    }
    mergeHyphenedWords(sentence);
  }

  private void mergeHyphenedWords(Sentence sentence) {
    List<Token> tokens = sentence.getTokens();
    // look for "-", check if it makes contact with the other hyphens
    boolean restart = true;
    int start = 1;
    while (restart) {
      restart = false;
      for (int i = start; i < tokens.size() - 1 && !restart; i++) {
        if ("-".equals(tokens.get(i).getLexeme())) {
          if (!hasCharacterBetween(tokens.get(i - 1), tokens.get(i))
              && !hasCharacterBetween(tokens.get(i), tokens.get(i + 1))) {
            Token a = tokens.get(i - 1);
            Token b = tokens.get(i + 1);
            if (PRONOMES_OBLIQUOS_ATONOS.contains(b.getLexeme().toLowerCase())) {
              // remove the "-"
              b.setSpan(new Span(b.getSpan().getStart() - 1, b.getSpan()
                  .getEnd()));
              b.setLexeme("-" + b.getLexeme());
              tokens.remove(i);
              restart = true;
              start = i + 1;
            } else {
              // merge the terms
              MorphologicalTag tag;
              if (PREFIXOS_HYPHENS.contains(a.getLexeme().toLowerCase())) {
                tag = b.getMorphologicalTag();
              } else {
                tag = merge(a.getMorphologicalTag(), b.getMorphologicalTag());
              }
              String lexeme = a.getLexeme() + "-" + b.getLexeme();
              StringBuilder lemma = new StringBuilder();
              if (a.getPrimitive() != null && a.getPrimitive().length() > 0) {
                lemma.append(a.getPrimitive());
              } else {
                lemma.append(a.getLexeme());
              }
              lemma.append("-");
              if (b.getPrimitive() != null && b.getPrimitive().length() > 0) {
                lemma.append(b.getPrimitive());
              } else {
                lemma.append(b.getLexeme());
              }
              // String lema = a.getPrimitive() + "-" + b.getPrimitive();
              Span span = new Span(a.getSpan().getStart(), b.getSpan().getEnd());
              Token newTok = new TokenCogroo(lexeme, span);
              newTok.setPrimitive(lemma.toString());
              newTok.setMorphologicalTag(tag);

              tokens.remove(i + 1);
              tokens.remove(i);
              tokens.set(i - 1, newTok);
              start = i;
              restart = true;
            }
          }
        }
      }
    }
  }

  FlorestaTagInterpreter ti = new FlorestaTagInterpreter();

  private MorphologicalTag merge(MorphologicalTag a, MorphologicalTag b) {

    MorphologicalTag ret = a.clone();
    Class aClass = a.getClazzE();
    Class bClass = b.getClazzE();

    if (!isVariable(aClass)) {
      ret = b.clone();
    } else {
      // prefer the noum
      if (aClass.equals(Class.NOUN) && bClass.equals(Class.NOUN)
          || aClass.equals(Class.ADJECTIVE) && bClass.equals(Class.ADJECTIVE)) {
        ret = b.clone();
        if (Gender.FEMALE.equals(a.getGenderE())
            || Gender.FEMALE.equals(b.getGenderE())) {
          ret.setGender(Gender.NEUTRAL);
        }
        if (Number.PLURAL.equals(a.getNumberE())
            || Number.PLURAL.equals(b.getNumberE())) {
          ret.setNumber(Number.PLURAL);
        }
      } else if (aClass.equals(Class.ADJECTIVE) && bClass.equals(Class.NOUN)) {
        ret = b.clone();
        ret.setClazz(Class.ADJECTIVE);
        if (Gender.FEMALE.equals(a.getGenderE())
            || Gender.FEMALE.equals(b.getGenderE())) {
          ret.setGender(Gender.NEUTRAL);
        }
        if (Number.PLURAL.equals(a.getNumberE())
            || Number.PLURAL.equals(b.getNumberE())) {
          ret.setNumber(Number.PLURAL);
        }
      } else if (aClass.equals(Class.VERB) || aClass.equals(Class.PREPOSITION)) {
        ret = b.clone();
        ret.setGender(Gender.MALE);
      } else if (aClass.equals(Class.NOUN)) {
        ret = a;
      } else if (bClass.equals(Class.NOUN)) {
        ret = b;
      }

      if (isVariable(aClass) && isVariable(bClass)) {
        Gender aGender = a.getGenderE();
        Gender bGender = b.getGenderE();

        Number aNumber = a.getNumberE();
        Number bNumber = b.getNumberE();

        if (aGender != null && bGender != null) {
          if (!aGender.equals(bGender)) {
            ret.setGender(Gender.NEUTRAL);
          }
        }

        if (aNumber != null && bNumber != null) {
          if (!aNumber.equals(bNumber)) {
            ret.setNumber(Number.NEUTRAL);
          }
        }
      }
    }

    // System.out.print("assertEquals(\"" + ti.serialize(ret) + "\", merge(\"");
    // System.out.print(ti.serialize(a));
    // System.out.print('"');
    // System.out.print(", ");
    // System.out.print('"');
    // System.out.print(ti.serialize(b));
    // System.out.print("\"));");
    //
    // System.out.println();

    return ret;
  }

  private boolean isVariable(Class a) {
    switch (a) {
    case ADJECTIVE:
    case NOUN:
    case PROPER_NOUN:
    case NUMERAL:
      return true;
    default:
      return false;
    }
  }

  private boolean hasCharacterBetween(Token a, Token b) {
    int aEnd = a.getSpan().getEnd();
    int bStart = b.getSpan().getStart();
    if (aEnd == bStart) {
      return false;
    }
    return true;
  }

}
