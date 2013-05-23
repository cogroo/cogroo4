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
package org.cogroo;

import opennlp.tools.chunker.Chunker;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.tokenize.Tokenizer;

public interface LanguageLoader {

  public SentenceDetector getSentenceDetector();

  public Tokenizer getTokenizer();

  public TokenNameFinder getProperNameFinder();

  public TokenNameFinder getExpressionFinder();

  public TokenNameFinder getContractionFinder();

  public POSTagger getPOSTagger();

  public Chunker getChunker();

  public Chunker getShallowParser();

}
