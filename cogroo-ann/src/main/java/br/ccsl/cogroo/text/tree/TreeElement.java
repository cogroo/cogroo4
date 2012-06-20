package br.ccsl.cogroo.text.tree;

public abstract class TreeElement {
  private String syntacticTag;
  private String morphologicalTag;
  private int level;
  private TreeElement parent;

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

  public TreeElement getParent() {
    return parent;
  }

  public void setParent(TreeElement parent) {
    this.parent = parent;
  }
}
