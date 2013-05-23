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
package cogroo.uima.eval;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import cogroo.uima.GoldenGrammarError;
import cogroo.uima.GoldenSentence;
import cogroo.uima.GrammarError;
import cogroo.uima.eval.HtmlWriter.Detail;

import com.google.common.base.Objects;

public class GrammarCheckerEvaluator extends JCasAnnotator_ImplBase {

  // public static final String PARAM_REPORTFMEASUER = "ReportFileFMeasure";
  // public static final String PARAM_REPORTDETAILS = "ReportFileDetails";

  public static final String PARAM_HTMLREPORTPATH = "HTMLReportPath";
  public static final String PARAM_CORPUSNAME = "CorpusName";
  public static final String PARAM_TEXTREPORTPATH = "TextReportPath";

  private BufferedWriter mReportF;
  private BufferedWriter mReportDetails;
  private Stats mFMeasure = new Stats();
  private Stats mFMeasureRules = new Stats();
  private HtmlWriter mHtmlWriter;

  /**
   * @see AnalysisComponent#initialize(UimaContext)
   */
  public void initialize(UimaContext aContext)
      throws ResourceInitializationException {
    String htmlReportPath = ((String) aContext
        .getConfigParameterValue(PARAM_HTMLREPORTPATH)).trim();
    String corpusName = ((String) aContext
        .getConfigParameterValue(PARAM_CORPUSNAME)).trim();
    String textReportPath = ((String) aContext
        .getConfigParameterValue(PARAM_TEXTREPORTPATH)).trim();
    
    new File(htmlReportPath).mkdirs();
    new File(htmlReportPath).mkdirs();

    String pathToReportFMeasure = textReportPath + "/" + corpusName
        + "-FMeasure.txt";
    String pathToReportDetails = textReportPath + "/" + corpusName
        + "-Details.txt";
    String pathToHtmlFMeasure = htmlReportPath + "/" + corpusName
        + "-FMeasure.html";
    String pathToHtmltDetails = htmlReportPath + "/" + corpusName
        + "-Details.html";

    try {
      mReportDetails = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(pathToReportDetails), "UTF-8"));
      mReportF = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(pathToReportFMeasure), "UTF-8"));
      mHtmlWriter = new HtmlWriter(pathToHtmlFMeasure, "Analysis", corpusName);
      mReportDetails
          .append("Type\tID\tTarget Err\tTarget Cat\tPred Err\tPred Cat\tRule\tRule Group\tSuggestion\tSentence\n");
    } catch (IOException e) {
      new RuntimeException("Couldn't init file", e);
    }
  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {

    List<Error> targetGrammarErrors = new ArrayList<Error>();
    List<Error> predictedGrammarErrors = new ArrayList<Error>();
    List<Error> sentences = new ArrayList<Error>();

    String docText = aJCas.getDocumentText();

    AnnotationIndex<Annotation> goldenGrammarErrorIndex = aJCas
        .getAnnotationIndex(GoldenGrammarError.type);
    for (Annotation annotation : goldenGrammarErrorIndex) {
      GoldenGrammarError a = (GoldenGrammarError) annotation;
      Error s = new Error(a.getBegin(), a.getEnd(), a.getCategory(), "TTT");
      targetGrammarErrors.add(s);
      mFMeasure.addTarget();
      mFMeasure.addTarget(a.getCategory());
    }

    AnnotationIndex<Annotation> grammarErrorIndex = aJCas
        .getAnnotationIndex(GrammarError.type);
    for (Annotation annotation : grammarErrorIndex) {
      GrammarError a = (GrammarError) annotation;
      String cat = a.getCategory();
      if (cat == null) {
        cat = "";
      }
      cat += "#" + a.getRuleId();
      Error s = new Error(a.getBegin(), a.getEnd(), cat, a.getReplace());
      s.setRuleId(a.getRuleId());
      predictedGrammarErrors.add(s);
    }

    AnnotationIndex<Annotation> sentIndex = aJCas
        .getAnnotationIndex(GoldenSentence.type);
    for (Annotation annotation : sentIndex) {
      GoldenSentence a = (GoldenSentence) annotation;
      Error s = new Error(a.getBegin(), a.getEnd(), a.getId(), "SSS");
      sentences.add(s);
      mFMeasure.addSentence();
    }

    normalizeTargetErrors(targetGrammarErrors, predictedGrammarErrors);
    List<Error> noRules = removeRules(predictedGrammarErrors);
    mFMeasure.updateScores(targetGrammarErrors, noRules);

    writeToReport(sentences, targetGrammarErrors, noRules,
        removeCat(predictedGrammarErrors), docText);
  }

  private List<Error> removeRules(List<Error> predictedGrammarErrors) {
    List<Error> out = new ArrayList<Error>();
    for (Error error : predictedGrammarErrors) {
      String cat = error.getType().substring(0, error.getType().indexOf("#"));
      Error e = new Error(error.getStart(), error.getEnd(), cat, error.getSuggestion());
      e.setRuleId(error.getRuleId());
      out.add(e);
    }
    return out;
  }

  private void writeToReport(List<Error> sentences,
      List<Error> targetGrammarErrors, List<Error> predictedGrammarErrors,
      List<String> rules, String docText) {

    Queue<Error> t = new LinkedList<Error>(targetGrammarErrors);
    Queue<Error> p = new LinkedList<Error>(predictedGrammarErrors);
    Queue<String> r = new LinkedList<String>(rules);

    SortedSet<ReportEntry> entries = new TreeSet<GrammarCheckerEvaluator.ReportEntry>();

    for (Error s : sentences) {
      while ((t.size() > 0 && s.contains(t.peek().getStart()))
          || (p.size() > 0 && s.contains(p.peek().getStart()))) {
        if (t.size() > 0 && p.size() > 0 && t.peek().equals(p.peek())) {
          addTP(p.peek().getType(), r.peek());
          System.out.println(docText.substring(s.getStart(), s.getEnd()));
          entries.add(new ReportEntry("TP", s, t.poll(), p.poll(), r.poll(),
              docText));
          mFMeasure.addTP();
        } else if (t.size() > 0 && p.size() > 0
            && t.peek().getStart() == p.peek().getStart()
            && t.peek().getEnd() == p.peek().getEnd()) {
          addFP(p.peek().getType(), r.peek());
          entries.add(new ReportEntry("FP*", s, t.poll(), p.poll(), r.poll(),
              docText));
          mFMeasure.addFP();
        } else if (t.size() > 0 && p.size() > 0
            && s.contains(t.peek().getStart())
            && s.contains(p.peek().getStart())) {
          // same sentence, sort it
          if (t.peek().getStart() <= p.peek().getStart()) {
            entries
                .add(new ReportEntry("FN", s, t.poll(), null, null, docText));
          } else {
            addFP(p.peek().getType(), r.peek());
            entries.add(new ReportEntry("FP", s, null, p.poll(), r.poll(),
                docText));
            mFMeasure.addFP();
          }
        } else if (t.size() > 0 && s.contains(t.peek().getStart())) {
          entries.add(new ReportEntry("FN", s, t.poll(), null, null, docText));
        } else if (p.size() > 0 && s.contains(p.peek().getStart())) {
          addFP(p.peek().getType(), r.peek());
          entries.add(new ReportEntry("FP", s, null, p.poll(), r.poll(),
              docText));
          mFMeasure.addFP();
        } else {
          throw new RuntimeException("Shouldn't get here.");
        }
      }
    }
    for (ReportEntry reportEntry : entries) {
      writeToReport(reportEntry);
    }

    if (t.size() > 0 || p.size() > 0) {

      throw new RuntimeException("Couldn't add some errors to report.");
    }
  }

  private void addFP(String type, String rule) {
    mFMeasure.addFP(type);
    if (rule == null) {
      System.out.println("aqui");
    }
    mFMeasureRules.addFP(rule);
  }

  private void addTP(String type, String rule) {
    mFMeasure.addTP(type);
    if (rule == null) {
      System.out.println("aqui");
    }
    mFMeasureRules.addTP(rule);
  }

  private List<String> removeCat(List<Error> predictedGrammarErrors) {
    List<String> ret = new ArrayList<String>(predictedGrammarErrors.size());
    for (Error s : predictedGrammarErrors) {
      ret.add(s.getType().substring(s.getType().indexOf("#") + 1));
    }
    return ret;
  }

  class ReportEntry implements Comparable<ReportEntry> {
    public final String type;
    public final Error selectedError;
    public final Error targetError;
    public final Error predictedError;
    public final String rule;
    public final String docText;

    public ReportEntry(String type, Error selected, Error target,
        Error predicted, String rule, String docText) {
      super();
      this.type = type;
      this.selectedError = selected;
      this.targetError = target;
      this.predictedError = predicted;
      this.rule = rule;
      this.docText = docText;
    }

    public int compareTo(ReportEntry other) {
      if (other == this) {
        return 0;
      } else {
        int val = selectedError.getType().compareTo(
            other.selectedError.getType());
        if (val != 0) {
          return val;
        }
        int minThis = getMinError(selectedError, targetError, predictedError);
        int minOther = getMinError(selectedError, targetError, predictedError);
        if (minThis != minOther) {
          return minThis - minOther;
        }
        return this.hashCode() - other.hashCode();
      }
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(type, selectedError, targetError, predictedError,
          rule, docText);
    }

    private int getMinError(Error selectedError2, Error targetError2,
        Error predictedError2) {
      int min = Integer.MAX_VALUE;
      if (selectedError2 != null && selectedError2.getStart() < min) {
        min = selectedError2.getStart();
      }
      if (targetError2 != null && targetError2.getStart() < min) {
        min = targetError2.getStart();
      }
      if (predictedError2 != null && predictedError2.getStart() < min) {
        min = predictedError2.getStart();
      }
      return min;
    }

  }

  private void writeToReport(ReportEntry re) {
    String type = re.type;
    Error s = re.selectedError;
    Error t = re.targetError;
    Error p = re.predictedError;
    String r = re.rule;
    String docText = re.docText;

    StringBuilder line = new StringBuilder();
    line.append(type + "\t");
    line.append(s.getType() + "\t");
    line.append(getDetail(t, docText) + "\t");
    line.append(getDetail(p, docText) + "\t");
    if (r != null) {
      line.append(r + "\t");
      line.append(getGroup(r) + "\t");
    } else {
      line.append("\t\t");
    }
    String text = s.getCoveredText(docText).toString()
        .replaceAll("[\t\n]", "   ");
    
    if(p != null) {
      line.append(p.getSuggestion()).append("\t");
    } else {
      line.append("\t");
    }
    
    line.append(text);
    // if(text.contains("Existe alguma criatura meio estátua, meio mulher?")) {
    // System.out.println();
    // }

    try {
      mReportDetails.append(line + "\n");
    } catch (IOException e) {

      throw new RuntimeException("Error writting to report.", e);
    }

  }

  private String getGroup(String r) {
    return RuleGroups.getGroup(r);
  }

  private String getDetail(Error p, String docText) {
    StringBuilder d = new StringBuilder();
    if (p != null) {
      d.append(p.getCoveredText(docText).toString().replaceAll("[\t\n]", "   ")
          + "\t");
      d.append(p.getType());
    } else {
      d.append("\t");
    }

    return d.toString();
  }

  private void normalizeTargetErrors(List<Error> targetGrammarErrors,
      List<Error> predictedGrammarErrors) {
    // usually the target is larger than predicted...
    for (int i = 0; i < targetGrammarErrors.size(); i++) {
      Error t = targetGrammarErrors.get(i);
      boolean cont = true;
      for (int j = 0; j < predictedGrammarErrors.size() && cont; j++) {
        Error p = predictedGrammarErrors.get(j);
        if (t.intersects(p)) {
          if (p.getType().contains(t.getType())
              || p.getType().matches("^#\\d+")) {
            targetGrammarErrors.set(i,
                new Error(p.getStart(), p.getEnd(), t.getType()));
            if (p.getType().contains("|")) {
              String rule = p.getType().substring(p.getType().indexOf("#") + 1);
              String cat = t.getType() + "#" + rule;
              predictedGrammarErrors.set(j, new Error(p.getStart(), p.getEnd(),
                  cat));
            }
            cont = false;
          }

        }
      }

    }
  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    super.collectionProcessComplete();
    try {
      String summary = mFMeasure.toString();
      mReportF.write(summary);

      List<Detail> details = new ArrayList<HtmlWriter.Detail>();

      details.add(new Detail("summaryCat",
          "Verdadeiros positivos, falsos positivos e target por categoria",
          mFMeasure.toFP_TP_Target_Table()));
      details.add(new Detail("summaryRules",
          "Verdadeiros positivos e falsos positivos por regra", mFMeasureRules
              .toFP_TP_Table()));

      mHtmlWriter.addData(summary, details, mFMeasure.getData(),
          mFMeasure.getSentences());
      mHtmlWriter.render();

    } catch (Exception e1) {
      e1.printStackTrace();
      new RuntimeException("Couldn't write to file", e1);
    }
    try {
      mReportDetails.close();
      mReportF.close();
    } catch (IOException e) {
      new RuntimeException("Couldn't close file", e);
    }
  }

}
