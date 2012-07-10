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
package org.cogroo.tools.featurizer;

import opennlp.tools.util.Sequence;

/**
 * The interface for featurizers which provide feature annotation for a sequence
 * of tokens.
 */
public interface Featurizer {

  /**
   * Generates chunk tags for the given sequence returning the result in an
   * array.
   * 
   * @param toks
   *          an array of the tokens or words of the sequence.
   * @param tags
   *          an array of the pos tags of the sequence.
   * 
   * @return an array of feature tags for each token in the sequence.
   */
  public String[] featurize(String[] toks, String tags[]);

  /**
   * Returns the top k chunk sequences for the specified sentence with the
   * specified pos-tags
   * 
   * @param sentence
   *          The tokens of the sentence.
   * @param tags
   *          The pos-tags for the specified sentence.
   * 
   * @return the top k feature sequences for the specified sentence.
   */
  public Sequence[] topKSequences(String[] sentence, String[] tags);

  /**
   * Returns the top k chunk sequences for the specified sentence with the
   * specified pos-tags
   * 
   * @param sentence
   *          The tokens of the sentence.
   * @param tags
   *          The pos-tags for the specified sentence.
   * @param minSequenceScore
   *          A lower bound on the score of a returned sequence.
   * 
   * @return the top k feature sequences for the specified sentence.
   */
  public Sequence[] topKSequences(String[] sentence, String[] tags,
      double minSequenceScore);
}
