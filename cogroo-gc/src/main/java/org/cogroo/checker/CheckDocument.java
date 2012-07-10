package org.cogroo.checker;

import java.util.List;

import org.cogroo.entities.Mistake;
import org.cogroo.entities.Sentence;
import org.cogroo.text.impl.DocumentImpl;
import org.cogroo.util.TextUtils;


public class CheckDocument extends DocumentImpl {
  
  public CheckDocument() {
    super();
  }

  private List<Mistake> mistakes;

  private List<Sentence> sentencesLegacy;
  
  public List<Mistake> getMistakes() {
    return mistakes;
  }

  public void setMistakes(List<Mistake> mistakes) {
    this.mistakes = mistakes;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(TextUtils.nicePrint(this));
    sb.append("\n");
    sb.append("Mistakes count: " + mistakes.size());
    for (int i = 0; i < mistakes.size(); i++) {
      sb.append("  Mistake [").append(i).append("]\n");
      sb.append(mistakes.get(i));
    }
    
    return sb.toString();
  }

  public List<Sentence> getSentencesLegacy() {
    return sentencesLegacy;
  }

  public void setSentencesLegacy(List<Sentence> sentencesLegacy) {
    this.sentencesLegacy = sentencesLegacy;
  }

}
