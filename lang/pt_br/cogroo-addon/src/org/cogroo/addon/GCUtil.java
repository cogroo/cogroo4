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
package org.cogroo.addon;

import java.util.Arrays;

import com.sun.star.beans.PropertyValue;
import com.sun.star.lang.Locale;

import com.sun.star.linguistic2.SingleProofreadingError;
import com.sun.star.linguistic2.ProofreadingResult;

//import com.sun.star.linguistic2.GrammarCheckingResult;
//import com.sun.star.linguistic2.SingleGrammarError;

public class GCUtil {

	public static String getDebugString(ProofreadingResult value) {
		StringBuilder sb = new StringBuilder();
		sb.append("Total errors: ");

		
		

		if (value.aErrors != null) {
			sb.append(value.aErrors.length + "\n");
			for (SingleProofreadingError err : value.aErrors) {
				sb.append("Error [").append(err.nErrorStart).append(" to ")
						.append(err.nErrorLength).append("]\n");
				sb.append("ShortComment [").append(err.aShortComment).append(
						"]\n");
				sb.append("FullMessage [").append(err.aFullComment).append(
						"]\n");
				sb.append("Suggestion ");
				if (err.aSuggestions != null && err.aSuggestions.length > 0) {
					for (String suggestion : err.aSuggestions) {
						sb.append("[").append(suggestion).append("]");
					}
				} else {
					sb.append("[none]");
				}
			}
		}
		return sb.toString();
	}

	public static String getDebugString(int[] arr) {
		return Arrays.toString(arr);
	}
	
	public static String getDebugString(PropertyValue[] arr)
	{
		StringBuffer b = new StringBuffer();
		for (PropertyValue prop : arr) {
			b.append(getDebugString(prop) + "; ");
		}

		return b.toString();
	}
	
	public static String getDebugString(PropertyValue property)
	{
		return property.toString();
	}

	public static String getDebugString(Locale[] arr) {
		StringBuffer b = new StringBuffer();
		for (Locale locale : arr) {
			b.append(getDebugString(locale) + "; ");
		}

		return b.toString();
	}

	public static String getDebugString(Locale locale) {

		StringBuffer sb = new StringBuffer();
		if (locale.Language != null && locale.Language.length() > 0)
			sb.append(locale.Language);
		if (locale.Country != null && locale.Country.length() > 0)
			sb.append("_" + locale.Country);
		if (locale.Variant != null && locale.Variant.length() > 0)
			sb.append("_" + locale.Variant);

		return sb.toString();
	}

	public static boolean isLocaleEqual(Locale first, Locale second) {
		if (getDebugString(second).startsWith(getDebugString(first)) ) {
			return true;
		}

		return false;
	}

	public static boolean isKnownLocale(Locale[] knownLocales, Locale locale) {
		for (Locale knownLocale : knownLocales) {
			if(isLocaleEqual(knownLocale, locale))
			{
				return  true;
			}
		}		
		return false;
	}

	public static void main(String[] args) {
		Locale a = new Locale("en", "US", "nabo");
		System.out.println(GCUtil.getDebugString(a));

		Locale b = new Locale("en", "US", null);
		System.out.println(GCUtil.getDebugString(b));

		Locale c = new Locale("en", "US", null);
		Locale br = new Locale("pt", "BR", null);
		Locale pt = new Locale("pt", "PT", null);
		Locale ar = new Locale("es", "AR", null);
		Locale pt_neutral  = new Locale("pt", "PT", "");
		Locale[] list = new Locale[]{c,pt_neutral};
		System.out.println(GCUtil.getDebugString(c));

		System.out.println("a=b: " + isLocaleEqual(a, b));
		System.out.println("b=c: " + isLocaleEqual(b, c));
		System.out.println("isKnown br: " + isKnownLocale(list, br));
		System.out.println("isKnown br: " + isKnownLocale(list, pt));
		System.out.println("isKnown ar: " + isKnownLocale(list, ar));
	}

}
