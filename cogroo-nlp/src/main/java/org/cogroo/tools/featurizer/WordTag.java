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
package org.cogroo.tools.featurizer;

import opennlp.tools.chunker.ChunkSample;

import com.google.common.base.Objects;

public class WordTag {

  private final String word;
  private final String postag;
  private final String chunktag;

  public WordTag(String word, String postag) {
    this(word, postag, null);
  }
  
  public WordTag(String word, String postag, String chunktag) {
    super();
    this.word = word;
    this.postag = postag;
    this.chunktag = chunktag;
  }

  public String getWord() {
    return word;
  }

  public String getPostag() {
    return postag;
  }

  public String getChunktag() {
    return chunktag;
  }

  public static WordTag[] create(String[] word, String[] postag) {
    WordTag[] arr = new WordTag[word.length];
    for (int i = 0; i < word.length; i++) {
      arr[i] = new WordTag(word[i], postag[i]);
    }
    return arr;
  }

  public static WordTag[] create(String[] word, String[] postag, String[] chunktag) {
    WordTag[] arr = new WordTag[word.length];
    for (int i = 0; i < word.length; i++) {
      arr[i] = new WordTag(word[i], postag[i], chunktag[i]);
    }
    return arr;
  }

  public static void extract(WordTag[] wt, String[] word, String[] tag) {
    for (int i = 0; i < wt.length; i++) {
      word[i] = wt[i].getWord();
      tag[i] = wt[i].getPostag();
    }
  }

  public static void extract(WordTag[] wt, String[] word, String[] tag, String[] chunks) {
    for (int i = 0; i < wt.length; i++) {
      word[i] = wt[i].getWord();
      if(wt[i].getChunktag() == null) {
        String t = wt[i].getPostag();
        int bar = t.indexOf("|");
        
        tag[i] = t.substring(0, bar);
        chunks[i] = t.substring(bar+1);
      } else {
        tag[i] = wt[i].getPostag();
        chunks[i] = wt[i].getChunktag();        
      }
    }
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o instanceof WordTag) {
      return Objects.equal(this.word, ((WordTag) o).word)
          && Objects.equal(this.postag, ((WordTag) o).postag)
          && Objects.equal(this.chunktag, ((WordTag) o).chunktag);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(word, postag, chunktag);
  }
  
  @Override
  public String toString() {
    if(getChunktag() == null)
      return getWord() + "_" + getPostag();
    else
      return getWord() + "_" + getPostag() + "_" + getChunktag();
  }

  public static WordTag[] create(ChunkSample cs) {
    WordTag[] wt = new WordTag[cs.getSentence().length];

    String[] sentence = cs.getSentence();
    String[] pos = cs.getTags();
    String[] chunks = cs.getPreds();
    
    for (int i = 0; i < wt.length; i++) {
      wt[i] = new WordTag(sentence[i], pos[i], chunks[i]);
    }

    return wt;
  }
}
