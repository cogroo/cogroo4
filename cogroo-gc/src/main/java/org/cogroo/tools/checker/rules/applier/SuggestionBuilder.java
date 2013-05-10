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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import opennlp.tools.util.Span;

import org.cogroo.entities.Chunk;
import org.cogroo.entities.Sentence;
import org.cogroo.entities.SyntacticChunk;
import org.cogroo.entities.Token;
import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.entities.impl.TokenCogroo;
import org.cogroo.tools.checker.rules.dictionary.CogrooTagDictionary;
import org.cogroo.tools.checker.rules.model.Boundaries;
import org.cogroo.tools.checker.rules.model.Rule;
import org.cogroo.tools.checker.rules.model.Rule.Method;
import org.cogroo.tools.checker.rules.model.Suggestion;
import org.cogroo.tools.checker.rules.model.Suggestion.Replace;
import org.cogroo.tools.checker.rules.model.Suggestion.ReplaceMapping;
import org.cogroo.tools.checker.rules.model.Suggestion.Swap;
import org.cogroo.tools.checker.rules.model.TagMask;
import org.cogroo.tools.checker.rules.model.TagMask.Gender;
import org.cogroo.tools.checker.rules.model.TagMask.Number;
import org.cogroo.tools.checker.rules.util.RuleUtils;
import org.cogroo.tools.checker.rules.util.TagMaskUtils;

import com.google.common.base.Optional;

/**
 * This class makes suggestions to correct the mistakes.
 * 
 * @author Marcelo Suzumura
 * @author Fábio Wang Gusukuma
 * @author William Colen
 * @version $Id: SuggestionBuilder.java 400 2007-04-19 02:36:40Z msuzumura $
 */
public class SuggestionBuilder {
  
  private CogrooTagDictionary dictionary;
  
  public SuggestionBuilder(CogrooTagDictionary dict) {
    this.dictionary = dict;
  }
  
  public String[] getTokenSuggestions(List<Token> matched, Token next, Rule rule) {
    // Each suggestionsAsString position will contain a suggestion.
    Set<String> suggestionsAsString = new HashSet<String>();
    for (Suggestion suggestion : rule.getSuggestion()) {
      String s = getTokenSuggestions(matched,
          next, suggestion, rule.getBoundaries());
      if (s != null && s.length() > 0)
        suggestionsAsString.add(s);
    }

    return suggestionsAsString.toArray(new String[suggestionsAsString.size()]);
  }
  
  public String[] getSyntacticSuggestions(List<SyntacticChunk> matched, Token next, Rule rule) {
    // Each suggestionsAsString position will contain a suggestion.
    Set<String> suggestionsAsString = new HashSet<String>();
    for (Suggestion suggestion : rule.getSuggestion()) {
      String s = getSyntacticSuggestions(matched,
          next, suggestion, rule.getBoundaries());
      if (s != null && s.length() > 0)
        suggestionsAsString.add(s);
    }

    return suggestionsAsString.toArray(new String[suggestionsAsString.size()]);
  }
  
  private String getTokenSuggestions(List<Token> matched, Token next,
      Suggestion suggestion, Boundaries boundaries) {
    
    // Gets only the tokens that are referred by the mistake. It considers chunks and subjverb!
    Token[] underlinedTokens = SuggestionBuilder.tokensSubArray(matched, boundaries);
    
    String[] mistakenTokensAsString = SuggestionBuilder.tokensSubArrayAsString(underlinedTokens);
    
    // Tells if a token was replaced by an empty string.
    boolean replacedByEmptyString[] = new boolean[mistakenTokensAsString.length];
    
    // If can not determine an inflection for the replacement, reject the suggestion.
    boolean reject = false;
    
    // Replaces.
    /*
        <Replace> work as follows.
        Lexeme  TagReference    Action
        0       0               does nothing
        0       1               gets primitive from the token in the sentence and queries for an inflection
        1       0               replaces for the lexeme
        1       1               gets primitive (<Lexeme>) from the <Replace> and queries for an inflection
    */
    for (Replace replace : suggestion.getReplace()) {
        if (replace.getTagReference() == null && replace.getLexeme() != null) { // L1, T0.
            // i.e., replacing a token with the given lexeme.
            // Beware of upper case...
            String replacement = replace.getLexeme();
            int replaceIndex = (int) replace.getIndex();
            mistakenTokensAsString[replaceIndex] = RuleUtils.useCasedString(mistakenTokensAsString[(int) replace.getIndex()], replacement);
            replacedByEmptyString[replaceIndex] = replacement.equals("") ? true : false;
        }
        else { // T1.
            String[] primitive;
            int replaceIndex = (int) replace.getIndex();
            if (replace.getLexeme() != null) { // L1, T1.
                // Gets the primitive from Replace and queries the dictionary for a replacement.
                String[] arr = {replace.getLexeme()};
                primitive = arr;
            } else { // L0, T1.
                // Gets the primitive from the sentence and queries the dictionary for a replacement.
                  primitive = matched.get((int) replace.getIndex()) .getPrimitive();
            }
            
            // @ wildcard from CoGrOO 1.0 is tricky, very tricky.
            // The idea here is that if there was a @ in the suggestion, we must get the gender or the
            // number of that particular token in order to correctly query the dictionary.
            
            TagMask tagMask = new TagMask();
            int index = 0;
            TagMask cloneTagMask;
            MorphologicalTag refMorphTag = null;
            if(replace.getTagReference() != null)
            {
                tagMask = replace.getTagReference().getTagMask();
                index = (int) replace.getTagReference().getIndex();
                refMorphTag = underlinedTokens[index].getMorphologicalTag();
            }
            else if( replace.getReference() != null)
            {
                tagMask = new TagMask();
                index = (int) replace.getReference().getIndex();
                refMorphTag = matched.get(index).getMorphologicalTag();
            }
            
            cloneTagMask = TagMaskUtils.clone(tagMask);
            
            // Gets the morphological tag of the referred token.
            
            if( replace.getReference() != null )
            {
                cloneTagMask = RuleUtils.createTagMaskFromReference(replace.getReference(), refMorphTag, null, null);
            }
            else
            {
                if (cloneTagMask.getGender() == Gender.NEUTRAL) {
                    Gender gender = refMorphTag.getGenderE();
                    if (Gender.MALE.equals(gender)) {
                        cloneTagMask.setGender(Gender.MALE);
                    } else if (Gender.FEMALE.equals(gender)) {
                        cloneTagMask.setGender(Gender.FEMALE);
                    }
                }
                if (cloneTagMask.getNumber() == Number.NEUTRAL) {
                    Number number = refMorphTag.getNumberE();
                    if (Number.SINGULAR.equals(number)) {
                        cloneTagMask.setNumber(Number.SINGULAR);
                    } else if (Number.PLURAL.equals(number)) {
                        cloneTagMask.setNumber(Number.PLURAL);
                    }
                }
            }
            
            Token originalToken;
            originalToken = underlinedTokens[replaceIndex];
            
            RuleUtils.completeMissingParts(cloneTagMask, originalToken.getMorphologicalTag());
            
            List<String> flexList = new ArrayList<String>();
            if(primitive != null) {
              for (String p : primitive) {
                String[] farr = dictionary.getInflectedPrimitive(p, cloneTagMask, false);
                if(farr != null) {
                  flexList.addAll(Arrays.asList(farr));
                }
              }
            }
            
            String[] flexArr = flexList.toArray(new String[flexList.size()]); // Can be empty.
            String flex = getBestFlexedWord(flexArr, originalToken, cloneTagMask);
            
            
            if (flex.equals("")) {
                reject = true;
            } else {
                // This workaround is so lame...
                flex = SuggestionBuilder.discardBeginningHyphen(flex);
                flex = RuleUtils.useCasedString(mistakenTokensAsString[(int) replace.getIndex()], flex);
                
                mistakenTokensAsString[(int) (replace.getIndex())] = flex;
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
                mistakenTokensAsString[(int) index] = RuleUtils.useCasedString(mistakenTokensAsString[(int) index], replaceMapping.getValue());
            }
        }
        
        // if the last token was a contraction, we should keep the other part of it in the suggestion
        if(replacedByEmptyString[replacedByEmptyString.length -  1]) {
          if(next != null) {
            Token removed = matched.get(matched.size() - 1);
            
            if(next.getSpan().equals(removed.getSpan())) {
              mistakenTokensAsString[replacedByEmptyString.length -  1] = next.getLexeme();
              replacedByEmptyString[replacedByEmptyString.length -  1] = false;
            }
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
  
	
	private String getSyntacticSuggestions(List<SyntacticChunk> matched, Token next,
	      Suggestion suggestion, Boundaries boundaries) {
		Token[] matchedTokens = extractTokens(matched);
		// Gets only the tokens that are referred by the mistake. It considers chunks and subjverb!
		Token[] underlinedTokens = tokensSubArraySynt(matched, boundaries);
		
		Span[] tokenIndex = tokenIndex(matched);
		
		String[] mistakenTokensAsString = SuggestionBuilder.tokensSubArrayAsString(underlinedTokens);
		
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
				// get the tokens of that chunk and mark as removed
				Span p = tokenIndex[replaceIndex];
				for (int i = p.getStart() + 1; i < p.getEnd(); i++) {
				  mistakenTokensAsString[i] = "";
				  replacedByEmptyString[i] = true;
                }
				
				mistakenTokensAsString[p.getStart()] = RuleUtils.useCasedString(mistakenTokensAsString[(int) replace.getIndex()], replacement);
				replacedByEmptyString[p.getStart()] = replacement.equals("") ? true : false;
			}
			else { // T1.
				List<String[]> primitives = new ArrayList<String[]>();
				int replaceIndex = (int) replace.getIndex();
				if (replace.getLexeme() != null) { // L1, T1.
					// Gets the primitive from Replace and queries the dictionary for a replacement.
				    String[] arr = {replace.getLexeme()};
					primitives.add(arr);
				} else { // L0, T1.
					// Gets the primitive from the sentence and queries the dictionary for a replacement.
				  for (Token toks : matched.get((int) replace.getIndex()).getTokens()) {
                    primitives.add(toks.getPrimitive());
                  }
				}
				
				// @ wildcard from CoGrOO 1.0 is tricky, very tricky.
				// The idea here is that if there was a @ in the suggestion, we must get the gender or the
				// number of that particular token in order to correctly query the dictionary.
				
				TagMask tagMask = new TagMask();
				int index = 0;
				TagMask cloneTagMask;
				MorphologicalTag refMorphTag = null;
				if(replace.getTagReference() != null)
				{
					tagMask = replace.getTagReference().getTagMask();
					index = (int) replace.getTagReference().getIndex();
					refMorphTag = matched.get(index).getMorphologicalTag();
				}
				else if( replace.getReference() != null)
				{
					tagMask = new TagMask();
					index = (int) replace.getReference().getIndex();
					refMorphTag = matched.get(index).getMorphologicalTag();
				}
				
				cloneTagMask = TagMaskUtils.clone(tagMask);
				
				// Gets the morphological tag of the referred token.
				
				if( replace.getReference() != null )
				{
					cloneTagMask = RuleUtils.createTagMaskFromReference(replace.getReference(), refMorphTag, null, null);
				}
				else
				{
					if (cloneTagMask.getGender() == Gender.NEUTRAL) {
						Gender gender = refMorphTag.getGenderE();
						if (Gender.MALE.equals(gender)) {
							cloneTagMask.setGender(Gender.MALE);
						} else if (Gender.FEMALE.equals(gender)) {
							cloneTagMask.setGender(Gender.FEMALE);
						}
					}
					if (cloneTagMask.getNumber() == Number.NEUTRAL) {
						Number number = refMorphTag.getNumberE();
						if (Number.SINGULAR.equals(number)) {
							cloneTagMask.setNumber(Number.SINGULAR);
						} else if (Number.PLURAL.equals(number)) {
							cloneTagMask.setNumber(Number.PLURAL);
						}
					}
				}
				
				List<Token> originalTokens = matched.get(replaceIndex).getTokens();
				
				String[] fixed = new String[matchedTokens.length];
				
				if(primitives != null) {
				  for (int i = 0; i < primitives.size(); i++) {
				    String[] primitive = primitives.get(i);
				    Token ot = originalTokens.get(i);
				    
				    TagMask tm = TagMaskUtils.clone(cloneTagMask);
				    RuleUtils.completeMissingParts(tm, ot.getMorphologicalTag());
				    
				    List<String> flexList = new ArrayList<String>();
	                  for (String p : primitive) {
	                    String[] farr = dictionary.getInflectedPrimitive(p, tm, false);
	                    if(farr != null) {
	                      flexList.addAll(Arrays.asList(farr));
	                    }
	                  } 
	                  
	                    String[] flexArr = flexList.toArray(new String[flexList.size()]); // Can be empty.
	                    
	                    String f = getBestFlexedWord(flexArr, ot, cloneTagMask);
	                    f = SuggestionBuilder.discardBeginningHyphen(f);
	                    f = RuleUtils.useCasedString(ot.getLexeme(), f);
	                    
//	                    mistakenTokensAsString[replaceIndex + i] = f;
	                    fixed[tokenIndex[replaceIndex].getStart() + i] = f;
                  }
				}
				int start = tokenIndex[boundaries.getLower()].getStart();
				for (int i = start; i < start + mistakenTokensAsString.length; i++) {
                  if(fixed[i] != null) {
                    mistakenTokensAsString[i - start] = fixed[i];
                  }
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
					mistakenTokensAsString[(int) index] = RuleUtils.useCasedString(mistakenTokensAsString[(int) index], replaceMapping.getValue());
				}
			}
	        
	        // if the last token was a contraction, we should keep the other part of it in the suggestion
//	        if(replacedByEmptyString[replacedByEmptyString.length -  1]) {
//	          if(next != null) {
//	            Token removed = matched.get(matched.size() - 1);
//	            
//	            if(next.getSpan().equals(removed.getSpan())) {
//	              mistakenTokensAsString[replacedByEmptyString.length -  1] = next.getLexeme();
//	              replacedByEmptyString[replacedByEmptyString.length -  1] = false;
//	            }
//	          }
//	        }
			
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
	
  private Token[] extractTokens(List<SyntacticChunk> matched) {
    List<Token> out = new ArrayList<Token>();
    for (SyntacticChunk sc : matched) {
      out.addAll(sc.getTokens());
    }
    return out.toArray(new Token[out.size()]);
  }

  private Span[] tokenIndex(List<SyntacticChunk> matched) {
    List<Span> out = new ArrayList<Span>();
    int count = 0;
    for (SyntacticChunk syntacticChunk : matched) {
      int start = count;
      int end = count + syntacticChunk.getTokens().size(); 
      out.add(new Span(start, end));
      count += syntacticChunk.getTokens().size();
    }
    return out.toArray(new Span[out.size()]);
  }

  private static SyntacticChunk[] getSyntacticChunks(Token[] tokens) {
	  
	  Stack<SyntacticChunk> stack = new Stack<SyntacticChunk>();
	  
	    for (Token token : tokens) {
          SyntacticChunk s = token.getSyntacticChunk();
          if(stack.isEmpty() || !stack.lastElement().equals(s)) {
            stack.add(s);
          }
        }
	  
	  return stack.toArray(new SyntacticChunk[stack.size()]);
  }

  private static String[] tokensSubArrayAsString(Token[] tokens)
	{
		String[] subArray = new String[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
		    if(tokens[i] != null) {
		      subArray[i] = ((TokenCogroo)tokens[i]).getLexeme();
		    } else {
		      subArray[i] = null;
		    }
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
	
  private static Token[] tokensSubArraySynt(List<SyntacticChunk> matched,
      Boundaries boundaries) {
    int start = boundaries.getLower();
    int end = matched.size() + boundaries.getUpper();
    List<SyntacticChunk> syntChunks = matched.subList(start, end);

    List<Token> out = new ArrayList<Token>();
    for (SyntacticChunk sc : syntChunks) {
      out.addAll(sc.getTokens());
    }
    return out.toArray(new Token[out.size()]);
  }
	
  private static Token[] tokensSubArray(List<Token> tokens, Boundaries boundaries) {
    int start = boundaries.getLower();
    int end = tokens.size() + boundaries.getUpper();
    List<Token> rootTokens = tokens.subList(start, end);
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
