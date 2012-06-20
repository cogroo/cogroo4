package br.ccsl.cogroo.text.impl;

import java.util.Collections;
import java.util.List;

import opennlp.tools.util.Span;
import br.ccsl.cogroo.text.Chunk;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.Token;

public class ChunkImpl implements Chunk {

  private Span span;

  private Sentence theSentence;

  private int index = -1;

  private String tag;
  
  public ChunkImpl(String tag, int start, int end, Sentence theSentence) {
    this.tag = tag;
    this.span = new Span(start, end);
    this.theSentence = theSentence;
  }
  
  @Override
  public String toString() {
    List<Token> tokens = theSentence.getTokens();
    StringBuilder sentence = new StringBuilder();

    sentence.append("Chunk: ").append(tag).append(" [ ");

    for (int i = span.getStart(); i < span.getEnd(); i++) {
      if (i == index)
        sentence.append("*");
      sentence.append(tokens.get(i).getLexeme()).append(" ");
    }
    sentence.append("]\n");

    return sentence.toString();
  }

  public void setHeadIndex(int index) {
    this.index = index;
  }

  public int getStart() {
    return span.getStart();
  }

  public int getEnd() {
    return span.getEnd();
  }

  public void setBoundaries(int start, int end) {
    span = new Span(start, end);
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public List<Token> getTokens() {
    return Collections.unmodifiableList(theSentence.getTokens().subList(
        getStart(), getEnd()));
  }

}
