package br.ccsl.cogroo.text;

import java.util.List;

/**
 * The <code>Document</code> class contains a text given by the user and also
 * its sentences separately in a list.
 */
public class Document {

  /** the <code>String</code> which contains the whole text */
  private String text;

  /** the list of every sentence in <code>text</code> */
  private List<Sentence> sentences;

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public List<Sentence> getSentences() {
    return sentences;
  }

  public void setSentences(List<Sentence> sentences) {
    this.sentences = sentences;
  }
}
