package org.cogroo.ruta.uima;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.ruta.engine.Ruta;
import org.cogroo.util.ResourcesUtil;

public class AEFactory {

  public static AnalysisEngine createRutaAE() {
    TypeSystemDescription tsd = TypeSystemDescriptionFactory
        .createTypeSystemDescription("cogroo.ruta.MainTypeSystem");
    try {
      String text = ResourcesUtil.getResourceAsString(AEFactory.class,
          "cogroo/ruta/Main.ruta");
      AnalysisEngineDescription aeDes = Ruta
          .createAnalysisEngineDescription(text, tsd);

      return UIMAFramework.produceAnalysisEngine(aeDes);
    } catch (Exception e1) {
      throw new RuntimeException("Failed to start Ruta AE", e1);
    }
  }
}
