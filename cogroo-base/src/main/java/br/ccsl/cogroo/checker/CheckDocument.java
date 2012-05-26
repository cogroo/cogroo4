package br.ccsl.cogroo.checker;

import java.util.List;

import br.ccsl.cogroo.entities.Mistake;
import br.ccsl.cogroo.text.Document;

public class CheckDocument extends Document {
  
  private List<Mistake> mistakes;

  public List<Mistake> getMistakes() {
    return mistakes;
  }

  public void setMistakes(List<Mistake> mistakes) {
    this.mistakes = mistakes;
  }
  
  

}
