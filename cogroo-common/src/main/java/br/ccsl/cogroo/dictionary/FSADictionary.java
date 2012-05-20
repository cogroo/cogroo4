package br.ccsl.cogroo.dictionary;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import morfologik.stemming.Dictionary;
import morfologik.stemming.DictionaryLookup;
import morfologik.stemming.WordData;
import opennlp.tools.postag.TagDictionary;

import com.google.common.io.ByteStreams;

public class FSADictionary implements TagDictionary {

  private DictionaryLookup dictLookup;

  public FSADictionary(DictionaryLookup dictLookup) {
    this.dictLookup = dictLookup;
  }

  public String[] getTags(String word) {
    synchronized (dictLookup) {
      List<WordData> data = dictLookup.lookup(word);
      if(data.size() > 0) {
        String[] tags = new String[data.size()];
        for (int i = 0; i < tags.length; i++) {
          tags[i] = data.get(i).getTag().toString();
        }
        return tags;
      }
    }
    return null;
  }
  
  public static TagDictionary create(String path) throws IllegalArgumentException, IOException {
    FileInputStream fsaData = new FileInputStream(path);
    FileInputStream featuresData = new FileInputStream(Dictionary.getExpectedFeaturesName(path));
    return create(fsaData, featuresData);
  }
  
  public static byte[] getFSADictionaryInfo(String path) throws IOException {
    FileInputStream featuresData = new FileInputStream(Dictionary.getExpectedFeaturesName(path));
    return ByteStreams.toByteArray(featuresData);
  }
  
  public static byte[] getFSADictionaryData(String path) throws IOException {
    FileInputStream featuresData = new FileInputStream(path);
    return ByteStreams.toByteArray(featuresData);
  }
  
  public static void main(String[] args) throws IllegalArgumentException, IOException {
    
    long start = System.nanoTime();
    FSADictionary td = (FSADictionary) create("fsa_dictionaries/pt_br.dict");
    System.out.println("Loading time ["
            + ((System.nanoTime() - start) / 1000000) + "ms]");
    Scanner kb = new Scanner(System.in);
    System.out.print("Enter a query: ");
    String input = kb.nextLine();
    while (!input.equals("q")) {
        if (input.equals("0")) {
            input = "casa";
        }
        System.out.println(Arrays.toString(td.getTags(input)));
        System.out.print("Enter a query: ");
        input = kb.nextLine();
    }
}

  public static FSADictionary create(
      InputStream fsaData, InputStream featuresData) throws IllegalArgumentException, IOException {
    DictionaryLookup dictLookup = new DictionaryLookup(Dictionary.readAndClose(fsaData, featuresData));
    return new FSADictionary(dictLookup);
  }

  public static TagDictionary create(byte[] dictData, byte[] dictInfo) throws IllegalArgumentException, IOException {
    return create(new ByteArrayInputStream(dictData), new ByteArrayInputStream(dictInfo));
  }
}
