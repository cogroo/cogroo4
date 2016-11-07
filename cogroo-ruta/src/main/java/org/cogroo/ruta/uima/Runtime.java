package org.cogroo.ruta.uima;

import java.io.IOException;
import java.util.Locale;

import org.cogroo.analyzer.Analyzer;
import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.checker.CheckDocument;
import org.cogroo.checker.GrammarChecker;

public class Runtime {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws IllegalArgumentException 
	 */
	public static void main(String[] args) throws IllegalArgumentException, IOException {

		ComponentFactory factory = ComponentFactory.create(new Locale("pt", "BR"));
		Analyzer pipe = factory.createPipe();
			
		// criamos o GrammarChecker 
		GrammarChecker gc = new GrammarChecker(pipe);
		
		// crie um CheckDocument com o texto:
		CheckDocument document = new CheckDocument("Eu gosto de torta.");
		// passe o doc pelo pipe
		gc.analyze(document);
		
		// obtenha os resultados em document.getMistakes(), ou simplesmente imprima o documento
		System.out.println(document);
	}

}
