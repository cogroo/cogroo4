package org.cogroo.gc.cmdline.grammarchecker;

import opennlp.tools.cmdline.ArgumentParser.ParameterDescription;
import opennlp.tools.cmdline.params.LanguageParams;

public interface LanguageCountryParams extends LanguageParams {
  @ParameterDescription(valueName = "country", description = "country which is being processed.")
  String getCountry();
}
