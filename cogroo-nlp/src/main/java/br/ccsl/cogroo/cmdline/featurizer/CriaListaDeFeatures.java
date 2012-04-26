package br.ccsl.cogroo.cmdline.featurizer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import br.ccsl.cogroo.formats.ad.ADFeatureSampleStreamFactory;
import br.ccsl.cogroo.formats.ad.ADFeaturizerSampleStream;
import br.ccsl.cogroo.tools.featurizer.FeatureSample;

public class CriaListaDeFeatures {
  
  public static void main(String[] args) throws IOException {
    FileInputStream in = new FileInputStream("/Users/wcolen/Documents/wrks/corpus/Bosque/Bosque_CF_8.0.ad.txt");
    ADFeaturizerSampleStream f = new ADFeaturizerSampleStream(in, "ISO-8859-1", false);
    
    FeatureSample fs = f.read();
    
    SortedMap<String, SortedSet<String>> map = new TreeMap<String, SortedSet<String>>();
    
    SortedSet<String> tagSet = new TreeSet<String>();
    
    while(fs != null){
      for(int i = 0; i < fs.getSentence().length; i++) {
        if(!map.containsKey(fs.getTags()[i])) {
          map.put(fs.getTags()[i], new TreeSet<String>());
        }
        map.get(fs.getTags()[i]).add(fs.getFeatures()[i]);
        
        
        for (String tag : fs.getFeatures()[i].split("_")) {
          tagSet.add(tag);
        }
      }
      
      
      fs = f.read();
    }
    
    f.close();
    
//    for (String t : map.keySet()) {
//      System.out.print(t + "\t");
//      for (String feats : map.get(t)) {
//        System.out.print(feats + " ");
//      }
//      System.out.println();
//    }
    
    for (String string : tagSet) {
      System.out.println(string);
    }
    
  }

}
