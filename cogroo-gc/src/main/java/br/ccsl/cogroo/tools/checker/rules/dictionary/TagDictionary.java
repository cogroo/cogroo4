/**
 * Copyright (C) 2008 CoGrOO Team (cogroo AT gmail DOT com)
 * 
 * CoGrOO Team (cogroo AT gmail DOT com)
 * LTA, PCS (Computer and Digital Systems Engineering Department),
 * Escola Politécnica da Universidade de São Paulo
 * Av. Prof. Luciano Gualberto, trav. 3, n. 380
 * CEP 05508-900 - São Paulo - SP - BRAZIL
 * 
 * http://cogroo.sourceforge.net/
 * 
 * This file is part of CoGrOO.
 * 
 * CoGrOO is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public as published by 
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CoGrOO is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with CoGrOO. If not, see <http://www.gnu.org/licenses/>.
 */

package br.ccsl.cogroo.tools.checker.rules.dictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import br.ccsl.cogroo.entities.impl.MorphologicalTag;
import br.ccsl.cogroo.interpreters.TagInterpreterI;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask;
import br.ccsl.cogroo.util.PairWordPOSTag;

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
