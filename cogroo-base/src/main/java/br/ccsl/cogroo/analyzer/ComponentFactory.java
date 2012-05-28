package br.ccsl.cogroo.analyzer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

  public AnalyzerI createSentenceDetector() {
    SentenceDetectorME sentenceDetector = null;
    InputStream modelIn = null;

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

      return new SentenceDetector(sentenceDetector);
    }
    return null;
  }

  public AnalyzerI createTokenizer() {
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

      return new Tokenizer(tokenizer);
    }
    return null;
  }

  public AnalyzerI createNameFinder() {
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

      return new NameFinder(nameFinder);
    }
    return null;
  }

  public AnalyzerI createContractionFinder() {
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

      return new ContractionFinder(contractionFinder);
    }
    return null;
  }

  public AnalyzerI createPOSTagger() {
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

      return new POSTagger(tagger);
    }
    return null;
  }

  public AnalyzerI createFeaturizer() {
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

      return new Featurizer(featurizer);
    }
    return null;
  }

  public AnalyzerI createLemmatizer() {

    try {
      FSADictionary dict = FSADictionary
          .createFromResources("/fsa_dictionaries/pos/pt_br_jspell.dict");
      Lemmatizer lemmatizer = new Lemmatizer(dict);

      return lemmatizer;

    } catch (IllegalArgumentException e) {
      LOGGER.fatal("Couldn't load ");
      throw new InitializationException("Couldn't load", e);
    } catch (IOException e) {
      LOGGER.fatal("Couldn't find the dictionary.");
      throw new InitializationException("Couldn't locate dictionary", e);
    }
  }

  public AnalyzerI createPipe() {
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
      default:
        throw new InitializationException("Unknown analyzer: " + analyzer);
      }
    }
    return pipe;
  }
}
