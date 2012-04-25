package br.ccsl.cogroo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import cogroo.exceptions.ExceptionMessages;
import cogroo.exceptions.CogrooRuntimeException;

import opennlp.tools.chunker.Chunker;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;


public class RuntimeLanguageLoader implements LanguageLoader {

  public static final String SENT = "/Users/wcolen/Documents/wrks/___MODELS/pt-sent.model";
  public static final String TOK = "/Users/wcolen/Documents/wrks/___MODELS/pt-token.model";
  public static final String PROP = "model/pt-prop.model";
  public static final String EXP = "model/pt-exp.model";
  public static final String CON = "model/pt-con.model";
  public static final String POS = "model/pt-pos.model";
  public static final String CHK = "model/pt-chk.model";
  public static final String SP = "model/pt-sp.model";


  public SentenceDetector getSentenceDetector() {
    InputStream modelIn;
    try {
      modelIn = new FileInputStream(SENT);
    } catch (FileNotFoundException e) {
      Object[] args = { SENT };
      throw new CogrooRuntimeException(
          ExceptionMessages.MODEL_FILE_NOT_FOUND, args, e);
    }
    SentenceModel model = null;
    try {
      model = new SentenceModel(modelIn);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (modelIn != null) {
        try {
          modelIn.close();
        } catch (IOException e) {
        }
      }
    }
    return new SentenceDetectorME(model);
  }

  public Tokenizer getTokenizer() {
    InputStream modelIn;
    try {
      modelIn = new FileInputStream(TOK);
    } catch (FileNotFoundException e) {
      Object[] args = { TOK };
      throw new CogrooRuntimeException(
          ExceptionMessages.MODEL_FILE_NOT_FOUND, args, e);
    }
    TokenizerModel model = null;
    try {
      model = new TokenizerModel(modelIn);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (modelIn != null) {
        try {
          modelIn.close();
        } catch (IOException e) {
        }
      }
    }
    return new TokenizerME(model);
  }

  public TokenNameFinder getProperNameFinder() {

    InputStream modelIn;
    try {
      modelIn = new FileInputStream(PROP);
    } catch (FileNotFoundException e) {
      Object[] args = { PROP };
      throw new CogrooRuntimeException(
          ExceptionMessages.MODEL_FILE_NOT_FOUND, args, e);
    }
    TokenNameFinderModel model = null;
    try {
      model = new TokenNameFinderModel(modelIn);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (modelIn != null) {
        try {
          modelIn.close();
        } catch (IOException e) {
        }
      }
    }
    return new NameFinderME(model);
  }

  public TokenNameFinder getExpressionFinder() {
    InputStream modelIn;
    try {
      modelIn = new FileInputStream(EXP);
    } catch (FileNotFoundException e) {
      Object[] args = { EXP };
      throw new CogrooRuntimeException(
          ExceptionMessages.MODEL_FILE_NOT_FOUND, args, e);
    }
    TokenNameFinderModel model = null;
    try {
      model = new TokenNameFinderModel(modelIn);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (modelIn != null) {
        try {
          modelIn.close();
        } catch (IOException e) {
        }
      }
    }
    return new NameFinderME(model);
  }

  public TokenNameFinder getContractionFinder() {
    InputStream modelIn;
    try {
      modelIn = new FileInputStream(CON);
    } catch (FileNotFoundException e) {
      Object[] args = { CON };
      throw new CogrooRuntimeException(
          ExceptionMessages.MODEL_FILE_NOT_FOUND, args, e);
    }
    TokenNameFinderModel model = null;
    try {
      model = new TokenNameFinderModel(modelIn);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (modelIn != null) {
        try {
          modelIn.close();
        } catch (IOException e) {
        }
      }
    }
    return new NameFinderME(model);
  }

  public POSTagger getPOSTagger() {

    InputStream modelIn;
    try {
      modelIn = new FileInputStream(POS);
    } catch (FileNotFoundException e) {
      Object[] args = { POS };
      throw new CogrooRuntimeException(
          ExceptionMessages.MODEL_FILE_NOT_FOUND, args, e);
    }
    POSModel model = null;
    try {
      model = new POSModel(modelIn);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (modelIn != null) {
        try {
          modelIn.close();
        } catch (IOException e) {
        }
      }
    }
    return new POSTaggerME(model);

  }

  public Chunker getChunker() {
    InputStream modelIn;
    try {
      modelIn = new FileInputStream(CHK);
    } catch (FileNotFoundException e) {
      Object[] args = { CHK };
      throw new CogrooRuntimeException(
          ExceptionMessages.MODEL_FILE_NOT_FOUND, args, e);
    }
    ChunkerModel model = null;
    try {
      model = new ChunkerModel(modelIn);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (modelIn != null) {
        try {
          modelIn.close();
        } catch (IOException e) {
        }
      }
    }
    return new ChunkerME(model);
  }

  public Chunker getShallowParser() {
    InputStream modelIn;
    try {
      modelIn = new FileInputStream(SP);
    } catch (FileNotFoundException e) {
      Object[] args = { SP };
      throw new CogrooRuntimeException(
          ExceptionMessages.MODEL_FILE_NOT_FOUND, args, e);
    }
    ChunkerModel model = null;
    try {
      model = new ChunkerModel(modelIn);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (modelIn != null) {
        try {
          modelIn.close();
        } catch (IOException e) {
        }
      }
    }
    return new ChunkerME(model);
  }

}
