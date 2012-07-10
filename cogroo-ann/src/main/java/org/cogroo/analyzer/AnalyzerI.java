package org.cogroo.analyzer;

import org.cogroo.text.Document;

/**
 * The <code>AnalyzerI</code> interface is responsible for analyzing part of the
 * document.
 * <p>
 * Some analyzers that implement this interface are:
 * <p>
 * <blockquote>
 * <pre>
 *      SenteceDetector
 *      Tokenizer
 *      NameFinder
 *      ContractionFinder
 *      POSTagger
 * </pre>
 * </blockquote>
 * <p>
 * The SentenceDetector, for example, looks for all sentences in a text and
 * keeps them separately in a list.
 * 
 * 
 */
public interface AnalyzerI {

  /**
   * Analyzes part of a text or a word.
   * <p>
   * For example, it can search all the
   * sentences in the text or tag every word in a sentence.
   * 
   * @param document
   *          contains the whole text given by the user. After an analysis it can store the text's sentences, words or its tags.
   */
  public void analyze(Document document);

}
