package br.ccsl.cogroo.analyzer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import org.apache.log4j.Logger;

import br.ccsl.cogroo.config.Analyzers;
import br.ccsl.cogroo.config.LanguageConfiguration;
import br.ccsl.cogroo.config.LanguageConfigurationUtil;
import br.ccsl.cogroo.config.Model;
import br.ccsl.cogroo.dictionary.impl.FSADictionary;
import br.ccsl.cogroo.tools.featurizer.FeaturizerME;
import br.ccsl.cogroo.tools.featurizer.FeaturizerModel;

import com.google.common.io.Closeables;

public class ComponentFactory implements ComponentFactoryI {

  protected static final Logger LOGGER = Logger
      .getLogger(ComponentFactory.class);

  private LanguageConfiguration lc = null;
  private Map<Analyzers, String> modelPathMap;

  private ComponentFactory() {

  }

  private ComponentFactory(LanguageConfiguration lc) {
    this.lc = lc;

    modelPathMap = new HashMap<Analyzers, String>(lc.getModel().size());

    for (Model m : lc.getModel()) {
      modelPathMap.put(m.getType(), m.getValue());
    }
  }

  public static ComponentFactory create(Locale locale) {
    ComponentFactory factory = null;

    factory = new ComponentFactory(LanguageConfigurationUtil.get(locale));

    return factory;
  }

  /**
   * Creates a {@link ComponentFactory} from a configuration file. The stream
   * remains open after execution.
   * 
   * @param configuration
   *          the configuration XML, that conforms with
   *          languageConfiguration.xsd
   * @return a {@link ComponentFactory}
   */
  public static ComponentFactory create(InputStream configuration) {
    ComponentFactory factory = null;
    factory = new ComponentFactory(LanguageConfigurationUtil.get(configuration));
    return factory;
  }

  public AnalyzerI createSentenceDetector() {
    long start = System.nanoTime();
    SentenceDetectorME sentenceDetector = null;
    InputStream modelIn = null;
    AnalyzerI analyzer = null;

    if (modelPathMap.containsKey(Analyzers.SENTENCE_DETECTOR)) {
      try {
        modelIn = ComponentFactory.class.getResourceAsStream(modelPathMap
            .get(Analyzers.SENTENCE_DETECTOR));
        SentenceModel model = new SentenceModel(modelIn);
        sentenceDetector = new SentenceDetectorME(model);
      } catch (IOException e) {
        LOGGER.fatal("Couldn't load sentence model!", e);
      } finally {
        Closeables.closeQuietly(modelIn);
      }

      if (sentenceDetector == null)
        throw new InitializationException(
            "Couldn't load SentenceDetectorME class");

      analyzer = new SentenceDetector(sentenceDetector);
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Initialized SentenceDetector in "
          + ((System.nanoTime() - start) / 1000000) + "ms]");
    }
    return analyzer;
  }

  public AnalyzerI createTokenizer() {
    long start = System.nanoTime();
    AnalyzerI analyzer = null;
    TokenizerME tokenizer = null;
    InputStream modelIn = null;

    if (modelPathMap.containsKey(Analyzers.TOKENIZER)) {
      try {
        modelIn = ComponentFactory.class.getResourceAsStream(modelPathMap
            .get(Analyzers.TOKENIZER));
        TokenizerModel model = new TokenizerModel(modelIn);
        tokenizer = new TokenizerME(model);
      } catch (IOException e) {
        LOGGER.fatal("Couldn't load tokenizer model!", e);
      } finally {
        Closeables.closeQuietly(modelIn);
      }

      if (tokenizer == null)
        throw new InitializationException("Couldn't load TokenizerME class");

      analyzer = new Tokenizer(tokenizer);
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Initialized Tokenizer in "
          + ((System.nanoTime() - start) / 1000000) + "ms]");
    }
    return analyzer;
  }

  public AnalyzerI createNameFinder() {
    long start = System.nanoTime();
    AnalyzerI analyzer = null;
    NameFinderME nameFinder = null;
    InputStream modelIn = null;

    if (modelPathMap.containsKey(Analyzers.NAME_FINDER)) {
      try {
        modelIn = ComponentFactory.class.getResourceAsStream(modelPathMap
            .get(Analyzers.NAME_FINDER));
        TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
        nameFinder = new NameFinderME(model);
      } catch (IOException e) {
        LOGGER.fatal("Couldn't load name finder model!", e);
      } finally {
        Closeables.closeQuietly(modelIn);
      }

      if (nameFinder == null)
        throw new InitializationException("Couldn't load NameFinderME class");

      analyzer = new NameFinder(nameFinder);
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Initialized NameFinder in "
          + ((System.nanoTime() - start) / 1000000) + "ms]");
    }
    return analyzer;
  }

  public AnalyzerI createContractionFinder() {
    long start = System.nanoTime();
    AnalyzerI analyzer = null;
    NameFinderME contractionFinder = null;
    InputStream modelIn = null;

    if (modelPathMap.containsKey(Analyzers.CONTRACTION_FINDER)) {
      try {
        modelIn = ComponentFactory.class.getResourceAsStream(modelPathMap
            .get(Analyzers.CONTRACTION_FINDER));
        TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
        contractionFinder = new NameFinderME(model);
      } catch (IOException e) {
        LOGGER.fatal("Couldn't load contractions finder model!", e);
      } finally {
        Closeables.closeQuietly(modelIn);
      }

      if (contractionFinder == null)
        throw new InitializationException("Couldn't load NameFinderME class");

      analyzer = new ContractionFinder(contractionFinder);
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Initialized ContractionFinder in "
          + ((System.nanoTime() - start) / 1000000) + "ms]");
    }
    return analyzer;
  }

  public AnalyzerI createPOSTagger() {
    long start = System.nanoTime();
    AnalyzerI analyzer = null;
    POSTaggerME tagger = null;
    InputStream modelIn = null;

    if (modelPathMap.containsKey(Analyzers.POS_TAGGER)) {
      try {
        modelIn = ComponentFactory.class.getResourceAsStream(modelPathMap
            .get(Analyzers.POS_TAGGER));
        POSModel model = new POSModel(modelIn);
        tagger = new POSTaggerME(model);
      } catch (IOException e) {
        LOGGER.fatal("Couldn't load POS-tagger model!", e);
      } finally {
        Closeables.closeQuietly(modelIn);
      }

      if (tagger == null)
        throw new InitializationException("Couldn't load POSTaggerME class");

      analyzer = new POSTagger(tagger);
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Initialized POSTagger in "
          + ((System.nanoTime() - start) / 1000000) + "ms]");
    }
    return analyzer;
  }

  public AnalyzerI createFeaturizer() {
    long start = System.nanoTime();
    AnalyzerI analyzer = null;
    FeaturizerME featurizer = null;
    InputStream modelIn = null;

    if (modelPathMap.containsKey(Analyzers.FEATURIZER)) {
      try {
        modelIn = ComponentFactory.class.getResourceAsStream(modelPathMap
            .get(Analyzers.FEATURIZER));
        FeaturizerModel model = new FeaturizerModel(modelIn);
        featurizer = new FeaturizerME(model);
      } catch (IOException e) {
        LOGGER.fatal("Couldn't load Featurizer model!", e);
      } finally {
        Closeables.closeQuietly(modelIn);
      }

      if (featurizer == null)
        throw new InitializationException("Couldn't load FeaturizerME class");

      analyzer = new Featurizer(featurizer);
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Initialized Featurizer in "
          + ((System.nanoTime() - start) / 1000000) + "ms]");
    }
    return analyzer;
  }

  public AnalyzerI createLemmatizer() {
    long start = System.nanoTime();
    AnalyzerI analyzer = null;

    try {
      FSADictionary dict = FSADictionary
          .createFromResources("/fsa_dictionaries/pos/pt_br_jspell.dict");
      Lemmatizer lemmatizer = new Lemmatizer(dict);

      analyzer = lemmatizer;

    } catch (IllegalArgumentException e) {
      LOGGER.fatal("Couldn't load ");
      throw new InitializationException("Couldn't load", e);
    } catch (IOException e) {
      LOGGER.fatal("Couldn't find the dictionary.");
      throw new InitializationException("Couldn't locate dictionary", e);
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Initialized Lemmatizer in "
          + ((System.nanoTime() - start) / 1000000) + "ms]");
    }

    return analyzer;
  }

  public AnalyzerI createChunker() {
    long start = System.nanoTime();
    AnalyzerI analyzer = null;
    ChunkerME chunker = null;
    InputStream modelIn = null;

    if (modelPathMap.containsKey(Analyzers.CHUNKER)) {
      try {
        modelIn = ComponentFactory.class.getResourceAsStream(modelPathMap
            .get(Analyzers.CHUNKER));
        ChunkerModel model = new ChunkerModel(modelIn);
        chunker = new ChunkerME(model);
      } catch (IOException e) {
        LOGGER.fatal("Couldn't load Chunker model!", e);
      } finally {
        Closeables.closeQuietly(modelIn);
      }

      if (chunker == null)
        throw new InitializationException("Couldn't load ChunkerME class");

      analyzer = new Chunker(chunker);
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Initialized Chunker in "
          + ((System.nanoTime() - start) / 1000000) + "ms]");
    }
    return analyzer;
  }
  
  public AnalyzerI createHeadFinder() {
    long start = System.nanoTime();
    AnalyzerI analyzer = null;
    ChunkerME headFinder = null;
    InputStream modelIn = null;

    if (modelPathMap.containsKey(Analyzers.HEAD_FINDER)) {
      try {
        modelIn = ComponentFactory.class.getResourceAsStream(modelPathMap
            .get(Analyzers.HEAD_FINDER));
        ChunkerModel model = new ChunkerModel(modelIn);
        headFinder = new ChunkerME(model);
      } catch (IOException e) {
        LOGGER.fatal("Couldn't load HeadFinder model!", e);
      } finally {
        Closeables.closeQuietly(modelIn);
      }

      if (headFinder == null)
        throw new InitializationException("Couldn't load ChunkerME class");

      analyzer = new HeadFinder(headFinder);
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Initialized HeadFinder in "
          + ((System.nanoTime() - start) / 1000000) + "ms]");
    }
    return analyzer;
  }
  
  public AnalyzerI createShallowParser() {
    long start = System.nanoTime();
    AnalyzerI analyzer = null;
    ChunkerME shallowParser = null;
    InputStream modelIn = null;

    if (modelPathMap.containsKey(Analyzers.SHALLOW_PARSER)) {
      try {
        modelIn = ComponentFactory.class.getResourceAsStream(modelPathMap
            .get(Analyzers.SHALLOW_PARSER));
        ChunkerModel model = new ChunkerModel(modelIn);
        shallowParser = new ChunkerME(model);
      } catch (IOException e) {
        LOGGER.fatal("Couldn't load ShallowParser model!", e);
      } finally {
        Closeables.closeQuietly(modelIn);
      }

      if (shallowParser == null)
        throw new InitializationException("Couldn't load ChunkerME class");

      analyzer = new ShallowParser(shallowParser);
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Initialized ShallowParser in "
          + ((System.nanoTime() - start) / 1000000) + "ms]");
    }
    return analyzer;
  }

  public AnalyzerI createPipe() {
    long start = System.nanoTime();
    Pipe pipe = new Pipe();

    for (Analyzers analyzer : lc.getPipe().getAnalyzer()) {
      switch (analyzer) {
      case SENTENCE_DETECTOR:
        pipe.add(this.createSentenceDetector());
        break;
      case TOKENIZER:
        pipe.add(this.createTokenizer());
        break;
      case NAME_FINDER:
        pipe.add(this.createNameFinder());
        break;
      case CONTRACTION_FINDER:
        pipe.add(this.createContractionFinder());
        break;
      case POS_TAGGER:
        pipe.add(this.createPOSTagger());
        break;
      case FEATURIZER:
        pipe.add(this.createFeaturizer());
        break;
      case LEMMATIZER:
        pipe.add(this.createLemmatizer());
        break;
      case CHUNKER:
        pipe.add(this.createChunker());
        break;
      case HEAD_FINDER:
        pipe.add(this.createHeadFinder());
        break;
      case SHALLOW_PARSER:
        pipe.add(this.createShallowParser());
        break;
      default:
        throw new InitializationException("Unknown analyzer: " + analyzer);
      }
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Initialized Pipe and its components in "
          + ((System.nanoTime() - start) / 1000000) + "ms]");
    }
    return pipe;
  }

}
