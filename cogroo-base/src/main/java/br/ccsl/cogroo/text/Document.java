package br.ccsl.cogroo.text;

import java.util.List;

public interface Document {

  public String getText();

  public void setText(String text);

  public List<Sentence> getSentences();

  public void setSentences(List<Sentence> sentences);

}