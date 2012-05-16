package br.ccsl.cogroo.analyzer;

public interface OpenNLPComponentFactoryI {
    
  public Analyzer createSentenceDetector();
  
  public Analyzer createTokenizer();
  
  public Analyzer createNameFinder();
  
  public Analyzer createContractionFinder();
  
  public Analyzer createPOSTagger();
}
