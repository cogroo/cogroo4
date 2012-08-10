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

import java.util.ArrayList;
import java.util.List;

public class VerbPlusPreps {

	private String verb;

	private List<Prep> preps = new ArrayList<Prep>();

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}

	public List<Prep> getPreps() {
		return preps;
	}

	public void addPreps(Prep prep) {
		preps.add(prep);
	}

//	Looks for a noun that matches the current verb and returns the preposition that should be linking them
	public Prep findWord(String word) {

		VerbPlusPreps vpp = this;
		List<Prep> preps = vpp.getPreps();

		for (Prep prep : preps) {

			if (prep.getObjects() != null) {
				List<String> objects = prep.getObjects();
				for (String string : objects) {
					if (string.equals(word)) {
						return prep;
					}
				}
			}
		}
		return null;
	}

}
