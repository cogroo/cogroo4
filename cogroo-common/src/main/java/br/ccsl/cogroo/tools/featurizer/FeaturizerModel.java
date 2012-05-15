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

package br.ccsl.cogroo.tools.featurizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import opennlp.model.AbstractModel;
import opennlp.tools.postag.ExtendedPOSDictionary;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.model.ArtifactSerializer;
import opennlp.tools.util.model.BaseModel;
import opennlp.tools.util.model.UncloseableInputStream;

/**
 * The {@link FeaturizerModel} is the model used by a learnable
 * {@link Featurizer}.
 * 
 * @see FeaturizerME
 */
public class FeaturizerModel extends BaseModel {

  static class ExtendedTagDictionarySerializer implements
      ArtifactSerializer<ExtendedPOSDictionary> {

    public ExtendedPOSDictionary create(InputStream in) throws IOException,
        InvalidFormatException {
      return ExtendedPOSDictionary.create(new UncloseableInputStream(in));
    }

    public void serialize(ExtendedPOSDictionary artifact, OutputStream out)
        throws IOException {
      artifact.serialize(out);
    }

    @SuppressWarnings("unchecked")
    static void register(Map<String, ArtifactSerializer> factories) {
      factories.put("tagdict", new ExtendedTagDictionarySerializer());
    }
  }

  private static final String COMPONENT_NAME = "FeaturizerME";
  private static final String FEATURIZER_MODEL_ENTRY_NAME = "featurizer.model";
  private static final String TAG_DICTIONARY_ENTRY_NAME = "tags.tagdict";
  private Set<String> poisonedDictionaryTags;

  public FeaturizerModel(String languageCode, AbstractModel featurizerModel,
      ExtendedPOSDictionary tagDictionary,
      Map<String, String> manifestInfoEntries) {

    super(COMPONENT_NAME, languageCode, manifestInfoEntries);

    if (tagDictionary != null)
      artifactMap.put(TAG_DICTIONARY_ENTRY_NAME, tagDictionary);

    artifactMap.put(FEATURIZER_MODEL_ENTRY_NAME, featurizerModel);

    checkArtifactMap();
  }
  
  @Override
  protected void createArtifactSerializers(
      Map<String, ArtifactSerializer> serializers) {

    super.createArtifactSerializers(serializers);
    
    serializers.put("tagdict", new ExtendedTagDictionarySerializer());
    
  }

  public FeaturizerModel(String languageCode, AbstractModel chunkerModel,
      ExtendedPOSDictionary dict) {
    this(languageCode, chunkerModel, dict, null);
  }

  public FeaturizerModel(InputStream in) throws IOException,
      InvalidFormatException {
    super(COMPONENT_NAME, in);
  }

  @Override
  protected void validateArtifactMap() throws InvalidFormatException {
    super.validateArtifactMap();

    if (!(artifactMap.get(FEATURIZER_MODEL_ENTRY_NAME) instanceof AbstractModel)) {
      throw new InvalidFormatException("Featurizer model is incomplete!");
    }

    // Ensure that the tag dictionary is compatible with the model
    Object tagdictEntry = artifactMap.get(TAG_DICTIONARY_ENTRY_NAME);

    if (tagdictEntry != null) {
      if (tagdictEntry instanceof ExtendedPOSDictionary) {
        ExtendedPOSDictionary posDict = (ExtendedPOSDictionary) tagdictEntry;

        Set<String> poisoned = new HashSet<String>();

        Set<String> dictTags = new HashSet<String>();

        for (String word : posDict) {
          String[] dicTag = posDict.getFeatureTag(word);
          Collections.addAll(dictTags, dicTag);
        }

        Set<String> modelTags = new HashSet<String>();

        AbstractModel featsModel = getFeaturizerModel();

        for (int i = 0; i < featsModel.getNumOutcomes(); i++) {
          String tag = featsModel.getOutcome(i);
          modelTags.add(tag);
        }

        // if (!modelTags.containsAll(dictTags)) {
        for (String d : dictTags) {
          if (!modelTags.contains(d)) {
            poisoned.add(d);
          }
        }
        this.poisonedDictionaryTags = Collections.unmodifiableSet(poisoned);
        if (poisonedDictionaryTags.size() > 0) {
          System.err
              .println("WARNING: Tag dictioinary contains tags which are unkown by the model! "
                  + this.poisonedDictionaryTags.toString());
        }
        // throw new InvalidFormatException("Tag dictioinary contains tags " +
        // "which are unkown by the model! " + unknownTag.toString());
        // }
      } else {
        throw new InvalidFormatException(
            "Abbreviations dictionary has wrong type!");
      }
    }
  }

  public AbstractModel getFeaturizerModel() {
    return (AbstractModel) artifactMap.get(FEATURIZER_MODEL_ENTRY_NAME);
  }

  /**
   * Retrieves the tag dictionary.
   * 
   * @return tag dictionary or null if not used
   */
  public ExtendedPOSDictionary getTagDictionary() {
    return (ExtendedPOSDictionary) artifactMap.get(TAG_DICTIONARY_ENTRY_NAME);
  }

  public Set<String> getDictionaryPoisonedTags() {
    return this.poisonedDictionaryTags;
  }

}
