package br.ccsl.cogroo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.google.common.io.Closeables;

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

  public Pipe() throws FileNotFoundException {
    InputStream modelIn = new FileInputStream("models/pt-sent.bin");

    try {
      SentenceModel model = new SentenceModel(modelIn);
      sentenceDetector = new SentenceDetectorME(model);
    } catch (IOException e) {
      LOGGER.fatal("Couldn't load sentence model!", e);
    } finally {
      Closeables.closeQuietly(modelIn);
    }
    
    modelIn = new FileInputStream("models/pt-token.bin");

    try {
      TokenizerModel model = new TokenizerModel(modelIn);
      tokenizer = new TokenizerME(model);
    }
    catch (IOException e) {
      LOGGER.fatal("Couldn't load tokenizer model!", e);
    }
    finally {
      Closeables.closeQuietly(modelIn);
    }
    
    modelIn = new FileInputStream("models/pt-pos-maxent.bin");

    try {
      POSModel model = new POSModel(modelIn);
      tagger = new POSTaggerME(model);
    }
    catch (IOException e) {
      LOGGER.fatal("Couldn't load tokenizer model!", e);
    }
    finally {
      Closeables.closeQuietly(modelIn);
    }
    
    
  }

  public void analyze(String text) {
    Span sentenceSpan[] = analyzeSentences (text);
    
    for (Span span : sentenceSpan) {
      String sentence = span.getCoveredText(text).toString();
      
      Span[] tokensSpan = analyzeTokens(sentence);
      
      String[] tokens = Span.spansToStrings(tokensSpan, sentence);
      
      tokens = analyzeTags(tokens);
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
      LOGGER.debug("Tokens: " + Arrays.toString(Span.spansToStrings(tokens, text)));
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
