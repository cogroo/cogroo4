package org.cogroo.ruta.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cogroo.tools.checker.JavaRuleDefinition;
import org.cogroo.tools.checker.RuleDefinition;
import org.cogroo.tools.checker.rules.model.Example;
import org.cogroo.util.ResourcesUtil;

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
	  Path path;
	  try {
		  path = ResourcesUtil.getResourceFile(RuleParser.class, fileName).toPath();
		  System.out.println(path);
		} catch (Exception e) {
			return Collections.emptySet();
		}
		return getRuleDefinitionList(path);
	}

	public static Set<RuleDefinition> getRuleDefinitionList(Path path) {
		Set<RuleDefinition> rules = new HashSet<RuleDefinition>();
		try {
			Map<String, String> rule = new HashMap<String, String>();
			List<String> correctExamples = new ArrayList<String>();
			List<String> incorrectExamples = new ArrayList<String>();
			
			for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
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
