package br.ccsl.cogroo.tools.checker;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import br.ccsl.cogroo.entities.Mistake;
import br.ccsl.cogroo.entities.Sentence;

public class TypedCheckerComposite extends GenericCheckerComposite<Sentence> implements TypedChecker {

  public TypedCheckerComposite(List<TypedChecker> aChildCheckers,
      boolean aAllowOverlaps) {
    super(convert(aChildCheckers), aAllowOverlaps);
  }

  private static List<GenericChecker<Sentence>> convert(
      List<TypedChecker> aChildCheckers) {
    List<GenericChecker<Sentence>> converted = new ArrayList<GenericChecker<Sentence>>(aChildCheckers.size());
    for (GenericChecker<Sentence> a : aChildCheckers) {
      converted.add(a);
    }
    return converted;
  }

  public List<Mistake> check(Sentence sentence) {
    List<Mistake> mistakes = new LinkedList<Mistake>();

    boolean[] occupied = new boolean[sentence.getSentence().length()];

    for (GenericChecker<Sentence> child : mChildCheckers) {
      List<Mistake> mistakesFromChild = child.check(sentence);
      mistakes.addAll(addFilteredMistakes(mistakesFromChild, occupied,
          sentence.getOffset()));
    }

    Collections.sort(mistakes, MISTAKE_COMPARATOR);

    return mistakes;
  }

}
