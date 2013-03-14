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

import org.apache.log4j.Logger;
import org.cogroo.ContractionUtility;
import org.cogroo.config.Analyzers;
import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.Token;
import org.cogroo.text.impl.TokenImpl;
import org.cogroo.util.TextUtils;


/**
 * The <code>ContractionFinder</code> class searches for contractions in a given
 * sentence and then expands them to their primitive form.
 * 
 */
public class ContractionFinder implements Analyzer {

  private NameFinderME contractionFinder;
  
  protected static final Logger LOGGER = Logger.getLogger(ContractionFinder.class);

  public ContractionFinder(NameFinderME contractionFinder) {
    this.contractionFinder = contractionFinder;
  }

  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();

    for (Sentence sentence : sentences) {
      Span[] contractionsSpan;
      
      synchronized (this.contractionFinder) {
        contractionsSpan = contractionFinder.find(TextUtils
            .tokensToString(sentence.getTokens()));
      }
      
      List<Token> newTokens = sentence.getTokens();

      for (int i = contractionsSpan.length - 1; i >= 0; i--) {

        int start = contractionsSpan[i].getStart();

        String lexeme = sentence.getTokens().get(start).getLexeme();
        String[] contractions = ContractionUtility.expand(lexeme);

        Token original = newTokens.remove(start);
        if(contractions != null) {
          for (int j = contractions.length - 1; j >= 0; j--) {
            Token token = new TokenImpl(original.getStart(), original.getEnd(), contractions[j]);
            newTokens.add(start, token);
  
            String caze = null;
            if (j == 0)
              caze = "B";
            else if (j == contractions.length - 1)
              caze = "E";
            else
              caze = "I";
  
            token.addContext(Analyzers.CONTRACTION_FINDER, caze);
          }
        } else {
          LOGGER.debug("Missing contraction: " + lexeme);
        }
      }
      sentence.setTokens(newTokens);
    }
  }
}
