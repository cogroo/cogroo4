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
package org.cogroo.tools.checker.checkers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.cogroo.entities.Mistake;
import org.cogroo.text.Sentence;
import org.cogroo.text.SyntacticChunk;
import org.cogroo.text.Token;
import org.cogroo.tools.checker.AbstractChecker;
import org.cogroo.tools.checker.JavaRuleDefinition;
import org.cogroo.tools.checker.RuleDefinitionI;
import org.cogroo.tools.checker.rules.model.Example;
import org.cogroo.tools.checker.rules.verbs.Noun;
import org.cogroo.tools.checker.rules.verbs.Prep;
import org.cogroo.tools.checker.rules.verbs.VerbPlusPreps;
import org.cogroo.tools.checker.rules.verbs.Verbs;

public class GovernmentChecker extends AbstractChecker {

  private static final String ID_PREFIX = "word combination:";

  private final Verbs verbs;

  public GovernmentChecker() {
    List<Example> examples = new ArrayList<Example>();
    examples
        .add(createExample("Ele assiste o filme.", "Ele assiste ao filme."));

    RuleDefinitionI wordCombination = new JavaRuleDefinition(ID, CATEGORY,
        GROUP, DESCRIPTION, MESSAGE, SHORT, examples);
    add(wordCombination);

    verbs = new Verbs();
  }

  static final String ID = ID_PREFIX + "WORD_COMB_TOKEN";
  static final String CATEGORY = "Erros sintáticos";
  static final String GROUP = "Regência verbal";
  static final String DESCRIPTION = "Procura por verbos e analisa sua regência.";
  static final String MESSAGE = "Problema com a regência verbal";
  static final String SHORT = "Regência verbal.";

  public String getIdPrefix() {
    return ID_PREFIX;
  }

  public int getPriority() {
    return 211;
  }

  public List<Mistake> check(Sentence sentence) {
    List<Mistake> mistakes = new LinkedList<Mistake>();
    int offset = sentence.getStart();

    Token verb = findVerb(sentence);
    List<Noun> nouns = findNouns(sentence);

    if (verb != null && verb.getLemmas().length > 0) {
      VerbPlusPreps vpp = verbs.getVerb(verb.getLemmas()[0]);
      // Only gives the first lemma. %TODO improve this case.

//      for (Noun noun : nouns) {
      if (nouns != null && nouns.size() > 0) {
        Noun noun = nouns.get(0);
        
        if (vpp != null) {

          /** the correct preposition to be used in the sentence. */
          Prep prep = vpp.findWord(noun.getNoun());

          // if prep is null, then no object to the main verb was found
          if (prep != null) {
            
            Token sentPrep = findPrep(sentence, noun);
            
            if (sentPrep != null) {
              // The original sentence has a preposition already, but it is
              // wrong.
              
              if (!sentPrep.getLexeme().equals(prep.getPreposition())) {

                int start = sentPrep.getStart() + offset;
                int end = sentPrep.getEnd() + offset;

                mistakes.add(createMistake(ID,
                    createSuggestion(verb, sentPrep, prep), start, end,
                    sentence.getText()));
              }
              
              
            } else {
              //The original sentence has no preposition in its objects, though
              // it should have.
              if (!prep.getPreposition().equals("_")) {

                int start = verb.getStart() + offset;
                int end = verb.getEnd() + offset;

                mistakes.add(createMistake(ID,
                    createSuggestion(verb, sentPrep, prep), start, end,
                    sentence.getText()));
              }
            }
          }
        }
      }
    }
    return mistakes;
  }

  
  /**
   * Looks for a noun in the sentence's objects.
   * 
   * @param sentence
   *          entered by the user
   * @return a <tt>List></tt> of every noun found in the sentence's objects and
   *         its location in the sentence
   */
  public List<Noun> findNouns(Sentence sentence) {
    List<Noun> nouns = new ArrayList<Noun>();

    List<SyntacticChunk> syntChunks = sentence.getSyntacticChunks();

    for (int i = 0; i < syntChunks.size(); i++) {
      String tag = syntChunks.get(i).getTag();

      if (tag.equals("PIV") || tag.equals("ACC") || tag.equals("SC")) {

        for (Token token : syntChunks.get(i).getTokens()) {

          if (token.getPOSTag().equals("n")
              || token.getPOSTag().equals("pron-pers")) {

            if (token.getLemmas() != null && token.getLemmas().length > 0) {
              nouns.add(new Noun(token.getLemmas()[0], token.getStart()));
            } else {
              nouns.add(new Noun(token.getLexeme(), token.getStart()));
            }
          } else { // Adiciona um nome próprio
            if (token.getPOSTag().equals("prop")) {
              nouns.add(new Noun("NP", token.getStart()));
            }
          }
        }
      }
    }

    // int[] spans = spans(sentence);
    // for (int i = 0; i < spans.length; i++) {
    // if (spans[i] != 1) {
    // Token token = sentence.getTokens().get(i);
    // if (token.getPOSTag().equals("n") ||
    // token.getPOSTag().equals("pron-pers")) {
    // if (token.getLemmas() != null && token.getLemmas().length > 0)
    // nouns.add(token.getLemmas()[0]);
    // else
    // nouns.add(token.getLexeme());
    // } else { // Adiciona um nome próprio
    // if (token.getPOSTag().equals("prop")) {
    // nouns.add("NP");
    // }
    // }
    // }
    // }

    return nouns;
  }

  /**
   * Looks in the sentence's objects for a preposition
   * 
   * @param sentence
   *          the original sentence typed by the user
   * @return the <tt>Token</tt> that contains the searched preposition, if it
   *         exists; otherwise returns <tt>null</tt>
   */
  public Token findPrep(Sentence sentence, Noun noun) {
    List<SyntacticChunk> syntChunks = sentence.getSyntacticChunks();

    for (int i = 0; i < syntChunks.size(); i++) {
      String tag = syntChunks.get(i).getTag();

      if (tag.equals("PIV") || tag.equals("ACC") || tag.equals("SC")
          || tag.equals("P")) {
        // %TODO Improve the accuracy from the SC and P syntactic chunks, in
        // order to remove them from this condition
        for (Token token : syntChunks.get(i).getTokens()) {
          if (token.getPOSTag().equals("prp")) {
            
            // The preposition is after the noun, then it isn't part of the
            // object, thus shouldn't be corrected
            if (token.getStart() > noun.getSpan()) {
              token = null;
            }
            
            return token;
          }
        }
      }
    }

    // In case the preposition ins't located in an object
    int[] spans = spans(sentence);
    for (int i = 0; i < spans.length; i++) {
      if (spans[i] != 1) {
        Token token = sentence.getTokens().get(i);
        if (token.getPOSTag().equals("prp")) {
          return token;
        }
      }
    }

    return null;
  }

  private int[] spans(Sentence sentence) {
    int[] spans = new int[sentence.getTokens().size()];

    for (SyntacticChunk sc : sentence.getSyntacticChunks()) {
      for (int i = sc.getStart(); i < sc.getEnd(); i++) {
        spans[i] = 1;
      }
    }

    return spans;
  }

  /**
   * Looks in a sentence for a verb.
   * 
   * @param sentence
   *          entered by the user
   * @return the <tt>Token</tt> which contains the searched verb, in case none
   *         was found returns <tt>null</tt>
   */
  public Token findVerb(Sentence sentence) {
    List<SyntacticChunk> syntChunks = sentence.getSyntacticChunks();

    for (int i = 0; i < syntChunks.size(); i++) {
      String tag = syntChunks.get(i).getTag();

      if (tag.equals("P") || tag.equals("MV") || tag.equals("PMV")
          || tag.equals("AUX") || tag.equals("PAUX"))
        return syntChunks.get(i).getTokens().get(0);
    }

    return null;
  }

  private String[] createSuggestion(Token token, Token sentPrep, Prep prep) {
    String[] array = null;

    if (prep.getPreposition().equals("_")) {
      array = new String[] { token.getLexeme() + " " };
      // MESSAGE = new String ("O verbo (" + token.getLexeme()
      // + ") com o sentido de (" + prep.getMeaning()
      // + ") não leva preposição.");
    } else {
      array = new String[] { token.getLexeme() + " " + prep.getPreposition() };
      // MESSAGE = new String ("O verbo " + token.getLexeme()
      // + " com o sentido de (" + prep.getMeaning()
      // + ") pede a preposição: " + prep.getPreposition());

    }

    return array;
  }

}
