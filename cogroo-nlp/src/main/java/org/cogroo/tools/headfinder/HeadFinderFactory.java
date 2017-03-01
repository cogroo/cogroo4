package org.cogroo.tools.headfinder;

import opennlp.tools.util.SequenceValidator;

import org.cogroo.tools.chunker2.ChunkerContextGenerator;
import org.cogroo.tools.chunker2.ChunkerFactory;
import org.cogroo.tools.chunker2.TokenTag;
import org.cogroo.tools.featurizer.WordTag;


public class HeadFinderFactory extends ChunkerFactory {

  @Override
  public ChunkerContextGenerator getContextGenerator() {
    return new HeadFinderContextGenerator();
  }
  
  @Override
  public SequenceValidator<TokenTag> getSequenceValidator() {
    return new HeadFinderSequenceValidator();
  }

}
