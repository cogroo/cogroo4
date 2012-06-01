package br.ccsl.cogroo.text.impl;

import java.util.List;

import opennlp.tools.util.Span;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.SyntacticChunk;
import br.ccsl.cogroo.text.Token;

public class SyntacticChunkImpl implements SyntacticChunk {
  
  private Span span;

  private Sentence theSentence;

  private String tag;

  public SyntacticChunkImpl(String tag, int start, int end, Sentence theSentence) {
    this.span = new Span(start, end);
    this.theSentence = theSentence;
    this.tag = tag;
  }
  
  @Override
  public String toString() {
    List<Token> tokens = theSentence.getTokens();
    StringBuilder sentence = new StringBuilder();

    sentence.append("SyntacticChunk: ").append(tag).append(" [ ");

    for (int i = span.getStart(); i < span.getEnd(); i++) {
      sentence.append(tokens.get(i).getLexeme()).append(" ");
    }
    sentence.append("]\n");

    return sentence.toString();
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public int getStart() {
    return span.getStart();
  }

  public int getEnd() {
    return span.getEnd();
  }
}
