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
package org.cogroo.analyzer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import opennlp.model.AbstractModel;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import org.apache.log4j.Logger;
import org.cogroo.config.Analyzers;
import org.cogroo.config.LanguageConfiguration;
import org.cogroo.config.LanguageConfigurationUtil;
import org.cogroo.config.Model;
import org.cogroo.dictionary.impl.FSADictionary;
import org.cogroo.tools.chunker2.ChunkerME;
import org.cogroo.tools.chunker2.ChunkerModel;
import org.cogroo.tools.featurizer.FeaturizerME;
import org.cogroo.tools.featurizer.FeaturizerModel;

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

  public Analyzer createSentenceDetector() {
    long start = System.nanoTime();
    SentenceDetectorME sentenceDetector = null;
    InputStream modelIn = null;
    Analyzer analyzer = null; 
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

  public Analyzer createTokenizer() {
    long start = System.nanoTime();
    Analyzer analyzer = null;
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

  public Analyzer createNameFinder() {
    long start = System.nanoTime();
    Analyzer analyzer = null;
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

  public Analyzer createContractionFinder() {
    long start = System.nanoTime();
    Analyzer analyzer = null;
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

  public Analyzer createPOSTagger() {
    long start = System.nanoTime();
    Analyzer analyzer = null;
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

  public Analyzer createFeaturizer() {
    long start = System.nanoTime();
    Analyzer analyzer = null;
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

  public Analyzer createLemmatizer() {
    long start = System.nanoTime();
    Analyzer analyzer = null;

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

  public Analyzer createChunker() {
    long start = System.nanoTime();
    Analyzer analyzer = null;
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
  
  public Analyzer createHeadFinder() {
    long start = System.nanoTime();
    Analyzer analyzer = null;
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
  
  public Analyzer createShallowParser() {
    long start = System.nanoTime();
    Analyzer analyzer = null;
    ChunkerME shallowParser = null;
    InputStream modelIn = null;

    if (modelPathMap.containsKey(Analyzers.SHALLOW_PARSER)) {
      try {
        modelIn = ComponentFactory.class.getResourceAsStream(modelPathMap
            .get(Analyzers.SHALLOW_PARSER));
        ChunkerModel model = new ChunkerModel(modelIn);
        logOutcomes(model.getChunkerModel());
        shallowParser = new ChunkerME(model, 20);
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

  private void logOutcomes(AbstractModel chunkerModel) {
    StringBuilder sb = new StringBuilder("Outcomes: ");
    for (int i = 0; i < chunkerModel.getNumOutcomes(); i++) {
      sb.append(chunkerModel.getOutcome(i)).append(" ");
    }
    LOGGER.info(sb.toString());
  }

  public Analyzer createPipe() {
    long start = System.nanoTime();
    Pipe pipe = new Pipe();

    
    // to accelerate the startup we do it in two steps. First we start initialization with
    // FutureTasks, and finally we wait for the results..
    
    FutureTask<Analyzer> future;
    List<FutureTask<Analyzer>> initializers = new LinkedList<FutureTask<Analyzer>>();
    ExecutorService executor = Executors.newCachedThreadPool();
    
    LOGGER.info("Loading pipe assynchronously...");
    
    for (Analyzers analyzer : lc.getPipe().getAnalyzer()) {
      switch (analyzer) {
      case SENTENCE_DETECTOR:
        future = new FutureTask<Analyzer>(new Callable<Analyzer>() {
          public Analyzer call() {
            return createSentenceDetector();
          }
        });
        executor.execute(future);
        initializers.add(future);
        break;
      case TOKENIZER:
        future = new FutureTask<Analyzer>(new Callable<Analyzer>() {
          public Analyzer call() {
            return createTokenizer();
          }
        });
        executor.execute(future);
        initializers.add(future);
        break;
      case NAME_FINDER:
        future = new FutureTask<Analyzer>(new Callable<Analyzer>() {
          public Analyzer call() {
            return createNameFinder();
          }
        });
        executor.execute(future);
        initializers.add(future);
        break;
      case CONTRACTION_FINDER:
        future = new FutureTask<Analyzer>(new Callable<Analyzer>() {
          public Analyzer call() {
            return createContractionFinder();
          }
        });
        executor.execute(future);
        initializers.add(future);
        break;
      case POS_TAGGER:
        future = new FutureTask<Analyzer>(new Callable<Analyzer>() {
          public Analyzer call() {
            return createPOSTagger();
          }
        });
        executor.execute(future);
        initializers.add(future);
        break;
      case FEATURIZER:
        future = new FutureTask<Analyzer>(new Callable<Analyzer>() {
          public Analyzer call() {
            return createFeaturizer();
          }
        });
        executor.execute(future);
        initializers.add(future);
        break;
      case LEMMATIZER:
        future = new FutureTask<Analyzer>(new Callable<Analyzer>() {
          public Analyzer call() {
            return createLemmatizer();
          }
        });
        executor.execute(future);
        initializers.add(future);
        break;
      case CHUNKER:
        future = new FutureTask<Analyzer>(new Callable<Analyzer>() {
          public Analyzer call() {
            return createChunker();
          }
        });
        executor.execute(future);
        initializers.add(future);
        break;
      case HEAD_FINDER:
        future = new FutureTask<Analyzer>(new Callable<Analyzer>() {
          public Analyzer call() {
            return createHeadFinder();
          }
        });
        executor.execute(future);
        initializers.add(future);
        break;
      case SHALLOW_PARSER:
        future = new FutureTask<Analyzer>(new Callable<Analyzer>() {
          public Analyzer call() {
            return createShallowParser();
          }
        });
        executor.execute(future);
        initializers.add(future);
        break;
      default:
        throw new InitializationException("Unknown analyzer: " + analyzer);
      }
    }
    
    // now we get it...
    for (FutureTask<Analyzer> futureTask : initializers) {
      try {
        pipe.add(futureTask.get());
      } catch (InterruptedException e) {
        throw new InitializationException("Failed to load pipe.", e);
      } catch (ExecutionException e) {
        throw new InitializationException("Failed to load pipe.", e);
      }
    }
    
    executor.shutdown();
    
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Initialized Pipe and its components in "
          + ((System.nanoTime() - start) / 1000000) + "ms]");
    }
    return pipe;
  }

}
