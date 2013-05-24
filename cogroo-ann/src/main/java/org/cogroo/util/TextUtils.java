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
package org.cogroo.util;

import java.util.Arrays;
import java.util.List;

import org.cogroo.config.Analyzers;
import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.Token;

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

    output.append("Document text: ").append(document.getText()).append("\n\n");

    if (document.getSentences() != null) {
      int cont = 0;
      for (Sentence sentence : document.getSentences()) {
        cont++;
        output.append("{Sentence ").append(cont).append(": ")
            .append(sentence.getText()).append("\n");

        List<Token> tokens = sentence.getTokens();

        String format;

        Joiner joiner = Joiner.on(", ");

        if (tokens != null) {
          String[] lexemes = new String[tokens.size()];
          String[] posTags = new String[tokens.size()];
          String[] features = new String[tokens.size()];
          String[] lemmas = new String[tokens.size()];
          String[] chunks = new String[tokens.size()];
          String[] schunks = new String[tokens.size()];

          output.append("   (token, class tag, feature tag, lexeme, chunks, function)\n");
          for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            
            lexemes[i] = Strings.nullToEmpty(t.getLexeme());
            posTags[i] = Strings.nullToEmpty(t.getPOSTag());
            features[i] = Strings.nullToEmpty(t.getFeatures());

            if (t.getLemmas() != null)
              lemmas[i] = joiner.join(t.getLemmas());
            else
              lemmas[i] = "";
            
            String head = "";
            if(t.isChunkHead()) {
              head = "*";
            }
            chunks[i] = t.getChunkTag() + head;
            
            schunks[i] = t.getSyntacticTag();
          }

          format = "   | %-" + maxSize(lexemes) + "s | %-" + maxSize(posTags)
              + "s | %-" + maxSize(features) + "s | %-" + maxSize(lemmas)
              + "s | %-" + maxSize(chunks) + "s | %-" + maxSize(schunks)
              + "s |\n";

          for (int i = 0; i < tokens.size(); i++) {

            output.append(String.format(format, lexemes[i], posTags[i],
                features[i], lemmas[i], chunks[i], schunks[i]));

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

        output.append("   Syntax tree: \n   ");
        output.append(sentence.asTree().toSyntaxTree());
        
        output.append("\n}\n");
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
