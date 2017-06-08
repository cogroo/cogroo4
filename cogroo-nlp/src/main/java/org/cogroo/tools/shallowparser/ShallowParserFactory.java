package org.cogroo.tools.shallowparser;


import opennlp.tools.chunker.ChunkerContextGenerator;
import opennlp.tools.chunker.ChunkerFactory;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.TokenTag;

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
