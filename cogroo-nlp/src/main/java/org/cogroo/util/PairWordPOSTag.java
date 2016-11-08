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
package org.cogroo.util;

import java.util.Objects;

public class PairWordPOSTag {
	
	private final String word;
	private final String posTag;
	
	public PairWordPOSTag(String lemma, String posTag) {
		super();
		this.word = lemma;
		this.posTag = posTag;
	}

	public String getWord() {
		return word;
	}

	public String getPosTag() {
		return posTag;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(word, posTag);
	}
	
	@Override
	public String toString() {
		return "("+this.word+","+this.posTag+")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;
	    if(obj == null) return false;
	    
	    if(obj instanceof PairWordPOSTag){
	        final PairWordPOSTag other = (PairWordPOSTag) obj;
	        return Objects.equals(word, other.word)
	            && Objects.equals(posTag, other.posTag);
	    } else{
	        return false;
	    }
	}

}
