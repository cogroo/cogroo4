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
import java.util.Map;

import org.cogroo.dictionary.FeatureDictionaryI;
import org.cogroo.dictionary.impl.FSADictionary;
import org.cogroo.dictionary.impl.FSAFeatureDictionary;
import org.cogroo.util.serializers.ByteArraySerializer;

import opennlp.tools.util.model.ArtifactProvider;
import opennlp.tools.util.model.ArtifactSerializer;

public class FSAFeaturizerFactory extends FeaturizerFactory {

  private static final String FSA_POSDICT_SUF = "fsa_data";
  private static final String FSA_DICT_INFO_SUF = "fsa_info";

  private static final String FSA_POSDICT = "feat_dict." + FSA_POSDICT_SUF;
  private static final String FSA_DICT_INFO = "feat_dict." + FSA_DICT_INFO_SUF;

  private byte[] dictInfo;
  private byte[] dictData;

  private FeatureDictionaryI fsaFeatureDictionary;

  /**
   * Creates a {@link FSAFeaturizerFactory} that provides the default
   * implementation of the resources.
   */
  public FSAFeaturizerFactory() {
  }
  
  /**
   * Creates a {@link FSAFeaturizerFactory}. Use this constructor to
   * programmatically create a factory.
   * 
   */
  public FSAFeaturizerFactory(FeatureDictionaryI featureDictionary) {
    super((FeatureDictionaryI) null);
  }
  
  @Override
  protected void init(FeatureDictionaryI featureDictionary) {
    super.init(featureDictionary);

    // get the dictionary path
    String path = System.getProperty("fsa.dict");
    if (path == null) {
      throw new IllegalArgumentException(
          "The property fsa.dict is missing! -Dfsa.dict=path");
    }

    // now we try to load it...
    try {
      this.dictInfo = FSADictionary.getFSADictionaryInfo(path);
      this.dictData = FSADictionary.getFSADictionaryData(path);
      this.fsaFeatureDictionary = FSAFeatureDictionary.create(dictData,
          dictInfo);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("The file is not a FSA dictionary!", e);
    } catch (IOException e) {
      throw new IllegalArgumentException(
          "Could not open the FSA dictionary or the .info file", e);
    }
  }
  
  @Override
  protected FeatureDictionaryI loadFeatureDictionary() {
    if (this.fsaFeatureDictionary == null) {
      if (artifactProvider != null) {
        Object obj = artifactProvider.getArtifact(FSA_POSDICT);
        if (obj != null) {
          byte[] data = (byte[]) artifactProvider.getArtifact(FSA_POSDICT);
          byte[] info = (byte[]) artifactProvider.getArtifact(FSA_DICT_INFO);

          try {
            this.fsaFeatureDictionary = FSAFeatureDictionary.create(data, info);
          } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "The file is not a FSA dictionary!", e);
          } catch (IOException e) {
            throw new IllegalArgumentException(
                "Could not open the FSA dictionary or the .info file", e);
          }
        }
      }
    }
    return this.fsaFeatureDictionary;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public Map<String, ArtifactSerializer> createArtifactSerializersMap() {
    Map<String, ArtifactSerializer> serializers = super
        .createArtifactSerializersMap();

    serializers.put(FSA_POSDICT_SUF, new ByteArraySerializer());
    serializers.put(FSA_DICT_INFO_SUF, new ByteArraySerializer());

    return serializers;
  }

  @Override
  public Map<String, Object> createArtifactMap() {
    Map<String, Object> artifactMap = super.createArtifactMap();
    artifactMap.put(FSA_POSDICT, this.dictData);
    artifactMap.put(FSA_DICT_INFO, this.dictInfo);
    return artifactMap;
  }

}
