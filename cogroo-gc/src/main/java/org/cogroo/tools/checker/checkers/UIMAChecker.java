package org.cogroo.tools.checker.checkers;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
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
	private Type mTokenType;
	private Feature mIDFeature;
	private Feature mLemmaFeature;

	
	private final HashSet<Integer> done = new HashSet<Integer>();

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
		
		// TODO: added for now in order to prevent this method to run more than once
		if (done.contains(sentence.getOffset()))
			System.exit(0);
		done.add(sentence.getOffset());
		

		List<Mistake> mistakes = new LinkedList<Mistake>();
		
		try {
			
			CAS cas = ae.newCAS();
			
			// http://article.gmane.org/gmane.comp.apache.uima.general/6274
			cas.setDocumentText(sentence.getDocumentText());
			
			
			converter.populateCas(sentence.getTextSentence(), cas);
			ae.process(cas);
			
			TypeSystem typeSystem = cas.getTypeSystem();
			initTypeSystem(typeSystem);
			
//			Feature idFeature = typeSystem.getFeatureByFullName("id");
			
			FSIndex<AnnotationFS> problems = cas.getAnnotationIndex(mProblemType);
			for (AnnotationFS problem : problems) {
				String id = problem.getFeatureValueAsString(mIDFeature);
				System.out.println("Maçã: " + problem.getCoveredText());
				mistakes.add(createMistake(id, createSuggestion(problem.getCoveredText()), problem.getBegin(), problem.getEnd(), sentence.getSentence()));
			}
			
			System.out.println("Batata -> " + problems.size());

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
				"cogroo.ruta.Main.PROBLEM");
		mIDFeature = AnnotatorUtil.getRequiredFeature(mProblemType, "id",
				CAS.TYPE_NAME_STRING);
		
		mTokenType = AnnotatorUtil.getType(typeSystem,
				"opennlp.uima.Token");
		mLemmaFeature = AnnotatorUtil.getRequiredFeature(mTokenType, "lemma",
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
		System.out.println("main:");
		ComponentFactory factory = ComponentFactory.create(new Locale("pt", "BR"));
		Analyzer cogroo = factory.createPipe();
		GrammarChecker gc = new GrammarChecker(cogroo);
		
		CheckDocument document = new CheckDocument("Quanto à lápis, não entendo. Quanto à computador, não entendo. Refiro-me à trabalhos remunerados. Refiro-me à reuniões extraordinárias. Fomos levados à crer. A uma hora estaremos partindo. As duas horas estaremos partindo. Os ônibus estacionaram a direita do pátio. Os ônibus estacionaram a esquerda do pátio. Em relação as atividades programadas. Com relação as atividades programadas. Devido as cobranças injustas. Enviei os documentos à você. Enviei os documentos à Vossa Excelência.  Quanto ao lápis, não entendo. Quanto ao computador, não entendo. Refiro-me aos trabalhos remunerados. Refiro-me às reuniões extraordinárias. Refiro-me a reuniões extraordinárias. Fomos levados a crer. À uma hora estaremos partindo. Daqui a uma hora estaremos partindo. Às duas horas estaremos partindo. Os ônibus estacionaram à direita do pátio. Os ônibus estacionaram à esquerda do pátio. Em relação a segurança dos menores. Em relação à segurança dos menores. Em relação às atividades programadas. Com relação a segurança dos menores. Com relação à segurança dos menores. Com relação às atividades programadas. Devido à cobrança injusta. Devido às cobranças injustas. Devido a cobrança injusta. Enviei os documentos a você. Enviei os documentos a Vossa Excelência.");
		// passe o doc pelo pipe
		gc.analyze(document);
	
	}
}
