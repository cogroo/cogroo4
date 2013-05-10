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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.util.Span;

import org.apache.log4j.Logger;
import org.cogroo.entities.Chunk;
import org.cogroo.entities.Mistake;
import org.cogroo.entities.Sentence;
import org.cogroo.entities.SyntacticChunk;
import org.cogroo.entities.Token;
import org.cogroo.entities.TokenGroup;
import org.cogroo.entities.impl.ChunkCogroo;
import org.cogroo.entities.impl.ChunkTag;
import org.cogroo.entities.impl.MistakeImpl;
import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.entities.impl.SyntacticTag;
import org.cogroo.entities.impl.TokenCogroo;
import org.cogroo.tools.checker.RuleDefinition;
import org.cogroo.tools.checker.TypedChecker;
import org.cogroo.tools.checker.rules.dictionary.CogrooTagDictionary;
import org.cogroo.tools.checker.rules.util.RuleUtils;
import org.cogroo.tools.checker.rules.util.RulesProperties;

import org.cogroo.tools.checker.rules.model.Boundaries;
import org.cogroo.tools.checker.rules.model.Element;
import org.cogroo.tools.checker.rules.model.Mask;
import org.cogroo.tools.checker.rules.model.PatternElement;
import org.cogroo.tools.checker.rules.model.Rule;
import org.cogroo.tools.checker.rules.model.Rule.Method;
import org.cogroo.tools.checker.rules.model.TagMask.ChunkFunction;
import org.cogroo.tools.checker.rules.model.TagMask.SyntacticFunction;
import org.cogroo.tools.checker.rules.model.TagMask;

/**
 * Applies error rules to a {@link Sentence} object.
 * 
 * @author Marcelo Suzumura (base version and appliers)
 * @author Fábio Wang Gusukuma (phrase local and subject-verb appliers)
 * @author William Colen
 */
public final class RulesApplier implements TypedChecker {
	
	private SuggestionBuilder suggestionBuilder;

  public RulesApplier(RulesTreesProvider rulesTreesProvider, CogrooTagDictionary dictionary) {
		this.rulesTreesProvider = rulesTreesProvider;
		this.dictionary = dictionary;
		this.suggestionBuilder = new SuggestionBuilder(this.dictionary);
	}
	
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(RulesApplier.class);

	private static final String ID_PREFIX = "xml:";

  private static final Object OUT_OF_BOUNDS = "%%OUT_OF_BOUNDS%%";
	
	private final Set<String> ignoredRules = new HashSet<String>();
	
	private final RulesTreesProvider rulesTreesProvider;
	
	private CogrooTagDictionary dictionary;
	
	/**
	 * Applies all active rules described in Rules.xml given a sentence properly tokenized, tagged, chunked
	 * and shallow parsed.
	 * 
	 * @param sentence
	 *            a tokenized, tagged, chunked and shallow parsed sentence.
	 * @param dictionary
	 *            a word and tag dictionary
	 * @return a list containing all the mistakes found in the sentence. Each mistake can be localized between
	 *         the character indexes given in the span field of the mistake.
	 */
	public List<Mistake> check(Sentence sentence) {
		long start = 0; 
		if(LOGGER.isDebugEnabled()) {
			start = System.nanoTime();
		}
		
		// Insert two empty tokens at the sentence start and end
		List<Token> tokens = new ArrayList<Token>();
		Token empty1 = new TokenCogroo("%%OUT_OF_BOUNDS%%", new Span(0, 0));
		empty1.setMorphologicalTag(new MorphologicalTag());
		ChunkTag ct = new ChunkTag();
		ct.setChunkFunction(ChunkFunction.OTHER);
		empty1.setChunkTag(ct);
		tokens.add(empty1);
		tokens.addAll(sentence.getTokens());
		Token empty2 = new TokenCogroo("%%OUT_OF_BOUNDS%%", new Span(0, 0));
		empty2.setMorphologicalTag(new MorphologicalTag());
		empty2.setChunkTag(ct);
		tokens.add(empty2);
		sentence.setTokens(tokens);
		
		List<Chunk> chunkz = new ArrayList<Chunk>();
		Chunk chunk1 = new ChunkCogroo(Collections.singletonList(empty1), 0);
        chunk1.setMorphologicalTag(new MorphologicalTag());
		empty1.setChunk(chunk1);
		chunkz.add(chunk1);
		chunkz.addAll(sentence.getChunks());
	    Chunk chunk2 = new ChunkCogroo(Collections.singletonList(empty2), 0);
	    chunk2.setMorphologicalTag(new MorphologicalTag());
	    empty2.setChunk(chunk2);
	    chunkz.add(chunk2);
	    sentence.setChunks(chunkz);
	    
	    
	    List<SyntacticChunk> synts = new ArrayList<SyntacticChunk>();
	    SyntacticChunk synt1 = new SyntacticChunk(Collections.singletonList(chunk1));
        SyntacticTag st = new SyntacticTag();
        st.setSyntacticFunction(SyntacticFunction.NONE);
	    synt1.setSyntacticTag(st);
	    empty1.setSyntacticChunk(synt1);
	    synts.add(synt1);
	    synts.addAll(sentence.getSyntacticChunks());
	    SyntacticChunk synt2 = new SyntacticChunk(Collections.singletonList(chunk1));
        synt2.setSyntacticTag(st);
        empty2.setSyntacticChunk(synt2);
        synts.add(synt2);
        sentence.setSyntacticChunks(synts);
	    
        
		// mistakes will hold mistakes found in the sentence.
		List<Mistake> mistakes = new ArrayList<Mistake>();

        // rules will hold the tree being used to seek for mistakes.
        RulesTree rulesTree;
        
		if(RulesProperties.APPLY_LOCAL) {
    		// Seeks for errors that can occur anywhere in the sentence (general).
    		rulesTree = this.rulesTreesProvider.getTrees().getGeneral();
    		// For each token in the sentence.
    		for (int i = 0; i < sentence.getTokens().size(); i++) {
    			// For each token, gets back to the initial state (hence 0).
    			List<State> nextStates = rulesTree.getRoot().getNextStates();
    			// i is the index of the token that began the rule applying process.
    			mistakes = this.getMistakes(mistakes, nextStates, sentence, i, i, new ArrayList<Token>(), sentence);
    		}
		}
		
		// remove aux tokens
		sentence.setTokens(sentence.getTokens().subList(1, sentence.getTokens().size() - 1));
		
		if(RulesProperties.APPLY_PHRASE_LOCAL) {
    		// Seeks for errors inside a chunk (phrase local).
    		rulesTree = this.rulesTreesProvider.getTrees().getPhraseLocal();
    		// For each chunk in the sentence.
    		List<Chunk> chunks = sentence.getChunks();
    		for (int i = 0; i < chunks.size(); i++) {
    			for (int j = 0; j < chunks.get(i).getTokens().size(); j++) {
    				// For each token, gets back to the initial state (hence 0).
    				List<State> nextStates = rulesTree.getRoot().getNextStates();
    				// j is the index of the token that began the rule applying process.
    				mistakes = this.getMistakes(mistakes, nextStates, chunks.get(i), j, j, new ArrayList<Token>(), sentence);
    			}
    		}
		}
		
		if(RulesProperties.APPLY_SUBJECT_VERB) {
    		// Seeks for errors between a subject and a main verb.
    		rulesTree = this.rulesTreesProvider.getTrees().getSubjectVerb();
    		// For each chunk in the sentence.
    		List<SyntacticChunk> syntacticChunks = sentence.getSyntacticChunks();
    		for (int i = 0; i < syntacticChunks.size(); i++) {
    			List<State> nextStates = rulesTree.getRoot().getNextStates();
    			mistakes = this.getMistakes(mistakes, nextStates, syntacticChunks, i, i, new ArrayList<SyntacticChunk>(), sentence);
    		}
		}
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Rules applied in " + (System.nanoTime() - start) / 1000 + "us");
		}
		filterIgnoredRules(mistakes);
		
		return mistakes;
	}

	/**
	 * A recursive method that iterates the sentence given a base token group (sentence or chunk). Used to
	 * match general and phrase local rules.
	 * 
	 * @param mistakes
	 *            a list of mistakes found in the process of checking the sentence
	 * @param currentStates
	 *            the applier will check if these states match the current token
	 * @param tokenGroup
	 *            can be a sentence or a chunk (classes that implement the interface TokenGroup)
	 * @param baseTokenIndex
	 *            the index of the token in which the process of searching for mistakes began
	 * @param currentTokenIndex
	 *            the index of the current token group
	 * @param sentence
	 *            the complete sentence, used to get the location of the mistake counted by chars inside the
	 *            sentence
	 * @param dictionary
	 *            a word and tag dictionary.
	 * @return the mistakes in the parameter <code>mistakes</code> plus the mistakes found in this
	 *         invocation, if any
	 */
	private List<Mistake> getMistakes(List<Mistake> mistakes, List<State> currentStates, TokenGroup tokenGroup, int baseTokenIndex, int currentTokenIndex, ArrayList<Token> matched, Sentence sentence) {

	    int offset = 0;
        if (tokenGroup instanceof Chunk) {
          offset = ((Chunk) tokenGroup).getFirstToken();
        }
	    
		for (State state : currentStates) {
		    PatternElement patternElement = state.getElement();
		    Token token = tokenGroup.getTokens().get(currentTokenIndex);
			boolean tokenAndElementMatched = this.match(token, patternElement, baseTokenIndex + offset, sentence);
			if (tokenAndElementMatched) {
			  
		        // need to clone because due to recursive implementation
		        ArrayList<Token> matchedClone = cloneList(matched);
		        matchedClone.add(token);
			    
				if (state instanceof AcceptState) {
					// Got a mistake!
					Rule rule = ((AcceptState) state).getRule();
					// The mistake is located between the tokens indicated by lower and upper.
					int lower = baseTokenIndex + rule.getBoundaries().getLower();
					int upper = currentTokenIndex + rule.getBoundaries().getUpper();
					lower += offset;
					upper += offset;
					// Pointing the mistake location using the chars in the sentence.
					int lowerCountedByChars = sentence.getTokens().get(lower).getSpan().getStart();
					int upperCountedByChars = sentence.getTokens().get(upper).getSpan().getEnd();
					// Suggestions.
                    String[] suggestions = new String[0];
	                Token next = null;    
                    if(tokenGroup.getTokens().size() > currentTokenIndex + 1) {
                      next = tokenGroup.getTokens().get(currentTokenIndex + 1);
	                }
                    try {
                      suggestions = suggestionBuilder.getTokenSuggestions(matchedClone, next, rule);
					} catch(NullPointerException e) {
					  LOGGER.error("Failed to apply rule " + rule.getId() + " in: " + sentence.getSentence(), e);
					}
					
					Mistake mistake = new MistakeImpl(ID_PREFIX + rule.getId(), getPriority(rule), rule.getMessage(), rule.getShortMessage(), suggestions, lowerCountedByChars + sentence.getOffset(), upperCountedByChars + sentence.getOffset(), rule.getExample(), sentence.getSentence());
					mistakes.add(mistake);
				} else if (currentTokenIndex + 1 < tokenGroup.getTokens().size()) {
					// Keep looking: recurse.
					this.getMistakes(mistakes, state.getNextStates(), tokenGroup, baseTokenIndex, currentTokenIndex + 1, matchedClone, sentence);
				}
			} else if(isOptional(patternElement)) {
			  // it is valid only if the next is valid here!
			  // just keep looking without movin to the next token
			  this.getMistakes(mistakes, state.getNextStates(), tokenGroup, baseTokenIndex, currentTokenIndex, matched, sentence);
			}
		}
		return mistakes;
	}

  private boolean isOptional(PatternElement patternElement) {
    return patternElement.isOptional() != null && patternElement.isOptional();
  }

  private int getPriority(Rule rule) {
	  if(rule.getPriority() != null) 
	    return rule.getPriority().intValue();
    return (int)(getPriority() - rule.getId());
  }

  /**
	 * A recursive method that iterates the sentence given a base chunk. Used to match subject-verb rules.
	 * 
	 * @param mistakes
	 *            a list of mistakes found in the process of checking the sentence
	 * @param currentStates
	 *            the applier will check if these states match the current token
	 * @param syntacticChunks
	 *            an array of chunks
	 * @param baseChunkIndex
	 *            the index of the chunk in which the process of searching for mistakes began
	 * @param currentChunkIndex
	 *            the index of the current chunk
	 * @param sentence
	 *            the complete sentence, used to get the location of the mistake counted by chars inside the
	 *            sentence
	 * @return the mistakes in the parameter <code>mistakes</code> plus the mistakes found in this
	 *         invocation, if any
	 */
	private List<Mistake> getMistakes(List<Mistake> mistakes, List<State> currentStates, List<SyntacticChunk> syntacticChunks, int baseChunkIndex, int currentChunkIndex, ArrayList<SyntacticChunk> matched, Sentence sentence) {
        
        for (State state : currentStates) {
            
		    PatternElement patternElement = state.getElement();
		    SyntacticChunk sc = syntacticChunks.get(currentChunkIndex);
			boolean chunkAndElementMatched = this.match(sc, patternElement, baseChunkIndex, sentence);
			if (chunkAndElementMatched) {
			    // need to clone due to recursive implementation
	            ArrayList<SyntacticChunk> matchedClone = cloneList(matched);
	            matchedClone.add(sc);
	            
				if (state instanceof AcceptState) {
					// Got a mistake!
					Rule rule = ((AcceptState) state).getRule();
					// The mistake is located between the chunks indicated by lower and upper.
					// Gets the lower index by chars.
					Boundaries b = rule.getBoundaries();
					
					int start = matchedClone.get(b.getLower()).getTokens().get(0).getSpan().getStart();
					List<Token> lastChk = matchedClone.get(matchedClone.size() - 1 + b.getUpper()).getTokens();
					int end = lastChk.get(lastChk.size() - 1).getSpan().getEnd();
					
					// Suggestions.
                    String[] suggestions = suggestionBuilder.getSyntacticSuggestions(
                        matchedClone, null, rule);
                    Mistake mistake = new MistakeImpl(ID_PREFIX + rule.getId(),
                        getPriority(rule), rule.getMessage(), rule.getShortMessage(),
                        suggestions, start, end, rule.getExample(),
                        sentence.getSentence());
					mistakes.add(mistake);
				} else if (currentChunkIndex + 1 < syntacticChunks.size()) {
					// Keep looking: recurse.
					this.getMistakes(mistakes, state.getNextStates(), syntacticChunks, baseChunkIndex, currentChunkIndex + 1, matchedClone, sentence);
				}
			} else if(isOptional(patternElement)) {
              // need to clone due to recursive implementation
              ArrayList<SyntacticChunk> matchedClone = cloneList(matched);
              matchedClone.add(NullSyntacticChunk.instance());
              // it is valid only if the next is valid here!
              // just keep looking without movin to the next token
			  this.getMistakes(mistakes, state.getNextStates(), syntacticChunks, baseChunkIndex, currentChunkIndex, matchedClone, sentence);
            }
		}
		return mistakes;
	}
	
	private boolean match(Token token, PatternElement patternElement, int baseTokenIndex, Sentence sentence) {
		if(patternElement.getElement() != null)
			return match(token, patternElement.getElement(), baseTokenIndex, sentence);

		if( patternElement.getComposition().getAnd() != null )
		{
			List<PatternElement> l = patternElement.getComposition().getAnd().getPatternElement();
			for (PatternElement pe : l) {
				boolean match = match(token, pe, baseTokenIndex, sentence);
				if(!match)
					return false;
			}
			return true;
		}
		else
		{
			if( patternElement.getComposition().getOr() != null )
			{
				List<PatternElement> l = patternElement.getComposition().getOr().getPatternElement();
				for (PatternElement pe : l) {
					boolean match = match(token, pe, baseTokenIndex, sentence);
					if(match)
						return true;
				}
				
				return false;
			}
		}
		LOGGER.error("Shouldn't get here.");
		return false;
	}
	
	private boolean match(SyntacticChunk chunk, PatternElement patternElement, int baseTokenIndex, Sentence sentence) {
	    //System.out.println(RuleUtils.getPatternElementAsString(patternElement));
		if(patternElement.getElement() != null)
			return match(chunk, patternElement.getElement(), baseTokenIndex, sentence);

		if( patternElement.getComposition().getAnd() != null )
		{
			List<PatternElement> l = patternElement.getComposition().getAnd().getPatternElement();
			for (PatternElement pe : l) {
				boolean match = match(chunk, pe, baseTokenIndex, sentence);
				if(!match)
					return false;
			}
			return true;
		}
		else
		{
			if( patternElement.getComposition().getOr() != null )
			{
				List<PatternElement> l = patternElement.getComposition().getOr().getPatternElement();
				for (PatternElement pe : l) {
					boolean match = match(chunk, pe, baseTokenIndex, sentence);
					if(match)
						return true;
				}
				
				return false;
			}
		}
		LOGGER.error("Shouldn't get here.");
		return false;
	}
	
	/**
	 * Determines if a token is matched by a rule element.
	 * 
	 * @param token the token to be matched by the element
	 * @param element the element to be matched against the token
	 * @return <code>true</code> if there's a match, <code>false</code> otherwise
	 */
	private boolean match(Token token, Element element, int baseTokenIndex, Sentence sentence) {
		boolean match;
		boolean negated;
		// Sees if the mask must or not match.
		// Negated is optional, so it can be null, true or false.
		// If null, consider as false.
		if (element.isNegated() == null) {
			match = false;
			negated = false;
		} else {
			match = element.isNegated().booleanValue();
			negated = element.isNegated().booleanValue();
		}
		for (Mask mask : element.getMask()) {
			// If the token must match the mask.
			if (!negated) {
				// If not negated, match starts as false and just one match is needed to make it true.
				if (mask.getLexemeMask() != null && mask.getLexemeMask().equalsIgnoreCase(token.getLexeme())) {
					match = true;
				} else if (mask.getPrimitiveMask() != null && matchLemma(token, mask.getPrimitiveMask())) {
					match = true;
				} else if (mask.getTagMask() != null && token.getMorphologicalTag() != null) {
					match = match | token.getMorphologicalTag().matchExact(mask.getTagMask(), false);
				} else if (mask.getTagReference() != null && token.getMorphologicalTag() != null) {
					match = match | token.getMorphologicalTag().match(RuleUtils.createTagMaskFromReference(mask.getTagReference(), sentence, baseTokenIndex), false);
				} else if (mask.getOutOfBounds() != null && (baseTokenIndex == 0 || baseTokenIndex == sentence.getTokens().size() -1)) {
					match = false;
				}
			} else { // The token must NOT match the mask.
				// If negated, match starts as true and just one match is needed to make it false.
				if (mask.getLexemeMask() != null && mask.getLexemeMask().equalsIgnoreCase(token.getLexeme())) {
					match = false;
				} else if (mask.getPrimitiveMask() != null && matchLemma(token, mask.getPrimitiveMask())) {
                  match = false;
                } else if (mask.getTagMask() != null && token!=null && token.getMorphologicalTag() != null) {
					match = match & !token.getMorphologicalTag().matchExact(mask.getTagMask(),false);
				} else if (mask.getTagReference() != null && token!=null && token.getMorphologicalTag() != null) {
					match = match & !token.getMorphologicalTag().match(RuleUtils.createTagMaskFromReference(mask.getTagReference(), sentence, baseTokenIndex), false);
				} else if (mask.getOutOfBounds() != null && (baseTokenIndex == 0 || baseTokenIndex == sentence.getTokens().size() -1)) {
					match = false;
				}
			}
		}
		return match;
	}
	
  /**
	 * Determines if a chunk is matched by a rule element.
	 * 
	 * @param chunk the chunk to be matched by the element
	 * @param element the element to be matched against the chunk
	 * @return <code>true</code> if there's a match, <code>false</code> otherwise
	 */
	private boolean match(SyntacticChunk chunk, Element element, int baseTokenIndex, Sentence sentence) {
		boolean match;
		boolean negated;
		// Sees if the mask must or not match.
		// Negated is optional, so it can be null, true or false.
		// If null, consider as false.
		 if (element.isNegated() == null) {
			match = false;
			negated = false;
		} else {
			match = element.isNegated().booleanValue();
			negated = element.isNegated().booleanValue();
		}
		for (Mask mask : element.getMask()) {
			// If the token must match the mask.
			if (!negated) {
				// If not negated, match starts as false and just one match is needed to make it true.
				if (mask.getLexemeMask() != null && mask.getLexemeMask().equalsIgnoreCase(chunk.toPlainText())) {
					match = true;
				} else if (mask.getTagMask() != null && chunk.getMorphologicalTag() != null) {
					match = match | (chunk.getMorphologicalTag().matchExact(mask.getTagMask(), false) && chunk.getSyntacticTag().match(mask.getTagMask()));
				} else if (mask.getPrimitiveMask() != null /*&& chunk.getTokens().size() > 0*/ && matchLemma(chunk.getChildChunks().get(0).getMainToken(), mask.getPrimitiveMask())) {
					match = true;
				} else if (mask.getTagReference() != null && chunk.getMorphologicalTag() != null) {
					TagMask t = RuleUtils.createTagMaskFromReferenceSyntatic(mask.getTagReference(), sentence, baseTokenIndex);
					match = match | (chunk.getMorphologicalTag().match(t, false) && (t.getSyntacticFunction() == null || chunk.getSyntacticTag().match(t)));
				} else if (mask.getOutOfBounds() != null
				     && chunk.getTokens().get(0).getLexeme().equals(OUT_OF_BOUNDS)) {
                    match = true;
                }
			} else { // The token must NOT match the mask.
				// If negated, match starts as true and just one match is needed to make it false.
				if ( mask.getLexemeMask() != null && mask.getLexemeMask().equalsIgnoreCase(chunk.toPlainText())) {
					match = false;
				} else if (mask.getTagMask() != null) {
					match = match & !(chunk.getMorphologicalTag().matchExact(mask.getTagMask(), false) && (mask.getTagMask().getSyntacticFunction() == null || chunk.getSyntacticTag().match(mask.getTagMask())));
				} else if (mask.getPrimitiveMask() != null /*&& chunk.getTokens().size() > 0*/ && matchLemma(chunk.getChildChunks().get(0).getMainToken(), mask.getPrimitiveMask())) {
                    match = false;
              }  else if (mask.getTagReference() != null) {
					TagMask t = RuleUtils.createTagMaskFromReferenceSyntatic(mask.getTagReference(), sentence, baseTokenIndex);
					match = match & !(chunk.getMorphologicalTag().match(t,false) && (t.getSyntacticFunction() == null || chunk.getSyntacticTag().match(t)));
			  } else if (mask.getOutOfBounds() != null && 
			        sentence.getSyntacticChunks().indexOf(chunk) == sentence.getSyntacticChunks().size() - 1) {
                    match = false;
              }
			}
		}
		return match;
	}
	
	public  void ignore(String ruleID)
	{
		if(LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Adding rule to ignored list. ID: " + ruleID);
		}
		synchronized (ignoredRules) {
			ignoredRules.add(ruleID);
		}		
	}
	
	public  void resetIgnored()
	{
		if(LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Reset ignored list.");
		}
		synchronized (ignoredRules) {
			ignoredRules.clear();
		}		
	}
	
	public  void filterIgnoredRules(List<Mistake> rules)
	{
		List<Mistake> ret = new ArrayList<Mistake>();
		synchronized (ignoredRules) {
			if(ignoredRules.size() == 0)
			{
				if(LOGGER.isDebugEnabled())
				{
					LOGGER.debug("No rules to ignore.");
				}
				return;
			}
			
			for(int i = 0; i < rules.size(); i++)
			{
				if(!ignoredRules.contains(rules.get(i).getRuleIdentifier()))
				{
					ret.add(rules.get(i));
				}
			}
			int n = rules.size() - ret.size();
			
			if( n != 0)
			{
				rules.clear();
				rules.addAll(ret);
			}
			if(LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Ignored " + n + " rules.");
			}

		}
	}
	
	public List<String> getCategories() {
		
		return null;
	}

	public String getIdPrefix() {
		return ID_PREFIX;
	}


	public int getPriority() {
		return 1000;
	}

	private Collection<RuleDefinition> definitions;

	public synchronized Collection<RuleDefinition> getRulesDefinition() {
		if (definitions != null) {
			return definitions;
		}
		
		List<Rule> rules =RulesXmlAccess.getInstance().getRules().getRule();
		List<RuleDefinition> d = new ArrayList<RuleDefinition>(
				rules.size());
		for (Rule rule : rules) {
			d.add(new XMLRuleDefinition(ID_PREFIX, rule));
		}
		definitions = Collections.unmodifiableCollection(d);
		return definitions;
	}
	
  private boolean matchLemma(Token token, String primitiveMask) {
    boolean match = false;
    String[] lemmas = token.getPrimitive();
    if(lemmas != null) {
      for (String lemma : lemmas) {
        if(lemma.equalsIgnoreCase(primitiveMask)) {
          match = true;
          break;
        }
      }
    }
    return match;
  }
  
  @SuppressWarnings("unchecked")
  private <T> ArrayList<T> cloneList(ArrayList<T> matched) {
    return (ArrayList<T>) matched.clone();
  }
}
