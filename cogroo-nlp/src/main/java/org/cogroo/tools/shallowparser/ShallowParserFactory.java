package org.cogroo.tools.shallowparser;

import org.cogroo.tools.chunker2.ChunkerContextGenerator;
import org.cogroo.tools.chunker2.ChunkerFactory;
import org.cogroo.tools.chunker2.TokenTag;

import opennlp.tools.util.SequenceValidator;

public class ShallowParserFactory extends ChunkerFactory {

  @Override
  public ChunkerContextGenerator getContextGenerator() {
    return new ShallowParserContextGenerator();
  }

  @Override
  public SequenceValidator<TokenTag> getSequenceValidator() {
    return new ShallowParserSequenceValidator();
  }

}
