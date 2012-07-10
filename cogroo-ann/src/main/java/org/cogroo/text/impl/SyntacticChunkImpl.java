package org.cogroo.text.impl;

import java.util.Collections;
import java.util.List;

import org.cogroo.text.Sentence;
import org.cogroo.text.SyntacticChunk;
import org.cogroo.text.Token;

import opennlp.tools.util.Span;

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
  
  public List<Token> getTokens() {
    return Collections.unmodifiableList(theSentence.getTokens().subList(
        getStart(), getEnd()));
  }
}
