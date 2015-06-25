package org.cogroo.tools;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

	private static List<Example> buildExampleArray(
			List<String> correctExamples, List<String> incorrectExamples) {
		List<Example> examples = new ArrayList<Example>();
		while (!correctExamples.isEmpty() && !incorrectExamples.isEmpty()) {
			String ce = correctExamples.remove(0);
			String ie = incorrectExamples.remove(0);
			Example example = new Example();
			example.setCorrect(ce);
			example.setIncorrect(ie);
			examples.add(example);
		}
		correctExamples.clear();
		incorrectExamples.clear();
		return examples;
	}

	public static Set<RuleDefinition> getRuleDefinitionList(String fileName) {
		URL url;
		try {
			url = Resources.getResource(fileName);
			System.out.println(url);
		} catch (Exception e) {
			return Collections.emptySet();
		}
		return getRuleDefinitionList(url);
	}

	public static Set<RuleDefinition> getRuleDefinitionList(URL url) {
		Set<RuleDefinition> rules = new HashSet<RuleDefinition>();
		try {
			Map<String, String> rule = new HashMap<String, String>();
			List<String> correctExamples = new ArrayList<String>();
			List<String> incorrectExamples = new ArrayList<String>();
			for (String line : Resources.readLines(url, Charsets.UTF_8)) {
				line = line.trim();
				if (line.length() == 0 && rule.containsKey("id")) {
					List<Example> examples = buildExampleArray(correctExamples,
							incorrectExamples);
					rules.add(new JavaRuleDefinition(rule.get("id"), rule
							.get("category"), rule.get("group"), rule
							.get("description"),
							"RUTA: " + rule.get("message"), "RUTA: "
									+ rule.get("shortMessage"), examples));
					rule.clear();
					continue;
				}
				String[] fields = line.split("=", 2);

				if (fields.length < 2)
					continue;

				fields[0] = fields[0].trim();
				fields[1] = fields[1].trim();

				if (fields[0].equals("correctExample"))
					correctExamples.add(fields[1]);
				else if (fields[0].equals("incorrectExample"))
					incorrectExamples.add(fields[1]);

				else {
					rule.put(fields[0], fields[1]);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return rules;
	}
}
