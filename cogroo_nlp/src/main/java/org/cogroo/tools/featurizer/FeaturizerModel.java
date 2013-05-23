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
import java.util.Map;

import org.cogroo.dictionary.FeatureDictionary;

import opennlp.model.AbstractModel;
import opennlp.tools.util.BaseToolFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.model.ArtifactSerializer;
import opennlp.tools.util.model.BaseModel;

/**
 * The {@link FeaturizerModel} is the model used by a learnable
 * {@link Featurizer}.
 * 
 * @see FeaturizerME
 */
public class FeaturizerModel extends BaseModel {

  private static final String COMPONENT_NAME = "FeaturizerME";
  public static final String FEATURIZER_MODEL_ENTRY_NAME = "featurizer.model";
//  private static final String TAG_DICTIONARY_ENTRY_NAME = "tags.tagdict";
//  private Set<String> poisonedDictionaryTags;

  public FeaturizerModel(String languageCode, AbstractModel featurizerModel,
      Map<String, String> manifestInfoEntries, FeaturizerFactory factory) {

    super(COMPONENT_NAME, languageCode, manifestInfoEntries, factory);
    
    if (featurizerModel == null)
      throw new IllegalArgumentException("The featurizerModel param must not be null!");

    artifactMap.put(FEATURIZER_MODEL_ENTRY_NAME, featurizerModel);

    checkArtifactMap();
  }
  
  public FeaturizerModel(InputStream in) throws IOException,
      InvalidFormatException {
    super(COMPONENT_NAME, in);
  }

  @Override
  protected Class<? extends BaseToolFactory> getDefaultFactory() {
    return DefaultFeaturizerFactory.class;
  }
  
  @Override
  protected void createArtifactSerializers(
      Map<String, ArtifactSerializer> serializers) {

    super.createArtifactSerializers(serializers);
    
  }

  @Override
  protected void validateArtifactMap() throws InvalidFormatException {
    super.validateArtifactMap();

    if (!(artifactMap.get(FEATURIZER_MODEL_ENTRY_NAME) instanceof AbstractModel)) {
      throw new InvalidFormatException("Featurizer model is incomplete!");
    }
  }

  
  public FeaturizerFactory getFactory() {
    return (FeaturizerFactory) this.toolFactory;
  }
  
  public AbstractModel getFeaturizerModel() {
    return (AbstractModel) artifactMap.get(FEATURIZER_MODEL_ENTRY_NAME);
  }

  /**
   * Retrieves the tag dictionary.
   * 
   * @return tag dictionary or null if not used
   */
  public FeatureDictionary getFeatureDictionary() {
    if(getFactory() != null)
      return getFactory().getFeatureDictionary();
    return null;
  }

//  public Set<String> getDictionaryPoisonedTags() {
//    return this.poisonedDictionaryTags;
//  }

}
