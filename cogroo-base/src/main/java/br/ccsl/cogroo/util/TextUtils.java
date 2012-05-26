package br.ccsl.cogroo.util;

import java.util.Arrays;
import java.util.List;

import br.ccsl.cogroo.config.Analyzers;
import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.Token;
import br.ccsl.cogroo.text.impl.TokenImpl;

/**
 * The <code>TextUtils</code> class deals with the code prints.
 */
public class TextUtils {

  public static String[] tokensToString(List<Token> tokens) {

    String[] tokensString = new String[tokens.size()];

    for (int i = 0; i < tokens.size(); i++) {
      tokensString[i] = tokens.get(i).getLexeme();
    }

    return tokensString;
  }

  public static String[][] additionalContext(List<Token> tokens,
      List<Analyzers> analyzers) {
    String[][] additionalContext = new String[tokens.size()][analyzers.size()];

    for (int i = 0; i < analyzers.size(); i++) {
      for (int j = 0; j < tokens.size(); j++) {
        Object object = ((TokenImpl) tokens.get(j))
            .getAdditionalContext(analyzers.get(i));

        if (object == null)
          additionalContext[j][i] = null;
        else
          additionalContext[j][i] = (String) object;
      }
    }

    return additionalContext;
  }

  /**
   * @return the <code>String</code> to be printed
   */
  public static String nicePrint(Document document) {
    StringBuilder output = new StringBuilder();

    output.append("Entered text: ").append(document.getText()).append("\n\n");

    if (document.getSentences() != null) {
      int cont = 0;
      for (Sentence sentence : document.getSentences()) {
        cont++;
        output.append("  Sentence ").append(cont).append(": ")
            .append(sentence.getText())
            .append("\n");

        List<Token> tokens = sentence.getTokens();

        if (tokens != null) {
          output.append("    Tokens: [");
          for (Token token : tokens) {
            output.append(" ").append(token.getLexeme());

            if (token.getPOSTag() != null) {
              output.append("(").append(token.getPOSTag()).append(")");
            }
            
            if (token.getFeatures() != null) {
              output.append("{").append(token.getFeatures()).append("}");
            }
            
            output.append(" ");
            
          }
          output.append("]\n\n");
        }

        String[][] addcontext = TextUtils.additionalContext(tokens,
            Arrays.asList(Analyzers.CONTRACTION_FINDER, Analyzers.NAME_FINDER));
        for (String[] strings : addcontext) {
          output.append(Arrays.toString(strings)).append("\n");
        }
      }
    }
    return output.toString();
  }
}
