package org.cogroo.tools.checker.rules.verbs;

import java.util.ArrayList;
import java.util.List;

public class VerbPlusPreps {

  private String verb;

  private List<Prep> preps = new ArrayList<Prep>();

  public String getVerb() {
    return verb;
  }

  public void setVerb(String verb) {
    this.verb = verb;
  }

  public List<Prep> getPreps() {
    return preps;
  }

  public void addPreps(Prep prep) {
    preps.add(prep);
  }

}
