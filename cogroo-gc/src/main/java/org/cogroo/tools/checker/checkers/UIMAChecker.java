package org.cogroo.tools.checker.checkers;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
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
import org.cogroo.tools.checker.AbstractTypedChecker;
import org.cogroo.tools.checker.checkers.uima.AnnotatorUtil;
import org.cogroo.tools.checker.checkers.uima.UimaCasAdapter;

public class UIMAChecker extends AbstractTypedChecker {
	
	private static final Logger LOGGER = Logger.getLogger(UIMAChecker.class);
	
	private AnalysisEngine ae;
	private UimaCasAdapter converter;
	private Type mProblemType;
	private Type mProblemDescription;
	private Feature mDescriptionFeature;

	public UIMAChecker() {
		
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription("MainTypeSystem");
		try {
			AnalysisEngineDescription aeDes = Ruta.createAnalysisEngineDescription("Main", tsd);
			
			this.ae = UIMAFramework.produceAnalysisEngine(aeDes);
		} catch (Exception e1) {
			LOGGER.fatal("Failed to start Ruta AE", e1);
			throw new RuntimeException("Failed to start Ruta AE",e1);
		}
		
			
		this.converter = new UimaCasAdapter();

	}

	@Override
	public List<Mistake> check(Sentence sentence) {

		try {

			CAS cas = ae.newCAS();
			converter.populateCas(sentence.getTextSentence(), cas);
			ae.process(cas);
			
			initTypeSystem(cas.getTypeSystem());
			
			FSIndex<AnnotationFS> problems = cas.getAnnotationIndex(mProblemType);
			
			for (AnnotationFS problem : problems) {
				System.out.println("Encontrou: " + problem.getCoveredText());
			}
			
			FSIndex<AnnotationFS> problemDescription = cas.getAnnotationIndex(mProblemDescription);
			
			for (AnnotationFS problem : problemDescription) {
				System.out.println("Encontrou: " + problem.getCoveredText() + " -> " + problem.getFeatureValueAsString(mDescriptionFeature));
			}
			

		} catch (Exception e) { // TODO: tratar exceptions corretamente
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Collections.emptyList();
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
		
		CheckDocument document = new CheckDocument("As conclusões estão meias confusas. A conclusão está meia confusa.");
		// passe o doc pelo pipe
		gc.analyze(document);
		
		// obtenha os resultados em document.getMistakes(), ou simplesmente imprima o documento
		System.out.println(document);
	}
}
