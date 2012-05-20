package br.ccsl.cogroo.tools.postag;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.postag.POSContextGenerator;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.postag.TagDictionary;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.model.ArtifactProvider;
import opennlp.tools.util.model.ArtifactSerializer;
import opennlp.tools.util.model.UncloseableInputStream;

public class PortugueseFactory extends POSTaggerFactory {

  private static final String EXTENDED_POSDICT = "EXTENDED_POSDICT";

  public PortugueseFactory(Dictionary ngramDictionary,
      TagDictionary posDictionary) {
    super(ngramDictionary, posDictionary);
  }

  public PortugueseFactory(ArtifactProvider artifactProvider) {
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
    if(this.posDictionary == null) {
      
      if(artifactProvider != null) {
        Object obj = artifactProvider.getArtifact(EXTENDED_POSDICT);
        if(obj != null) {
          this.posDictionary = (POSDictionary) artifactProvider.getArtifact(EXTENDED_POSDICT);
        }
      }
    }
      
    return this.posDictionary;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public Map<String, ArtifactSerializer> createArtifactSerializersMap() {
    Map<String, ArtifactSerializer> serializers = super
        .createArtifactSerializersMap();

    serializers.put(EXTENDED_POSDICT, new POSDictionarySerializer(this));
    return serializers;
  }

  @Override
  public Map<String, Object> createArtifactMap() {
    Map<String, Object> artifactMap = super.createArtifactMap();
    if (this.posDictionary != null)
      artifactMap.put(EXTENDED_POSDICT, this.posDictionary);
    return artifactMap;
  }
  
  @Override
  public TagDictionary createTagDictionary(InputStream in)
      throws InvalidFormatException, IOException {
    return super.createTagDictionary(in);
  }

  static class POSDictionarySerializer implements
      ArtifactSerializer<POSDictionary> {

    private POSTaggerFactory factory;

    public POSDictionarySerializer(POSTaggerFactory factory) {
      this.factory = factory;
    }

    public POSDictionary create(InputStream in) throws IOException,
        InvalidFormatException {
      return (POSDictionary)factory.createTagDictionary(new UncloseableInputStream(in));
    }

    public void serialize(POSDictionary artifact, OutputStream out)
        throws IOException {
      artifact.serialize(out);
    }
  }
  
}
