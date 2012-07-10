package org.cogroo.cmdline.featurizer;
//package org.cogroo.cmdline.featurizer;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.HashSet;
//import java.util.Map.Entry;
//import java.util.Set;
//import java.util.SortedMap;
//import java.util.SortedSet;
//import java.util.TreeMap;
//import java.util.TreeSet;
//
//import opennlp.tools.postag.ExtendedPOSDictionary;
//import org.cogroo.formats.ad.ADFeaturizerSampleStream;
//import org.cogroo.tools.featurizer.FeatureSample;
//
//public class CriaListaDeFeatures {
//  
//
//  public static void main(String[] args)  throws IOException {
//    
//    ExtendedPOSDictionary dict = null;
//    dict = ExtendedPOSDictionary.create(new FileInputStream("/Users/wcolen/Documents/wrks/cogroo4/cogroo4/cogroo-dict/res/tagdict.xml"));
//    
//    FileInputStream in = new FileInputStream(
//        "/Users/wcolen/Documents/wrks/corpus/Bosque/Bosque_CF_8.0.ad.txt");
//    ADFeaturizerSampleStream f = new ADFeaturizerSampleStream(in, "ISO-8859-1",
//        false);
//
//    FeatureSample fs = f.read();
//
//    SortedMap<String, SortedSet<String>> tagsCorpus = new TreeMap<String, SortedSet<String>>();
//    SortedMap<String, SortedSet<String>> tagsDict = new TreeMap<String, SortedSet<String>>();
//    
//    Set<String> knownFeats = new HashSet<String>();
//
//    while (fs != null) {
//      for (int i = 0; i < fs.getSentence().length; i++) {
//        String postag = fs.getTags()[i];
//        String feat = fs.getFeatures()[i];
//        if("intj".equals(postag) && !"-".equals(feat)) {
//          System.out.println("achei: " + fs.getSentence()[i] + " feats: " + feat);
//        }
//        
//        if (!tagsCorpus.containsKey(postag)) {
//          tagsCorpus.put(postag, new TreeSet<String>());
//        }
//        tagsCorpus.get(postag).add(feat);
//
//      }
//
//      fs = f.read();
//    }
//
//    f.close();
//    
//    for (String word : dict) {
//      String[] tags = dict.getTags(word);
//      for (String tag : tags) {
//        String[] feats = dict.getFeatures(word, tag);
//        if(!tagsDict.containsKey(tag)) {
//          tagsDict.put(tag, new TreeSet<String>());
//        }
//        for (String string : feats) {
//          tagsDict.get(tag).add(string.replace("_", "="));
//        }
//      }
//    }
//
//    // for (String t : map.keySet()) {
//    // System.out.print(t + "\t");
//    // for (String feats : map.get(t)) {
//    // System.out.print(feats + " ");
//    // }
//    // System.out.println();
//    // }
//
//    for (Entry<String, SortedSet<String>> entry : tagsCorpus.entrySet()) {
//      System.out.println(entry.getKey());
//      for (String feat : entry.getValue()) {
//        System.out.println(" -- " + feat);  
//        knownFeats.add(feat);
//      }
//      
//      System.out.println();
//    }
//    
//    System.out.println("============================================");
//     
//    
//    for (Entry<String, SortedSet<String>> entry : tagsDict.entrySet()) {
//      System.out.println(entry.getKey());
//      for (String feat : entry.getValue()) {
//        System.out.print(" -- " + feat);
//        if(!knownFeats.contains(feat)) {
//          System.out.print(" *");
//        }
//        System.out.println();
//      }
//      
//      System.out.println();
//    }
//
//  }
//
//  
//  public static void criaListaDeFeatures() throws IOException {
//    FileInputStream in = new FileInputStream(
//        "/Users/wcolen/Documents/wrks/corpus/Bosque/Bosque_CF_8.0.ad.txt");
//    ADFeaturizerSampleStream f = new ADFeaturizerSampleStream(in, "ISO-8859-1",
//        false);
//
//    FeatureSample fs = f.read();
//
//    SortedMap<String, SortedSet<String>> map = new TreeMap<String, SortedSet<String>>();
//
//    SortedSet<String> tagSet = new TreeSet<String>();
//
//    while (fs != null) {
//      for (int i = 0; i < fs.getSentence().length; i++) {
//        if (!map.containsKey(fs.getTags()[i])) {
//          map.put(fs.getTags()[i], new TreeSet<String>());
//        }
//        map.get(fs.getTags()[i]).add(fs.getFeatures()[i]);
//
//        for (String tag : fs.getFeatures()[i].split("_")) {
//          tagSet.add(tag);
//        }
//      }
//
//      fs = f.read();
//    }
//
//    f.close();
//
//    // for (String t : map.keySet()) {
//    // System.out.print(t + "\t");
//    // for (String feats : map.get(t)) {
//    // System.out.print(feats + " ");
//    // }
//    // System.out.println();
//    // }
//
//    for (String string : tagSet) {
//      System.out.println(string);
//    }
//
//  }
//
//}
