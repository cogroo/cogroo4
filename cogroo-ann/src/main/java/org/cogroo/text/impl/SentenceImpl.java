package org.cogroo.text.impl;

import java.util.List;

import org.cogroo.text.Chunk;
import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.SyntacticChunk;
import org.cogroo.text.Token;
import org.cogroo.text.tree.Node;
import org.cogroo.text.tree.TreeUtil;

import opennlp.tools.util.Span;

import com.google.common.base.Objects;

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
  
  public List<Chunk> getChunks() {
    return chunks;
  }

  public void setChunks(List<Chunk> chunks) {
    this.chunks = chunks;
  }
  
  public List<SyntacticChunk> getSyntacticChunks() {
    return syntacticChunks;
  }

  public void setSyntacticChunks(List<SyntacticChunk> syntacticChunks) {
    this.syntacticChunks = syntacticChunks;
  }
  
  public Node asTree() {
    return TreeUtil.createTree(this);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SentenceImpl) {
      SentenceImpl that = (SentenceImpl) obj;
      return Objects.equal(this.tokens, that.tokens)
          && Objects.equal(this.span, that.span);
    }
    return false;
  }

  @Override
  public String toString() {

    return Objects.toStringHelper(this).add("span", span).add("tk", tokens)
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(span, tokens);
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

}
