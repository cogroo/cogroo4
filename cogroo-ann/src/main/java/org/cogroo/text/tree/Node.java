package org.cogroo.text.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node extends TreeElement implements Serializable {

  private static final long serialVersionUID = 5419391069465738001L;

  private List<TreeElement> elems = new ArrayList<TreeElement>();

  public void addElement(TreeElement element) {
    elems.add(element);
  }

  public TreeElement[] getElements() {
    return elems.toArray(new TreeElement[elems.size()]);
  }

  public List<TreeElement> getElems() {
    return elems;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    // print itself and its children
    for (int i = 0; i < this.getLevel(); i++) {
      sb.append("=");
    }
    sb.append(this.getSyntacticTag());
    if (this.getMorphologicalTag() != null) {
      sb.append(this.getMorphologicalTag());
    }
    sb.append("\n");
    for (TreeElement element : elems) {
      sb.append(element.toString());
    }
    return sb.toString();
  }

  @Override
  public String toSyntaxTree() {
    return "[" + getSyntacticTag() + " " + toSyntaxTree(getElements()) + "]";
  }

  @Override
  public String toTreebank() {
    return "(" + getSyntacticTag() + " " + toTreebank(getElements()) + ")";
  }

  private String toSyntaxTree(TreeElement[] elements) {
    StringBuilder sb = new StringBuilder();
    for (TreeElement treeElement : elements) {
      sb.append(treeElement.toSyntaxTree() + " ");
    }
    return sb.toString();
  }

  private String toTreebank(TreeElement[] elements) {
    StringBuilder sb = new StringBuilder();
    for (TreeElement treeElement : elements) {
      sb.append(treeElement.toTreebank() + " ");
    }
    return sb.toString();
  }
}
