package org.cogroo.tools.headfinder;


import opennlp.tools.chunker.ChunkerContextGenerator;
import opennlp.tools.chunker.ChunkerFactory;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.TokenTag;

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
