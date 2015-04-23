package org.cogroo.tools.checker.checkers;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import org.cogroo.entities.Mistake;
import org.cogroo.tools.checker.AbstractTypedChecker;

public class UIMAChecker extends AbstractTypedChecker {

	@Override
	public List<Mistake> check(org.cogroo.entities.Sentence sentence) {
		
//		String[] suggestions = { "use o uima!" };
//		
//		Mistake mistake = createMistake("01", suggestions,
//			      0, 5, arg0.getSentence());
//		
//		List<Mistake> mistakes = new LinkedList<Mistake>();
//
//		mistakes.add(mistake);
		
		
		
		// TODO: >>> colocar no construtor do UIMAChecker
		File specFile = new File("/home/vinicius.vendramini/CoGrOO/cogroo4/RUTA/descriptor/Rule1Engine.xml");
		
		XMLInputSource in;
		try {
			in = new XMLInputSource(specFile);
			ResourceSpecifier specifier = UIMAFramework.getXMLParser().
					parseResourceSpecifier(in);
			// for import by name... set the datapath in the ResourceManager
			AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier);
			
			// <<<<
			
			CAS cas = ae.newCAS();
			
			// TODO: >>> Como transformar Sentence do CoGrOO em anotacoes no CAS aqui nesse ponto?
			
			cas.setDocumentText("This is my document.");
			
			
			// <<<
			
			// OK, rodar o RUTA
			ae.process(cas);
			
			// Criar Mistakes a partir das anotacoes PROBLEM e retornar a lista de Mistakes.
			
		} catch (Exception e) { // TODO: tratar exceptions corretamente
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return Collections.emptyList();
	}

	@Override
	public String getIdPrefix() {
		return "uima:";
	}

	@Override
	public int getPriority() {
		return 100;
	}


}
