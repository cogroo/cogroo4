package br.ccsl.cogroo.tools.checker.checkers;

import java.util.Collections;
import java.util.List;

import br.ccsl.cogroo.entities.Mistake;
import br.ccsl.cogroo.entities.Sentence;
import br.ccsl.cogroo.entities.SyntacticChunk;
import br.ccsl.cogroo.entities.Token;
import br.ccsl.cogroo.entities.impl.SyntacticTag;
import br.ccsl.cogroo.tools.checker.AbstractChecker;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.SyntacticFunction;
import br.ccsl.cogroo.tools.checker.rules.verbs.Prep;
import br.ccsl.cogroo.tools.checker.rules.verbs.VerbPlusPreps;
import br.ccsl.cogroo.tools.checker.rules.verbs.Verbs;

public class WordCombinationChecker extends AbstractChecker {

  private static final String ID_PREFIX = "word combination:";

  public static void main(String[] args) {
    WordCombinationChecker wcc = new WordCombinationChecker();

  }

  public String findVerb(Sentence sentence) {
    String word = null;
    List<SyntacticChunk> syntChunks = sentence.getSyntacticChunks();
    List<Token> tokens = null;
    Verbs verbs = new Verbs();
    VerbPlusPreps verb = null;

    for (int i = 0; i < syntChunks.size(); i++) {
      SyntacticTag tag = syntChunks.get(i).getSyntacticTag();

      switch (tag.getSyntacticFunction()) {
      
      case VERB:
        tokens = syntChunks.get(i).getTokens();
        for (Token token : tokens) {
          System.out.println(token.getLexeme());
          String lemma = token.getPrimitive();
          verb = verbs.getVerb(lemma);
        }
        break;

      case DIRECT_OBJECT:
        tokens = syntChunks.get(i).getTokens();
        for (Token token : tokens) {
          String primitive = token.getPrimitive();
          List<String> list = verb.getPreps().get(i).getObjects();
          for (String string : list) {
            if (string.equals(primitive))
              ;
          }
        }
        break;
      }
    }

    return word;
  }

  // List<TreeElement> root;
  // List<String> preps = verbs.getPreps(verb);

  public String getIdPrefix() {
    return ID_PREFIX;
  }

  public List<Mistake> check(Sentence sentence) {
    String text = sentence.getSentence();
    // List<Mistake> mistakes = new LinkedList<Mistake>();
    // int offset = sentence.getSpan().getStart();

    String verb = findVerb(sentence);

    return Collections.emptyList();
  }

  public int getPriority() {
    return 215;
  }

}
