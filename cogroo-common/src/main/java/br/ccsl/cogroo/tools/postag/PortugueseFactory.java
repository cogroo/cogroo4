package br.ccsl.cogroo.tools.postag;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.model.ArtifactProvider;
import opennlp.tools.util.model.ArtifactSerializer;
import opennlp.tools.util.model.UncloseableInputStream;

public class PortugueseFactory extends POSTaggerFactory {
  
  private static final String EXTENDED_POSDICT = "EXTENDED_POSDICT";
  private POSDictionary extendedPOSDict;
  
  public PortugueseFactory(Dictionary ngramDictionary, POSDictionary posDictionary) {
    super(ngramDictionary, null);
    this.extendedPOSDict = posDictionary;
  }
  
  public PortugueseFactory(ArtifactProvider artifactProvider) {
    super(artifactProvider);
  }

  @Override
  public SequenceValidator<String> getSequenceValidator() {
    return new PortuguesePOSSequenceValidator(getPOSDictionary());
  }
  
  public POSDictionary getPOSDictionary() {
    return (POSDictionary) artifactProvider.getArtifact(EXTENDED_POSDICT);
  }
  
  @Override
  @SuppressWarnings("rawtypes")
  public Map<String, ArtifactSerializer> createArtifactSerializersMap() {
    Map<String, ArtifactSerializer> serializers = super.createArtifactSerializersMap();
    
    serializers.put(EXTENDED_POSDICT, new POSDictionarySerializer());
    return serializers;
  }
  
  @Override
  public Map<String, Object> createArtifactMap() {
    Map<String, Object> artifactMap = super.createArtifactMap();
    if(this.extendedPOSDict != null)
      artifactMap.put(EXTENDED_POSDICT, this.extendedPOSDict);
    return artifactMap;
  }
  
  static class POSDictionarySerializer implements ArtifactSerializer<POSDictionary> {

    public POSDictionary create(InputStream in) throws IOException,
        InvalidFormatException {
      return POSDictionary.create(new UncloseableInputStream(in));
    }

    public void serialize(POSDictionary artifact, OutputStream out)
        throws IOException {
      artifact.serialize(out);
    }
  }
  
}
