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

import br.ccsl.cogroo.analyzer.AnalyzerI;
import br.ccsl.cogroo.analyzer.ComponentFactory;
import br.ccsl.cogroo.analyzer.Pipe;
import br.ccsl.cogroo.checker.CheckDocument;
import br.ccsl.cogroo.checker.GrammarCheckerAnalyzer;
import br.ccsl.cogroo.entities.Mistake;
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
    
    InputStream in = ComponentFactory.class.getResourceAsStream("/models.xml");
    
    ComponentFactory factory = ComponentFactory.create(in);
    
    mCogroo = factory.createPipe();
    
    GrammarCheckerAnalyzer checker;
    try {
      checker = new GrammarCheckerAnalyzer();
    } catch (IllegalArgumentException e) {
      throw new ResourceInitializationException(e);
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
    
    ((Pipe) mCogroo).add(checker);
    
    Closeables.closeQuietly(in);

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
