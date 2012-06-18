package br.ccsl.cogroo.tools.featurizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import opennlp.model.AbstractModel;
import opennlp.tools.util.BaseToolFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.model.ArtifactProvider;
import opennlp.tools.util.model.ArtifactSerializer;
import opennlp.tools.util.model.UncloseableInputStream;
import br.ccsl.cogroo.dictionary.FeatureDictionaryI;

public abstract class FeaturizerFactory extends BaseToolFactory {

//  private static final String POISONED_TAGS_ENTRY_NAME = "poisonedtags.serialized_set";
  
  protected FeatureDictionaryI featureDictionary;

  private Set<String> poisonedDictionaryTags = null;

  /**
   * Creates a {@link FeaturizerFactory} that provides the default
   * implementation of the resources.
   */
  public FeaturizerFactory() {
  }

  /**
   * Creates a {@link FeaturizerFactory} with an
   * {@link ArtifactProvider} that will be used to retrieve artifacts. This
   * constructor will try to get the ngram and tags dictionaries from the
   * artifact provider.
   * <p>
   * Sub-classes should implement a constructor with this signatures and call
   * this constructor.
   * <p>
   * This will be used to load the factory from a serialized POSModel.
   */
  public FeaturizerFactory(ArtifactProvider artifactProvider) {
    super(artifactProvider);
  }

  /**
   * Creates a {@link FeaturizerFactory}. Use this constructor to
   * programmatically create a factory.
   * 
   */
  public FeaturizerFactory(FeatureDictionaryI featureDictionary) {
    this.featureDictionary = featureDictionary;
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
  public Map<String, Object> createArtifactMap() {
    Map<String, Object> artifactMap = super.createArtifactMap();
    
    // add a empty set that will be populated latter
//    artifactMap.put(POISONED_TAGS_ENTRY_NAME, new HashSet<String>());

    return artifactMap;
  }

  public FeatureDictionaryI getFeatureDictionary() {
    if (this.featureDictionary == null)
      this.featureDictionary = loadFeatureDictionary();
    return this.featureDictionary;
  }

  protected abstract FeatureDictionaryI loadFeatureDictionary();

  public Set<String> getDictionaryPoisonedTags() {
//    if (this.poisonedDictionaryTags == null && artifactProvider != null)
//      this.poisonedDictionaryTags = artifactProvider
//          .getArtifact(POISONED_TAGS_ENTRY_NAME);
    return this.poisonedDictionaryTags;
  }

  public FeaturizerContextGenerator getFeaturizerContextGenerator() {
    return new DefaultFeaturizerContextGenerator();
  }

  public SequenceValidator<WordTag> getSequenceValidator() {
    return new DefaultFeaturizerSequenceValidator(getFeatureDictionary(),
        this.getDictionaryPoisonedTags());
  }

  // call this method to find the poisoned tags. Call only during training
  // because the poisoned tags are persisted...
  protected void validateFeatureDictionary() {
    
    FeatureDictionaryI dict = getFeatureDictionary();
    if (dict != null) {
      if (dict instanceof Iterable<?>) {
        FeatureDictionaryI posDict = (FeatureDictionaryI) dict;

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
        
        if (poisonedDictionaryTags.size() > 0) {
          System.err
              .println("WARNING: Feature dictioinary contains tags which are unkown by the model! "
                  + this.poisonedDictionaryTags.toString());
        }
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
      FeatureDictionaryI posDictionary) throws InvalidFormatException {
    if (subclassName == null) {
      // will create the default factory
      return new DefaultFeaturizerFactory(posDictionary);
    }
    FeaturizerFactory theFactory = null;
    Class<? extends BaseToolFactory> factoryClass = loadSubclass(subclassName);
    if (factoryClass != null) {
      try {
        Constructor<?> constructor = null;
        constructor = factoryClass.getConstructor(FeatureDictionaryI.class);
        theFactory = (FeaturizerFactory) constructor.newInstance(posDictionary);
      } catch (NoSuchMethodException e) {
        String msg = "Could not instantiate the " + subclassName
            + ". The mandatory constructor (FeatureDictionaryI) is missing.";
        System.err.println(msg);
        throw new IllegalArgumentException(msg);
      } catch (Exception e) {
        String msg = "Could not instantiate the " + subclassName
            + ". The constructor (FeatureDictionaryI) throw an exception.";
        System.err.println(msg);
        e.printStackTrace();
        throw new InvalidFormatException(msg);
      }
    }
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
