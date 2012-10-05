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
package org.cogroo.tools.chunker2;

import org.cogroo.tools.featurizer.WordTag;

import opennlp.tools.util.SequenceValidator;

public class DefaultChunkerSequenceValidator implements SequenceValidator<WordTag>{

  private boolean validOutcome(String outcome, String prevOutcome) {
    if (outcome.startsWith("I-")) {
      if (prevOutcome == null) {
        return (false);
      }
      else {
        if (prevOutcome.equals("O")) {
          return (false);
        }
        if (!prevOutcome.substring(2).equals(outcome.substring(2))) {
          return (false);
        }
      }
    }
    return true;
  }

  protected boolean validOutcome(String outcome, String[] sequence) {
    String prevOutcome = null;
    if (sequence.length > 0) {
      prevOutcome = sequence[sequence.length-1];
    }
    return validOutcome(outcome,prevOutcome);
  }
  

  @Override
  public boolean validSequence(int i, WordTag[] inputSequence,
      String[] outcomesSequence, String outcome) {
    return validOutcome(outcome, outcomesSequence);
  }
  
}
