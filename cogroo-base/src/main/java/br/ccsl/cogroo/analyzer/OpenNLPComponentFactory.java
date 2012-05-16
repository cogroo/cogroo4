package br.ccsl.cogroo.analyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.xml.bind.JAXBException;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import org.apache.log4j.Logger;

import br.ccsl.cogroo.config.LanguageConfiguration;
import br.ccsl.cogroo.config.LanguageConfigurationUtil;

import com.google.common.io.Closeables;

public class OpenNLPComponentFactory implements OpenNLPComponentFactoryI {

  protected static final Logger LOGGER = Logger
      .getLogger(OpenNLPComponentFactory.class);
  InputStream modelIn = null;

  private OpenNLPComponentFactory() {
  }

  public static OpenNLPComponentFactory create(Locale locale) {
    
    try {
      LanguageConfiguration lc = LanguageConfigurationUtil.get(locale);
    } catch (FileNotFoundException e) {
      
      e.printStackTrace();
    } catch (JAXBException e) {
      
      e.printStackTrace();
    }
    
    return new OpenNLPComponentFactory();
  }

  public Analyzer createSentenceDetector() {
    SentenceDetectorME sentenceDetector = null;

    try {
      modelIn = new FileInputStream("models/pt-sent.model");
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

  public Analyzer createTokenizer() {
    TokenizerME tokenizer = null;

    try {
      modelIn = new FileInputStream("models/pt-tok.model");
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

  public Analyzer createNameFinder() {
    NameFinderME nameFinder = null;

    try {
      modelIn = new FileInputStream("models/pt-prop.model");
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

  public Analyzer createContractionFinder() {
    NameFinderME contractionFinder = null;

    try {
      modelIn = new FileInputStream("models/pt-con.model");
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

  public Analyzer createPOSTagger() {
    POSTaggerME tagger = null;

    try {
      modelIn = new FileInputStream("models/pt-pos-maxent.bin");
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
}
