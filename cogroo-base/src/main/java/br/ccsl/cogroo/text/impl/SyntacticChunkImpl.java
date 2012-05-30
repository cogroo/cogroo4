package br.ccsl.cogroo.text.impl;

import java.util.List;

import opennlp.tools.util.Span;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.SyntacticChunk;
import br.ccsl.cogroo.text.Token;

public class SyntacticChunkImpl implements SyntacticChunk {
  
  private Span span;

  private Sentence theSentence;

  public SyntacticChunkImpl(Span span, Sentence theSentence) {
    this.span = span;
    this.theSentence = theSentence;
  }
  
  @Override
  public String toString() {
    List<Token> tokens = theSentence.getTokens();
    StringBuilder sentence = new StringBuilder();

    sentence.append("SyntacticChunk: ").append(span.getType()).append(" [ ");

    for (int i = span.getStart(); i < span.getEnd(); i++) {
      sentence.append(tokens.get(i).getLexeme()).append(" ");
    }
    sentence.append("]\n");

    return sentence.toString();
  }

  public Span getSpan() {
    return span;
  }

  public void setSpan(Span span) {
    this.span = span;
  }

}
