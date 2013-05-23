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
package org.cogroo.tools.chunker2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import opennlp.model.AbstractModel;
import opennlp.tools.util.BaseToolFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.model.ArtifactSerializer;
import opennlp.tools.util.model.BaseModel;

/**
 * The {@link ChunkerModel} is the model used by a learnable
 * {@link Chunker}.
 * 
 * @see ChunkerME
 */
public class ChunkerModel extends BaseModel {

  private static final String COMPONENT_NAME = "ChunkerME2";
  public static final String CHUNKER_MODEL_ENTRY_NAME = "chunker2.model";

  public ChunkerModel(String languageCode, AbstractModel featurizerModel,
      Map<String, String> manifestInfoEntries, ChunkerFactory factory) {

    super(COMPONENT_NAME, languageCode, manifestInfoEntries, factory);
    
    if (featurizerModel == null)
      throw new IllegalArgumentException("The chunkerModel param must not be null!");

    artifactMap.put(CHUNKER_MODEL_ENTRY_NAME, featurizerModel);

    checkArtifactMap();
  }
  
  public ChunkerModel(InputStream in) throws IOException,
      InvalidFormatException {
    super(COMPONENT_NAME, in);
  }

  @Override
  protected Class<? extends BaseToolFactory> getDefaultFactory() {
    return ChunkerFactory.class;
  }
  
  @Override
  protected void createArtifactSerializers(
      Map<String, ArtifactSerializer> serializers) {

    super.createArtifactSerializers(serializers);
    
  }

  @Override
  protected void validateArtifactMap() throws InvalidFormatException {
    super.validateArtifactMap();

    if (!(artifactMap.get(CHUNKER_MODEL_ENTRY_NAME) instanceof AbstractModel)) {
      throw new InvalidFormatException("Chunker model is incomplete!");
    }
  }

  
  public ChunkerFactory getFactory() {
    return (ChunkerFactory) this.toolFactory;
  }
  
  public AbstractModel getChunkerModel() {
    return (AbstractModel) artifactMap.get(CHUNKER_MODEL_ENTRY_NAME);
  }

}
