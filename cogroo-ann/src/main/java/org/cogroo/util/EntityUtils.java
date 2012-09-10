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

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.Span;

import org.cogroo.text.Token;
import org.cogroo.text.impl.TokenImpl;

public class EntityUtils {

  public static List<Token> groupTokensChar(String text, List<Token> toks,
      List<Span> charSpans, String additionalContext) {
    if (charSpans == null || charSpans.size() == 0) {
      return toks;
    }

    int lastVisitedTok = 0;
    List<Span> spans = new ArrayList<Span>(charSpans.size());

    for (Span ch : charSpans) {
      // System.out.println("looking for: " + ch.getCoveredText(text));
      Token aToken = toks.get(lastVisitedTok);
      while (aToken.getStart() < ch.getStart()) {
        lastVisitedTok++;
        aToken = toks.get(lastVisitedTok);
      }
      int start = lastVisitedTok;
      while (aToken.getEnd() < ch.getEnd()) {
        lastVisitedTok++;
        aToken = toks.get(lastVisitedTok);
      }
      int end = lastVisitedTok + 1;
      Span tokSpan = new Span(start, end);
      spans.add(tokSpan);
    }

    return groupTokens(text, toks, spans, additionalContext);
  }

  public static List<Token> groupTokens(String text, List<Token> toks,
      List<? extends Span> spans) {
    return groupTokens(text, toks, spans, null);
  }

  public static List<Token> groupTokens(String text, List<Token> toks,
      List<? extends Span> spans, String additionalContext) {
    for (int i = spans.size() - 1; i >= 0; i--) {
      Span span = spans.get(i);
      if (span.length() > 0) {
        int s = toks.get(span.getStart()).getStart();
        int e = toks.get(span.getEnd() - 1).getEnd();
        String lexeme = text.substring(s, e).replace(" ", "_");

        List<Token> removeToks = new ArrayList<Token>();
        for (int j = span.getEnd() - 1; j >= span.getStart(); j--) {
          removeToks.add(toks.remove(j));
        }
        Token t = new TokenImpl(s, e, lexeme);
        t.setPOSTag(span.getType());

        // if(additionalContext != null) {
        // t.addContext(analyzer, additionalContext);
        // t.setAdditionalContext(additionalContext);
        // }

        toks.add(span.getStart(), t);
      }
    }
    return toks;
  }

}
