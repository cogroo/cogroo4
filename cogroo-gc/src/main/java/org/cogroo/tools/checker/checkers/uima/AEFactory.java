package org.cogroo.tools.checker.checkers.uima;

import java.net.URL;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.ruta.engine.Ruta;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class AEFactory {

	public static AnalysisEngine createRutaAE() {
		TypeSystemDescription tsd = TypeSystemDescriptionFactory
				.createTypeSystemDescription("cogroo.ruta.MainTypeSystem");
		try {
			URL url = Resources.getResource("cogroo/ruta/Main.ruta");
			String text = Resources.toString(url, Charsets.UTF_8);
			AnalysisEngineDescription aeDes = Ruta
					.createAnalysisEngineDescription(text, tsd);

			return UIMAFramework.produceAnalysisEngine(aeDes);
		} catch (Exception e1) {
			throw new RuntimeException("Failed to start Ruta AE", e1);
		}
	}
}
