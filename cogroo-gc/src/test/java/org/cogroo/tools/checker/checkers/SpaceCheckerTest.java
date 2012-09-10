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

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

import org.cogroo.entities.Mistake;
import org.cogroo.entities.Sentence;
import org.cogroo.tools.checker.RuleDefinitionI;
import org.junit.Test;

public class SpaceCheckerTest {

//  private Dictionary loadAbbDict() throws InvalidFormatException, IOException {
//    Dictionary abbDict = new Dictionary(this.getClass().getResourceAsStream(
//        "/dictionaries/pt_br/abbr.xml"));
//    return abbDict;
//  }
//
//  @Test
//  public void testEXTRA_BETWEEN_WORDS() throws InvalidFormatException, IOException {
//    SpaceChecker checker = createSpaceChecker();
//
//    String id = SpaceChecker.EXTRA_BETWEEN_WORDS_ID;
//
//    RuleDefinitionI def = checker.getRuleDefinition(id);
//
//    String correct = def.getExamples().get(0).getCorrect();
//    String incorrect = def.getExamples().get(0).getIncorrect();
//
//    Sentence sentence = createSentence(incorrect);
//    List<Mistake> mistakes = checker.check(sentence);
//
//    Mistake mistake = mistakes.get(0);
//
//    String fixed = fix(mistake, incorrect);
//
//    assertEquals(1, mistakes.size());
//    assertEquals(correct, fixed);
//    assertEquals(id, mistake.getRuleIdentifier());
//    assertEquals(def.getShortMessage(), mistake.getShortMessage());
//    assertEquals(def.getMessage(), mistake.getLongMessage());
//  }
//
//  @Test
//  public void testEXTRA_BEFORE_RIGHT_PUNCT() throws InvalidFormatException, IOException {
//    SpaceChecker checker = createSpaceChecker();
//
//    String id = SpaceChecker.EXTRA_BEFORE_RIGHT_PUNCT_ID;
//
//    RuleDefinitionI def = checker.getRuleDefinition(id);
//
//    String correct = def.getExamples().get(0).getCorrect();
//    String incorrect = def.getExamples().get(0).getIncorrect();
//
//    Sentence sentence = createSentence(incorrect);
//    List<Mistake> mistakes = checker.check(sentence);
//
//    Mistake mistake = mistakes.get(0);
//
//    String fixed = fix(mistake, incorrect);
//
//    assertEquals(1, mistakes.size());
//    assertEquals(correct, fixed);
//    assertEquals(id, mistake.getRuleIdentifier());
//    assertEquals(def.getShortMessage(), mistake.getShortMessage());
//    assertEquals(def.getMessage(), mistake.getLongMessage());
//  }
//
//  @Test
//  public void testEXTRA_AFTER_LEFT_PUNCT() throws InvalidFormatException, IOException {
//    SpaceChecker checker = createSpaceChecker();
//
//    String id = SpaceChecker.EXTRA_AFTER_LEFT_PUNCT_ID;
//
//    RuleDefinitionI def = checker.getRuleDefinition(id);
//
//    String correct = def.getExamples().get(0).getCorrect();
//    String incorrect = def.getExamples().get(0).getIncorrect();
//
//    Sentence sentence = createSentence(incorrect);
//    List<Mistake> mistakes = checker.check(sentence);
//
//    Mistake mistake = mistakes.get(0);
//
//    String fixed = fix(mistake, incorrect);
//
//    assertEquals(1, mistakes.size());
//    assertEquals(correct, fixed);
//    assertEquals(id, mistake.getRuleIdentifier());
//    assertEquals(def.getShortMessage(), mistake.getShortMessage());
//    assertEquals(def.getMessage(), mistake.getLongMessage());
//  }
//
//  @Test
//  public void testMISSING_SPACE_AFTER_PUNCT() throws InvalidFormatException, IOException {
//    SpaceChecker checker = createSpaceChecker();
//
//    String id = SpaceChecker.MISSING_SPACE_AFTER_PUNCT_ID;
//
//    RuleDefinitionI def = checker.getRuleDefinition(id);
//
//    String correct = def.getExamples().get(0).getCorrect();
//    String incorrect = def.getExamples().get(0).getIncorrect();
//
//    Sentence sentence = createSentence(incorrect);
//    List<Mistake> mistakes = checker.check(sentence);
//
//    Mistake mistake = mistakes.get(0);
//
//    String fixed = fix(mistake, incorrect);
//
//    assertEquals(1, mistakes.size());
//    assertEquals(correct, fixed);
//    assertEquals(id, mistake.getRuleIdentifier());
//    assertEquals(def.getShortMessage(), mistake.getShortMessage());
//    assertEquals(def.getMessage(), mistake.getLongMessage());
//  }
//
//  @Test
//  public void testNoError() throws InvalidFormatException, IOException {
//    Sentence sentence = createSentence("Abc (abc).");
//    SpaceChecker checker = createSpaceChecker();
//    List<Mistake> mistakes = checker.check(sentence);
//
//    assertEquals(0, mistakes.size());
//  }
//
//  @Test
//  public void testParenthesisPeriod() throws InvalidFormatException, IOException {
//    Sentence sentence = createSentence("Meu tel. é \"(12) 4789-4928\"!\n");
//    SpaceChecker checker = createSpaceChecker();
//    List<Mistake> mistakes = checker.check(sentence);
//
//    assertEquals(0, mistakes.size());
//  }
//
//  @Test
//  public void testEMail() throws InvalidFormatException, IOException {
//    Sentence sentence = createSentence("Meu e-mail é asdf@linux.ime.usp.br!\n");
//
//    SpaceChecker checker = createSpaceChecker();
//    List<Mistake> mistakes = checker.check(sentence);
//
//    assertEquals(0, mistakes.size());
//  }
//
//  @Test
//  public void testMoney() throws InvalidFormatException, IOException {
//    Sentence sentence = createSentence("Eu tenho R$ 4,00\n");
//
//    SpaceChecker checker = createSpaceChecker();
//    List<Mistake> mistakes = checker.check(sentence);
//
//    assertEquals(0, mistakes.size());
//  }
//
//  @Test
//  public void testNumber() throws InvalidFormatException, IOException {
//    Sentence sentence = createSentence("Capítulo 2.1.4, seção 2.1.2!\n");
//
//    SpaceChecker checker = createSpaceChecker();
//    List<Mistake> mistakes = checker.check(sentence);
//
//    assertEquals(0, mistakes.size());
//  }
//
//  @Test
//  public void testAbbreviation() throws InvalidFormatException, IOException {
//    Sentence sentence = createSentence("O Sr. L.A.P. morreu.\n");
//
//    SpaceChecker checker = createSpaceChecker();
//    List<Mistake> mistakes = checker.check(sentence);
//
//    assertEquals(0, mistakes.size());
//  }
//
//  @Test
//  public void testAbbreviation2() throws InvalidFormatException, IOException {
//    Sentence sentence = createSentence("A palavra N.Sra. está no dicionário de abreviações.\n");
//
//    SpaceChecker checker = new SpaceChecker(loadAbbDict());
//    List<Mistake> mistakes = checker.check(sentence);
//
//    assertEquals(0, mistakes.size());
//  }
//
//  @Test
//  public void testAbbreviation3() throws InvalidFormatException, IOException {
//    Sentence sentence = createSentence("O Sr. L.P.D., e seu irmão (A.P.D.) morreram.\n");
//
//    SpaceChecker checker = new SpaceChecker(loadAbbDict());
//    List<Mistake> mistakes = checker.check(sentence);
//
//    assertEquals(0, mistakes.size());
//  }
//
//  @Test
//  public void testURL() throws InvalidFormatException, IOException {
//    Sentence sentence = createSentence("O site é: (http://www.ime.usp.br/~finger).\n");
//
//    SpaceChecker checker = new SpaceChecker(loadAbbDict());
//    List<Mistake> mistakes = checker.check(sentence);
//
//    assertEquals(0, mistakes.size());
//  }
//
//  @Test
//  public void testParenthesis() throws InvalidFormatException, IOException {
//    Sentence sentence = createSentence("Os sistemas (digestivo, respiratório, etc.)\n");
//
//    SpaceChecker checker = new SpaceChecker(loadAbbDict());
//    List<Mistake> mistakes = checker.check(sentence);
//
//    assertEquals(0, mistakes.size());
//  }
//
//  private String fix(Mistake mistake, String incorrect) {
//    int start = mistake.getStart();
//    int end = mistake.getEnd();
//    String replace = mistake.getSuggestions()[0];
//
//    String fixed = incorrect.substring(0, start) + replace
//        + incorrect.substring(end);
//    return fixed;
//  }
//
//  private Sentence createSentence(String test) {
//    Sentence sentence = new Sentence();
//
//    sentence.setSentence(test);
//    sentence.setSpan(new Span(0, test.length()));
//    return sentence;
//  }
//
//  private SpaceChecker createSpaceChecker() throws InvalidFormatException, IOException {
//    return new SpaceChecker(loadAbbDict());
//  }

}
