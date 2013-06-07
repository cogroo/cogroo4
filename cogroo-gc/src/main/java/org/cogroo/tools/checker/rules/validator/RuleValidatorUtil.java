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

package org.cogroo.tools.checker.rules.validator;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.Span;

import org.cogroo.entities.Mistake;
import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.Token;

public class RuleValidatorUtil {
  public static Sentence getMistakeStartSentence(Document doc, Mistake mistake) {
    for (Sentence sent : doc.getSentences()) {
      Span s = new Span(sent.getStart(), sent.getEnd());
      if (s.contains(mistake.getStart())) {
        return sent;
      }
    }
    return null;
  }
  
  public static List<Token> getMistakeCoveredTokens(Sentence sent, Mistake mistake) {
    List<Token> out = new ArrayList<Token>();
    Span s = new Span(mistake.getStart(), mistake.getEnd());
    for (Token token : sent.getTokens()) {
      if(s.contains(token.getStart())) {
        out.add(token);
      }
    }
    
    return out;
  }
}
