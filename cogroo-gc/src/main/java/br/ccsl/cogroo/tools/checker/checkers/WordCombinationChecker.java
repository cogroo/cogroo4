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

    for (SyntacticChunk syntacticChunk : syntChunks) {
      SyntacticTag tag = syntacticChunk.getSyntacticTag();

      if (tag.getSyntacticFunction() == SyntacticFunction.VERB) {
        tokens = syntacticChunk.getTokens();

        for (Token token : tokens) {
          System.out.println(token.getLexeme());
          String lemma = token.getPrimitive();

          VerbPlusPreps verb = verbs.getVerb(lemma);
          
          
          
          if (verb != null) {
            List<Prep> preps = verb.getPreps();
            
            for (Prep prep : preps) {
              
            }
          }

        }

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
//    List<Mistake> mistakes = new LinkedList<Mistake>();
//    int offset = sentence.getSpan().getStart();

    String verb = findVerb(sentence);
    
    return Collections.emptyList();
  }

  public int getPriority() {
    return 215;
  }

}
