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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.cogroo.exceptions.CogrooRuntimeException;
import org.cogroo.text.Document;

/**
 * The <code>Pipe</code> class contains a sequence of analyzers.
 * <p>
 * It follows the composite pattern to manage the analyzers. Uses the method
 * {@link #add(Analyzer)} to add analyzers into the pipe.
 * </p>
 * The {@link #analyze(Document)} method is thread-safe, if all analyzers in it
 * are also thread-safe, which is the case of the default analyzers.
 * 
 */
public class Pipe implements Analyzer {

  protected static final Logger LOGGER = Logger.getLogger(Pipe.class);
  private List<Analyzer> mChildAnalyzers = new ArrayList<Analyzer>();

  /**
   * Adds an analyzer into the pipe.
   * <p>
   * Follows the composite pattern standards.
   * 
   * @param aAnalyzer
   *          is the analyzer to be added in the pipe.
   */
  public void add(Analyzer aAnalyzer) {
    mChildAnalyzers.add(aAnalyzer);
  }

  public void analyze(Document document) {

    for (Analyzer analyzer : mChildAnalyzers) {
      try {
        analyzer.analyze(document);
      } catch (Exception e) {
        LOGGER.error("An analyzer raised an exeception. Analyzer: "
            + analyzer.getClass().getCanonicalName() + " Document text: ["
            + document.getText() + "]", e);
        throw new CogrooRuntimeException(e);
      }
    }

  }
}