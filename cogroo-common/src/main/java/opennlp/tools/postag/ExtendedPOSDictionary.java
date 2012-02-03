/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreemnets.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package opennlp.tools.postag;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import opennlp.tools.dictionary.serializer.Attributes;
import opennlp.tools.dictionary.serializer.DictionarySerializer;
import opennlp.tools.dictionary.serializer.Entry;
import opennlp.tools.dictionary.serializer.EntryInserter;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.StringList;
import opennlp.tools.util.StringUtil;

/**
 * Provides a means of determining which tags are valid for a particular word
 * based on a tag dictionary read from a file.
 */
public class ExtendedPOSDictionary implements Iterable<String>, ExtendedTagDictionary {

  // word => [ tag => [lemma, feature]]
  //private Map<String, String[]> dictionary;
  private Map<String, List<Triple>> dictionary;
  
  private static final String ATTR_TAGS = "tags";
  private static final String ATTR_LEMMAS = "lemmas";
  private static final String ATTR_FEATS = "feats";

  private boolean caseSensitive = true;
  
  public ExtendedPOSDictionary() {
    this(true);
  }

  public ExtendedPOSDictionary(boolean caseSensitive) {
    dictionary = new HashMap<String, List<Triple>>();
    this.caseSensitive = caseSensitive;
  }

  /**
   * Returns a list of valid tags for the specified word.
   *
   * @param word The word.
   *
   * @return A list of valid tags for the specified word or
   * null if no information is available for that word.
   */
  public String[] getTags(String word) {
    if (caseSensitive) {
      return getTags(dictionary.get(word));
    }
    else {
      return getTags(dictionary.get(word.toLowerCase()));
    }
  }
  
  public String[] getFeatureTag(String word) {
    if (caseSensitive) {
      return getFeats(dictionary.get(word));
    }
    else {
      return getFeats(dictionary.get(word.toLowerCase()));
    }
  }
  
  public String[] getCompleteTag(String word) {
    if (caseSensitive) {
      return getCompleteTag(dictionary.get(word));
    }
    else {
      return getCompleteTag(dictionary.get(word.toLowerCase()));
    }
  }

  void addTriple(String word, Triple triple) {
    addTriple(dictionary, word, triple);
  }
  
  static void addTriple(Map<String, List<Triple>> dic, String word, Triple triple) {
    if(!dic.containsKey(word)) {
      dic.put(word, new ArrayList<ExtendedPOSDictionary.Triple>());
    }
    dic.get(word).add(triple);
  }

  /**
   * Retrieves an iterator over all words in the dictionary.
   */
  public Iterator<String> iterator() {
    return dictionary.keySet().iterator();
  }

  private static String tagsToString(String tags[]) {

    StringBuilder tagString = new StringBuilder();

    for (String tag : tags) {
      tagString.append(tag);
      tagString.append(' ');
    }

    // remove last space
    if (tagString.length() > 0) {
      tagString.setLength(tagString.length() - 1);
    }

    return tagString.toString();
  }

  /**
   * Writes the {@link ExtendedPOSDictionary} to the given {@link OutputStream};
   *
   * After the serialization is finished the provided
   * {@link OutputStream} remains open.
   *
   * @param out
   *            the {@link OutputStream} to write the dictionary into.
   *
   * @throws IOException
   *             if writing to the {@link OutputStream} fails
   */
  public void serialize(OutputStream out) throws IOException {
    Iterator<Entry> entries = new Iterator<Entry>() {

      Iterator<String> iterator = dictionary.keySet().iterator();

      public boolean hasNext() {
        return iterator.hasNext();
      }

      public Entry next() {

        String word = iterator.next();
        List<Triple> triples = dictionary.get(word);
        Attributes tagAttribute = new Attributes();
        
        String[] tags = new String[triples.size()];
        String[] lemmas = new String[triples.size()];
        String[] feats = new String[triples.size()];
        
        int i = 0;
        for (Triple t : triples) {
          tags[i] = t.getClazz();
          lemmas[i] = t.getLemma();
          feats[i++] = t.getFeats();
        }
        
        tagAttribute.setValue(ATTR_TAGS, tagsToString(tags));
        tagAttribute.setValue(ATTR_LEMMAS, tagsToString(lemmas));
        tagAttribute.setValue(ATTR_FEATS, tagsToString(feats));

        return new Entry(new StringList(word), tagAttribute);
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };

    DictionarySerializer.serialize(out, entries, caseSensitive);
  }

  @Override
  public boolean equals(Object o) {

    if (o == this) {
      return true;
    }
    else if (o instanceof ExtendedPOSDictionary) {
      ExtendedPOSDictionary dictionary = (ExtendedPOSDictionary) o;

      if (this.dictionary.size() == dictionary.dictionary.size()) {

        for (String word : this) {

          List<Triple> aTriples = this.dictionary.get(word);
          List<Triple> bTriples = dictionary.dictionary.get(word);
          if (!aTriples.equals(bTriples)) {
            return false;
          }
        }

        return true;
      }
    }

    return false;
  }

  @Override
  public String toString() {
    StringBuilder dictionaryString = new StringBuilder();
    int i = 0;
    for (String word : dictionary.keySet()) {
      dictionaryString.append(word).append(" -> ").append(tagsToString(getTags(word)));
      dictionaryString.append("\n");
      if(i > 3) break;
      i++;
    }

    // remove last new line
    if (dictionaryString.length() > 0) {
      dictionaryString.setLength(dictionaryString.length() -1);
    }

    return dictionaryString.toString();
  }

  /**
   * Creates a new {@link ExtendedPOSDictionary} from a provided {@link InputStream}.
   *
   * After creation is finished the provided {@link InputStream} is closed.
   *
   * @param in
   *
   * @return the pos dictionary
   *
   * @throws IOException
   * @throws InvalidFormatException
   */
  public static ExtendedPOSDictionary create(InputStream in) throws IOException, InvalidFormatException {

    final ExtendedPOSDictionary newPosDict = new ExtendedPOSDictionary();

    boolean isCaseSensitive = DictionarySerializer.create(in, new EntryInserter() {
      public void insert(Entry entry) throws InvalidFormatException {

        String tagString = entry.getAttributes().getValue(ATTR_TAGS);
        String lemmaString = entry.getAttributes().getValue(ATTR_LEMMAS);
        String featString = entry.getAttributes().getValue(ATTR_FEATS);

        String[] tags = tagString.split(" ");
        String[] lemmas = lemmaString.split(" ");
        String[] feats = featString.split(" ");

        StringList word = entry.getTokens();

        if (word.size() != 1)
          throw new InvalidFormatException("Each entry must have exactly one token! "+word);

        if(tags.length != lemmas.length || tags.length != feats.length) {
          throw new InvalidFormatException("Each entry must have exactly number of tags, lemmas and feats! "+ word);
        }
        
        addTriple(newPosDict.dictionary, word.getToken(0), createTriple(tags, lemmas, feats));
      }});

    newPosDict.caseSensitive = isCaseSensitive;
    
    // TODO: The dictionary API needs to be improved to do this better!
    if (!isCaseSensitive) {
      Map<String, List<Triple>> lowerCasedDictionary = new HashMap<String, List<Triple>>();
      
      for (java.util.Map.Entry<String, List<Triple>> entry : newPosDict.dictionary.entrySet()) {
        lowerCasedDictionary.put(StringUtil.toLowerCase(entry.getKey()), entry.getValue());
      }
      
      newPosDict.dictionary = lowerCasedDictionary;
    }
    
    return newPosDict;
  }

  protected static void addTriple(Map<String, List<Triple>> dict,
      String token, Triple[] triple) {
    for (Triple t : triple) {
      addTriple(dict, token, t);
    }
  }

  public String[] getFeatureTag(String word, String tag) {
    List<String> feats = new ArrayList<String>();
    if(caseSensitive) {
      List<Triple> triples = dictionary.get(word);
      if(triples == null) return null;
      for (Triple t : triples) {
        if(tag.equals(t.getClazz())) {
          feats.add( t.getFeats());
        }
      }
    } else {
      List<Triple> triples = dictionary.get(word.toLowerCase());
      if(triples == null) return null;
      for (Triple t : triples) {
        if(tag.equals(t.getClazz())) {
          feats.add( t.getFeats());
        }
      }
    }
    if(feats.size() > 0) {
      return feats.toArray(new String[feats.size()]);
    }
    return null;
  }

  public String getLemma(String word, String tag) {
    if(caseSensitive) {
      List<Triple> triples = dictionary.get(word);
      if(triples == null) return null;
      for (Triple t : triples) {
        if(tag.equals(t.getClazz())) {
          return t.getLemma();
        }
      }
    } else {
      List<Triple> triples = dictionary.get(word.toLowerCase());
      if(triples == null) return null;
      for (Triple t : triples) {
        if(tag.equals(t.getClazz())) {
          return t.getLemma();
        }
      }
    }
    return null;
  }
  
  private static String[] getFeats(List<Triple> triples) {
    if(triples == null)
      return null;
    String[] feats = new String[triples.size()];
    int i = 0;
    for (Triple t : triples) {
      feats[i++] = t.getFeats();
    }
    return feats;
  }
  
  private String[] getCompleteTag(List<Triple> triples) {
    if(triples == null)
      return null;
    String[] feats = new String[triples.size()];
    int i = 0;
    for (Triple t : triples) {
      feats[i++] = t.getClazz() + "_" + t.getFeats();
    }
    return feats;
  }
  
  private static String[] getTags(List<Triple> triples) {
    if(triples == null) {
      return null;
    }
    String[] tags = new String[triples.size()];
    int i = 0;
    for (Triple t : triples) {
      tags[i++] = t.getClazz();
    }
    return tags;
  }
  
  protected static Triple[] createTriple(String[] tags, String[] lemmas,
      String[] feats) {
    Triple[] triples = new Triple[tags.length];
    for (int i = 0; i < tags.length; i++) {
      triples[i] = new Triple(tags[i], lemmas[i], feats[i]);
    }
    return triples;
  }
  
  static class Triple {
    private final String clazz;
    private final String lemma;
    private final String feats;
    
    public Triple(String clazz, String lemma, String feats) {
      this.clazz = clazz;
      this.lemma = lemma;
      this.feats = feats;
    }

    public String getClazz() {
      return clazz;
    }

    public String getLemma() {
      return lemma;
    }

    public String getFeats() {
      return feats;
    }
    
    @Override
    public String toString() {
      return lemma + ": " + clazz + " " + feats;
    }
    
    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      else if (o instanceof Triple) {
        Triple other = (Triple)o;
        
        if(this.clazz != null) {
          if(!this.clazz.equals(other.clazz)) {
            return false;
          }
        } else if(other.clazz != null) {
          return false;
        }
        
        if(this.lemma != null) {
          if(!this.lemma.equals(other.lemma)) {
            return false;
          }
        } else if(other.lemma != null) {
          return false;
        }
        
        if(this.feats != null) {
          if(!this.feats.equals(other.feats)) {
            return false;
          }
        } else if(other.feats != null) {
          return false;
        }
        
      }
      
      return true;
    }
  }
}
