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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.cogroo.dictionary.FeatureDictionaryI;
import org.cogroo.tools.featurizer.WordTag;

import morfologik.stemming.Dictionary;
import morfologik.stemming.DictionaryLookup;
import morfologik.stemming.WordData;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.io.ByteStreams;

public class FSAFeatureDictionary implements FeatureDictionaryI, Iterable<WordTag> {

  private DictionaryLookup dictLookup;

  private Cache<WordTag, Optional<String[]>> cache = CacheBuilder.newBuilder()
      .maximumSize(100).expireAfterWrite(10, TimeUnit.MINUTES)
      .build(new CacheLoader<WordTag, Optional<String[]>>() {
        public Optional<String[]> load(WordTag key) {
          String[] val = lookup(key);
          return Optional.fromNullable(val);
        }
      });

  public FSAFeatureDictionary(DictionaryLookup dictLookup) {
    this.dictLookup = dictLookup;
  }

  private String[] lookup(WordTag key) {
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
    try {
      return cache.get(new WordTag(word, pos)).orNull();
    } catch (ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
  }

  public static FeatureDictionaryI create(String path)
      throws IllegalArgumentException, IOException {
    FileInputStream fsaData = new FileInputStream(path);
    FileInputStream featuresData = new FileInputStream(
        Dictionary.getExpectedFeaturesName(path));
    return create(fsaData, featuresData);
  }

  public static byte[] getFSADictionaryInfo(String path) throws IOException {
    FileInputStream featuresData = new FileInputStream(
        Dictionary.getExpectedFeaturesName(path));
    return ByteStreams.toByteArray(featuresData);
  }

  public static byte[] getFSADictionaryData(String path) throws IOException {
    FileInputStream featuresData = new FileInputStream(path);
    return ByteStreams.toByteArray(featuresData);
  }

  public static FeatureDictionaryI create(InputStream fsaData,
      InputStream featuresData) throws IllegalArgumentException, IOException {
    DictionaryLookup dictLookup = new DictionaryLookup(Dictionary.readAndClose(
        fsaData, featuresData));
    return new FSAFeatureDictionary(dictLookup);
  }

  public static FeatureDictionaryI create(byte[] dictData, byte[] dictInfo)
      throws IllegalArgumentException, IOException {
    return create(new ByteArrayInputStream(dictData), new ByteArrayInputStream(
        dictInfo));
  }

  public static void main(String[] args) throws IllegalArgumentException,
      IOException {

    long start = System.nanoTime();
    FSAFeatureDictionary td = (FSAFeatureDictionary) create("fsa_dictionaries/featurizer/pt_br_feats.dict");
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

  
  public static FeatureDictionaryI createFromResources(String path)
      throws IllegalArgumentException, IOException {
    
    InputStream dic = FSAFeatureDictionary.class.getResourceAsStream(path);
    InputStream info = FSAFeatureDictionary.class.getResourceAsStream(Dictionary.getExpectedFeaturesName(path));
    
    FeatureDictionaryI fsa = create(dic, info);
    
    dic.close();
    info.close();
    
    return fsa;
  }
}
