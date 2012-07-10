package br.ccsl.cogroo.tools.checker.rules.dictionary;

import java.util.List;

import org.cogroo.util.PairWordPOSTag;



public interface LexicalDictionary {
	
	//public boolean isWordDeleted(String word);
	
	public boolean wordExists(String word);
	
	public List<PairWordPOSTag> getWordsAndPosTagsForLemma(String aLemma);
	
	/** Compute the pair Lemma + POSTag for a word */
	public List<PairWordPOSTag> getLemmasAndPosTagsForWord(String aWord);
	
	public List<String> getPOSTagsForWord(String word);

}
