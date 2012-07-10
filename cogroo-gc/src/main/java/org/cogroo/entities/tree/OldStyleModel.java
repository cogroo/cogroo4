package org.cogroo.entities.tree;

import java.util.ArrayList;
import java.util.List;

import org.cogroo.entities.Sentence;
import org.cogroo.entities.Token;
import org.cogroo.entities.impl.ChunkTag;
import org.cogroo.entities.impl.SyntacticTag;

import org.cogroo.tools.checker.rules.model.TagMask.ChunkFunction;
import org.cogroo.tools.checker.rules.model.TagMask.SyntacticFunction;

public class OldStyleModel {

  private final static ChunkTag BOUNDARY_NOUN_PHRASE;
  private final static ChunkTag BOUNDARY_NOUN_PHRASE_MAIN;
  private final static ChunkTag BOUNDARY_VERB_PHRASE_MAIN;

  private final static ChunkTag INTERMEDIARY_NOUN_PHRASE;
  private final static ChunkTag INTERMEDIARY_NOUN_PHRASE_MAIN;
  private final static ChunkTag INTERMEDIARY_VERB_PHRASE;
  private final static ChunkTag OTHER;

  private final static SyntacticTag SYNT_NONE;
  private final static SyntacticTag SYNT_SUBJECT;
  private final static SyntacticTag SYNT_VERB;

  static {
    BOUNDARY_NOUN_PHRASE = new ChunkTag();
    BOUNDARY_NOUN_PHRASE.setChunkFunction(ChunkFunction.BOUNDARY_NOUN_PHRASE);
    BOUNDARY_NOUN_PHRASE_MAIN = new ChunkTag();
    BOUNDARY_NOUN_PHRASE_MAIN
        .setChunkFunction(ChunkFunction.BOUNDARY_NOUN_PHRASE_MAIN);
    BOUNDARY_VERB_PHRASE_MAIN = new ChunkTag();
    BOUNDARY_VERB_PHRASE_MAIN
        .setChunkFunction(ChunkFunction.BOUNDARY_VERB_PHRASE_MAIN);

    INTERMEDIARY_NOUN_PHRASE = new ChunkTag();
    INTERMEDIARY_NOUN_PHRASE
        .setChunkFunction(ChunkFunction.INTERMEDIARY_NOUN_PHRASE);
    INTERMEDIARY_NOUN_PHRASE_MAIN = new ChunkTag();
    INTERMEDIARY_NOUN_PHRASE_MAIN
        .setChunkFunction(ChunkFunction.INTERMEDIARY_NOUN_PHRASE_MAIN);
    INTERMEDIARY_VERB_PHRASE = new ChunkTag();
    INTERMEDIARY_VERB_PHRASE
        .setChunkFunction(ChunkFunction.INTERMEDIARY_VERB_PHRASE);

    OTHER = new ChunkTag();
    OTHER.setChunkFunction(ChunkFunction.OTHER);

    SYNT_NONE = new SyntacticTag();
    SYNT_NONE.setSyntacticFunction(SyntacticFunction.NONE);
    SYNT_SUBJECT = new SyntacticTag();
    SYNT_SUBJECT.setSyntacticFunction(SyntacticFunction.SUBJECT);
    SYNT_VERB = new SyntacticTag();
    SYNT_VERB.setSyntacticFunction(SyntacticFunction.VERB);
  }

  public static Node createTree(Sentence sent) {

    List<Token> tokens = sent.getTokens();
    List<List<Token>> tokenClusters = new ArrayList<List<Token>>();

    for (Token token : tokens) {
      if (isOtherPhrase(token) || isBoundary(token)) {
        addNewCluster(tokenClusters, token);
      } else if (isContinuation(tokenClusters, token)) {
        merge(tokenClusters, token);
      }
    }

    Node root = new Node();
    root.setLevel(0);
    root.setSyntacticTag("S");

    for (List<Token> cluster : tokenClusters) {
      String syntTag = syntactTagForCluster(cluster);
      if (syntTag != null) {
        addSyntNode(cluster, syntTag, root);
      } else {
        addPhraseNode(cluster, root);
      }
    }

    return root;

  }

  private static void addPhraseNode(List<Token> cluster, Node parent) {
    String tag = phraseTagForCluster(cluster);
    if (tag != null) {
      addPhraseNode(cluster, tag, parent);
    } else {
      addLeafs(cluster, parent);
    }

  }

  private static void addLeafs(List<Token> cluster, Node parent) {
    for (Token token : cluster) {
      Leaf n = new Leaf();
      n.setLevel(parent.getLevel() + 1);
      n.setMorphologicalTag(token.getMorphologicalTag().getClazzE().toString());
      n.setLexeme(token.getLexeme());
      n.setLemma(token.getPrimitive());
      parent.addElement(n);
    }
  }

  private static void addPhraseNode(List<Token> cluster, String tag, Node parent) {
    Node n = new Node();
    n.setLevel(parent.getLevel() + 1);
    n.setSyntacticTag(tag);
    parent.addElement(n);
    addLeafs(cluster, n);
  }

  private static String phraseTagForCluster(List<Token> cluster) {
    for (Token token : cluster) {
      if (isBoundaryOfNounPhrase(token) || isIntermediaryNounPhrase(token)) {
        return "NP";
      } else if (isBoundaryOfVerbPhrase(token)
          || isIntermediaryVerbPhrase(token)) {
        return "VP";
      }
    }
    return null;
  }

  private static void addSyntNode(List<Token> cluster, String syntTag,
      Node parent) {
    Node n = new Node();
    n.setLevel(parent.getLevel() + 1);
    n.setSyntacticTag(syntTag);
    parent.addElement(n);
    addPhraseNode(cluster, n);
  }

  private static String syntactTagForCluster(List<Token> cluster) {
    for (Token token : cluster) {
      if (token.getSyntacticTag().match(SYNT_SUBJECT)) {
        return "SUBJ";
      } else if (token.getSyntacticTag().match(SYNT_VERB)) {
        return "VERB";
      }
    }
    return null;
  }

  private static void merge(List<List<Token>> tokenClusters, Token token) {
    tokenClusters.get(tokenClusters.size() - 1).add(token);
  }

  private static Token getLastTokenOfCluster(List<List<Token>> tokenClusters) {
    if (tokenClusters.size() > 0) {
      List<Token> tokenCluster = tokenClusters.get(tokenClusters.size() - 1);
      return tokenCluster.get(tokenCluster.size() - 1); // never empty
    }
    return null;
  }

  private static boolean isContinuation(List<List<Token>> tokenClusters,
      Token token) {
    Token lastToken = getLastTokenOfCluster(tokenClusters);
    if (lastToken == null) {
      return false;
    }
    if (isBoundaryOfNounPhrase(lastToken)
        || isIntermediaryNounPhrase(lastToken)) {
      if (isIntermediaryNounPhrase(token)) {
        return true;
      } else {
        return false;
      }
    } else if (isBoundaryOfVerbPhrase(lastToken)
        || isIntermediaryVerbPhrase(lastToken)) {
      if (isIntermediaryVerbPhrase(token)) {
        return true;
      } else {
        return false;
      }
    }

    return false;
  }

  private static void addNewCluster(List<List<Token>> tokenClusters, Token token) {
    List<Token> other = new ArrayList<Token>();
    other.add(token);
    tokenClusters.add(other);

  }

  private static boolean isBoundary(Token token) {
    return token.getChunkTag().match(BOUNDARY_NOUN_PHRASE)
        || token.getChunkTag().match(BOUNDARY_NOUN_PHRASE_MAIN)
        || token.getChunkTag().match(BOUNDARY_VERB_PHRASE_MAIN);
  }

  private static boolean isBoundaryOfNounPhrase(Token token) {
    return token.getChunkTag().match(BOUNDARY_NOUN_PHRASE)
        || token.getChunkTag().match(BOUNDARY_NOUN_PHRASE_MAIN);
  }

  private static boolean isBoundaryOfVerbPhrase(Token token) {
    return token.getChunkTag().match(BOUNDARY_VERB_PHRASE_MAIN);
  }

  private static boolean isIntermediaryNounPhrase(Token token) {
    return token.getChunkTag().match(INTERMEDIARY_NOUN_PHRASE)
        || token.getChunkTag().match(INTERMEDIARY_NOUN_PHRASE_MAIN);
  }

  private static boolean isIntermediaryVerbPhrase(Token token) {
    return token.getChunkTag().match(INTERMEDIARY_VERB_PHRASE);
  }

  private static boolean isOtherPhrase(Token token) {
    return token.getChunkTag().match(OTHER);
  }
}
