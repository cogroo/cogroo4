package org.cogroo.tools.checker.rules.verbs;

import java.util.ArrayList;
import java.util.List;

public class Prep {

  private String preposition;

  private String meaning;
  
  private List<String> objects;

  public String getPreposition() {
    return preposition;
  }

  public void setPreposition(String preposition) {
    this.preposition = preposition;
  }

  public String getMeaning() {
    return meaning;
  }

  public void setMeaning(String meaning) {
    this.meaning = meaning;
  }

  public List<String> getObjects() {
    return objects;
  }

  public void setObjects(String list) {
    String[] words = list.split(",\\s?");
    List<String> obj = new ArrayList<String>();
    for (String string : words) {
      obj.add(string);
    }
    this.objects = obj;
  }

}
