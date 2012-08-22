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
package org.cogroo.tools.postag;

import static org.junit.Assert.*;

import org.cogroo.tools.postag.PortuguesePOSSequenceValidator;
import org.junit.Test;

public class PortuguesePOSSequenceValidatorTest {

  @Test
  public void testValidOutcomeStringString() {
    
    assertTrue(PortuguesePOSSequenceValidator.validOutcome("a", "a", "b"));
    
    assertTrue(PortuguesePOSSequenceValidator.validOutcome("a", "I-a", "I-a"));
    
    assertTrue(PortuguesePOSSequenceValidator.validOutcome("a", "I-a", "B-a"));
    
    assertTrue(PortuguesePOSSequenceValidator.validOutcome("a", "B-a", "I-a"));
    
    assertTrue(PortuguesePOSSequenceValidator.validOutcome("a", "B-a", "b"));
    
    assertFalse(PortuguesePOSSequenceValidator.validOutcome("a", "B-a", "B-a"));
    
    assertFalse(PortuguesePOSSequenceValidator.validOutcome("a", "I-b", "B-a"));
    
    assertFalse(PortuguesePOSSequenceValidator.validOutcome("a", "I-b", "I-a"));
    
    assertFalse(PortuguesePOSSequenceValidator.validOutcome("a", "I-b", "a"));
    
  }

}
