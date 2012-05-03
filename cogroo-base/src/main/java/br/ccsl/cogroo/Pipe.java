package br.ccsl.cogroo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import br.ccsl.cogroo.entities.Sentence;

import com.google.common.io.Closeables;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

public class Pipe {

  protected static final Logger LOGGER = Logger.getLogger(Pipe.class);
  private SentenceDetectorME sentenceDetector;
  private TokenizerME tokenizer;
  private POSTaggerME tagger;
  private NameFinderME nameFinder;
  private NameFinderME contractionFinder;

  public Pipe() throws FileNotFoundException {
    InputStream modelIn = new FileInputStream("models/pt-sent.model");

    try {
      SentenceModel model = new SentenceModel(modelIn);
      sentenceDetector = new SentenceDetectorME(model);
    } catch (IOException e) {
      LOGGER.fatal("Couldn't load sentence model!", e);
    } finally {
      Closeables.closeQuietly(modelIn);
    }

    modelIn = new FileInputStream("models/pt-tok.model");

    try {
      TokenizerModel model = new TokenizerModel(modelIn);
      tokenizer = new TokenizerME(model);
    } catch (IOException e) {
      LOGGER.fatal("Couldn't load tokenizer model!", e);
    } finally {
      Closeables.closeQuietly(modelIn);
    }

    modelIn = new FileInputStream("models/pt-pos-maxent.bin");

    try {
      POSModel model = new POSModel(modelIn);
      tagger = new POSTaggerME(model);
    } catch (IOException e) {
      LOGGER.fatal("Couldn't load POS-tagger model!", e);
    } finally {
      Closeables.closeQuietly(modelIn);
    }

    modelIn = new FileInputStream("models/pt-prop.model");

    try {
      TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
      nameFinder = new NameFinderME(model);
    } catch (IOException e) {
      LOGGER.fatal("Couldn't load name finder model!", e);
    } finally {
      Closeables.closeQuietly(modelIn);
    }
    
    modelIn = new FileInputStream("models/pt-con.model");

    try {
      TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
      contractionFinder = new NameFinderME(model);
    } catch (IOException e) {
      LOGGER.fatal("Couldn't load contractions finder model!", e);
    } finally {
      Closeables.closeQuietly(modelIn);
    }
    
  }

  public void analyze(String text) {
    Span sentenceSpan[] = analyzeSentences(text);
    
    for (Span span : sentenceSpan) {
      String sentenceString = span.getCoveredText(text).toString();

      Span[] tokensSpan = analyzeTokens(sentenceString);

      String[] tokens = Span.spansToStrings(tokensSpan, sentenceString);
      tokens = findNames(tokens);
      tokens = findContractions(tokens);

      String[] tags = analyzeTags(tokens);
    }
  }

  public Span[] analyzeSentences(String text) {
    Span sentences[] = sentenceDetector.sentPosDetect(text);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Found sentences: "
          + Arrays.toString(Span.spansToStrings(sentences, text)));
    }

    return sentences;
  }

  public Span[] analyzeTokens(String text) {
    Span tokens[] = tokenizer.tokenizePos(text);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Tokens: "
          + Arrays.toString(Span.spansToStrings(tokens, text)));
    }

    return tokens;
  }

  public String[] analyzeTags(String[] tokens) {
    String[] tag = tagger.tag(tokens);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Tags: " + Arrays.toString(tag));
    }

    return tag;
  }

  public String[] findNames(String[] tokens) {
    Span[] namesSpan = nameFinder.find(tokens);

    if (namesSpan.length > 0) {
      List<String> newTokens = new ArrayList<String>();
      // i -> namesSpan; j -> tokens  
      for (int i = 0, j = 0; j < tokens.length; j++)
        
          if (i < namesSpan.length && namesSpan[i].contains(j)) {
            String name = tokens[j++];
            for (; j < namesSpan[i].getEnd(); j++) {
              name = name + "_" + tokens[j];
            }
            j--;
            newTokens.add(name);
            i++;
          }
          else
            newTokens.add(tokens[j]);
      
      tokens = newTokens.toArray(new String[newTokens.size()]);
      LOGGER.debug("Tokens com nomes: " + Arrays.toString(tokens));
      }
    
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Names: " + Arrays.toString(namesSpan));
    }
    
    return tokens;
  }

  public String[] findContractions(String[] tokens) {
    Span[] contractionsSpan = contractionFinder.find(tokens);
    
    if (contractionsSpan.length > 0) {
      List<String> newTokens = new ArrayList<String>();
      // i -> contractionsSpan; j -> tokens  
      for (int i = 0, j = 0; j < tokens.length; j++)
        
          if (i < contractionsSpan.length && contractionsSpan[i].contains(j)) {
            String[] contraction = ContractionUtility.expand(tokens[j]);
            
            for (int k = 0; k < contraction.length; k++)
              newTokens.add(contraction[k]);
            i++;
          }
          else
            newTokens.add(tokens[j]);
      
      tokens = newTokens.toArray(new String[newTokens.size()]);
      LOGGER.debug("Tokens com contrações: " + Arrays.toString(tokens));
      }
    
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Contrações: " + Arrays.toString(contractionsSpan));
    }
    
    return tokens;
  }
  
  /**
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {

    long start = System.nanoTime();
    Pipe pipe = new Pipe();

    System.out.println("Loading time ["
        + ((System.nanoTime() - start) / 1000000) + "ms]");
    Scanner kb = new Scanner(System.in);
    System.out.print("Enter the sentence: ");
    String input = kb.nextLine();
    while (!input.equals("q")) {
      if (input.equals("0")) {
        input = "Fomos levados à crer que os menino são burro de doer. As menina chegaram.";
      }

      pipe.analyze(input);

      System.out.print("Enter the sentence: ");
      input = kb.nextLine();
    }

  }

}
