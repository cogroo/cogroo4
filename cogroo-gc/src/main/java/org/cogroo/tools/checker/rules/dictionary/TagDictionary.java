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
package org.cogroo.tools.checker.rules.dictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.interpreters.TagInterpreterI;
import org.cogroo.util.PairWordPOSTag;

import org.cogroo.tools.checker.rules.model.TagMask;

/**
 * Provides access to the FSA tag dictionary
 * @author William Colen
 *
 */
public class TagDictionary implements CogrooTagDictionary {
	
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(TagDictionary.class);
	
	LexicalDictionary access;
	private boolean caseSensitive;
	private TagInterpreterI dicTI;
	
	public TagDictionary(LexicalDictionary access, boolean caseSensitive, TagInterpreterI tagInterpreter)
	{
		this.dicTI = tagInterpreter;
		this.caseSensitive = caseSensitive;
		this.access = access;
	}

	public boolean exists(String word, boolean cs) {
		
		if( cs )
			return this.access.wordExists(word);
		else
			return this.access.wordExists(word.toLowerCase());
		
	}

	public String[] getInflectedPrimitive(String primitive, TagMask tagMask, boolean cs) {
			List<PairWordPOSTag> lemmaTag;
			if (cs) {
				lemmaTag = this.access.getWordsAndPosTagsForLemma(primitive);
			} else {
				lemmaTag = this.access.getWordsAndPosTagsForLemma(primitive.toLowerCase());
			}
			if (lemmaTag == null) { // Defensive programming.
				lemmaTag = new ArrayList<PairWordPOSTag>();
			}
			Set<String> inflectedLexemes = new HashSet<String>(lemmaTag.size());
			for (PairWordPOSTag pair : lemmaTag) {
				MorphologicalTag t = dicTI.parseMorphologicalTag(pair.getPosTag());
				if (t.match(tagMask)) {
					inflectedLexemes.add(pair.getWord());
				}
			}
			return inflectedLexemes.toArray(new String[inflectedLexemes.size()]);

	}

	public String[] getPrimitive(String lexeme, TagMask tagMask, boolean cs) {
		Set<String> primitiveSet = new HashSet<String>();
		List<PairWordPOSTag> pairs;
		if (cs) {
			pairs = this.access.getLemmasAndPosTagsForWord(lexeme);
		} else {
			pairs = this.access.getLemmasAndPosTagsForWord(lexeme.toLowerCase());
		}
		for (PairWordPOSTag lemmaPOSTag : pairs) {
			if ((dicTI.parseMorphologicalTag(lemmaPOSTag.getPosTag())).match(tagMask)) {
				primitiveSet.add(lemmaPOSTag.getWord());
			}
		}
		if (primitiveSet.isEmpty()) { // Do not return null, it is better to return an empty string.
			primitiveSet.add("");
		}
		return primitiveSet.toArray(new String[primitiveSet.size()]);
	}

	public String[] getPrimitive(String lexeme, MorphologicalTag morphologicalTag, boolean cs) {
		
		Set<String> primitiveSet = new HashSet<String>();
		List<PairWordPOSTag> pairs;
		
		if (cs) {
			pairs = this.access.getLemmasAndPosTagsForWord(lexeme);
		} else {
			pairs = this.access.getLemmasAndPosTagsForWord(lexeme.toLowerCase());
		}
		
		for (PairWordPOSTag lemmaPOSTag : pairs) {
			if ((dicTI.parseMorphologicalTag(lemmaPOSTag.getPosTag())).match(morphologicalTag)) {
				primitiveSet.add(lemmaPOSTag.getWord());
			}
		}
		if (primitiveSet.isEmpty()) { // Do not return null, it is better to return an empty string.
			return null;
		}
		return primitiveSet.toArray(new String[primitiveSet.size()]);
	}

	public boolean match(String lexeme, TagMask tagMask, boolean cs) {
		
		List<PairWordPOSTag> pairs;
		if (cs) {
			pairs = this.access.getLemmasAndPosTagsForWord(lexeme);
		} else {
			pairs = this.access.getLemmasAndPosTagsForWord(lexeme.toLowerCase());
		}
		
		for (PairWordPOSTag lemmaPOSTag : pairs) {
			MorphologicalTag m = dicTI.parseMorphologicalTag(lemmaPOSTag.getPosTag());
			if(m.match(tagMask)) {
				return true;
			}
		}
		
		return false;
	}
	
	public MorphologicalTag[] getTags(String word, boolean cs) {
		if (cs) {
			return convertToTargetConvention(this.access.getPOSTagsForWord(word));
		}
		String lowerCaseWord = word.toLowerCase();
		if(lowerCaseWord.equals(word))
			return convertToTargetConvention(this.access.getPOSTagsForWord(word));
		else
		{
			Set<String> tags = new HashSet<String>();
			List<String> lc = this.access.getPOSTagsForWord(lowerCaseWord);
			if(lc != null) {
				tags.addAll(lc);
			}
			List<String> t = this.access.getPOSTagsForWord(word);
			if(t != null) {
				tags.addAll(t);
			}
			return convertToTargetConvention(tags);
		}
	}

	public MorphologicalTag[] getTags(String word) {

		return getTags(word, this.caseSensitive);
	}
	
	public MorphologicalTag convertToTargetConvention(String ori) {
		return this.dicTI.parseMorphologicalTag(ori);
	}
	
	private MorphologicalTag[] convertToTargetConvention(Collection<String> ori) {
		if(ori == null) {
			return null;
		}
		List<MorphologicalTag> tag = new ArrayList<MorphologicalTag>(ori.size());
		for (String morphologicalTag : ori) {
			tag.add(convertToTargetConvention(morphologicalTag));
		}
		return tag.toArray(new MorphologicalTag[tag.size()]);
	}

  public TagInterpreterI getTagInterpreter() {
    return dicTI;
  }
	
//	private String[] extractTag(String lexeme){
//		String[] arr = null;//this.word.stemAndForm(lexeme);
//
//		if( arr == null )
//			return null;
//		
//		String[] tags = new String[arr.length/2];
//		for (int i = 0; i < arr.length; i+=2) {
//			tags[i/2] = arr[i+1];
//		}
//		return tags;
//	}
}
