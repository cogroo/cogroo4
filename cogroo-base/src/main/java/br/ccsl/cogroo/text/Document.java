package br.ccsl.cogroo.text;

import java.util.List;

public interface Document {

  public abstract String getText();

  public abstract void setText(String text);

  public abstract List<Sentence> getSentences();

  public abstract void setSentences(List<Sentence> sentences);

}