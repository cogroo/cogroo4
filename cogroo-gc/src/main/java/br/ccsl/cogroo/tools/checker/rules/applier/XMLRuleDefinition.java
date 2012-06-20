package br.ccsl.cogroo.tools.checker.rules.applier;

import java.util.List;

import br.ccsl.cogroo.tools.checker.RuleDefinitionI;
import br.ccsl.cogroo.tools.checker.RuleType;
import br.ccsl.cogroo.tools.checker.rules.model.Example;
import br.ccsl.cogroo.tools.checker.rules.model.Pattern;
import br.ccsl.cogroo.tools.checker.rules.model.Rule;
import br.ccsl.cogroo.tools.checker.rules.model.Rule.Method;
import br.ccsl.cogroo.tools.checker.rules.util.RuleUtils;

public class XMLRuleDefinition implements RuleDefinitionI {

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
