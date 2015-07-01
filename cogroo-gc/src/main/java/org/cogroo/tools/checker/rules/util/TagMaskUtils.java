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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cogroo.entities.Token;
import org.cogroo.tools.checker.rules.model.TagMask;

/**
 * Utility methods to work with {@link TagMask}s. Specially to clone a
 * {@link TagMask} .
 * 
 * @author colen
 * 
 */
public class TagMaskUtils {

	private static final Pattern REPLACE_TAGR2 = Pattern
			.compile("(\\w+)\\s*=\\s*([\\w-]+)");
	// .compile("(number|gender|class|person|tense|mood)\\s*=\\s*([\\w-]+)");
	private static final Pattern REPLACE_R2 = Pattern.compile("(\\w+)");

	// .compile("(gender|number|class|person|tense|mood)");

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

	public static TagMask parse(String text) {
		TagMask tm = new TagMask();
		Matcher m = REPLACE_TAGR2.matcher(text);

		while (m.find()) {
			String property = m.group(1);
			String value = m.group(2).replace('-', ' ');

			switch (property) {
			case "number":
				tm.setNumber(TagMask.Number.fromValue(value));
				break;
			case "gender":
				tm.setGender(TagMask.Gender.fromValue(value));
				break;
			case "class":
				tm.setClazz(TagMask.Class.fromValue(value));
				break;
			case "person":
				tm.setPerson(TagMask.Person.fromValue(value));
				break;
			case "tense":
				tm.setTense(TagMask.Tense.fromValue(value));
				break;
			case "mood":
				tm.setMood(TagMask.Mood.fromValue(value));
				break;
			default:
				throw new IllegalArgumentException("Invalid property: '"
						+ property + "'");
			}

		}

		return tm;
	}

	public static TagMask createTagMaskFromToken(Token token, String text) {
		TagMask tm = new TagMask();
		Matcher m = REPLACE_R2.matcher(text);
		while (m.find()) {
			String property = m.group(1);
			switch (property) {
			case "number":
				tm.setNumber(token.getMorphologicalTag().getNumberE());
				break;
			case "gender":
				tm.setGender(token.getMorphologicalTag().getGenderE());
				break;
			case "class":
				tm.setClazz(token.getMorphologicalTag().getClazzE());
				break;
			case "person":
				tm.setPerson(token.getMorphologicalTag().getPersonE());
				break;
			case "tense":
				tm.setTense(token.getMorphologicalTag().getTense());
				break;
			case "mood":
				tm.setMood(token.getMorphologicalTag().getMood());
				break;
			default:
				break;
			}

		}

		return tm;
	}

}
