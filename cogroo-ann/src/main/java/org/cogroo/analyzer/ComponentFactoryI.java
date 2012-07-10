package org.cogroo.analyzer;


/**
 *  Creates the analyzers using the OpenNLP components.
 *  <p>
 *  Follows the factory design pattern.
 *
 */
public interface ComponentFactoryI {
  
  /**
   * @return {@link SentenceDetector} if this {@link AnalyzerI} in the corresponding language exists, otherwise <tt>null</tt>.
   */
  public AnalyzerI createSentenceDetector();

  /**
   * @return {@link Tokenizer} if this Analyzer in the corresponding language exists, otherwise <tt>null</tt>.
   */
  public AnalyzerI createTokenizer();
  
  /**
   * @return {@link NameFinder} if this Analyzer in the corresponding language exists, otherwise <tt>null</tt>.
   */
  public AnalyzerI createNameFinder();
  
/**
 * @return {@link ContractionFinder} if this Analyzer in the corresponding language exists, otherwise <tt>null</tt>.
 */
  public AnalyzerI createContractionFinder();
  
  /**
   * @return {@link POSTagger} if this Analyzer in the corresponding language exists, otherwise <tt>null</tt>.
   */
  public AnalyzerI createPOSTagger();
  
  public AnalyzerI createFeaturizer();
  
  /**
   * @return {@link Pipe} according to the corresponding language.
   */
  public AnalyzerI createPipe();
}
