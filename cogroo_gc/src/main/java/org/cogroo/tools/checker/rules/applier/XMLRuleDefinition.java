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
package org.cogroo.tools.checker.rules.applier;

import java.util.List;

import org.cogroo.tools.checker.RuleDefinition;
import org.cogroo.tools.checker.RuleType;
import org.cogroo.tools.checker.rules.util.RuleUtils;

import org.cogroo.tools.checker.rules.model.Example;
import org.cogroo.tools.checker.rules.model.Pattern;
import org.cogroo.tools.checker.rules.model.Rule;
import org.cogroo.tools.checker.rules.model.Rule.Method;

public class XMLRuleDefinition implements RuleDefinition {

	private final String id;
	private final String category;
	private final String group;
	private final String message;
	private final String shortMessage;
	private final List<Example> examples;
	private final Method method;
	private final String description;
	private final Pattern pattern;

	public XMLRuleDefinition(String prefix, Rule rule) {
		super();
		this.id = prefix + rule.getId();
		this.category = rule.getType();
		this.group = rule.getGroup();
		this.method = rule.getMethod();
		this.message = rule.getMessage();
		this.shortMessage = rule.getShortMessage();
		this.examples = rule.getExample();
		this.description = RuleUtils.getPatternAsString(rule);
		this.pattern = rule.getPattern();
	}

	public String getId() {
		return id;
	}

	public String getCategory() {
		return category;
	}

	public String getGroup() {
		return group;
	}

	public String getDescription() {
		return this.description;
	}

	public String getMessage() {
		return message;
	}

	public String getShortMessage() {
		return shortMessage;
	}

	public List<Example> getExamples() {
		return examples;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public RuleType getRuleType() {
		RuleType t;
		switch (method) {
		case PHRASE_LOCAL:
			t = RuleType.XML_PHRASE_LOCAL;
			break;
		case SUBJECT_VERB:
			t = RuleType.XML_SUBJECT_VERB;
			break;
		default:
			t = RuleType.XML_GENERAL;
			break;
		}
		return t;
	}

	public boolean isXMLBased() {
		return true;
	}

}
