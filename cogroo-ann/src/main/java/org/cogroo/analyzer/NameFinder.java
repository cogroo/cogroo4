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
package org.cogroo.analyzer;

import java.util.List;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.Span;

import org.cogroo.config.Analyzers;
import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.Token;
import org.cogroo.text.impl.TokenImpl;
import org.cogroo.util.TextUtils;


/**
 * The <code>NameFinder</code> class searches for subsequent proper nouns in the
 * document sentences and gathers each of these sets in one word.
 * 
 */
public class NameFinder implements Analyzer {

  private NameFinderME nameFinder;

  public NameFinder(NameFinderME nameFinder) {
    this.nameFinder = nameFinder;
  }

  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();

    for (Sentence sentence : sentences) {
      Span[] namesSpan;
      synchronized (this.nameFinder) {
        namesSpan = nameFinder.find(TextUtils.tokensToString(sentence
            .getTokens()));
      }
      
      List<Token> newTokens = sentence.getTokens();

      for (int i = namesSpan.length - 1; i >= 0; i--) {
        int start = namesSpan[i].getStart(), end = namesSpan[i].getEnd();

        int chStart = newTokens.get(start).getStart();
        int chEnd = newTokens.get(end - 1).getEnd();

        String name = sentence.getText().substring(chStart, chEnd).replace(" ", "_");
        newTokens.remove(end - 1);

        for (int j = end - 2; j >= start; j--) {
          newTokens.remove(j);
        }
        Token token = new TokenImpl(chStart, chEnd, name);
        newTokens.add(start, token);
        
        token.addContext(Analyzers.NAME_FINDER, "P");
        
      }
      sentence.setTokens(newTokens);
    }
  }
}
