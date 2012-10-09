package org.cogroo.tools.shallowparser;

import opennlp.tools.util.SequenceValidator;

import org.cogroo.tools.chunker2.ChunkerContextGenerator;
import org.cogroo.tools.chunker2.ChunkerFactory;
import org.cogroo.tools.featurizer.WordTag;

public class ShallowParserFactory extends ChunkerFactory {

  @Override
  public ChunkerContextGenerator getContextGenerator() {
    return new ShallowParserContextGenerator();
  }

  @Override
  public SequenceValidator<WordTag> getSequenceValidator() {
    return new ShallowParserSequenceValidator();
  }

}
