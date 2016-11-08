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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import morfologik.stemming.Dictionary;
import morfologik.stemming.DictionaryLookup;
import morfologik.stemming.WordData;
import opennlp.tools.util.Cache;

import org.cogroo.dictionary.FeatureDictionary;
import org.cogroo.tools.featurizer.WordTag;

import static org.cogroo.util.ByteArrayUtil.toByteArray;


public class FSAFeatureDictionary implements FeatureDictionary, Iterable<WordTag> {

  private DictionaryLookup dictLookup;

  public FSAFeatureDictionary(DictionaryLookup dictLookup) {
    this.dictLookup = dictLookup;
  }

  private final Cache cache = new Cache(500);
  
  private String[] lookup(WordTag key) {
    if(key == null) {
      return null;
    } 
    
    String[] arr = (String[]) cache.get(key);
    if(arr != null) {
      return arr;
    }
    synchronized (dictLookup) {
      List<WordData> data = dictLookup.lookup(key.getWord());
      if (data.size() > 0) {
        final String prefix = key.getPostag() + "#";
        List<String> tags = new ArrayList<String>(data.size());
        for (int i = 0; i < data.size(); i++) {
          String completeTag = data.get(i).getTag().toString();
          if (completeTag.startsWith(prefix) || key.getPostag() == null) {
            tags.add(completeTag.substring(completeTag.indexOf("#") + 1));
          }
        }
        return tags.toArray(new String[tags.size()]);
      }
    }
    return null;
  }

  // add all features if pos == null
  public String[] getFeatures(String word, String pos) {
      return lookup(new WordTag(word, pos));
  }

  public static FeatureDictionary create(String path)
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

  public static FeatureDictionary create(InputStream fsaData,
      InputStream featuresData) throws IllegalArgumentException, IOException {
    DictionaryLookup dictLookup = new DictionaryLookup(Dictionary.readAndClose(
        fsaData, featuresData));
    return new FSAFeatureDictionary(dictLookup);
  }

  public static FeatureDictionary create(byte[] dictData, byte[] dictInfo)
      throws IllegalArgumentException, IOException {
    return create(new ByteArrayInputStream(dictData), new ByteArrayInputStream(
        dictInfo));
  }

  public static void main(String[] args) throws IllegalArgumentException,
      IOException {

    long start = System.nanoTime();
    FSAFeatureDictionary td = (FSAFeatureDictionary) create("../lang/pt_br/cogroo-res/fsa_dictionaries/featurizer/pt_br_feats.dict");
    System.out.println("Loading time ["
        + ((System.nanoTime() - start) / 1000000) + "ms]");
    Scanner kb = new Scanner(System.in);
    System.out.print("Enter a query: ");
    String input = kb.nextLine();
    while (!input.equals("q")) {
      if (input.equals("0")) {
        input = "casa";
      }
      String[] parts = input.split("\\s+");
      if (parts.length == 2) {
        System.out.println(Arrays.toString(td.getFeatures(parts[0], parts[1])));
      } else {
        System.out.println("invalid... enter a space separated word + postag");
      }
      System.out.print("Enter a query: ");
      input = kb.nextLine();
    }
  }
  
  private static class IteratorWrapper implements Iterator<WordTag> {
    private final Iterator<WordData> innerIterator;

    public IteratorWrapper(Iterator<WordData> iterator) {
      this.innerIterator = iterator;
    }

    public boolean hasNext() {
      return innerIterator.hasNext();
    }

    public WordTag next() {
      WordData wd = innerIterator.next();
      if(wd != null) {
        String completeTag = wd.getTag().toString();
        return new WordTag(wd.getWord().toString(), completeTag.substring(completeTag.indexOf("#") + 1));
      }
      return null;
    }

    public void remove() {
      innerIterator.remove();
    }
  }

  public Iterator<WordTag> iterator() {
    return new IteratorWrapper(this.dictLookup.iterator());
  }

  
  public static FeatureDictionary createFromResources(String path)
      throws IllegalArgumentException, IOException {
    
    InputStream dic = FSAFeatureDictionary.class.getResourceAsStream(path);
    InputStream info = FSAFeatureDictionary.class.getResourceAsStream(Dictionary.getExpectedFeaturesName(path));
    
    FeatureDictionary fsa = create(dic, info);
    
    dic.close();
    info.close();
    
    return fsa;
  }
}
