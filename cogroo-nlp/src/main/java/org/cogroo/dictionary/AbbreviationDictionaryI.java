package org.cogroo.dictionary;

public interface AbbreviationDictionaryI {

	/**
	 * Checks if this dictionary has the given entry.
	 * 
	 * @param token
	 *            the token to query
	 * 
	 * @return true if it contains the entry otherwise false
	 */
	public abstract boolean contains(String tokens);

}