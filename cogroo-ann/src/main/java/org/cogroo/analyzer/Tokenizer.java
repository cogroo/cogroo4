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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.Token;
import org.cogroo.text.impl.TokenImpl;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.util.Span;

/**
 * The <code>Tokenizer</code> class separates every word in a given sentence and allocates them in a
 * list of tokens.
 * 
 */
public class Tokenizer implements Analyzer {

  private TokenizerME tokenizer;

  public Tokenizer(TokenizerME tokenizer) {
    this.tokenizer = tokenizer;
  }

  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();

    for (Sentence sentence : sentences) {
      String sentenceString = sentence.getText();
      Span[] tokensSpan;

      synchronized (this.tokenizer) {
        tokensSpan = tokenizer.tokenizePos(preprocess(sentenceString));
      }

      List<Token> tokens = new ArrayList<Token>(tokensSpan.length);

      for (int i = 0; i < tokensSpan.length; i++) {
        Token token = new TokenImpl(tokensSpan[i].getStart(), tokensSpan[i].getEnd() , tokensSpan[i]
            .getCoveredText(sentenceString).toString());
        tokens.add(token);
      }
      sentence.setTokens(tokens);
    }
  }

  private static final Pattern OPEN_QUOTATION = Pattern.compile("[«“]");
  private static final Pattern CLOSE_QUOTATION = Pattern.compile("[»”]");
  
  private String preprocess(String sentenceString) {
    sentenceString = OPEN_QUOTATION.matcher(sentenceString).replaceAll("\"");
    sentenceString = CLOSE_QUOTATION.matcher(sentenceString).replaceAll("\"");
    return sentenceString;
  }
}
