package org.cogroo.entities.tree;

public class Leaf extends TreeElement {

  private String word;
  private String lemma;

  public void setLexeme(String lexeme) {
    this.word = lexeme;
  }

  public String getLexeme() {
    return word;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    // print itself and its children
    for (int i = 0; i < this.getLevel(); i++) {
      sb.append("=");
    }
    if (this.getSyntacticTag() != null) {
      sb.append(this.getSyntacticTag() + "(" + this.getMorphologicalTag()
          + ") ");
    }
    sb.append(this.word + "\n");
    return sb.toString();
  }

  public void setLemma(String lemma) {
    this.lemma = lemma;
  }

  public String getLemma() {
    return lemma;
  }

  @Override
  public String toSyntaxTree() {

    return "[" + getMorphologicalTag() + " " + word + "]"; // word + "{" +
                                                           // getMorphologicalTag()
                                                           // + " '" +
                                                           // getLemma() + "'" +
                                                           // "}";
  }
}
