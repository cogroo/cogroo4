package br.ccsl.cogroo.text;

import opennlp.tools.util.Span;

public interface SyntacticChunk {

  public Span getSpan();

  public void setSpan(Span span);
}
