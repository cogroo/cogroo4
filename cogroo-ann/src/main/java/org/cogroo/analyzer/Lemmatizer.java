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

import org.cogroo.dictionary.LemmaDictionary;
import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.Token;


public class Lemmatizer implements Analyzer {
  private LemmaDictionary dict;

  public Lemmatizer(LemmaDictionary dict) {
    this.dict = dict;
  }
  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();
    
    for (Sentence sentence : sentences) {
      List<Token> tokens = sentence.getTokens();
      
      for (int i = 0; i < tokens.size(); i++) {
        String tag = tokens.get(i).getPOSTag();
        String word = tokens.get(i).getLexeme();
        
        String[] lemmas = dict.getLemmas(word, tag);
        
        if (lemmas == null || lemmas.length == 0) {
          lemmas = dict.getLemmas(word.toLowerCase(), tag);
        }
        
        tokens.get(i).setLemmas(lemmas);
      }
    }
  }
}
