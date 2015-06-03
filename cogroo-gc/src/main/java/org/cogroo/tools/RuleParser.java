package org.cogroo.tools;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.Charsets;
import org.cogroo.tools.checker.RuleDefinition;

import com.google.common.io.Resources;

public class RuleParser {

	
	private RuleParser() {
		
	}
	
	public static RuleDefinition getRuleDefinition(String fileName) {
		try {
			URL url = Resources.getResource(fileName);
			String text = Resources.toString(url, Charsets.UTF_8);
			System.out.println(text);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
