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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import opennlp.tools.ml.model.AbstractModel;
import opennlp.tools.util.BaseToolFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.ext.ExtensionLoader;
import opennlp.tools.util.model.ArtifactSerializer;
import opennlp.tools.util.model.UncloseableInputStream;

import org.cogroo.dictionary.FeatureDictionary;

public abstract class FeaturizerFactory extends BaseToolFactory {

//  private static final String POISONED_TAGS_ENTRY_NAME = "poisonedtags.serialized_set";
  
  private static final String CG_FLAGS_PROPERTY = "cgFlags";
  protected FeatureDictionary featureDictionary;
  private Set<String> poisonedDictionaryTags = null;
  private String cgFlags;
  
  /**
   * Creates a {@link FeaturizerFactory} that provides the default
   * implementation of the resources.
   */
  public FeaturizerFactory() {
  }

  /**
   * Creates a {@link FeaturizerFactory}. Use this constructor to
   * programmatically create a factory.
   * 
   */
  public FeaturizerFactory(FeatureDictionary featureDictionary, String cgFlags) {
    this.init(featureDictionary, cgFlags);
  }
  
  protected void init(FeatureDictionary featureDictionary, String cgFlags) {
    this.featureDictionary = featureDictionary;
    this.cgFlags = cgFlags;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public Map<String, ArtifactSerializer> createArtifactSerializersMap() {
    Map<String, ArtifactSerializer> serializers = super
        .createArtifactSerializersMap();
    SetSerializer.register(serializers);
    return serializers;
  }
  
  @Override
  public Map<String, String> createManifestEntries() {
    Map<String, String> manifestEntries = super.createManifestEntries();

    // EOS characters are optional
    if (getCGFlags() != null)
      manifestEntries.put(CG_FLAGS_PROPERTY, getCGFlags());

    return manifestEntries;
  }

  @Override
  public Map<String, Object> createArtifactMap() {
    Map<String, Object> artifactMap = super.createArtifactMap();
    
    // add a empty set that will be populated latter
//    artifactMap.put(POISONED_TAGS_ENTRY_NAME, new HashSet<String>());

    return artifactMap;
  }
  
  public String getCGFlags() {
    if (this.cgFlags == null) {
      if (artifactProvider != null) {
        String prop = this.artifactProvider
            .getManifestProperty(CG_FLAGS_PROPERTY);
        if (prop != null) {
          this.cgFlags = prop;
        }
      }
      
      if (this.cgFlags == null) {
        this.cgFlags = "wshnc";
      }
    }
    return this.cgFlags;
  }

  public FeatureDictionary getFeatureDictionary() {
    if (this.featureDictionary == null)
      this.featureDictionary = loadFeatureDictionary();
    return this.featureDictionary;
  }

  protected abstract FeatureDictionary loadFeatureDictionary();

  public Set<String> getDictionaryPoisonedTags() {
//    if (this.poisonedDictionaryTags == null && artifactProvider != null)
//      this.poisonedDictionaryTags = artifactProvider
//          .getArtifact(POISONED_TAGS_ENTRY_NAME);
    return this.poisonedDictionaryTags;
  }

  public FeaturizerContextGenerator getFeaturizerContextGenerator() {
    return new DefaultFeaturizerContextGenerator(getCGFlags());
  }

  public SequenceValidator<WordTag> getSequenceValidator() {
    return new DefaultFeaturizerSequenceValidator(getFeatureDictionary(),
        this.getDictionaryPoisonedTags());
  }

  // call this method to find the poisoned tags. Call only during training
  // because the poisoned tags are persisted...
  protected void validateFeatureDictionary() {
    
    FeatureDictionary dict = getFeatureDictionary();
    if (dict != null) {
      if (dict instanceof Iterable<?>) {
        FeatureDictionary posDict = (FeatureDictionary) dict;

        Set<String> dictTags = new HashSet<String>();
        Set<String> poisoned = new HashSet<String>();

        for (WordTag wt : (Iterable<WordTag>) posDict) {
          dictTags.add(wt.getPostag());
        }

        Set<String> modelTags = new HashSet<String>();

        AbstractModel posModel = this.artifactProvider
            .getArtifact(FeaturizerModel.FEATURIZER_MODEL_ENTRY_NAME);
        
        for (int i = 0; i < posModel.getNumOutcomes(); i++) {
          modelTags.add(posModel.getOutcome(i));
        }

        for (String d : dictTags) {
          if (!modelTags.contains(d)) {
            poisoned.add(d);
          }
        }

        this.poisonedDictionaryTags = Collections.unmodifiableSet(poisoned);
        
//        if (poisonedDictionaryTags.size() > 0) {
//          System.err
//              .println("WARNING: Feature dictioinary contains tags which are unkown by the model! "
//                  + this.poisonedDictionaryTags.toString());
//        }
      }
    }
  }

  @Override
  public void validateArtifactMap() throws InvalidFormatException {

    // Ensure that the tag dictionary is compatible with the model
//    Object poisonedTags = this.artifactProvider
//        .getArtifact(POISONED_TAGS_ENTRY_NAME);

//    if (poisonedTags != null && !(poisonedTags instanceof Set<?>)) {
//      throw new InvalidFormatException("Invalid serialized poisoned tags!");
//    }
    
    validateFeatureDictionary();
  }
  
  public static FeaturizerFactory create(String subclassName,
      FeatureDictionary posDictionary, String cgFlags) throws InvalidFormatException {
    if (subclassName == null) {
      // will create the default factory
      return new DefaultFeaturizerFactory(posDictionary, cgFlags);
    }
    FeaturizerFactory theFactory = ExtensionLoader.instantiateExtension(FeaturizerFactory.class, subclassName);
    theFactory.init(posDictionary, cgFlags);
    
    return theFactory;
  }
}

class SetSerializer implements ArtifactSerializer<Set<String>> {

  @SuppressWarnings("unchecked")
  public Set<String> create(InputStream in) throws IOException,
      InvalidFormatException {
    ObjectInputStream oin = null;
    Set<String> set = null;
    oin = new ObjectInputStream(new UncloseableInputStream(in));
    try {
      set = (Set<String>) oin.readObject();
    } catch (ClassNotFoundException e) {
      System.err.println("could not restore serialied object");
      e.printStackTrace();
    }

    return Collections.unmodifiableSet(set);
  }

  public void serialize(Set<String> artifact, OutputStream out)
      throws IOException {
    ObjectOutputStream objOut = null;
    objOut = new ObjectOutputStream(out);
    objOut.writeObject(artifact);
  }

  static void register(
      @SuppressWarnings("rawtypes") Map<String, ArtifactSerializer> factories) {
    factories.put("serialized_set", new SetSerializer());
  }
}
