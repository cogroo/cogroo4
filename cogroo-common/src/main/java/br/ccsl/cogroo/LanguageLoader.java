package br.ccsl.cogroo;

import opennlp.tools.chunker.Chunker;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.tokenize.Tokenizer;

public interface LanguageLoader {

  public SentenceDetector getSentenceDetector();

  public Tokenizer getTokenizer();

  public TokenNameFinder getProperNameFinder();

  public TokenNameFinder getExpressionFinder();

  public TokenNameFinder getContractionFinder();

  public POSTagger getPOSTagger();

  public Chunker getChunker();

  public Chunker getShallowParser();

}
