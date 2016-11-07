package org.cogroo.ruta.uima;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.cogroo.text.Chunk;
import org.cogroo.text.Sentence;
import org.cogroo.text.SyntacticChunk;
import org.cogroo.text.Token;

public class UimaCasAdapter {
	
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

	private Feature mChunkFeature;

	private Feature mLemmaFeature;

	private Feature mFeaturesFeature;

	private Feature mLexemeFeature;

	private Feature mChunkHead;

	private Type mSyntacticChunkType;

	private Feature mSyntacticChunkFeature;


	private boolean typesystemInitialized = false;
	
	/**
	 * Initializes the type system.
	 */
	private void typeSystemInit(TypeSystem typeSystem)
			throws AnalysisEngineProcessException {
		
		if(typesystemInitialized == true) {
			return;
		}
		
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
				CAS.TYPE_NAME_STRING);

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

		typesystemInitialized = true;
	}

	public void populateCas(Sentence sentence, CAS tcas) throws AnalysisEngineProcessException, CASRuntimeException {
		
		typeSystemInit(tcas.getTypeSystem());

//		for (Sentence sentence : document.getSentences()) {

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
				String[] lemmas = token.getLemmas();
				
				String lemma = StringUtils.join(lemmas, " ");
				tokenAnnotationArr[i].setStringValue(this.mLemmaFeature, lemma);
				
//				StringArrayFS lemmas = tcas.createStringArrayFS(token
//						.getLemmas().length);
//				lemmas.copyFromArray(token.getLemmas(), 0, 0,
//						token.getLemmas().length);
//				tokenAnnotationArr[i].setFeatureValue(this.mLemmaFeature,
//						lemmas);

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
//		}
	}

	public Type getSentenceType() {
		return mSentenceType;
	}

	public Type getTokenType() {
		return mTokenType;
	}

	public Type getChunkType() {
		return mChunkType;
	}

	public Feature getPosFeature() {
		return mPosFeature;
	}

	public Feature getChunkFeature() {
		return mChunkFeature;
	}

	public Feature getLemmaFeature() {
		return mLemmaFeature;
	}

	public Feature getFeaturesFeature() {
		return mFeaturesFeature;
	}

	public Feature getLexemeFeature() {
		return mLexemeFeature;
	}

	public Feature getChunkHead() {
		return mChunkHead;
	}

	public Type getSyntacticChunkType() {
		return mSyntacticChunkType;
	}

	public Feature getSyntacticChunkFeature() {
		return mSyntacticChunkFeature;
	}

}