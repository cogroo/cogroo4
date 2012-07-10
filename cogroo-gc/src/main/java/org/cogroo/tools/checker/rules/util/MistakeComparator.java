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
package org.cogroo.tools.checker.rules.util;

import java.util.Comparator;

import org.cogroo.entities.Mistake;


/**
 * Utility class to compare {@link Mistake} objects
 * 
 * @author Marcelo Suzumura
 */
public class MistakeComparator implements Comparator<Mistake> {

	public int compare(Mistake m1, Mistake m2) {
		if (m1.getStart() < m2.getStart()) {
			return -1;
		} else if (m1.getStart() > m2.getStart()) {
			return 1;
		}
		return 0;
	}

}
