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
