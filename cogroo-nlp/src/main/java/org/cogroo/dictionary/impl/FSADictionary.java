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
package org.cogroo.dictionary.impl;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.cogroo.dictionary.LemmaDictionary;
import org.cogroo.util.PairWordPOSTag;

import morfologik.stemming.Dictionary;
import morfologik.stemming.DictionaryLookup;
import morfologik.stemming.WordData;
import opennlp.tools.postag.TagDictionary;
import opennlp.tools.util.Cache;

import static org.cogroo.util.ByteArrayUtil.toByteArray;

public class FSADictionary implements TagDictionary, LemmaDictionary, Iterable<String> {

  protected static final Logger LOGGER = Logger.getLogger(FSADictionary.class);
  private DictionaryLookup dictLookup;


  private FSADictionary(DictionaryLookup dictLookup) {
    this.dictLookup = dictLookup;
  }
  
  private Cache tagCache = new Cache(500);

  private String[] tagLookup(String word) {
    if(word == null || word.isEmpty()) {
      return null;
    } else {
      String[] tags = (String[]) tagCache.get(word);
      if(tags != null) {
        return tags;
      }
    }
    synchronized (dictLookup) {
      List<WordData> data = dictLookup.lookup(word);
      if (data.size() > 0) {
        List<String> tags = new ArrayList<String>(data.size());
        for (int i = 0; i < data.size(); i++) {
          if(isValid(data.get(i))) {
            tags.add(data.get(i).getTag().toString());
          }
        }
        if(tags.size() > 0) {
          String[] res = tags.toArray(new String[tags.size()]);
          tagCache.put(word, res);
          return res;
        }
        return null;
      }
    }
    return null;
  }
  
  private Cache lemmaTagCache = new Cache(500);
  
  private List<PairWordPOSTag> lemmaTagLookup(String word) {
    if(word == null || word.isEmpty()) {
      return null;
    } else {
      List<PairWordPOSTag> tags = (List<PairWordPOSTag>) lemmaTagCache.get(word);
      if(tags != null) {
        return tags;
      }
    }
    List<PairWordPOSTag> list;
    synchronized (dictLookup) {
      List<WordData> data = dictLookup.lookup(word);
      if (data.size() > 0) {
        list = new ArrayList<PairWordPOSTag>(data.size());
        for (int i = 0; i < data.size(); i++) {
          WordData wd = data.get(i);
          if(isValid(wd)) {
            list.add(new PairWordPOSTag(wd.getStem().toString(), wd.getTag()
                .toString()));
          }
        }
        List<PairWordPOSTag> tags = Collections.unmodifiableList(list);
        lemmaTagCache.put(word, tags);
        return tags;
      }
    }
    return Collections.emptyList();
  }

  private boolean isValid(WordData wd) {
    if(wd.getStem() == null) {
      LOGGER.error("Got invalid entry from FSA dictionary: " + wd);
      return false;
    }
    
    return true;
  }

  public String[] getTags(String word) {
    if (word == null) {
      return null;
    }
    return tagLookup(word);
  }
  
  public String[] getLemmas(String word, String tag) {
    List<String> output = new ArrayList<String>();
    List<PairWordPOSTag> pairs = lemmaTagLookup(word);
    for (PairWordPOSTag pairWordPOSTag : pairs) {
      boolean match = false;
      if (pairWordPOSTag.getPosTag().equals(tag)) {
        match = true;
      } else {
        // TODO: this is language specific !!
        if ("n-adj".equals(pairWordPOSTag.getPosTag())) {
          if ("n".equals(tag) || "adj".equals(tag)) {
            match = true;
          }
        } else if ("n-adj".equals(tag)) {
          if ("n".equals(pairWordPOSTag.getPosTag())
              || "adj".equals(pairWordPOSTag.getPosTag())) {
            match = true;
          }
        }
      }
      if (match)
        output.add(pairWordPOSTag.getWord());
    }
    
    return output.toArray(new String[output.size()]);
  }
  
  /** This is used by rule system */
  public List<PairWordPOSTag> getTagsAndLemms(String aWord) {
    // TODO: acabar isso usando Cache.. Colocar cache no
    return lemmaTagLookup(aWord);
  }

  public static TagDictionary create(String path)
      throws IllegalArgumentException, IOException {
    FileInputStream fsaData = new FileInputStream(path);
    FileInputStream featuresData = new FileInputStream(
        Dictionary.getExpectedFeaturesName(path));
    return create(fsaData, featuresData);
  }

  public static byte[] getFSADictionaryInfo(String path) throws IOException {
    FileInputStream featuresData = new FileInputStream(
        Dictionary.getExpectedFeaturesName(path));
    return toByteArray(featuresData);
  }

  public static byte[] getFSADictionaryData(String path) throws IOException {
    FileInputStream featuresData = new FileInputStream(path);
    return toByteArray(featuresData);
  }

  public static FSADictionary create(InputStream fsaData,
      InputStream featuresData) throws IllegalArgumentException, IOException {
    DictionaryLookup dictLookup = new DictionaryLookup(Dictionary.readAndClose(
        fsaData, featuresData));
    return new FSADictionary(dictLookup);
  }

  public static TagDictionary create(byte[] dictData, byte[] dictInfo)
      throws IllegalArgumentException, IOException {
    return create(new ByteArrayInputStream(dictData), new ByteArrayInputStream(
        dictInfo));
  }
  
  public static FSADictionary createFromResources(String path)
      throws IllegalArgumentException, IOException {
    
    InputStream dic = FSADictionary.class.getResourceAsStream(path);
    InputStream info = FSADictionary.class.getResourceAsStream(Dictionary.getExpectedFeaturesName(path));
    
    FSADictionary fsa = create(dic, info);
    
    dic.close();
    info.close();
    
    return fsa;
  }

  private static class IteratorWrapper implements Iterator<String> {
    private final Iterator<WordData> innerIterator;

    public IteratorWrapper(Iterator<WordData> iterator) {
      this.innerIterator = iterator;
    }

    public boolean hasNext() {
      return innerIterator.hasNext();
    }

    public String next() {
      WordData wd = innerIterator.next();
      if (wd != null) {
        return wd.getWord().toString();
      }
      return null;
    }

    public void remove() {
      innerIterator.remove();
    }
  }

  public Iterator<String> iterator() {
    return new IteratorWrapper(this.dictLookup.iterator());
  }

  public static void main(String[] args) throws IllegalArgumentException,
      IOException {

    long start = System.nanoTime();
    

    String path = "/fsa_dictionaries/pos/pt_br_jspell_corpus";
    InputStream dict = FSADictionary.class.getResourceAsStream(path + ".dict");
    InputStream info = FSADictionary.class.getResourceAsStream(path + ".info");
    
    FSADictionary td = (FSADictionary) create(dict, info);
    System.out.println("Loading time ["
        + ((System.nanoTime() - start) / 1000000) + "ms]");
    Scanner kb = new Scanner(System.in);
    System.out.print("Enter a query: ");
    String input = kb.nextLine();
    while (!input.equals("q")) {
      if (input.equals("0")) {
        input = "cão";
      }
      List<PairWordPOSTag> pair = td.getTagsAndLemms(input);
      for (PairWordPOSTag pairWordPOSTag : pair) {
        System.out.println(pairWordPOSTag.getPosTag() + " : " + pairWordPOSTag.getWord());
      }
      System.out.print("Enter a query: ");
      input = kb.nextLine();
    }
  }
}
