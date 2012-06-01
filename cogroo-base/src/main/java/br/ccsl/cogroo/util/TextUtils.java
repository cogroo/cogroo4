package br.ccsl.cogroo.util;

import java.util.Arrays;
import java.util.List;

import br.ccsl.cogroo.config.Analyzers;
import br.ccsl.cogroo.text.Chunk;
import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.SyntacticChunk;
import br.ccsl.cogroo.text.Token;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

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

    String[][] additionalContext = new String[analyzers.size()][tokens.size()];

    for (int i = 0; i < analyzers.size(); i++) {
      for (int j = 0; j < tokens.size(); j++) {
        Object object = tokens.get(j).getAdditionalContext(analyzers.get(i));

        if (object == null)
          additionalContext[i][j] = null;
        else
          additionalContext[i][j] = (String) object;
      }
    }

    return additionalContext;
  }

  /**
   * @return the <code>String</code> to be printed
   */
  public static String nicePrint(Document document) {
    boolean printAdditionalContext = false;
    StringBuilder output = new StringBuilder();

    output.append("Entered text: ").append(document.getText()).append("\n\n");

    if (document.getSentences() != null) {
      int cont = 0;
      for (Sentence sentence : document.getSentences()) {
        cont++;
        output.append("  Sentence ").append(cont).append(": ")
            .append(sentence.getText()).append("\n");

        List<Token> tokens = sentence.getTokens();

        String format = "  %10s %10s %9s %8s\n";

        Joiner joiner = Joiner.on(", ");

        if (tokens != null) {
          String[] lexemes = new String[tokens.size()];
          String[] posTags = new String[tokens.size()];
          String[] features = new String[tokens.size()];
          String[] lemmas = new String[tokens.size()];

          output.append("   Tokens:\n");
          for (int i = 0; i < tokens.size(); i++) {

            lexemes[i] = Strings.nullToEmpty(tokens.get(i).getLexeme());
            posTags[i] = Strings.nullToEmpty(tokens.get(i).getPOSTag());
            features[i] = Strings.nullToEmpty(tokens.get(i).getFeatures());

            if (tokens.get(i).getLemmas() != null)
              lemmas[i] = joiner.join(tokens.get(i).getLemmas());
            else
              lemmas[i] = "";
          }

          format = " | %-" + maxSize(lexemes) + "s | %-" + maxSize(posTags)
              + "s | %-" + maxSize(features) + "s | %-" + maxSize(lemmas)
              + "s |\n";

          for (int i = 0; i < tokens.size(); i++) {

            output.append(String.format(format, lexemes[i], posTags[i],
                features[i], lemmas[i]));

          }
          output.append("\n");

          if (printAdditionalContext) {
            String[][] addcontext = TextUtils.additionalContext(tokens, Arrays
                .asList(Analyzers.CONTRACTION_FINDER, Analyzers.NAME_FINDER));
            for (String[] line : addcontext) {
              for (String col : line) {
                output.append("[");
                if (col == null) {
                  output.append("-");
                } else {
                  output.append(col);
                }
                output.append("]");
              }
              output.append("\n");
            }
          }

        }

        if (sentence.getChunks() != null) {
          List<Chunk> chunks = sentence.getChunks();
          for (Chunk chunk : chunks) {
            output.append(chunk.toString());
          }
        }
        
        if (sentence.getSyntacticChunks() != null) {
          List<SyntacticChunk> chunks = sentence.getSyntacticChunks();
          for (SyntacticChunk sc : chunks) {
            output.append(sc.toString());
          }
        }
        
        output.append(sentence.asTree());
        
        output.append("\n");
      }
    }
    return output.toString();
  }

  private static int maxSize(String[] string) {
    int size = 1;
    for (String string2 : string) {
      if (size < string2.length()) {
        size = string2.length();
      }
    }
    return size;
  }

}
