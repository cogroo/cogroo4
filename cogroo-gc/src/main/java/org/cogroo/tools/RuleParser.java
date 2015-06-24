package org.cogroo.tools;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.Charsets;
import org.cogroo.tools.checker.JavaRuleDefinition;
import org.cogroo.tools.checker.RuleDefinition;
import org.cogroo.tools.checker.rules.model.Example;

import com.google.common.io.Resources;

public class RuleParser {

	private RuleParser() {

	}

	public static Set<RuleDefinition> getsRuleDefinition(String fileName) {
		List<RuleDefinition> rules = new ArrayList<RuleDefinition>();
		URL url;

		try {
			url = Resources.getResource(fileName);
		} catch (Exception e) {
			return Collections.emptySet();
		}

		try {
			Map<String, String> rule = new HashMap<String, String>();

			Example example = null;
			boolean isExample = false;
			List<Example> examples = new ArrayList<Example>();
			List<String> correctExamples = new ArrayList<String>();
			List<String> incorrectExamples = new ArrayList<String>();
			for (String line : Resources.readLines(url, Charsets.UTF_8)) {

				line = line.trim();

				if (line.length() == 0)
					continue;

				String[] fields = line.split("=", 2);

				if (fields.length < 2)
					continue;

				fields[0] = fields[0].trim();
				fields[1] = fields[1].trim();
				if (fields[0].equals("correctExample")) {
					if (!isExample) 
						example = new Example();
					isExample = true;
					example.setCorrect(fields[1]);
				} else if (fields[0].equals("incorrectExample")) {
					if (!isExample)
						example = new Example();
					isExample = true;
					example.setIncorrect(fields[1]);
				}

				if (isExample) {
					isExample = false;
					example.setIncorrect(fields[1]);
					examples.add(example);
				} else 
						|| fields[0].equals("incorrectExample")) {
					
				} else {
					rule.put(fields[0], fields[1]);
				}

			}

			return new JavaRuleDefinition(rule.get("id"), rule.get("category"),
					rule.get("group"), rule.get("description"),
					rule.get("message"), rule.get("shortMessage"), examples);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
