//package org.cogroo.tools.checker.checkers.uima;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
//import java.util.Locale;
//
//import org.apache.uima.analysis_engine.AnalysisEngine;
//import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
//import org.apache.uima.cas.CAS;
//import org.apache.uima.cas.CASRuntimeException;
//import org.apache.uima.cas.FSIndex;
//import org.apache.uima.cas.text.AnnotationFS;
//import org.apache.uima.resource.ResourceInitializationException;
//import org.cogroo.analyzer.Analyzer;
//import org.cogroo.analyzer.ComponentFactory;
//import org.cogroo.ruta.uima.AEFactory;
//import org.cogroo.ruta.uima.UimaCasAdapter;
//import org.cogroo.text.Document;
//import org.cogroo.text.Sentence;
//import org.cogroo.text.impl.DocumentImpl;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//public class UimaCasAdapterTest {
//
//	private static Analyzer cogroo;
//
//	@BeforeClass
//	public static void setUpClass() throws Exception {
//		// create a cogroo instance
//		ComponentFactory factory = ComponentFactory.create(new Locale("pt",
//				"BR"));
//		cogroo = factory.createPipe();
//	}
//
//	@Test
//	public void testCreateCogroo() {
//		assertNotNull(cogroo);
//	}
//
//	@Test
//	public void testCreateSentence() {
//		// Criar uma Sentence
//		String sentence = "Uma longa frase.";
//
//		Sentence sentenceObj = createSentence(sentence);
//		assertNotNull(sentenceObj);
//		assertEquals(sentence, sentenceObj.getText());
//	}
//
//	@Test
//	public void testCreateCAS() throws ResourceInitializationException {
//		CAS cas = createCAS();
//		assertNotNull("The cas should not be null.", cas);
//	}
//
//	@Test
//	public void testPopulateCas() throws AnalysisEngineProcessException,
//			CASRuntimeException, ResourceInitializationException {
//		CAS cas = createCAS();
//
//		UimaCasAdapter adapter = new UimaCasAdapter();
//
//		String sentenceText = "Uma longa frase.";
//		adapter.populateCas(createSentence(sentenceText), cas);
//
//		// check if we have one sentence
//
//		FSIndex<AnnotationFS> sentences = cas.getAnnotationIndex(adapter
//				.getSentenceType());
//
//		assertEquals(1, sentences.size());
//
//		for (AnnotationFS sentence : sentences) {
//			assertEquals("Sentence: sentence begin should be 0.", 0,
//					sentence.getBegin());
//
//			assertEquals("Sentence: sentence end does not match.",
//					sentenceText.length(), sentence.getEnd());
//		}
//
//		// TODO: do the same for other annotations!
//	}
//
//	private CAS createCAS() throws ResourceInitializationException {
//		AnalysisEngine ae = AEFactory.createRutaAE();
//		return ae.newCAS();
//	}
//
//	private Sentence createSentence(String sentence) {
//		Document doc = new DocumentImpl(sentence);
//		cogroo.analyze(doc);
//		return doc.getSentences().get(0);
//	}
//
//}
