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
