package br.ccsl.cogroo.entities.tree;

public abstract class TreeElement {
  private String syntacticTag;
  private String morphologicalTag;
  private int level;

  public void setSyntacticTag(String syntacticTag) {
    this.syntacticTag = syntacticTag;
  }

  public String getSyntacticTag() {
    return syntacticTag;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public int getLevel() {
    return level;
  }

  public void setMorphologicalTag(String morphologicalTag) {
    this.morphologicalTag = morphologicalTag;
  }

  public String getMorphologicalTag() {
    return morphologicalTag;
  }

  public abstract String toSyntaxTree();
}
