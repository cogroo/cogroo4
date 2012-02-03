package br.ccsl.cogroo.tools.featurizer;

public class WordTag {
  
  private final String word;
  private final String postag;
  
  public WordTag(String word, String postag) {
    super();
    this.word = word;
    this.postag = postag;
  }

  public String getWord() {
    return word;
  }

  public String getPostag() {
    return postag;
  }
  
  public static WordTag[] create(String[] word, String[] postag) {
    WordTag[] arr = new WordTag[word.length];
    for (int i = 0; i < word.length; i++) {
      arr[i] = new WordTag(word[i], postag[i]);
    }
    return arr;
  }
  
  public static void extract(WordTag[] wt, String[] word, String[] tag) {
    for (int i = 0; i < wt.length; i++) {
      word[i] = wt[i].getWord();
      tag[i] = wt[i].getPostag();
    }
  }
}
