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

import java.util.ArrayList;
import java.util.List;

import org.cogroo.text.Chunk;
import org.cogroo.text.Sentence;
import org.cogroo.text.SyntacticChunk;
import org.cogroo.text.Token;


public class TreeUtil {

  public static Node createTree(Sentence sent) {
    Node root = new Node();
    root.setLevel(0);
    root.setSyntacticTag("S");

    List<TreeElement> elements = createLeafsList(sent);
    TreeElement[] originalElements = elements.toArray(new TreeElement[elements
        .size()]);

    List<Chunk> chunks = sent.getChunks();
    List<SyntacticChunk> syntChunks = sent.getSyntacticChunks();

    for (int i = chunks.size() - 1; i >= 0; i--) {
      Node node = new Node();

      node.setSyntacticTag(chunks.get(i).getTag());
      node.setMorphologicalTag(null);
      node.setLevel(2);

      for (int j = chunks.get(i).getStart(); j < chunks.get(i).getEnd(); j++) {
        node.addElement(elements.get(j));
        elements.get(j).setParent(node);
      }

      for (int j = chunks.get(i).getEnd() - 1; j >= chunks.get(i).getStart(); j--) {
        elements.remove(j);
      }

      elements.add(chunks.get(i).getStart(), node);
    }

    for (int i = 0; i < syntChunks.size(); i++) {
      Node node = new Node();

      node.setSyntacticTag(syntChunks.get(i).getTag());
      node.setMorphologicalTag(null);
      node.setLevel(1);

      List<TreeElement> toRemove = new ArrayList<TreeElement>();
      List<TreeElement> sons = new ArrayList<TreeElement>();

      for (int j = syntChunks.get(i).getEnd() - 1; j >= syntChunks.get(i)
          .getStart(); j--) {

        if (originalElements[j].getParent() == null) {
          sons.add(0, originalElements[j]);
          toRemove.add(originalElements[j]);
          originalElements[j].setParent(node);
        } else {
          if (sons.size() == 0
              || sons.get(0) != originalElements[j].getParent()) {
            sons.add(0, originalElements[j].getParent());
            toRemove.add(originalElements[j].getParent());
          }
        }
      }
      
      for (TreeElement son : sons)
        node.addElement(son);
      
      int index = elements.indexOf(toRemove.get(toRemove.size() - 1));
      while(index == -1 && toRemove.size() > 1) {
        toRemove.remove(toRemove.size() - 1);
        index = elements.indexOf(toRemove.get(toRemove.size() - 1));
      }

      for (TreeElement element : toRemove)
        elements.remove(element);

      if(index >= 0)
        elements.add(index, node);
      //else
      //  elements.add(0, node);

      node.setParent(root);
    }

    for (TreeElement element : elements) {
      root.addElement(element);
    }

    return root;
  }

  public static List<TreeElement> createLeafsList(Sentence sentence) {
    List<TreeElement> leafs = new ArrayList<TreeElement>();

    List<Token> tokens = sentence.getTokens();

    for (Token token : tokens) {
      Leaf leaf = new Leaf(token.getLexeme(), token.getLemmas());
      leaf.setLevel(3);
      leaf.setMorphologicalTag(token.getPOSTag());
      leaf.setFeatureTag(token.getFeatures());
      leafs.add(leaf);
    }

    return leafs;
  }

}
