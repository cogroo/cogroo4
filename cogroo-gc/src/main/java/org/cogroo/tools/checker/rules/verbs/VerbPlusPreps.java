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
package org.cogroo.tools.checker.rules.verbs;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerbPlusPreps {

	private final Map<String, Prep> prepsMap;
  
	public VerbPlusPreps(List<Prep> preps) {
	  Map<String, Prep> map = new HashMap<String, Prep>();
	  
	  for (Prep prep : preps) {
	    for (String word : prep.getObjects()) {
	      map.put(word, prep);
	    }
	  }
	  
	  prepsMap = Collections.unmodifiableMap(map);
	    
  }

//	Looks for a noun that matches the current verb and returns the preposition that should be linking them
	public Prep findWord(String word) {
		return prepsMap.get(word);
	}

}
