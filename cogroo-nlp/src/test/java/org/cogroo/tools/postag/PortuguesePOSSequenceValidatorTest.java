package org.cogroo.tools.postag;

import static org.junit.Assert.*;

import org.cogroo.tools.postag.PortuguesePOSSequenceValidator;
import org.junit.Test;

public class PortuguesePOSSequenceValidatorTest {

  @Test
  public void testValidOutcomeStringString() {
    
    assertTrue(PortuguesePOSSequenceValidator.validOutcome("a", "b"));
    
    assertTrue(PortuguesePOSSequenceValidator.validOutcome("I-a", "I-a"));
    
    assertTrue(PortuguesePOSSequenceValidator.validOutcome("I-a", "B-a"));
    
    assertTrue(PortuguesePOSSequenceValidator.validOutcome("B-a", "I-a"));
    
    assertTrue(PortuguesePOSSequenceValidator.validOutcome("B-a", "b"));
    
    assertFalse(PortuguesePOSSequenceValidator.validOutcome("B-a", "B-a"));
    
    assertFalse(PortuguesePOSSequenceValidator.validOutcome("I-b", "B-a"));
    
    assertFalse(PortuguesePOSSequenceValidator.validOutcome("I-b", "I-a"));
    
    assertFalse(PortuguesePOSSequenceValidator.validOutcome("I-b", "a"));
  
  }

}
