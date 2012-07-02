/**
 * Copyright (C) 2008 William Colen<colen at users.sourceforge.net>
 * 
 * http://lingualquanta.sourceforge.net/ooointegration
 * 
 * This file is part of Lingual Quanta OpenOffice.org Integration.
 * 
 * OOoIntegration is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Publicas published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OOoIntegration is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOoIntegration.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * NOTICE:
 * Some peaces of the code came from other projects I studied. 
 * I have to thanks the developers of:
 * 	CoGrOO
 * 		Site: cogroo.sourceforge.net 
 * 		License: LGPL
 * 		Used as a sample of Grammar Checker Addon.
 * 	LinguageTool 
 * 		Site: www.languagetool.org
 * 		License: LGPL
 * 		Used as a sample of Grammar Checker Addon that
 * 		implements the XGrammarChecker interface.
 *  	dxf2calc 
 *  		Site: www.abj.dk/dxf2calc
 * 		License: SODA-WARE
 *  		Was used as more sofisticated sample of OOo Addon, specially 
 *  		as a sample of how to find the Addon folder during runtime and
 *  		how do a nice About dialog box. 
 */

package br.ccsl.cogroo.addon;

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
