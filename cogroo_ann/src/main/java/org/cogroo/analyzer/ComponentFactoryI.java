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


/**
 *  Creates the analyzers using the OpenNLP components.
 *  <p>
 *  Follows the factory design pattern.
 *
 */
public interface ComponentFactoryI {
  
  /**
   * @return {@link SentenceDetector} if this {@link Analyzer} in the corresponding language exists, otherwise <tt>null</tt>.
   */
  public Analyzer createSentenceDetector();

  /**
   * @return {@link Tokenizer} if this Analyzer in the corresponding language exists, otherwise <tt>null</tt>.
   */
  public Analyzer createTokenizer();
  
  /**
   * @return {@link NameFinder} if this Analyzer in the corresponding language exists, otherwise <tt>null</tt>.
   */
  public Analyzer createNameFinder();
  
/**
 * @return {@link ContractionFinder} if this Analyzer in the corresponding language exists, otherwise <tt>null</tt>.
 */
  public Analyzer createContractionFinder();
  
  /**
   * @return {@link POSTagger} if this Analyzer in the corresponding language exists, otherwise <tt>null</tt>.
   */
  public Analyzer createPOSTagger();
  
  public Analyzer createFeaturizer();
  
  /**
   * @return {@link Pipe} according to the corresponding language.
   */
  public Analyzer createPipe();
}
