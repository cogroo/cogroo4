package org.cogroo.tools.checker.checkers;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.ruta.engine.Ruta;
import org.cogroo.analyzer.Analyzer;
import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.checker.CheckDocument;
import org.cogroo.checker.GrammarChecker;
import org.cogroo.entities.Mistake;
import org.cogroo.entities.Sentence;
import org.cogroo.tools.RuleParser;
import org.cogroo.tools.checker.AbstractTypedChecker;
import org.cogroo.tools.checker.RuleDefinition;
import org.cogroo.tools.checker.checkers.uima.AnnotatorUtil;
import org.cogroo.tools.checker.checkers.uima.UimaCasAdapter;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class UIMAChecker extends AbstractTypedChecker {
	
	private static final Logger LOGGER = Logger.getLogger(UIMAChecker.class);
	
	private AnalysisEngine ae;
	private final UimaCasAdapter converter;
	private Type mProblemType;
	private Type mProblemDescription;
	private Feature mDescriptionFeature;

	public UIMAChecker() {
		
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription("MainTypeSystem");
		try {
			URL url = Resources.getResource("Main.ruta");
			String text = Resources.toString(url, Charsets.UTF_8);
			AnalysisEngineDescription aeDes = Ruta.createAnalysisEngineDescription(text, tsd);
			
			this.ae = UIMAFramework.produceAnalysisEngine(aeDes);
		} catch (Exception e1) {
			LOGGER.fatal("Failed to start Ruta AE", e1);
			throw new RuntimeException("Failed to start Ruta AE",e1);
		}
		
		int i = 1;
		while (true) {
			String filename = i + ".txt";
			RuleDefinition ruleDef = RuleParser.getRuleDefinition(filename);
						
			if (ruleDef == null) {
				break;
			}
						
			add(ruleDef);
			i++;
		}
		
			
		this.converter = new UimaCasAdapter();

	}

	@Override
	public List<Mistake> check(Sentence sentence) {

		List<Mistake> mistakes = new LinkedList<Mistake>();
		
		try {
			
			CAS cas = ae.newCAS();
			converter.populateCas(sentence.getTextSentence(), cas);
			ae.process(cas);
			initTypeSystem(cas.getTypeSystem());
			
			FSIndex<AnnotationFS> problems = cas.getAnnotationIndex(mProblemType);
			for (AnnotationFS problem : problems) {
				mistakes.add(createMistake("1", createSuggestion(problem.getCoveredText()), problem.getBegin(), problem.getEnd(), sentence.getSentence()));
			}
			
			FSIndex<AnnotationFS> problemDescription = cas.getAnnotationIndex(mProblemDescription);
			
			System.out.println("Batata " + problemDescription.size()
					+ "\n -> " + problems.size());
			
			for (AnnotationFS problem : problemDescription) {
				System.out.println("Encontrou: " + problem.getCoveredText() + " -> " + problem.getFeatureValueAsString(mDescriptionFeature));
			}
			

		} catch (Exception e) { // TODO: tratar exceptions corretamente
			e.printStackTrace();
		}

		return mistakes;
	} 

	private String[] createSuggestion(String error) {

		String[] array = { error };

		return array;
	}
	
	private boolean typeSystemInitialized = false;
	private synchronized void initTypeSystem(TypeSystem typeSystem) throws AnalysisEngineProcessException {
		if(typeSystemInitialized == true) {
			return;
		}
		mProblemType = AnnotatorUtil.getType(typeSystem,
				"cogroo.ruta.Base.PROBLEM");
		mProblemDescription = AnnotatorUtil.getType(typeSystem,
				"cogroo.ruta.Base.PROBLEM_DESCRIPTION");
		mDescriptionFeature = AnnotatorUtil.getRequiredFeature(mProblemDescription, "description",
				CAS.TYPE_NAME_STRING);
		
		typeSystemInitialized = true;
	}

	@Override
	public String getIdPrefix() {
		return "uima:";
	}

	@Override
	public int getPriority() {
		return 100;
	}
	
	public static void main(String[] args) throws IllegalArgumentException, IOException {
		ComponentFactory factory = ComponentFactory.create(new Locale("pt", "BR"));
		Analyzer cogroo = factory.createPipe();
		GrammarChecker gc = new GrammarChecker(cogroo);
		
		CheckDocument document = new CheckDocument("Refiro-me à trabalho remunerado.");
		// passe o doc pelo pipe
		gc.analyze(document);
	
	}
}
