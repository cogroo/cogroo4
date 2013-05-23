/**
 * Copyright (C) 2012 cogroo <cogroo@cogroo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
