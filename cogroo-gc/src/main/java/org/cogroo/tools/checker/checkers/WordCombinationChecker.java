package org.cogroo.tools.checker.checkers;

import java.util.List;

import org.cogroo.entities.Mistake;
import org.cogroo.text.Sentence;
import org.cogroo.text.SyntacticChunk;
import org.cogroo.text.Token;
import org.cogroo.tools.checker.AbstractTypedChecker;
import org.cogroo.tools.checker.rules.verbs.Prep;
import org.cogroo.tools.checker.rules.verbs.VerbPlusPreps;
import org.cogroo.tools.checker.rules.verbs.Verbs;


public class WordCombinationChecker extends AbstractTypedChecker {

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
    List<Prep> preps = null;

    for (int i = 0; i < syntChunks.size(); i++) {
      String tag = syntChunks.get(i).getTag();

      if (tag.equals("P")) {
        tokens = syntChunks.get(i).getTokens();
        for (Token token : tokens) {
          System.out.println(token.getLexeme());
          String[] lemma = token.getLemmas();
          verb = verbs.getVerb(lemma[0]);
        }
      }
      else
      if (tag.equals("PIV")) {
        String prep = null;
        tokens = syntChunks.get(i).getTokens();

        for (Token token : tokens) {

          if (token.getPOSTag().equals("prp")) {
            prep = token.getLexeme();
            continue;
          }

          else if (token.getPOSTag().equals("n")) {
            String[] lemma = token.getLemmas();
            
            if (verb != null) {
              preps = verb.getPreps();
              for (Prep prep2 : preps) {
                if (!prep2.equals(prep)) {
                  List<String> objects = prep2.getObjects();
                  
                  for (String string : objects) {
                    if (token.getLemmas()[0].equals(string)) {
                      
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    return word;
  }

  public String getIdPrefix() {
    return ID_PREFIX;
  }

  public int getPriority() {
    return 215;
  }

  public List<Mistake> check(org.cogroo.entities.Sentence sentence) {
    // TODO Auto-generated method stub
    return null;
  }

}