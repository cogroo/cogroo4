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
package org.cogroo.entities.tree;

import java.util.List;

import opennlp.tools.util.Span;

import org.cogroo.entities.SyntacticChunk;
import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.tools.checker.rules.model.TagMask;
import org.cogroo.tools.checker.rules.model.TagMask.SyntacticFunction;

public class TextModel {

  private final TagMask VERB;
  private final TagMask NONE;
  private final TagMask SUBJ;
  
  private Node root;
  // private final Text text;

  public final static String SPAN = "Span: ";
  public final static String SYNTACTIC_FUNCTION = "Synt.: ";
  public final static String CHUNK_FUNCTION = "Chunk.: ";
  public final static String MORPH_FUNCTION = "Morph.: ";

  public TextModel(org.cogroo.entities.Sentence sentence) {
    this.VERB = new TagMask();
    this.VERB.setSyntacticFunction(SyntacticFunction.VERB);
    this.SUBJ = new TagMask();
    this.SUBJ.setSyntacticFunction(SyntacticFunction.SUBJECT);
    this.NONE = new TagMask();
    this.NONE.setSyntacticFunction(SyntacticFunction.NONE);

    root = toRoot(sentence);

  }

  public Node getRoot() {
    return root;
  }

  private Span getSpan(List<org.cogroo.entities.Token> tokens) {
    if (tokens != null && tokens.size() > 0) {
      return new Span(tokens.get(0).getSpan().getStart(), tokens
          .get(tokens.size() - 1).getSpan().getEnd());
    }
    return null;
  }

  private Sentence toRoot(org.cogroo.entities.Sentence sentence) {
    Sentence sent = new Sentence();
    sent.setLevel(0);
    // Span span = getSpan(sentence.getTokens());

    List<SyntacticChunk> chunks = sentence.getSyntacticChunks();
    for (SyntacticChunk syntacticChunk : chunks) {
        addChild(syntacticChunk, sent);
    }
    return sent;
  }

  private void addChild(SyntacticChunk syntacticChunk, Node parent) {
    if (syntacticChunk.getSyntacticTag() != null && !syntacticChunk.getSyntacticTag().match(this.NONE)) {
      Chunk c = new Chunk();
      c.setLevel(parent.getLevel() + 1);
      c.setSyntacticTag(syntacticChunk.getSyntacticTag().toVerboseString());
      
      for (org.cogroo.entities.Chunk child : syntacticChunk.getChildChunks()) {
        addChild(child, c);
      }
      parent.addElement(c);
    } else {
      for (org.cogroo.entities.Chunk chunk : syntacticChunk.getChildChunks()) {
        addChild(chunk, parent);
      }
    }
  }

  private void addChild(org.cogroo.entities.Chunk chunk, Node parent) {
    if(chunk.getType() == null) {
      addChild(chunk.getTokens(), parent);
    } else {
      Chunk c = new Chunk();
      c.setLevel(parent.getLevel() + 1);
      c.setSyntacticTag(chunk.getType());
      
      addChild(chunk.getTokens(), c);
  
      parent.addElement(c);
    }
  }

  private void addChild(List<org.cogroo.entities.Token> tokenList,
      Node parent) {
    for (int i = 0; i < tokenList.size(); i++) {
      addChild(tokenList.get(i), parent, isHead(i, tokenList.get(i)));
    }
  }

  private boolean isHead(int i, org.cogroo.entities.Token token) {
    int relativeIndex = i; //- token.getChunk().getFirstToken();
    if(relativeIndex == token.getChunk().getRelativeHeadIndex())
      return true;
    return false;
  }

  private void addChild(org.cogroo.entities.Token token, Node parent, boolean isHead) {
    Token t = new Token();
    t.setLevel(parent.getLevel() + 1);
    t.setLexeme(token.getLexeme());
    t.setIsChunkHead(isHead);
    t.setMorphologicalTag(token.getMorphologicalTag().getClazzE().name());
    
    MorphologicalTag mt = token.getMorphologicalTag().clone();
    mt.setClazz(null);
    
    t.setFeatures(mt.toString());
    t.setLemma(token.getPrimitive());
    // Span span = token.getSpan();
    // t.details.add(MORPH_FUNCTION +
    // token.getMorphologicalTag().toVerboseString());
    // t.details.add(SPAN + span);
    // t.parent = parent;

    parent.addElement(t);
  }

  // public class Node
  // {
  // protected Node parent;
  // protected String text;
  // protected Span span;
  // protected List<String> details = new ArrayList<String>();
  // protected List<Node> children;
  //
  // public List<Edge> getAllEdges()
  // {
  // List<Edge> edges = new ArrayList<Edge>();
  // if(children != null)
  // {
  // for (Node node : children) {
  // edges.addAll(node.getAllEdges());
  // edges.add(new Edge(this, node));
  // }
  // }
  //
  // return edges;
  // }
  //
  // public String getText()
  // {
  // if(this.details.size() > 0)
  // {
  // StringBuilder sb = new StringBuilder(text);
  // for (String d : details) {
  // sb.append("\n" + d);
  // }
  //
  // return sb.toString();
  // }
  // return text;
  // }
  // }

  // public class Edge
  // {
  // private Node parent;
  // private Node child;
  // public Edge(Node parent, Node child) {
  // super();
  // this.parent = parent;
  // this.child = child;
  // }
  // public Node getParent() {
  // return parent;
  // }
  // public Node getChild() {
  // return child;
  // }
  // }

  public class Text extends Node {

  }

  public class Sentence extends Node {
    @Override
    public String getSyntacticTag() {
      return "S";
    }
  }

  public class Chunk extends Node {

  }

  public class Token extends Leaf {

  }

}
