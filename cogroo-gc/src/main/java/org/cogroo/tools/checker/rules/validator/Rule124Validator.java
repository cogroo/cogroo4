/**
 * Copyright (C) 2012 cogroo <cogroo@cogroo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cogroo.tools.checker.rules.validator;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.cogroo.entities.Mistake;
import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.Token;

public class Rule124Validator implements RulePostValidator {

  private static final Logger LOGGER = Logger.getLogger(Rule124Validator.class);

  @Override
  public boolean isValid(Mistake mistake, Document doc) {
    Sentence sent = RuleValidatorUtil.getMistakeStartSentence(doc, mistake);
    List<Token> tokens = RuleValidatorUtil.getMistakeCoveredTokens(sent,
        mistake);

    if(LOGGER.isDebugEnabled()) {
      LOGGER.debug("Evaluating sentence [" + sent.getText() + "]");
      LOGGER.debug(" mistake tokens:" + Arrays.toString(tokens.toArray()));
    }
    
    // 1) a regra é invalida caso depois do predicativo inicie com um artigo, definido ou nao
    // http://comunidade.cogroo.org/reports/547
    if(exccaoDeArtigoIniciandoPredicativo(tokens)) {
      return false;
    }
      
      
    return true;  
    
  }

  private boolean exccaoDeArtigoIniciandoPredicativo(List<Token> tokens) {
    String pos = tokens.get(0).getPOSTag();
    if("art".equals(pos) || "pron-det".equals(pos)) {
      return true;
    }
    return false;
  }

}
