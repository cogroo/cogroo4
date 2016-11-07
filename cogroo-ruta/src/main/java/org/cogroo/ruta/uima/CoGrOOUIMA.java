package org.cogroo.ruta.uima;

import java.util.Locale;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.CasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.StringArrayFS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.cogroo.analyzer.Analyzer;
import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.text.Chunk;
import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.SyntacticChunk;
import org.cogroo.text.Token;
import org.cogroo.text.impl.DocumentImpl;

public class CoGrOOUIMA extends CasAnnotator_ImplBase {

	/**
	 * Type parameters.
	 */
	public static final String CHUNK_TYPE_PARAMETER = "opennlp.uima.ChunkType";

	/**
	 * The chunk tag feature parameter
	 */
	public static final String CHUNK_TAG_FEATURE_PARAMETER = "opennlp.uima.ChunkTagFeature";

	private Type mSentenceType;

	private Type mTokenType;

	private Type mChunkType;

	private Feature mPosFeature;

	private Analyzer cogroo;

	private UimaContext context;

	private Logger mLogger;

	private Feature mChunkFeature;

	private Feature mLemmaFeature;

	private Feature mFeaturesFeature;

	private Feature mLexemeFeature;

	private Feature mChunkHead;

	private Type mSyntacticChunkType;

	private Feature mSyntacticChunkFeature;

	/**
	 * Initializes a new instance.
	 *
	 * Note: Use {@link #initialize(UimaContext) } to initialize this instance.
	 * Not use the constructor.
	 */
	public CoGrOOUIMA() {
		// must not be implemented !
	}

	/**
	 * Initializes the current instance with the given context.
	 *
	 * Note: Do all initialization in this method, do not use the constructor.
	 */
	public void initialize(UimaContext context)
			throws ResourceInitializationException {

		super.initialize(context);

		this.context = context;

		mLogger = context.getLogger();

		if (mLogger.isLoggable(Level.INFO)) {
			mLogger.log(Level.INFO, "Initializing the CoGrOO annotator.");
		}

		ComponentFactory factory = ComponentFactory.create(new Locale("pt",
				"BR"));
		cogroo = factory.createPipe();
	}

	/**
	 * Initializes the type system.
	 */
	public void typeSystemInit(TypeSystem typeSystem)
			throws AnalysisEngineProcessException {

		// sentence type
		mSentenceType = AnnotatorUtil.getType(typeSystem,
				"opennlp.uima.Sentence");

		// token type
		mTokenType = AnnotatorUtil.getType(typeSystem, "opennlp.uima.Token");

		// pos feature
		mPosFeature = AnnotatorUtil.getRequiredFeature(mTokenType, "pos",
				CAS.TYPE_NAME_STRING);

		// lexeme feature
		mLexemeFeature = AnnotatorUtil.getRequiredFeature(mTokenType, "lexeme",
				CAS.TYPE_NAME_STRING);

		// lemma feature
		mLemmaFeature = AnnotatorUtil.getRequiredFeature(mTokenType, "lemma",
				CAS.TYPE_NAME_STRING_ARRAY);

		// features feature
		mFeaturesFeature = AnnotatorUtil.getRequiredFeature(mTokenType,
				"features", CAS.TYPE_NAME_STRING);

		// chunk type
		mChunkType = AnnotatorUtil.getType(typeSystem, "opennlp.uima.Chunk");

		// chunk feature
		mChunkFeature = AnnotatorUtil.getRequiredFeature(mChunkType,
				"chunkType", CAS.TYPE_NAME_STRING);

		// chunk head feature
		mChunkHead = AnnotatorUtil.getRequiredFeature(mChunkType, "head",
				"opennlp.uima.Token");

		// syntactic chunk type
		mSyntacticChunkType = AnnotatorUtil.getType(typeSystem,
				"opennlp.uima.SyntacticChunk");

		// chunk feature
		mSyntacticChunkFeature = AnnotatorUtil
				.getRequiredFeature(mSyntacticChunkType, "syntChunkType",
						CAS.TYPE_NAME_STRING);
	}

	/**
	 * Performs chunking on the given tcas object.
	 */
	public void process(CAS tcas) {

		String text = tcas.getDocumentText();
		Document document = new DocumentImpl(text);

		cogroo.analyze(document);

		for (Sentence sentence : document.getSentences()) {

			// create sentence annotations
			AnnotationFS sentenceAnn = tcas.createAnnotation(mSentenceType,
					sentence.getStart(), sentence.getEnd());
			tcas.getIndexRepository().addFS(sentenceAnn);

			int sentenceOffset = sentence.getStart();

			AnnotationFS[] tokenAnnotationArr = new AnnotationFS[sentence
					.getTokens().size()];
			int i = 0;
			for (Token token : sentence.getTokens()) {
				// create token annotations
				tokenAnnotationArr[i] = tcas.createAnnotation(mTokenType,
						sentenceOffset + token.getStart(), sentenceOffset
								+ token.getEnd());

				// add POSTag annotations
				tokenAnnotationArr[i].setStringValue(this.mPosFeature,
						token.getPOSTag());

				// add lexeme annotations
				tokenAnnotationArr[i].setStringValue(this.mLexemeFeature,
						token.getLexeme());

				// add lemma annotations
				StringArrayFS lemmas = tcas.createStringArrayFS(token
						.getLemmas().length);
				lemmas.copyFromArray(token.getLemmas(), 0, 0,
						token.getLemmas().length);
				tokenAnnotationArr[i].setFeatureValue(this.mLemmaFeature,
						lemmas);

				tokenAnnotationArr[i].setStringValue(this.mFeaturesFeature,
						token.getFeatures());

				tcas.getIndexRepository().addFS(tokenAnnotationArr[i]);
				i++;
			}

			// chunks
			for (Chunk chunk : sentence.getChunks()) {

				int start = sentence.getTokens().get(chunk.getStart())
						.getStart()
						+ sentenceOffset;
				int end = sentence.getTokens().get(chunk.getEnd() - 1).getEnd()
						+ sentenceOffset;

				AnnotationFS chunkAnn = tcas.createAnnotation(mChunkType,
						start, end);

				chunkAnn.setStringValue(mChunkFeature, chunk.getTag());

				if(chunk.getHeadIndex() >= 0) {
					chunkAnn.setFeatureValue(mChunkHead,
							tokenAnnotationArr[chunk.getHeadIndex()]);
				}

				tcas.getIndexRepository().addFS(chunkAnn);
			}

			// syntactic chunk
			for (SyntacticChunk sc : sentence.getSyntacticChunks()) {
				int start = sentence.getTokens().get(sc.getStart()).getStart()
						+ sentenceOffset;
				int end = sentence.getTokens().get(sc.getEnd() - 1).getEnd()
						+ sentenceOffset;

				AnnotationFS syntChunkAnn = tcas.createAnnotation(
						mSyntacticChunkType, start, end);

				syntChunkAnn.setStringValue(mSyntacticChunkFeature, sc.getTag());

				tcas.getIndexRepository().addFS(syntChunkAnn);
			}
		}
	}

	/**
	 * Releases allocated resources.
	 */
	public void destroy() {
		// dereference model to allow garbage collection
		cogroo = null;
	}
}