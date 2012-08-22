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
package cogroo.uima.ae;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.cogroo.analyzer.AnalyzerI;
import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.analyzer.Pipe;
import org.cogroo.checker.CheckDocument;
import org.cogroo.checker.GrammarCheckerAnalyzer;
import org.cogroo.entities.Mistake;

import cogroo.uima.GoldenSentence;
import cogroo.uima.GrammarError;

import com.google.common.io.Closeables;

public class NewTagsetBaselineCogrooAE extends JCasAnnotator_ImplBase {

  /**
   * Work on sentences instead of analyzing the full text.
   */
  public static final String PARAM_BYSENTENCES = "BySentences";

  /**
   * Directory with the resources.
   */
  public static final String PARAM_RESOURCESPATH = "ResourcesDir";

  public static final String PARAM_RULESTOIGNORE = "RulesToIgnore";

  private Boolean mIsBySentences;

  private AnalyzerI mCogroo;

  private Logger mLogger;

  /**
   * @see AnalysisComponent#initialize(UimaContext)
   */
  public void initialize(UimaContext aContext)
      throws ResourceInitializationException {
    
    mCogroo = createCogroo();
    
    String[] rulesToIgnore = (String[]) aContext
        .getConfigParameterValue(PARAM_RULESTOIGNORE);

    mIsBySentences = (Boolean) aContext
        .getConfigParameterValue(PARAM_BYSENTENCES);
    if (null == mIsBySentences) { // could be null if not set, it is optional
      mIsBySentences = Boolean.FALSE;
    }

    String[] ignoreRules = new String[rulesToIgnore.length];
    for (int i = 0; i < rulesToIgnore.length; i++) {
      ignoreRules[i] = rulesToIgnore[i].trim();
    }

    if (ignoreRules.length > 0) {
//      Checker ap = config.getChecker();
//      for (String rule : ignoreRules) {
//        ap.ignore(rule);
//      }
    }
    mLogger = aContext.getLogger();
  }

  public static AnalyzerI createCogroo() throws ResourceInitializationException {
    InputStream in = ComponentFactory.class.getResourceAsStream("/models.xml");
    
    ComponentFactory factory = ComponentFactory.create(in);
    
    Pipe cogroo = (Pipe) factory.createPipe();
    
    GrammarCheckerAnalyzer checker;
    try {
      checker = new GrammarCheckerAnalyzer();
    } catch (IllegalArgumentException e) {
      throw new ResourceInitializationException(e);
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
    
    cogroo.add(checker);
    
    Closeables.closeQuietly(in);

    return cogroo;
  }

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {

    AnnotationIndex<Annotation> sentIndex = jcas
        .getAnnotationIndex(GoldenSentence.type);
    for (Annotation annotation : sentIndex) {
      GoldenSentence s = (GoldenSentence) annotation;
      int start = s.getBegin();
      String text = s.getCoveredText();
      try {

        CheckDocument doc = new CheckDocument();
        doc.setText(text);
        mCogroo.analyze(doc);
        List<Mistake> mistakes = doc.getMistakes();
        for (Mistake mistake : mistakes) {
          GrammarError ge = new GrammarError(jcas);
          ge.setBegin(start + mistake.getStart());
          ge.setEnd(start + mistake.getEnd());
          ge.setRuleId(mistake.getRuleIdentifier());
          ge.setCategory(Categories.getCat(mistake.getRuleIdentifier()));
          ge.setError(text.substring(mistake.getStart(), mistake.getEnd()));
          if (mistake.getSuggestions() != null
              && mistake.getSuggestions().length > 0)
            ge.setReplace(mistake.getSuggestions()[0]);
          ge.addToIndexes();
        }
      } catch (Throwable e) {
        System.out.println("Failed: " + text);
        e.printStackTrace();
        mLogger.log(Level.SEVERE, "Failed: " + text,e);
      }

    }

  }

}
