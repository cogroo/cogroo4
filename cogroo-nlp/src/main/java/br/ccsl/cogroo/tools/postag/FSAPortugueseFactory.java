package br.ccsl.cogroo.tools.postag;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.postag.POSContextGenerator;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.postag.TagDictionary;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.model.ArtifactProvider;
import opennlp.tools.util.model.ArtifactSerializer;
import br.ccsl.cogroo.dictionary.impl.FSADictionary;
import br.ccsl.cogroo.util.serializers.ByteArraySerializer;

public class FSAPortugueseFactory extends POSTaggerFactory {

  private static final String FSA_POSDICT_SUF = "fsa_data";
  private static final String FSA_DICT_INFO_SUF = "fsa_info";
  
  private static final String FSA_POSDICT = "dict." + FSA_POSDICT_SUF;
  private static final String FSA_DICT_INFO = "dict." + FSA_DICT_INFO_SUF;
  
  private TagDictionary dict;
  
  private byte[] dictInfo;
  private byte[] dictData;

  public FSAPortugueseFactory(Dictionary ngramDictionary,
      TagDictionary posDictionary) {
    super(ngramDictionary, null);
    this.dict = posDictionary;
    
    // get the dictionary path
    String path = System.getProperty("fsa.dict");
    if(path == null) {
      throw new IllegalArgumentException("The property fsa.dict is missing! -Dfsa.dict=path");
    }
    
    // now we try to load it...
    try {
      this.dictInfo = FSADictionary.getFSADictionaryInfo(path);
      this.dictData = FSADictionary.getFSADictionaryData(path);
      this.dict = FSADictionary.create(dictData, dictInfo);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("The file is not a FSA dictionary!", e);
    } catch (IOException e) {
      throw new IllegalArgumentException("Could not open the FSA dictionary or the .info file", e);
    }
  }

  public FSAPortugueseFactory(ArtifactProvider artifactProvider) {
    super(artifactProvider);
  }

  @Override
  public SequenceValidator<String> getSequenceValidator() {
    return new PortuguesePOSSequenceValidator(getTagDictionary());
  }

  @Override
  public POSContextGenerator getPOSContextGenerator(int cacheSize) {
    return new PortuguesePOSContextGenerator(cacheSize, getDictionary());
  }
  
  @Override
  public POSContextGenerator getPOSContextGenerator() {
    return new PortuguesePOSContextGenerator(getDictionary());
  }

  @Override
  public TagDictionary getTagDictionary() {
    if(this.dict == null) {
      
      if(artifactProvider != null) {
        Object obj = artifactProvider.getArtifact(FSA_POSDICT);
        if(obj != null) {
          byte[] data = (byte[]) artifactProvider.getArtifact(FSA_POSDICT);
          byte[] info = (byte[]) artifactProvider.getArtifact(FSA_DICT_INFO);
          
          try {
            this.dict = FSADictionary.create(data, info);
          } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }
    }
      
    return this.dict;
  }
  
  @Override
  public void setTagDictionary(TagDictionary dictionary) {
    throw new UnsupportedOperationException("FSA factory does not support this operation");
  }
  
  @Override
  public TagDictionary createEmptyTagDictionary() {
    throw new UnsupportedOperationException("FSA factory does not support this operation");
  }
  
  @Override
  public TagDictionary createTagDictionary(File dictionary)
      throws InvalidFormatException, FileNotFoundException, IOException {
    throw new UnsupportedOperationException("FSA factory does not support this operation");
  }
  
  @Override
  public TagDictionary createTagDictionary(InputStream in)
      throws InvalidFormatException, IOException {
    throw new UnsupportedOperationException("FSA factory does not support this operation");
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
