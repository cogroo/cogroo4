package cogroo.uima.ae;

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

import br.usp.pcs.lta.cogroo.configuration.LegacyRuntimeConfiguration;
import br.usp.pcs.lta.cogroo.configuration.RuntimeConfigurationI;
import br.usp.pcs.lta.cogroo.entity.Mistake;
import br.usp.pcs.lta.cogroo.grammarchecker.CogrooI;
import br.usp.pcs.lta.cogroo.tools.checker.Checker;
import cogroo.MultiCogroo;
import cogroo.uima.GoldenSentence;
import cogroo.uima.GrammarError;

public class BaselineCogrooAE extends JCasAnnotator_ImplBase {
	
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

	private CogrooI mCogroo;

	private Logger mLogger;
	  
	  
	  
	  /**
	   * @see AnalysisComponent#initialize(UimaContext)
	   */
	  public void initialize(UimaContext aContext) throws ResourceInitializationException {
		  

	    String pathToResources = aContext.getDataPath() + "/" + ((String) aContext.getConfigParameterValue(PARAM_RESOURCESPATH)).trim();
        
        System.out.println("Path to resources: " + pathToResources);
        
		  
		  String[] rulesToIgnore = (String[]) aContext.getConfigParameterValue(PARAM_RULESTOIGNORE);
		    
		  mIsBySentences = (Boolean) aContext.getConfigParameterValue(PARAM_BYSENTENCES);
		    if (null == mIsBySentences) { // could be null if not set, it is optional
		    	mIsBySentences = Boolean.FALSE;
		    }
		    
			RuntimeConfigurationI config = 
				new LegacyRuntimeConfiguration(pathToResources);
			
			String[] ignoreRules = new String[rulesToIgnore.length];
			for (int i = 0; i < rulesToIgnore.length; i++) {
				ignoreRules[i] = rulesToIgnore[i].trim();
			}
					
			// instantiate the grammar checker passing the configuration
			mCogroo = new MultiCogroo(config);
			
			if(ignoreRules.length > 0) {
				Checker ap = config.getChecker();
				for (String rule : ignoreRules) {
					ap.ignore(rule);
				}
			}
			mLogger = aContext.getLogger();
	  }

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		
		AnnotationIndex<Annotation> sentIndex = jcas.getAnnotationIndex(GoldenSentence.type);
		for (Annotation annotation : sentIndex) {
			GoldenSentence s = (GoldenSentence)annotation;
			int start = s.getBegin();
			String text = s.getCoveredText();
			try {
//				mLogger.log(Level.SEVERE, "Will check: " + text);
//				if(text.contains("Duvidavam de que precisasses de apoio.")) {
//					System.out.println();
//				}
//				System.out.println(text);
				List<Mistake> mistakes = mCogroo.checkText(text);
			    for (Mistake mistake : mistakes) {
					GrammarError ge = new GrammarError(jcas);
					ge.setBegin(start + mistake.getStart());
					ge.setEnd(start + mistake.getEnd());
					ge.setRuleId(mistake.getRuleIdentifier());
					ge.setCategory(Categories.getCat(mistake.getRuleIdentifier()));
					ge.setError(text.substring(mistake.getStart(), mistake.getEnd()));
					if(mistake.getSuggestions() != null && mistake.getSuggestions().length > 0)
						ge.setReplace(mistake.getSuggestions()[0]);
					ge.addToIndexes();
				}
			} catch (Throwable e) {
				System.out.println("Failed: " + text);
				mLogger.log(Level.SEVERE, "Failed: " + text);
			}

			
		}
	    
	    
	    
	}

}
