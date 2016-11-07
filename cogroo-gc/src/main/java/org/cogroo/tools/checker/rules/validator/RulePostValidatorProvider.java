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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.cogroo.entities.Mistake;
import org.cogroo.text.Document;
import org.cogroo.tools.checker.rules.applier.RulesApplier;

public class RulePostValidatorProvider implements RulePostValidator {

  private static final Map<String, RulePostValidator> VALIDATORS;

  static {
    Map<String, RulePostValidator> _validator = new HashMap<String, RulePostValidator>();

    // TODO: comentado para LABXP 2015
//    _validator.put(id("124"), new Rule124Validator());

    VALIDATORS = Collections.unmodifiableMap(_validator);
  }

  private static String id(String value) {
    return RulesApplier.ID_PREFIX + value;
  }

  @Override
  public boolean isValid(Mistake mistake, Document doc) {
    if (VALIDATORS.containsKey(mistake.getRuleIdentifier())) {
      return VALIDATORS.get(mistake.getRuleIdentifier()).isValid(mistake, doc);
    }
    return true;
  }
}
