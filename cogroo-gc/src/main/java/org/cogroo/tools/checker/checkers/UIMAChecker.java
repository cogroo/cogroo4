package org.cogroo.tools.checker.checkers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.cogroo.tools.checker.rules.model.Example;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class UIMAChecker extends AbstractTypedChecker {

	private static final Logger LOGGER = Logger.getLogger(UIMAChecker.class);

	private AnalysisEngine ae;
	private final UimaCasAdapter converter;
	private Type mProblemType;
	private Type mTokenType;
	private Feature mIDFeature, mSuggestionFeature;
	private final boolean developing = false;
	private static final Pattern SWAP = Pattern
			.compile("swap\\s+(\\d+)\\s+(\\d+)");
	private static final Pattern REPLACE_LEXEME = Pattern
			.compile("replace\\s+(\\d+)\\s+with\\s+'([^']+)'");
	private static final Pattern REPLACE_MAPPING = Pattern
			.compile("replace\\s+(\\d+)\\s+with\\s*\\{((\\s*'[^']+'\\s*=>\\s*'[^']+'\\s*)+)\\}");
	private static final Pattern REPLACE_MAPPING2 = Pattern
			.compile("'([^']+)'\\s*=>\\s*'([^']+)'");
	private static final Pattern REPLACE_TAGR = Pattern
			.compile("replace\\s+(\\d+)\\s+with\\s+(\\d+)\\s+in\\s+\\(((\\s*(number|gender|class|person|tense|mood)\\s*=\\s*[\\w-]+\\s*)+)\\)");
	private static final Pattern REPLACE_TAGR2 = Pattern
			.compile("(number|gender|class|person|tense|mood)\\s*=\\s*([\\w-]+)");
	private static final Pattern REPLACE_R = Pattern
			.compile("set\\s+\\((\\s*(gender|number|class|person|tense|mood)\\s*)+\\)\\s+of\\s+(\\d+)\\s+to\\s+match\\s+(\\d+)");
	private static final Pattern REPLACE_R2 = Pattern
			.compile("(gender|number|class|person|tense|mood)");

	private final HashSet<Integer> done = new HashSet<Integer>();

	public UIMAChecker() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Initializing UIMA Checker constructor.");
		}
		// TODO: move the following lines to a new class:
		// AnalysisEngineFactory: create typesystem and analysis engine

		TypeSystemDescription tsd = TypeSystemDescriptionFactory
				.createTypeSystemDescription("cogroo.ruta.MainTypeSystem");

		try {
			URL url = Resources.getResource("cogroo/ruta/Main.ruta");
			String text = Resources.toString(url, Charsets.UTF_8);
			AnalysisEngineDescription aeDes = Ruta
					.createAnalysisEngineDescription(text, tsd);

			this.ae = UIMAFramework.produceAnalysisEngine(aeDes);
		} catch (Exception e1) {
			LOGGER.fatal("Failed to start Ruta AE", e1);
			throw new RuntimeException("Failed to start Ruta AE", e1);
		}

		String fileName = "cogroo/ruta/Regras.txt";
		URL url = Resources.getResource(fileName);
		for (RuleDefinition ruleDef : RuleParser.getRuleDefinitionList(url))
			add(ruleDef);
		this.converter = new UimaCasAdapter();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("UIMA Checker constructor finished.");
		}
	}

	@Override
	public List<Mistake> check(Sentence sentence) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Checking sentence: '%s'", sentence));
		}

		// TODO: added for now in order to prevent this method to run more than
		// once
		// if (done.contains(sentence.getOffset()))
		// System.exit(0);
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
			Set<Integer> processed = new HashSet<Integer>();
			FSIndex<AnnotationFS> problems = cas
					.getAnnotationIndex(mProblemType);
			for (AnnotationFS problem : problems) {
				if (processed.contains(problem.getBegin()))
					continue;
				else
					processed.add(problem.getBegin());
				List<String> suggestions = new ArrayList<String>(4);
				String id = problem.getFeatureValueAsString(mIDFeature);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Found a mistake, id = " + id);
				}

				RuleDefinition rd = getRuleDefinition(id);
				System.out.println("\nID = '" + id + "'");
				System.out.println("TEXTO DO PROBLEM: '"
						+ problem.getCoveredText() + "'");

				System.out.println("MENSAGEM: '" + rd.getMessage() + "'");
				System.out.println("MENSAGEM CURTA: '" + rd.getShortMessage()
						+ "'");
				System.out.println("DESCRIÇÃO: '" + rd.getDescription() + "'");
				System.out.println("CATEGORIA: '" + rd.getCategory() + "'");
				System.out.println("GRUPO: '" + rd.getCategory() + "'");
				String suggestionRuta = problem
						.getFeatureValueAsString(mSuggestionFeature);
				System.out.println("SUGESTÃO: '" + suggestionRuta + "'");
				for (Example example : rd.getExamples())
					System.out
							.format("EXEMPLO   CORRETO: '%s'\nEXEMPLO INCORRETO: '%s'\n",
									example.getCorrect(),
									example.getIncorrect());
				String[] originalTokens = problem.getCoveredText().split(
						"(\\s+|-)");
				for (String sugPossibility : suggestionRuta.split("\\|")) {
					String[] tokens = originalTokens.clone();
					for (String sugItem : sugPossibility.split(";")) {
						Matcher m;
						if ((m = SWAP.matcher(sugItem)).find()) {
							int i = Integer.parseInt(m.group(1)) - 1;
							int j = Integer.parseInt(m.group(2)) - 1;
							String tmp = tokens[i];
							tokens[i] = tokens[j];
							tokens[j] = tmp;
						} else if ((m = REPLACE_LEXEME.matcher(sugItem)).find()) {
							int i = Integer.parseInt(m.group(1)) - 1;
							String text = m.group(2);
							tokens[i] = text;
						} else if ((m = REPLACE_MAPPING.matcher(sugItem))
								.find()) {
							System.out.println("AQUI");
							int i = Integer.parseInt(m.group(1)) - 1;
							Matcher m2 = REPLACE_MAPPING2.matcher(m.group(2));
							while (m2.find()) {
								if (m2.group(1).equals(tokens[i])) {
									tokens[i] = m2.group(2);
									break;
								}
							}
						} else if ((m = REPLACE_TAGR.matcher(sugItem)).find()) {
							int i = Integer.parseInt(m.group(1)) - 1;
							int j = Integer.parseInt(m.group(2)) - 1;
							System.out.format("(%s)\n", m.group(3));
							Matcher m2 = REPLACE_TAGR2.matcher(m.group(3));
							HashMap<String, String> hash = new HashMap<String, String>(
									4);
							while (m2.find()) {
								String property = m2.group(1);
								String value = m2.group(2);
								hash.put(property, value);
							}
							System.out
									.format("TROCAR TOKEN '%s' POR '%s' COM OS SEGUINTES ATRIBUTOS:\n",
											tokens[i], tokens[j]);
							for (String p : hash.keySet()) {
								System.out.format("%s=%s  ", p, hash.get(p));
								// how?
							}
							System.out.println();
						} else if ((m = REPLACE_R.matcher(sugItem)).find()) {
							// TODO: corrigir regex para entrar aqui!
							System.out.println(".......");
							int i = Integer.parseInt(m.group(3)) - 1;
							int j = Integer.parseInt(m.group(4)) - 1;
							Matcher m2 = REPLACE_R2.matcher(m.group(1));
							HashSet<String> properties = new HashSet<String>(4);
							while (m2.find()) {
								properties.add(m2.group(1));
							}
							System.out
									.format("DEFINIR AS SEGUINTES PROPRIEDADES DE '%s' PARA CONCORDAR COM '%s':\n",
											tokens[i], tokens[j]);
							for (String p : properties) {
								System.out.format("%s ", p);
								// how?
							}
						}
					}
					StringBuilder s = new StringBuilder(tokens[0]);
					for (int k = 1; k < tokens.length; k++) {
						s.append(" " + tokens[k]);
					}
					System.out.format("SUGESTÃO: '%s'\n", s);
					suggestions.add(s.toString());
				}
				mistakes.add(createMistake(id,
						suggestions.toArray(new String[0]), problem.getBegin(),
						problem.getEnd(), sentence.getSentence()));

			}

		} catch (Exception e) { // TODO: tratar exceptions corretamente
			// e.printStackTrace();
			LOGGER.fatal("Exception checking sentence with Ruta. ", e);
		}

		// for (Mistake m : mistakes)
		// System.out.println(m.getFullMessage());
		return mistakes;
	}

	private String[] createSuggestion(String error) {

		String[] array = { error };

		return array;
	}

	private boolean typeSystemInitialized = false;

	private synchronized void initTypeSystem(TypeSystem typeSystem)
			throws AnalysisEngineProcessException {
		if (typeSystemInitialized == true) {
			return;
		}
		mProblemType = AnnotatorUtil.getType(typeSystem,
				"cogroo.ruta.Main.PROBLEM");
		mIDFeature = AnnotatorUtil.getRequiredFeature(mProblemType, "id",
				CAS.TYPE_NAME_STRING);
		mSuggestionFeature = AnnotatorUtil.getRequiredFeature(mProblemType,
				"suggestion", CAS.TYPE_NAME_STRING);
		// mTokenType = AnnotatorUtil.getType(typeSystem,
		// "opennlp.uima.Token");
		// mLemmaFeature = AnnotatorUtil.getRequiredFeature(mTokenType, "lemma",
		// CAS.TYPE_NAME_STRING);

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

	public static void main(String[] args) throws IllegalArgumentException,
			IOException {
		ComponentFactory factory = ComponentFactory.create(new Locale("pt",
				"BR"));
		Analyzer cogroo = factory.createPipe();
		GrammarChecker gc = new GrammarChecker(cogroo);
		// CheckDocument document = new
		// CheckDocument("Quanto à lápis, não entendo. Quanto à computador, não entendo. Refiro-me à trabalhos remunerados. Refiro-me à reuniões extraordinárias. Fomos levados à crer. A uma hora estaremos partindo. As duas horas estaremos partindo. Os ônibus estacionaram a direita do pátio. Os ônibus estacionaram a esquerda do pátio. Em relação as atividades programadas. Com relação as atividades programadas. Devido as cobranças injustas. Enviei os documentos à você. Enviei os documentos à Vossa Excelência.  Quanto ao lápis, não entendo. Quanto ao computador, não entendo. Refiro-me aos trabalhos remunerados. Refiro-me às reuniões extraordinárias. Refiro-me a reuniões extraordinárias. Fomos levados a crer. À uma hora estaremos partindo. Daqui a uma hora estaremos partindo. Às duas horas estaremos partindo. Os ônibus estacionaram à direita do pátio. Os ônibus estacionaram à esquerda do pátio. Em relação a segurança dos menores. Em relação à segurança dos menores. Em relação às atividades programadas. Com relação a segurança dos menores. Com relação à segurança dos menores. Com relação às atividades programadas. Devido à cobrança injusta. Devido às cobranças injustas. Devido a cobrança injusta. Enviei os documentos a você. Enviei os documentos a Vossa Excelência.");
		CheckDocument document = new CheckDocument(
		// "Já fazem dias que não o vejo.");
		// "Jamais ocorreu-nos esta ideia; agora está anexo os documentos solicitados.");
		// passe o doc pelo pipe
				"Os casacos bonito ficaram prontos.");
		gc.analyze(document);

	}
}
