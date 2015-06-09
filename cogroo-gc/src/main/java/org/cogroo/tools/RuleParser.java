package org.cogroo.tools;

import java.awt.List;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.Charsets;
import org.cogroo.tools.checker.JavaRuleDefinition;
import org.cogroo.tools.checker.RuleDefinition;
import org.cogroo.tools.checker.rules.model.Example;

import com.google.common.io.Resources;

public class RuleParser {

	
	private RuleParser() {
		
	}
	
	public static RuleDefinition getRuleDefinition(String fileName) {
		URL url;
		
		try {
			url = Resources.getResource(fileName);
		}
		catch (Exception e) {
			return null;
		}
		
		try {
			Map<String,String> rule = new HashMap<String,String>();
			
			Example example = null;
			boolean isExample = false;
			ArrayList<Example> examples = new ArrayList<Example>();
			
			for (String line : Resources.readLines(url, Charsets.UTF_8)) {
				
				line = line.trim();
				
				if (line.length() == 0) continue;
				
				System.out.println("Uva: " + line);
				
				String[] fields = line.split("=", 2);

				if (fields.length < 2) continue;
				
				System.out.println("Campo: " + fields[0] + ", valor: " + fields[1]);
			
				fields[0] = fields[0].trim();
				fields[1] = fields[1].trim();
				
				if (isExample) {
					isExample = false;
					example.setIncorrect(fields[1]);
					examples.add(example);
				}
				else if (fields[0].equals("correctExample")) {
					isExample = true;
					example = new Example();
					example.setCorrect(fields[1]);
				}
				else {
					rule.put(fields[0], fields[1]);
				}
				
			}
			
			return new JavaRuleDefinition(rule.get("id"), rule.get("category"), rule.get("group"), rule.get("description"), rule.get("message"), rule.get("shortMessage"), examples);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
