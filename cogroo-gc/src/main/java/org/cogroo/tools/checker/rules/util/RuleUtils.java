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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cogroo.entities.Sentence;
import org.cogroo.entities.impl.ChunkTag;
import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.entities.impl.SyntacticTag;

import org.cogroo.tools.checker.rules.model.Composition;
import org.cogroo.tools.checker.rules.model.Element;
import org.cogroo.tools.checker.rules.model.Mask;
import org.cogroo.tools.checker.rules.model.Operator;
import org.cogroo.tools.checker.rules.model.PatternElement;
import org.cogroo.tools.checker.rules.model.Reference;
import org.cogroo.tools.checker.rules.model.Reference.Property;
import org.cogroo.tools.checker.rules.model.Rule;
import org.cogroo.tools.checker.rules.model.Suggestion;
import org.cogroo.tools.checker.rules.model.Suggestion.Replace;
import org.cogroo.tools.checker.rules.model.Suggestion.ReplaceMapping;
import org.cogroo.tools.checker.rules.model.Suggestion.Swap;
import org.cogroo.tools.checker.rules.model.TagMask;
import org.cogroo.tools.checker.rules.model.TagMask.Gender;
import org.cogroo.tools.checker.rules.model.TagMask.Number;

/**
 * Set of utility methods, mostly to convert a rule element to a human readable string.
 * 
 * @author Marcelo Suzumura
 * @author William Colen
 */
public class RuleUtils {

	public static Map<RuleInfo, String> getRuleAsString(Rule rule) {
		Map<RuleInfo, String> map = new HashMap<RuleInfo, String>();
		map.put(RuleInfo.METHOD, getMethodAsString(rule));
		map.put(RuleInfo.TYPE, getTypeAsString(rule));
		map.put(RuleInfo.GROUP, getGroupAsString(rule));
		map.put(RuleInfo.MESSAGE, getMessageAsString(rule));
		map.put(RuleInfo.SHORTMESSAGE, getShortMessageAsString(rule));
		map.put(RuleInfo.PATTERN, getPatternAsString(rule));
		map.put(RuleInfo.BOUNDARIES, getBoundariesAsString(rule));
		map.put(RuleInfo.SUGGESTIONS, getSuggestionsAsString(rule));
		return map;
	}

	public static String getMethodAsString(Rule rule) {
		return rule.getMethod().value();
	}

	public static String getTypeAsString(Rule rule) {
		return rule.getType();
	}

	public static String getGroupAsString(Rule rule) {
		return rule.getGroup();
	}

	public static String getMessageAsString(Rule rule) {
		return rule.getMessage();
	}
	
	public static String getShortMessageAsString(Rule rule) {
		return rule.getShortMessage();
	}

	public static String getPatternAsString(Rule rule) {
		StringBuilder sb = new StringBuilder();
		for (PatternElement patternElement : rule.getPattern()
				.getPatternElement()) {
			sb.append(getPatternElementAsString(patternElement));
			sb.append(" ");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static String getPatternElementAsString(PatternElement patternElement) {
		if (patternElement.getElement() != null)
			return getElementAsString(patternElement.getElement());
		else if (patternElement.getComposition() != null)
			return getCompositionAsString(patternElement.getComposition());

		return "NULL";
	}

	private static String getCompositionAsString(Composition composition) {
		if (composition.getAnd() != null)
			return getOperatorAsString(composition.getAnd(), " & ");
		else if (composition.getOr() != null)
			return getOperatorAsString(composition.getOr(), " | ");

		return "NULL";
	}

	private static String getOperatorAsString(Operator operator, String op) {
		List<PatternElement> peList = operator.getPatternElement();
		StringBuilder sb = new StringBuilder();
		sb.append("( ");

		int i = 0;
		for (; i < peList.size() - 1; i++) {
			sb.append(getPatternElementAsString(peList.get(i)) + op);
		}

		sb.append(getPatternElementAsString(peList.get(i)) + ") ");

		return sb.toString();
	}

	/**
	 * Gets the string representation of an element.
	 * 
	 * @param element
	 *            the element to be planified to a string
	 * @return the element as a string
	 */
	public static String getElementAsString(Element element) {
		StringBuilder sb = new StringBuilder();

		if (element.isNegated() != null && element.isNegated().booleanValue()) {
			sb.append("~");
		}

		int masks = element.getMask().size();
		if (masks > 1) {
			sb.append("(");
		}

		int maskCounter = 0;
		for (Mask mask : element.getMask()) {
			// Encloses lexemes between quotes.
			if (mask.getLexemeMask() != null) {
				sb.append("\"").append(mask.getLexemeMask()).append("\"");
			} else if (mask.getPrimitiveMask() != null) {
				// Primitives are enclosed between curly brackets.
				sb.append("{").append(mask.getPrimitiveMask()).append("}");
			} else if (mask.getTagMask() != null) {
				sb.append(getTagMaskAsString(mask.getTagMask()));
			} else if (mask.getTagReference() != null) {
				sb.append(getTagReferenceAsString(mask.getTagReference()));
			}
			if (maskCounter < masks - 1) {
				sb.append("|");
			}
			maskCounter++;
		}

		if (masks > 1) {
			sb.append(")");
		}

		return sb.toString();
	}

	public static String getTagReferenceAsString(Reference tagRef) {
		StringBuilder sb = new StringBuilder();
		String index = Long.toString(tagRef.getIndex());
		sb.append("( ref[" + index + "] ");
		tagRef.getProperty();
		for (Property prop : tagRef.getProperty()) {
			sb.append(prop + " ");
		}
		sb.append(")");
		return sb.toString();
	}

	public static String getTagMaskAsString(TagMask tagMask) {
		StringBuilder sb = new StringBuilder();
		if (tagMask.getSyntacticFunction() != null) {
			sb.append(tagMask.getSyntacticFunction().value()).append("_");
		}
		if (tagMask.getClazz() != null) {
			sb.append(tagMask.getClazz().value()).append("_");
		}
		if (tagMask.getGender() != null) {
			sb.append(tagMask.getGender().value()).append("_");
		}
		if (tagMask.getNumber() != null) {
			sb.append(tagMask.getNumber().value()).append("_");
		}
		if (tagMask.getCase() != null) {
			sb.append(tagMask.getCase().value()).append("_");
		}
		if (tagMask.getPerson() != null) {
			sb.append(tagMask.getPerson().value()).append("_");
		}
		if (tagMask.getTense() != null) {
			sb.append(tagMask.getTense().value()).append("_");
		}
		if (tagMask.getMood() != null) {
			sb.append(tagMask.getMood().value()).append("_");
		}
		if (tagMask.getPunctuation() != null) {
			sb.append(tagMask.getPunctuation().value()).append("_");
		}
		return sb.toString();
	}

	public static String getBoundariesAsString(Rule rule) {
		return rule.getBoundaries().getLower() + " "
				+ rule.getBoundaries().getUpper();
	}

	public static String getSuggestionsAsString(Rule rule) {
		StringBuilder sb = new StringBuilder();

		if (rule.getSuggestion().isEmpty()) {
			sb.append("none");
		}

		for (Suggestion suggestion : rule.getSuggestion()) {
			// Replaces.
			if (!suggestion.getReplace().isEmpty()) {
				sb.append("Replace: ");
			}
			for (Replace replace : suggestion.getReplace()) {
				sb.append(replace.getIndex());
				sb.append(" <=> ");
				if (replace.getLexeme() != null) {
					sb.append("\"");
					sb.append(replace.getLexeme());
					sb.append("\"");
				} else if (replace.getTagReference() != null) {
					sb.append(replace.getTagReference().getIndex());
					sb.append("_");
					sb.append(getTagMaskAsString(replace.getTagReference()
							.getTagMask()));
				}
				sb.append("|");
			}
			sb = removeLastVerticalBar(sb);

			// Replace mappings.
			if (!suggestion.getReplaceMapping().isEmpty()) {
				sb.append("Replace Mapping: ");
			}
			for (ReplaceMapping replaceMapping : suggestion.getReplaceMapping()) {
				sb.append(replaceMapping.getIndex());
				sb.append(" ");
				sb.append(replaceMapping.getKey());
				sb.append(" => ");
				sb.append(replaceMapping.getValue());
				sb.append("|");
			}
			sb = removeLastVerticalBar(sb);

			// Swaps.
			if (!suggestion.getSwap().isEmpty()) {
				sb.append("Swap: ");
			}
			for (Swap swap : suggestion.getSwap()) {
				sb.append(swap.getA());
				sb.append(" <=> ");
				sb.append(swap.getB());
				sb.append("|");
			}
			sb = removeLastVerticalBar(sb);
			sb.append("\n");
		}
		return sb.toString();
	}

	private static StringBuilder removeLastVerticalBar(StringBuilder sb) {
		if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '|') {
			return sb.deleteCharAt(sb.length() - 1);
		}
		return sb;
	}

	public enum RuleInfo {
		METHOD, TYPE, GROUP, MESSAGE, SHORTMESSAGE, PATTERN, BOUNDARIES, SUGGESTIONS
	}
	
	public static TagMask createTagMaskFromReference(Reference ref,
			MorphologicalTag mTag, ChunkTag cTag, SyntacticTag sTag) {
		TagMask t = new TagMask();
		if (mTag == null) {
			return t;
		}
		for (Property p : ref.getProperty()) {
			switch (p) {
				case CLASS :
					t.setClazz(mTag.getClazzE());
					break;
				case GENDER :
					if(Gender.NEUTRAL != mTag.getGenderE())
						t.setGender(mTag.getGenderE());
					break;
				case NUMBER :
					if(Number.NEUTRAL != mTag.getNumberE())
						t.setNumber(mTag.getNumberE());
					break;
				case PERSON :
					t.setPerson(mTag.getPersonE());
					break;
				case SYNTACTIC_FUNCTION :
					t.setSyntacticFunction(TagMask.SyntacticFunction
							.fromValue(sTag.toVerboseString()));
					break;
				case CHUNK_FUNCTION :
					t.setChunkFunction(TagMask.ChunkFunction.fromValue(cTag
							.toVerboseString()));
					break;
				default :
					break;
			}
		}

		return t;
	}

	public static TagMask createTagMaskFromReference(Reference ref,
			Sentence sent, int refPos) {
		
		int pos = refPos + (int)ref.getIndex();
		if(pos >= 0 && pos < sent.getTokens().size())
		{
			MorphologicalTag mTag = sent.getTokens().get(pos).getMorphologicalTag();
			ChunkTag cTag = sent.getTokens().get(pos).getChunkTag();
			SyntacticTag sTag = sent.getTokens().get(pos).getSyntacticTag();
			return createTagMaskFromReference(ref, mTag, cTag, sTag);
		}
		else
		{
			return new TagMask();
		}
	}
	
	public static TagMask createTagMaskFromReferenceSyntatic(Reference ref,
           Sentence sent, int refPos) {
       
       int pos = refPos + (int)ref.getIndex();
       if(pos >= 0 && pos < sent.getSyntacticChunks().size())
       {
           MorphologicalTag mTag = sent.getSyntacticChunks().get(pos).getMorphologicalTag();
//           ChunkTag cTag = sent.getSyntacticChunks().get(pos).getChunkTag();
           SyntacticTag sTag = sent.getSyntacticChunks().get(pos).getSyntacticTag();
           return createTagMaskFromReference(ref, mTag, null, sTag);
       }
       else
       {
           return new TagMask();
       }
   }

  public static void completeMissingParts(TagMask tagMask,
      MorphologicalTag tag) {
    
    if(tagMask.getCase() == null) {
      tagMask.setCase(tag.getCase());
    }
    
    if(tagMask.getClazz() == null) {
      tagMask.setClazz(tag.getClazzE());
    }
    
    if(tagMask.getGender() == null) {
      tagMask.setGender(tag.getGenderE());
    }
    
    if(tagMask.getMood() == null) {
      tagMask.setMood(tag.getMood());
    }
    
    if(tagMask.getNumber() == null) {
      tagMask.setNumber(tag.getNumberE());
    }
    
    if(tagMask.getPerson() == null) {
      tagMask.setPerson(tag.getPersonE());
    }
    
    if(tagMask.getPunctuation() == null) {
      tagMask.setPunctuation(tag.getPunctuation());
    }
    
    if(tagMask.getTense() == null) {
      tagMask.setTense(tag.getTense());
    }
    
    if(tagMask.getPunctuation() == null) {
      tagMask.setPunctuation(tag.getPunctuation());
    }
    
  }

//	public static void main(String[] args) {
//		// Rule rule = RulesService.getInstance().getRule(69, true);
//		// System.out.println(patternAsString(rule.getPattern()));
//		Rules rules = new RulesContainerHelper().getContainerForXMLAccess().getComponent(RulesProvider.class).getRules();
//		
//		for (Rule rule : rules.getRule()) {
//			// System.out.println(rule.getId() + "\t" +
//			// getPatternAsString(rule));
//			System.out.println(rule.getId() + "\t"
//					+ getSuggestionsAsString(rule));
//		}
//	}

}
