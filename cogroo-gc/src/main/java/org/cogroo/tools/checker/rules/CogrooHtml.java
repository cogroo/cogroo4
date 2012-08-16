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
package org.cogroo.tools.checker.rules;

import java.io.File;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.analyzer.Pipe;
import org.cogroo.checker.CheckDocument;
import org.cogroo.checker.GrammarCheckerAnalyzer;
import org.cogroo.entities.Mistake;
import org.cogroo.entities.impl.MistakeImpl;
import org.cogroo.text.Sentence;
import org.cogroo.tools.checker.rules.applier.RulesProvider;
import org.cogroo.tools.checker.rules.applier.RulesXmlAccess;
import org.cogroo.tools.checker.rules.model.Example;
import org.cogroo.tools.checker.rules.model.Rule;
import org.cogroo.tools.checker.rules.model.Rules;
import org.cogroo.tools.checker.rules.util.RuleUtils;
import org.cogroo.tools.checker.rules.util.RuleUtils.RuleInfo;

import com.google.common.io.Files;

/**
 * This class grammar checks all examples from the rules file and prints an html report
 * showing which rules are working.
 * 
 * @author Marcelo Suzumura
 */
public class CogrooHtml {
    
    /**
     * The file in which the report will be written.
     */
    private Writer out;
    
    /**
     * The rules.
     */
    private Rules rules;
    
    /**
     * The grammar checker.
     */
    private Pipe cogroo;
    
    /**
     * Examples that were not matched by any rule at all.
     */
    private List<Rule> no = new ArrayList<Rule>();
    
    /**
     * Examples matched by the correct rule.
     */
    private List<Rule> ok = new ArrayList<Rule>();
    
    /**
     * Examples matched by the correct rule, but matched by other rules too.
     */
    private List<Rule> partial = new ArrayList<Rule>();
    
    /**
     * Examples matched only by other rules.
     */
    private List<Rule> wrong = new ArrayList<Rule>();
    
    /**
     * List of rules that does not have any suggestion.
     */
    private List<Rule> noSuggestions = new ArrayList<Rule>();
    
    /**
     * List of rules that does have bad suggestion.
     */
    private List<Rule> badSuggestion = new ArrayList<Rule>();
    
    /**
     * Maps rules ids and the sentences that caused an exception.
     */
    private Map<Long, List<String>> exceptions = new LinkedHashMap<Long, List<String>>();
    
    private Map<Long, String> rulesInfo = new HashMap<Long, String>();

    public CogrooHtml() throws Exception {
      out = Files.newWriter(new File("reports/rules_status.html"), Charset.forName("UTF-8"));
      
      GrammarCheckerAnalyzer gca = new GrammarCheckerAnalyzer();
      ComponentFactory factory = ComponentFactory.create(new Locale("pt", "BR"));
      cogroo = (Pipe) factory.createPipe();
      cogroo.add(gca);
      
//      TagDictionary td = new TagDictionary(new FSALexicalDictionary(), false,
//          new FlorestaTagInterpreter());
//      
//      converter = new TextEntitiesConverter(td);
      
      this.rules = getRules();
    }
    
    
    
    private Rules getRules() {
      // Create XML rules applier
      RulesProvider xmlProvider = new RulesProvider(RulesXmlAccess.getInstance(),
          false);
      return xmlProvider.getRules();
    }
    
    private void test() throws Exception {
        this.printHtmlHeader();
        
        int totalRules = 0;
        for (Rule rule : this.rules.getRule()) {
            // Only active rules will be considered.
            if (rule.isActive()) {
                // Consolidates rule information.
                this.prepareRuleInfo(rule);
                
                totalRules++;
                this.printRuleHeader(rule);
                
                // Each position contains the mistakes for each example.
                List<List<Mistake>> sentencesMistakes = new ArrayList<List<Mistake>>(rule.getExample().size());
                List<Sentence> sentences = new ArrayList<Sentence>(rule.getExample().size());
                
                // Checks each incorrect example of the rule.
                for (Example example : rule.getExample()) {
                    // Check sentence for mistakes.
                    try {
                        CheckDocument d = new CheckDocument();
                        d.setText(example.getIncorrect());
                        
                        this.cogroo.analyze(d);
                        
                        sentencesMistakes.add(d.getMistakes());
                        sentences.add(d.getSentences().get(0));
                    } catch (RuntimeException e) {
                      e.printStackTrace();
                        sentencesMistakes.add(null);
                        sentences.add(null);
                        this.logException(Long.valueOf(rule.getId()), example.getIncorrect());
                    }
                }
                this.evaluateMistakes(sentencesMistakes, sentences, rule);
                this.printRuleFooter(rule);
            }
            this.storeNoSuggestionRule(rule);
        }
        
        this.printReport("ok", this.ok, totalRules, "00ff00");
        this.printReport("partial", this.partial, totalRules, "ffff00");
        this.printReport("no", this.no, totalRules, "ff8000");
        this.printReport("wrong", this.wrong, totalRules, "ff0000");
        
        this.printNoSuggestion();
        this.printBadSuggestion();
        this.printExceptionReport();
        this.printHtmlFooter();
        
        this.out.close();
    }
    
    private void prepareRuleInfo(Rule rule) {
        Map<RuleInfo, String> mapInfo = RuleUtils.getRuleAsString(rule);
        StringBuilder sb = new StringBuilder();
        sb.append("<table border=\"1\">");
        sb.append("<tr><th>Method</th><td>").append(mapInfo.get(RuleInfo.METHOD)).append("</td></tr>");
        sb.append("<tr><th>Type</th><td>").append(mapInfo.get(RuleInfo.TYPE)).append("</td></tr>");
        sb.append("<tr><th>Group</th><td>").append(mapInfo.get(RuleInfo.GROUP)).append("</td></tr>");
        sb.append("<tr><th>Message</th><td>").append(mapInfo.get(RuleInfo.MESSAGE)).append("</td></tr>");
        sb.append("<tr><th>ShortMessage</th><td>").append(mapInfo.get(RuleInfo.SHORTMESSAGE)).append("</td></tr>");
        sb.append("<tr><th>Pattern</th><td>").append(mapInfo.get(RuleInfo.PATTERN)).append("</td></tr>");
        sb.append("<tr><th>Boundaries</th><td>").append(mapInfo.get(RuleInfo.BOUNDARIES)).append("</td></tr>");
        sb.append("<tr><th>Suggestions</th><td>").append(mapInfo.get(RuleInfo.SUGGESTIONS)).append("</td></tr>");
        sb.append("</table>");
        String escapedHtml = this.escapeHtmlChars(sb.toString());
        String newLineToBr = this.newLineToBr(escapedHtml);
        this.rulesInfo.put(Long.valueOf(rule.getId()), newLineToBr);
    }
    
    private String prepareOverlib(Rule rule, String text) {
        StringBuilder sb = new StringBuilder();
        sb.append("<a href=\"javascript:void(0);\" onmouseover=\"return overlib('");
        sb.append(this.rulesInfo.get(Long.valueOf(rule.getId())));
        sb.append("', WIDTH, 768, LEFT, ABOVE, STICKY, CAPTION, 'Rule ");
        sb.append(rule.getId());
        sb.append("', MOUSEOFF);\" onmouseout=\"return nd();\">");
        sb.append(text != null ? text + " " : "");
        sb.append(Long.toString(rule.getId()));
        sb.append("</a>");
        return sb.toString();
    }
    
    private void printHtmlHeader() throws Exception {
        this.out.append("<html>\n");
        this.out.append("<head>\n");
        // Imports the tooltip javascript.
        this.out.append("   <script type='text/javascript' src='overlib.js'></script>\n");
        this.out.append("   <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/> \n");
        this.out.append("</head>\n");
        this.out.append("<body>\n");
        this.out.append("<div id='overDiv' style='position:absolute; visibility:hidden; z-index:1000;'></div>\n");
    }
    
    private void printHtmlFooter() throws Exception {
        this.out.append("</body>\n");
        this.out.append("</html>\n");
    }
    
    private void printRuleHeader(Rule rule) throws Exception {
        this.out.append("<table border='1' width='100%'>\n");
        this.out.append("   <tr align='center'>\n");
//      this.out.append("       <th colspan='2'>Rule ").append(Long.toString(rule.getId())).append("</th>\n");
        this.out.append("       <th colspan='2'>");
        this.out.append(this.prepareOverlib(rule, "Rule"));
        this.out.append("</th>\n");
        this.out.append("   </tr>\n");
    }
    
    private void printExamples(Example example, Sentence incorrectSentence) throws Exception {
        this.out.append("   <tr>\n");
        this.out.append("       <td width='10%'>Incorrect</td>\n");
//      this.out.append("       <td width='90%'>").append(example.getIncorrect()).append("</td>\n");
        this.out.append("       <td width='90%'>").append(this.prepareAnalysisOverlib(example.getIncorrect(), incorrectSentence)).append("</td>\n");
        this.out.append("   </tr>\n");
        this.out.append("   <tr>\n");
        this.out.append("       <td width='10%'>Correct</td>\n");
        this.out.append("       <td width='90%'>").append(example.getCorrect()).append("</td>\n");
        this.out.append("   </tr>\n");
    }
    
    private String prepareAnalysisOverlib(String incorrect, Sentence incorrectSentence) {
        StringBuilder sb = new StringBuilder();
        sb.append("<a href=\"javascript:void(0);\" onmouseover=\"return overlib('");
        sb.append(this.prepareAnalysisTable(incorrectSentence));
        sb.append("', WIDTH, 480, LEFT, ABOVE, STICKY, CAPTION, 'Analysis for &quot;");
        sb.append(incorrect);
        sb.append("&quot;', MOUSEOFF);\" onmouseout=\"return nd();\">");
        sb.append(incorrect);
        sb.append("</a>");
        return sb.toString();
    }
    
    private String prepareAnalysisTable(Sentence incorrectSentence) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table border=\"1\">");
        sb.append("<tr><th width=\"15%\">SyntTag</th><th width=\"15%\">ChunkTag</th><th width=\"20%\">Lexeme</th><th width=\"20%\">Primitive</th><th width=\"30%\">MorphoTag</th></tr>");
        for (List<String> row : getAnalysisAsTable(incorrectSentence)) {
            sb.append("<tr>");
            for (String column : row) {
                if(column == null) {
                    column = "null";
                }
                sb.append("<td>").append(column.equals("") ? "&nbsp;" : column).append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        return this.escapeHtmlChars(sb.toString());
    }
    
    public List<List<String>> getAnalysisAsTable(Sentence sentence) {
        if (sentence.getTokens().size() > 0 && sentence.getTokens().get(0).getPOSTag() == null) {
            throw new IllegalStateException("The sentence was not analyzed yet.");
        }
        
        List<List<String>> analysis = new ArrayList<List<String>>(5);
        for (int i = 0; i < sentence.getTokens().size(); i++) {
            List<String> row = new ArrayList<String>();
            row.add(sentence.getTokens().get(i).getSyntacticTag().toString());
            row.add(sentence.getTokens().get(i).getChunkTag().toString());
            row.add(sentence.getTokens().get(i).getLexeme());
            row.add(Arrays.toString(sentence.getTokens().get(i).getLemmas()));
            row.add(sentence.getTokens().get(i).getPOSTag() + "=" + sentence.getTokens().get(i).getFeatures());
            analysis.add(row);
        }
        return analysis;
    }
    
    private void evaluateMistakes(List<List<Mistake>> sentencesMistakes, List<Sentence> sentences, Rule rule) throws Exception {
        int zeroMatches = 0;
        int wrongMatches = 0;
        int correctMatches = 0;
        
        // sentencesMistakes.size() equals to the number of examples.
        int sentence = 0;
        for (List<Mistake> mistakes : sentencesMistakes) {
            Example example = rule.getExample().get(sentence);
            // Prints the incorrect and correct examples.
            this.printExamples(example, sentences.get(sentence));
            // Checks for null in case an exception occurred.
            if (mistakes != null) {
                if (mistakes.isEmpty()) {
                    zeroMatches++;
                } else { // There were mistakes.
                    for (Mistake mistake : mistakes) {
                        if (((MistakeImpl) mistake).getRuleIdentifier().equals(appendPrefix(rule.getId()))) {
                            correctMatches++;
                            this.out.append("   <tr bgcolor='00ff00'>\n");
                        } else {
                            wrongMatches++;
                            this.out.append("   <tr bgcolor='ff0000'>");
                        }
                        StringBuilder markedIncorrect = new StringBuilder(example.getIncorrect());
                        try {
                            markedIncorrect.insert(mistake.getStart(), ">>>");
                            markedIncorrect.insert(mistake.getEnd() + 3, "<<<");
                        } catch (RuntimeException e) {
                            this.logException(Long.valueOf(rule.getId()), example.getIncorrect());
                        }
                        // Incorrect sentence with the mistake between >>> and <<<.
                        this.out.append("       <td width='10%'>");
                        this.out.append(mistake.getRuleIdentifier());
                        this.out.append("</td>\n");
                        this.out.append("       <td width='90%'>").append(markedIncorrect);
                        for (String suggestion : mistake.getSuggestions()) {
                            this.out.append(" [");
                            this.out.append(escapeHtmlSpaces(suggestion));
                            this.out.append("]");
                        }
                        this.out.append("</td>\n");
                        this.out.append("   </tr>\n");
                    }
                }
            }
            sentence++;
        }
        
        if (zeroMatches > 0 && correctMatches == 0 && wrongMatches == 0) {
            this.no.add(rule);
        } else if (zeroMatches > 0 && correctMatches == 0 && wrongMatches > 0) {
            this.wrong.add(rule);
        } else if (zeroMatches > 0 && correctMatches > 0 && wrongMatches == 0) {
            this.partial.add(rule);
        } else if (zeroMatches > 0 && correctMatches > 0 && wrongMatches > 0) {
            this.partial.add(rule);
        } else if (zeroMatches == 0 && correctMatches == 0 && wrongMatches == 0) {
            // An exception occurred.
        } else if (zeroMatches == 0 && correctMatches == 0 && wrongMatches > 0) {
            this.wrong.add(rule);
        } else if (zeroMatches == 0 && correctMatches > 0 && wrongMatches == 0) {
            this.ok.add(rule);
            // point to try the suggestion
            checkSuggestion(rule, sentencesMistakes, sentences);
        } else if (zeroMatches == 0 && correctMatches > 0 && wrongMatches > 0) {
            this.partial.add(rule);
        }
    }
    
    private String appendPrefix(long id) {
        return "xml:" + Long.toString(id);
    }

    private void checkSuggestion(Rule rule, List<List<Mistake>> mistakesList, List<Sentence> sentences)
    {
        for (int i = 0; i < sentences.size(); i++) {
            Sentence s = sentences.get(i);
            List<Mistake> ml = mistakesList.get(i);
            for (Mistake mistake : ml) {
                String[] suggestions = mistake.getSuggestions();
                // at least one suggestion should fit
                boolean suggestionIsOk = false;
                for (String suggestion : suggestions) {
                    StringBuffer sb = new StringBuffer(s.getText());
                    sb.replace(mistake.getStart(), mistake.getEnd(), suggestion);
                    for (Example example : rule.getExample()) {
                        if(example.getIncorrect().contains(s.getText()))
                        {
                            if(example.getCorrect().contains(sb))
                            {
                                suggestionIsOk = true;
                            }
                            else
                            {
                                System.out.println();
                            }
                        }
                    }
                    
                }
                if(suggestionIsOk == false && suggestions.length > 0)
                {
                    this.badSuggestion.add(rule);
                }
            }
        }
    }
    
    private void printRuleFooter(Rule rule) throws Exception {
        this.out.append("   <tr>\n");
        this.out.append("       <td>Status</td>\n");
        if (this.no.contains(rule)) {
            this.out.append("       <td bgcolor='ff8000'></td>\n"); // orange.
        } else if (this.ok.contains(rule)) {
            this.out.append("       <td bgcolor='00ff00'></td>\n"); // green.
        } else if (this.wrong.contains(rule)) {
            this.out.append("       <td bgcolor='ff0000'></td>\n"); // red.
        } else if (this.partial.contains(rule)) {
            this.out.append("       <td bgcolor='ffff00'></td>\n"); // yellow.
        }
        this.out.append("   </tr>\n");
        this.out.append("</table>\n");
        this.out.append("<br />\n");
    }
    
    private void printReport(String type, List<Rule> rulesStatus, int totalRules, String color) throws Exception {
        this.out.append("<table border='1' width='100%'>\n");
        this.out.append("   <tr align='center' bgcolor='" + color + "'>\n");
        this.out.append("       <td>").append(type).append(" = ");
        this.out.append(rulesStatus.size() + "/" + totalRules);
        this.out.append(" = ");
        this.out.append(Double.toString((double) rulesStatus.size() / totalRules * 100));
        this.out.append("%</td>\n");
        this.out.append("   </tr>\n");
        this.out.append("   <tr>\n");
        this.out.append("       <td>");
        StringBuilder sb = new StringBuilder();
        for (Rule rule : rulesStatus) {
            sb.append(this.prepareOverlib(rule, null) + ", ");
        }
        if (sb.toString().endsWith(", ")) {
            sb.delete(sb.length() - 2, sb.length()); // Removes the extra ", " at the end.
        }
        this.out.append(sb);
        this.out.append("</td>\n");
        this.out.append("   </tr>\n");
        this.out.append("</table>\n");
        this.out.append("<br />\n");
    }
    
    private void printNoSuggestion() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (Rule rule : this.noSuggestions) {
            sb.append(this.prepareOverlib(rule, null) + ", ");
        }
        if (sb.toString().endsWith(", ")) {
            sb.delete(sb.length() - 2, sb.length()); // Removes the extra ", " at the end.
        }
        this.out.append("<table border='1' width='100%'>\n");
        this.out.append("   <tr>\n");
        this.out.append("       <th>no suggestions = ");
        this.out.append(Integer.toString(this.noSuggestions.size()) + "/" + this.rules.getRule().size());
        this.out.append(" = ");
        this.out.append(Double.toString((double) this.noSuggestions.size() / this.rules.getRule().size() * 100));
        this.out.append("%</th>\n");
        this.out.append("   </tr>\n");
        this.out.append("   <tr>\n");
        this.out.append("       <td>").append(sb.toString()).append("</td>\n");
        this.out.append("   </tr>\n");
        this.out.append("</table>\n");
    }
    
    private void printBadSuggestion() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (Rule rule : this.badSuggestion) {
            sb.append(this.prepareOverlib(rule, null) + ", ");
        }
        if (sb.toString().endsWith(", ")) {
            sb.delete(sb.length() - 2, sb.length()); // Removes the extra ", " at the end.
        }
        this.out.append("<table border='1' width='100%'>\n");
        this.out.append("   <tr>\n");
        this.out.append("       <th>bad suggestions = ");
        this.out.append(Integer.toString(this.badSuggestion.size()) + "/" + this.rules.getRule().size());
        this.out.append(" = ");
        this.out.append(Double.toString((double) this.badSuggestion.size() / this.rules.getRule().size() * 100));
        this.out.append("%</th>\n");
        this.out.append("   </tr>\n");
        this.out.append("   <tr>\n");
        this.out.append("       <td>").append(sb.toString()).append("</td>\n");
        this.out.append("   </tr>\n");
        this.out.append("</table>\n");
    }
    
    private void printExceptionReport() throws Exception {
        this.out.append("<table border='1' width='100%'>\n");
        this.out.append("   <tr>\n");
        boolean first = true;
        for (Entry<Long, List<String>> entry : this.exceptions.entrySet()) {
            Long id = entry.getKey();
            List<String> causes = entry.getValue();
            for (String cause : entry.getValue()) {
                if (first) {
                    this.out.append("       <td rowspan='").append(Integer.toString(causes.size())).append("'>\n");
                    this.out.append(id.toString());
                    this.out.append("       </td>");
                    this.out.append("       <td>");
                    this.out.append(cause);
                    this.out.append("       </td>");
                    this.out.append("   </tr>");
                } else {
                    this.out.append("   <tr>");
                    this.out.append("       <td>");
                    this.out.append(cause);
                    this.out.append("       </td>");
                }
            }
        }
        this.out.append("   </tr>\n");
        this.out.append("</table>");
    }
    
    private void storeNoSuggestionRule(Rule rule) {
        if (rule.getSuggestion().isEmpty()) {
            this.noSuggestions.add(rule);
        }
    }

    
    private void logException(Long ruleId, String incorrectExample) {
        // Stores the sentence that generated the exception.
        if (this.exceptions.get(ruleId) == null) {
            List<String> cause = new ArrayList<String>();
            cause.add(incorrectExample);
            this.exceptions.put(ruleId, cause);
        } else {
            this.exceptions.get(ruleId).add(incorrectExample);
        }
    }

    private String escapeHtmlSpaces(String string) {
        String escaped = string;
        escaped = escaped.replaceAll(" ", "&nbsp;");
        return escaped;
    }
    
    private String escapeHtmlChars(String string) {
        String escaped = string;
        escaped = escaped.replaceAll("\\\"", "&quot;");
        escaped = escaped.replaceAll("<", "&lt;");
        escaped = escaped.replaceAll(">", "&gt;");
        return escaped;
    }
    
    private String newLineToBr(String string) {
        return string.replaceAll("\\n", "<br/>");
    }
    
    public static void main(String[] args) throws Exception {
        CogrooHtml testCogrooHtml = new CogrooHtml();
        testCogrooHtml.test();
    }

}
