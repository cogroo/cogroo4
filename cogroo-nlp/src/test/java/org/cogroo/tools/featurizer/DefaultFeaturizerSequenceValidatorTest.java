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

import static org.junit.Assert.*;

import org.cogroo.tools.featurizer.DefaultFeaturizerSequenceValidator;
import org.junit.Test;

public class DefaultFeaturizerSequenceValidatorTest {

  @Test
  public void testMatches() {
    assertFalse(DefaultFeaturizerSequenceValidator.matches("V=PCP=F=S", "F=S"));
    
    assertTrue(DefaultFeaturizerSequenceValidator.matches("0/1S", "0/1/3S"));
    assertTrue(DefaultFeaturizerSequenceValidator.matches("1S", "0/1/3S"));
    assertFalse(DefaultFeaturizerSequenceValidator.matches("2S", "0/1/3S"));
    assertFalse(DefaultFeaturizerSequenceValidator.matches("1P", "0/1/3S"));
    assertFalse(DefaultFeaturizerSequenceValidator.matches("3P", "0/1/3S"));
    
    assertTrue(DefaultFeaturizerSequenceValidator.matches("M=S", "M/F=S/P"));
    assertTrue(DefaultFeaturizerSequenceValidator.matches("M=S", "M/F=S"));
    
    assertFalse(DefaultFeaturizerSequenceValidator.matches("M", "M/F=S"));
    
    
  }

}
