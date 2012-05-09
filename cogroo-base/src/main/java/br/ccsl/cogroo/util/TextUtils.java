package br.ccsl.cogroo.util;

import java.util.List;

import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.Token;

public class TextUtils {

  public static String[] tokensToString(List<Token> tokens) {

    String[] tokensString = new String[tokens.size()];

    for (int i = 0; i < tokens.size(); i++) {
      tokensString[i] = tokens.get(i).getLexeme();
    }

    return tokensString;
  }

  public static String nicePrint(Document document) {
    StringBuilder output = new StringBuilder();

    output.append("Entered text: ").append(document.getText()).append("\n\n");

    if (document.getSentences() != null) {
      int cont = 0;
      for (Sentence sentence : document.getSentences()) {
        cont++;
        output.append("  Sentence ").append(cont).append(": ")
            .append(sentence.getCoveredSentence(document.getText()))
            .append("\n");

        List<Token> tokens = sentence.getTokens();

        if (tokens != null) {
          output.append("    Tokens: [");
          for (Token token : tokens) {
            output.append(" ").append(token.getLexeme());

            if (token.getPOSTag() != null) {
              output.append("(").append(token.getPOSTag()).append(")");
            }
            output.append(" ");
          }
          output.append("]\n\n");
        }
      }
    }
    return output.toString();
  }
}
