package br.ccsl.cogroo.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.util.TextUtils;

/**
 * The <code>Pipe</code> class contains a sequence of analyzers.    
 * <p>
 * It follows the composite pattern to manage the analyzers. Uses the method {@link #add(AnalyzerI)} to add analyzers into the pipe.  
 *
 */
public class Pipe implements AnalyzerI {

  protected static final Logger LOGGER = Logger.getLogger(Pipe.class);
  private List<AnalyzerI> mChildAnalyzers = new ArrayList<AnalyzerI>();
  
  /**
   * Adds an analyzer into the pipe.
   * <p>
   * Follows the composite pattern standards.
   * 
   * @param aAnalyzer is the analyzer to be added in the pipe. 
   */
  public void add(AnalyzerI aAnalyzer) {
    mChildAnalyzers.add(aAnalyzer);
  }

  public void analyze(Document document) {

    for (AnalyzerI analyzer : mChildAnalyzers) {
      analyzer.analyze(document);
    }

    System.out.println(TextUtils.nicePrint(document));
  }

}