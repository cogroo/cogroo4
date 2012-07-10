package org.cogroo.util;

import com.google.common.base.Objects;

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
		return Objects.hashCode(word, posTag);
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
	        return Objects.equal(word, other.word)
	            && Objects.equal(posTag, other.posTag);
	    } else{
	        return false;
	    }
	}

}
