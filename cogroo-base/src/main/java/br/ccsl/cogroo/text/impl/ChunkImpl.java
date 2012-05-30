package br.ccsl.cogroo.text.impl;

import java.util.List;

import opennlp.tools.util.Span;
import br.ccsl.cogroo.text.Chunk;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.Token;

public class ChunkImpl implements Chunk {

  private Span span;

  private Sentence theSentence;

  private int index = -1;
  
  public ChunkImpl(Span span, Sentence theSentence) {
    this.span = span;
    this.theSentence = theSentence;
  }
  
  @Override
  public String toString() {
    List<Token> tokens = theSentence.getTokens();
    StringBuilder sentence = new StringBuilder();

    sentence.append("Chunk: ").append(span.getType()).append(" [ ");

    for (int i = span.getStart(); i < span.getEnd(); i++) {
      if (i == index)
        sentence.append("*");
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

  public void setHeadIndex(int index) {
    this.index = index;
  }

}
