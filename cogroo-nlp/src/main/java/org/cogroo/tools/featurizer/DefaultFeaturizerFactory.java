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
import java.io.OutputStream;
import java.util.Map;

import opennlp.tools.postag.ExtendedPOSDictionary;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.model.ArtifactSerializer;
import opennlp.tools.util.model.UncloseableInputStream;

import org.cogroo.dictionary.FeatureDictionary;

public class DefaultFeaturizerFactory extends FeaturizerFactory {

  private static final String FEATURE_DICTIONARY_ENTRY_NAME = "tags.tagdict";

  /**
   * Creates a {@link DefaultFeaturizerFactory} that provides the default
   * implementation of the resources.
   */
  public DefaultFeaturizerFactory() {
  }

  /**
   * Creates a {@link DefaultFeaturizerFactory}. Use this constructor to
   * programmatically create a factory.
   * 
   */
  public DefaultFeaturizerFactory(FeatureDictionary featureDictionary, String cgFlags) {
    super(featureDictionary, cgFlags);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public Map<String, ArtifactSerializer> createArtifactSerializersMap() {
    Map<String, ArtifactSerializer> serializers = super
        .createArtifactSerializersMap();
    
    ExtendedTagDictionarySerializer.register(serializers);

    return serializers;
  }

  @Override
  public Map<String, Object> createArtifactMap() {
    Map<String, Object> artifactMap = super.createArtifactMap();

    if (featureDictionary != null)
      artifactMap.put(FEATURE_DICTIONARY_ENTRY_NAME, featureDictionary);

    return artifactMap;
  }

  @Override
  protected FeatureDictionary loadFeatureDictionary() {
    if (artifactProvider != null)
      return artifactProvider.getArtifact(FEATURE_DICTIONARY_ENTRY_NAME);
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void validateArtifactMap() throws InvalidFormatException {

    super.validateArtifactMap();
    
    Object tagdictEntry = this.artifactProvider
        .getArtifact(FEATURE_DICTIONARY_ENTRY_NAME);

    if (tagdictEntry != null) {
      if (!(tagdictEntry instanceof FeatureDictionary)) {
        throw new InvalidFormatException("Feature dictionary has wrong type!");
      }
    }
  }

  static class POSDictionarySerializer implements
      ArtifactSerializer<POSDictionary> {

    public POSDictionary create(InputStream in) throws IOException,
        InvalidFormatException {
      return POSDictionary.create(new UncloseableInputStream(in));
    }

    public void serialize(POSDictionary artifact, OutputStream out)
        throws IOException {
      artifact.serialize(out);
    }

    @SuppressWarnings("rawtypes")
    static void register(Map<String, ArtifactSerializer> factories) {
      factories.put("tagdict", new POSDictionarySerializer());
    }
  }

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

    static void register(
        @SuppressWarnings("rawtypes") Map<String, ArtifactSerializer> factories) {
      factories.put("tagdict", new ExtendedTagDictionarySerializer());
    }
  }

}
