package org.cogroo.ruta.checker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.cogroo.analyzer.Analyzer;
import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.checker.CheckDocument;
import org.cogroo.checker.GrammarChecker;
import org.cogroo.entities.Mistake;
import org.cogroo.entities.Sentence;
import org.cogroo.entities.Token;
import org.cogroo.ruta.tools.RuleParser;
import org.cogroo.ruta.uima.AEFactory;
import org.cogroo.ruta.uima.AnnotatorUtil;
import org.cogroo.ruta.uima.UimaCasAdapter;
import org.cogroo.tools.checker.AbstractTypedChecker;
import org.cogroo.tools.checker.RuleDefinition;
import org.cogroo.tools.checker.rules.applier.SuggestionBuilder;
import org.cogroo.tools.checker.rules.dictionary.CogrooTagDictionary;
import org.cogroo.tools.checker.rules.model.Example;
import org.cogroo.tools.checker.rules.model.TagMask;
import org.cogroo.tools.checker.rules.util.TagMaskUtils;

import opennlp.tools.util.Span;

public class UIMAChecker extends AbstractTypedChecker {

	private static final Logger LOGGER = Logger.getLogger(UIMAChecker.class);

	private final AnalysisEngine ae;
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
	private static final Pattern REPLACE_R = Pattern
			.compile("set\\s+\\((\\s*(gender|number|class|person|tense|mood)\\s*)+\\)\\s+of\\s+(\\d+)\\s+to\\s+match\\s+(\\d+)");

	private final HashSet<Integer> done = new HashSet<Integer>();

	private final CogrooTagDictionary tagDictionary;

	private final SuggestionBuilder suggestionBuilder;

	public UIMAChecker(CogrooTagDictionary td) {

		this.tagDictionary = td;
		this.suggestionBuilder = new SuggestionBuilder(this.tagDictionary);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Initializing UIMA Checker constructor.");
		}
		this.ae = AEFactory.createRutaAE();
		String fileName = "cogroo/ruta/Regras.txt";
		for (RuleDefinition ruleDef : RuleParser.getRuleDefinitionList(fileName))
			add(ruleDef);
		this.converter = new UimaCasAdapter();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("UIMA Checker constructor finished.");
		}
	}

	private static String applyCase(String s, int type) {
		switch (type) {
		case 0:
			return s.toLowerCase();
		case 1:
			return Character.toUpperCase(s.charAt(0)) + s.substring(1);
		case 2:
			return s.toUpperCase();
		default:
			throw new IllegalArgumentException();
		}
	}

	private static int getCase(String s) {
		char first = s.charAt(0);
		char second = s.length() > 1 ? s.charAt(1) : 'a';
		// if a word has only one letter, suppose the "other letters" are
		// lower-case.
		if (Character.isLowerCase(first))
			return 0;
		else if (Character.isUpperCase(second))
			return 2;
		else
			return 1;
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

				List<Token> coveredTokens = getTokens(sentence,
						problem.getBegin(), problem.getEnd());

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
							String tagMaskStr = m.group(3);
							TagMask tagMask = TagMaskUtils.parse(tagMaskStr);

							tokens[i] = suggestionBuilder.getBestFlexedWord(
									coveredTokens.get(j), tagMask);
						} else if ((m = REPLACE_R.matcher(sugItem)).find()) {
							int i = Integer.parseInt(m.group(3)) - 1;
							int j = Integer.parseInt(m.group(4)) - 1;
							String tagMaskStr = m.group(1);
							TagMask tagMask = TagMaskUtils
									.createTagMaskFromToken(
											coveredTokens.get(j), tagMaskStr);
							tokens[i] = suggestionBuilder.getBestFlexedWord(
									coveredTokens.get(i), tagMask);
						}
					}
					StringBuilder s = new StringBuilder(applyCase(tokens[0],
							getCase(originalTokens[0])));
					for (int k = 1; k < tokens.length; k++) {
						s.append(" "
								+ applyCase(tokens[k],
										getCase(originalTokens[k])));
					}
					System.out.format("SUGESTÃO: '%s'\n", s);
					suggestions.add(s.toString());
				}
				mistakes.add(createMistake(id,
						suggestions.toArray(new String[0]), problem.getBegin(),
						problem.getEnd(), sentence.getSentence()));

			}

		} catch (Exception e) { // TODO: tratar exceptions corretamente
			LOGGER.fatal("Exception checking sentence with Ruta. ", e);
		}
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

	private List<Token> getTokens(Sentence sentence, int begin, int end) {
		List<Token> tokens = new ArrayList<>();
		Span span = new Span(begin, end);

		for (Token token : sentence.getTokens()) {
			if (span.contains(token.getSpan())) {
				tokens.add(token);
			}
		}
		return tokens;
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
				"segue anexos o documento solicitado.");
		gc.analyze(document);

	}
}
