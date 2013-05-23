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

import org.cogroo.tools.checker.rules.model.TagMask;

/**
 * Utility methods to work with {@link TagMask}s. Specially to clone a {@link TagMask}
 * .
 * 
 * @author colen
 *
 */
public class TagMaskUtils {

	/**
	 * Creates an identical copy of the parameter <code>tagMask</code>.
	 * 
	 * @param tagMask
	 *            the tag mask to be cloned
	 * @return the clone
	 */
	public static TagMask clone(TagMask tagMask) {
		TagMask clone = new TagMask();
		clone.setCase(tagMask.getCase());
		clone.setChunkFunction(tagMask.getChunkFunction());
		clone.setClazz(tagMask.getClazz());
		clone.setGender(tagMask.getGender());
		clone.setMood(tagMask.getMood());
		clone.setNumber(tagMask.getNumber());
		clone.setPerson(tagMask.getPerson());
		clone.setPunctuation(tagMask.getPunctuation());
		clone.setSyntacticFunction(tagMask.getSyntacticFunction());
		clone.setTense(tagMask.getTense());
		return clone;
	}

}
