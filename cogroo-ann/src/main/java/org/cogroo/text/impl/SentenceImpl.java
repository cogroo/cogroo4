/**
 * Copyright (C) 2012 cogroo <cogroo@cogroo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cogroo.text.impl;

import java.util.List;
import java.util.Objects;

import org.cogroo.text.Chunk;
import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.SyntacticChunk;
import org.cogroo.text.Token;
import org.cogroo.text.tree.Node;
import org.cogroo.text.tree.TreeUtil;
import org.cogroo.util.ToStringHelper;

import opennlp.tools.util.Span;

/**
 * The <code>Sentence</code> class contains the position of the sentence in the
 * text and the list of word in it.
 */
public class SentenceImpl implements Sentence {

  /** the position of the sentence in the text */
  private Span span;

  /** the list every token in the sentence */
  private List<Token> tokens;
  
  private List<Chunk> chunks;
  
  private List<SyntacticChunk> syntacticChunks;
  
  /* a reference to the document that contains this sentence */
  private Document theDocument;
  
  private double tokensProb;
  
  public SentenceImpl(int start, int end, Document theDocument) {
    this(start, end, null, theDocument);
  }

  public SentenceImpl(int start, int end, List<Token> tokens, Document theDocument) {
    this.span = new Span(start, end);
    this.tokens = tokens;
    this.theDocument = theDocument;
  }

  /* (non-Javadoc)
   * @see org.cogroo.text.Sentence#getText()
   */
  public String getText() {
    return span.getCoveredText(theDocument.getText()).toString();
  }


  /* (non-Javadoc)
   * @see org.cogroo.text.Sentence#getTokens()
   */
  public List<Token> getTokens() {
    return tokens;
  }

  /* (non-Javadoc)
   * @see org.cogroo.text.Sentence#setTokens(java.util.List)
   */
  public void setTokens(List<Token> tokens) {
    this.tokens = tokens;
  }
  
  @Override
  public List<Chunk> getChunks() {
    return chunks;
  }

  @Override
  public void setChunks(List<Chunk> chunks) {
    this.chunks = chunks;
  }
  
  @Override
  public List<SyntacticChunk> getSyntacticChunks() {
    return syntacticChunks;
  }

  @Override
  public void setSyntacticChunks(List<SyntacticChunk> syntacticChunks) {
    this.syntacticChunks = syntacticChunks;
  }
  
  @Override
  public Node asTree() {
    return TreeUtil.createTree(this);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SentenceImpl) {
      SentenceImpl that = (SentenceImpl) obj;
      return Objects.equals(this.tokens, that.tokens)
          && Objects.equals(this.span, that.span);
    }
    return false;
  }

  @Override
  public String toString() {

    return ToStringHelper.toStringHelper(this).add("span", span).add("tk", tokens)
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(span, tokens);
  }

  @Override
  public int getStart() {
    return span.getStart();
  }

  @Override
  public int getEnd() {
    return span.getEnd();
  }

  @Override
  public void setBoundaries(int start, int end) {
    span = new Span(start, end);
  }
  
  @Override
  public double getTokensProb() {
    return tokensProb;
  }

  @Override
  public void setTokensProb(double prob) {
    tokensProb = prob;
  }

}
