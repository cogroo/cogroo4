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
package org.cogroo.tools.checker.rules.applier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cogroo.entities.Sentence;
import org.cogroo.entities.Token;
import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.entities.impl.TokenCogroo;
import org.cogroo.tools.checker.rules.dictionary.CogrooTagDictionary;
import org.cogroo.tools.checker.rules.util.RuleUtils;
import org.cogroo.tools.checker.rules.util.TagMaskUtils;

import org.cogroo.tools.checker.rules.model.Suggestion;
import org.cogroo.tools.checker.rules.model.Suggestion.Replace;
import org.cogroo.tools.checker.rules.model.Suggestion.ReplaceMapping;
import org.cogroo.tools.checker.rules.model.Suggestion.Swap;
import org.cogroo.tools.checker.rules.model.TagMask;
import org.cogroo.tools.checker.rules.model.TagMask.Gender;
import org.cogroo.tools.checker.rules.model.TagMask.Number;

/**
 * This class makes suggestions to correct the mistakes.
 * 
 * @author Marcelo Suzumura
 * @author Fábio Wang Gusukuma
 * @author William Colen
 * @version $Id: SuggestionBuilder.java 400 2007-04-19 02:36:40Z msuzumura $
 */
public class SuggestionBuilder {

	/**
	 * Determines suggestions for the mistake found in the sentence.
	 * 
	 * @param sentence
	 *            the processed sentence
	 * @param lower
	 *            the first token of the mistake
	 * @param upper
	 *            the last token of the mistake
	 * @param suggestions
	 *            the suggestions patterns from the rule
	 * @param dictionary
	 *            a tag-word-primitive dictionary
	 * @return an array of suggestions to correct the mistake
	 */
	public static String[] getSuggestions(Sentence sentence, boolean considerChunk, int baseIndex, int lower, int upper, List<Suggestion> suggestions, CogrooTagDictionary dictionary) {
		// Each suggestionsAsString position will contain a suggestion.
		Set<String> suggestionsAsString = new HashSet<String>();
		for (Suggestion suggestion : suggestions) {
			String s = getSuggestions(sentence, considerChunk, baseIndex, lower, upper, suggestion, dictionary);
			if(s!= null && s.length() > 0)
				suggestionsAsString.add(s);
		}
		
		return suggestionsAsString.toArray(new String[suggestionsAsString.size()]);
	}
	
	public static String getSuggestions(Sentence sentence, boolean considerChunk, int baseIndex, int lower, int upper, Suggestion suggestion, CogrooTagDictionary dictionary) {
		
		// Gets only the tokens that are referred by the mistake.
		Token[] tokens = SuggestionBuilder.tokensSubArray(sentence, lower, upper, considerChunk);
		String[] mistakenTokensAsString = SuggestionBuilder.tokensSubArrayAsString(tokens);
		
		// Tells if a token was replaced by an empty string.
		boolean replacedByEmptyString[] = new boolean[mistakenTokensAsString.length];
		// If can not determine an inflection for the replacement, reject the suggestion.
		boolean reject = false;
		
		// Replaces.
		/*
			<Replace> work as follows.
			Lexeme	TagReference	Action
			0		0				does nothing
			0		1				gets primitive from the token in the sentence and queries for an inflection
			1		0				replaces for the lexeme
			1		1				gets primitive (<Lexeme>) from the <Replace> and queries for an inflection
		*/
		for (Replace replace : suggestion.getReplace()) {
			if (replace.getTagReference() == null && replace.getLexeme() != null) { // L1, T0.
				// i.e., replacing a token with the given lexeme.
				// Beware of upper case...
				String replacement = replace.getLexeme();
				int replaceIndex = (int) replace.getIndex();
				mistakenTokensAsString[replaceIndex] = SuggestionBuilder.useCasedString(mistakenTokensAsString[(int) replace.getIndex()], replacement);
				replacedByEmptyString[replaceIndex] = replacement.equals("") ? true : false;
			}
			else { // T1.
				String primitive;
				int replaceIndex = (int) replace.getIndex();
				if (replace.getLexeme() != null) { // L1, T1.
					// Gets the primitive from Replace and queries the dictionary for a replacement.
					primitive = replace.getLexeme();
				} else { // L0, T1.
					// Gets the primitive from the sentence and queries the dictionary for a replacement.
					//primitive = sentence.getTokens().get((int) replace.getIndex() + lower) .getPrimitive();
					primitive = tokens[replaceIndex].getPrimitive();
				}
				
				// @ wildcard from CoGrOO 1.0 is tricky, very tricky.
				// The idea here is that if there was a @ in the suggestion, we must get the gender or the
				// number of that particular token in order to correctly query the dictionary.
				
				TagMask tagMask = new TagMask();
				int index = 0;
				TagMask cloneTagMask;
				MorphologicalTag morphologicalTag = null;
				if(replace.getTagReference() != null)
				{
					tagMask = replace.getTagReference().getTagMask();
					index = (int) replace.getTagReference().getIndex();
					morphologicalTag = tokens[index].getMorphologicalTag();
				}
				else if( replace.getReference() != null)
				{
					tagMask = new TagMask();
					index = (int) replace.getReference().getIndex() + baseIndex;
					morphologicalTag = sentence.getTokens().get(index).getMorphologicalTag();
				}
				
				cloneTagMask = TagMaskUtils.clone(tagMask);
				
				// Gets the morphological tag of the referred token.
				
				if( replace.getReference() != null )
				{
					cloneTagMask = RuleUtils.createTagMaskFromReference(replace.getReference(), morphologicalTag, null, null);
				}
				else
				{
					if (cloneTagMask.getGender() == Gender.NEUTRAL) {
						Gender gender = morphologicalTag.getGenderE();
						if (Gender.MALE.equals(gender)) {
							cloneTagMask.setGender(Gender.MALE);
						} else if (Gender.FEMALE.equals(gender)) {
							cloneTagMask.setGender(Gender.FEMALE);
						}
					}
					if (cloneTagMask.getNumber() == Number.NEUTRAL) {
						Number number = morphologicalTag.getNumberE();
						if (Number.SINGULAR.equals(number)) {
							cloneTagMask.setNumber(Number.SINGULAR);
						} else if (Number.PLURAL.equals(number)) {
							cloneTagMask.setNumber(Number.PLURAL);
						}
					}
				}
				
				
				String[] flexArr = dictionary.getInflectedPrimitive(primitive, cloneTagMask, false); // Can be empty.
				String flex = getBestFlexedWord(flexArr, sentence.getTokens().get(replaceIndex + lower), cloneTagMask);
				
				
				if (flex.equals("")) {
					reject = true;
				} else {
					// This workaround is so lame...
					flex = SuggestionBuilder.discardBeginningHyphen(flex);
					flex = SuggestionBuilder.useCasedString(mistakenTokensAsString[(int) replace.getIndex()], flex);
					mistakenTokensAsString[(int) replace.getIndex()] = flex;
				}
			}
		}
		
		if (!reject) { // If not reject, there's still hope... if reject, do not swap nor concatenate.
			// Swaps.
			for (Swap swap : suggestion.getSwap()) {
				int a = (int) swap.getA();
				int b = (int) swap.getB();
				String temp = SuggestionBuilder.discardBeginningHyphen(mistakenTokensAsString[a]);
				mistakenTokensAsString[a] = SuggestionBuilder.discardBeginningHyphen(mistakenTokensAsString[b]);
				mistakenTokensAsString[b] = temp;
			}
			
			// Replaces if the lexeme matches with the mapping key.
			for (ReplaceMapping replaceMapping : suggestion.getReplaceMapping()) {
				long index = replaceMapping.getIndex();
				if (replaceMapping.getKey().equals(mistakenTokensAsString[(int) index].toLowerCase())) {
					mistakenTokensAsString[(int) index] = SuggestionBuilder.useCasedString(mistakenTokensAsString[(int) index], replaceMapping.getValue());
				}
			}
			
			// Concatenates the suggestions to obtain a single string.
			String suggestionAsString = "";
			for (int i = 0; i < mistakenTokensAsString.length; i++) {
				if (mistakenTokensAsString[i].startsWith("-") || replacedByEmptyString[i]) {
					suggestionAsString += mistakenTokensAsString[i];
				} else {
					suggestionAsString += " " + mistakenTokensAsString[i];
				}
			}
			// Adds this suggestion to the suggestions determined so far.
			return suggestionAsString.trim();
		}
		return null;
	}
	
//	/**
//	 * Gets the tokens from the <code>tokens</code> array which are between <code>lower</code> and
//	 * <code>upper</code> positions.
//	 * 
//	 * @param tokens
//	 *            the array from which the slice will be taken
//	 * @param lower
//	 *            the index of the first token to be included in the slice
//	 * @param upper
//	 *            the index of the last token to be included in the slice
//	 * @return an array containing the desired tokens
//	 */
//	private static String[] tokensSubArrayAsString(List<Token> tokens, int lower, int upper) {
//		String[] subArray = new String[upper - lower + 1];
//		for (int i = lower; i <= upper; i++) {
//			subArray[i - lower] = ((TokenCogroo)tokens.get(i)).getLexeme();
//		}
//		return subArray;
//	}
	
	private static String[] tokensSubArrayAsString(Token[] tokens)
	{
		String[] subArray = new String[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			subArray[i] = ((TokenCogroo)tokens[i]).getLexeme();
		}	
		return subArray;
	}
	
	/**
	 * Gets the tokens from the <code>tokens</code> array which are between <code>lower</code> and
	 * <code>upper</code> positions.
	 * 
	 * @param sentence
	 *            the sentence containing the desired tokens
	 * @param lower
	 *            the index of the first token to be included in the slice
	 * @param upper
	 *            the index of the last token to be included in the slice
	 * @return an array containing the desired tokens
	 */
	private static Token[] tokensSubArray(Sentence sentence, int lower, int upper, boolean considerChunk) {
		List<Token> rootTokens = sentence.getTokens().subList(lower, upper+1);
		if(considerChunk)
		{
			List<Token> resp = new ArrayList<Token>(rootTokens.size());
			for (int i = 0; i < rootTokens.size(); i++) {
				resp.addAll(getTokensRecursively(rootTokens.get(i)));	
			}
			return rootTokens.toArray(new Token[rootTokens.size()]);
		}
		
		return rootTokens.toArray(new Token[rootTokens.size()]);
	}
	
	private static List<Token> getTokensRecursively(Token rootToken)
	{
		List<Token> tl = new ArrayList<Token>();
		if(rootToken.getChunk() != null && rootToken.getChunk().getTokens().size() > 1)
		{
			//for (Token token : rootToken.getChunk().getTokens()) {
				//if(token != rootToken)
					tl.addAll(rootToken.getChunk().getTokens());
			//}
			return tl;
		}
		
		tl.add(rootToken);
		return tl;
		
	}
	
	/**
	 * Removes the beginning hyphen from the string, if any.
	 * 
	 * @param string
	 *            the string that will have the beginning hyphen removed
	 * @return the string without the beginning hyphen
	 */
	private static String discardBeginningHyphen(String string) {
		String noHyphenString = string;
		if (string.startsWith("-")) {
			noHyphenString = string.substring(1); // Oh well, just throw away the awful "-". No one will ever notice...
		}
		return noHyphenString;
	}
	
	/**
	 * Checks the case of the first char from <code>replaceable</code> and changes the first char from the
	 * <code>replacement</code> accordingly.
	 * 
	 * @param replaceable
	 *            the string that will be replaced
	 * @param replacement
	 *            the string that will be used to replace the <code>replaceable</code>
	 * @return the replacement, beginning with upper case if the <code>replaceable</code> begins too or
	 *         lower case, if not
	 */
	private static String useCasedString(String replaceable, String replacement) {
		String replacementCased = replacement;
		if (replacement.length() > 1) {
			// If the first char of the replaceable lexeme is upper case...
			if (Character.isUpperCase(replaceable.charAt(0))) {
				// ... so must be its replacement.
				replacementCased = Character.toUpperCase(replacement.charAt(0)) + replacement.substring(1);
			} else {
				// ... the replacement must be lower case.
				replacementCased = Character.toLowerCase(replacement.charAt(0)) + replacement.substring(1);
			}
		} else if (replacement.length() == 1) {
			// If the first char of the replaceable lexeme is upper case...
			if (Character.isUpperCase(replaceable.charAt(0))) {
				// ... so must be its replacement.
				replacementCased = String.valueOf(Character.toUpperCase(replacement.charAt(0)));
			} else {
				// ... the replacement must be lower case.
				replacementCased = String.valueOf(Character.toLowerCase(replacement.charAt(0)));
			}
		}
		return replacementCased;
	}
	
	private static String getBestFlexedWord(String[] flex, Token original, TagMask tagMask)
	{
		
		if(flex.length > 1)
		{
			// lets choose the most common
			HashMap<String, Integer> visitedLexemes = new HashMap<String, Integer>();
			
			for (String pair : flex) {
				String lex = pair.toLowerCase();
				if(!visitedLexemes.containsKey(lex))
					visitedLexemes.put(lex, new Integer(0));
				else
					visitedLexemes.put(lex, new Integer(visitedLexemes.get(lex) + 1));
			}
			String selectedLexeme = null;
			int bestValue = 0;
			for (String lex : visitedLexemes.keySet()) {
				if(bestValue <= visitedLexemes.get(lex))
				{
					bestValue = visitedLexemes.get(lex);
					selectedLexeme = lex;
				}
			}
			return selectedLexeme;
			
		}
		else if(flex.length == 1)
		{
			return flex[0];
		}

		// lenght 0
		return "";
		
	}
	
}
