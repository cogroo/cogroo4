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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import opennlp.tools.util.eval.FMeasure;
import cogroo.uima.Pair;
import cogroo.uima.ae.Categories;

public class Stats {

  private FMeasure mGeneralFMeasure = new FMeasure();
  private Map<String, FMeasure> mFMeasureForOutcome = new HashMap<String, FMeasure>();

  private Map<String, AtomicInteger> tpForOutcome = new HashMap<String, AtomicInteger>();
  private Map<String, AtomicInteger> fpForOutcome = new HashMap<String, AtomicInteger>();
  private Map<String, AtomicInteger> targetForOutcome = new HashMap<String, AtomicInteger>();

  private int tp = 0;
  private int fp = 0;
  private int target = 0;
  private int senteces = 0;

  public void addTP() {
    tp++;
  }

  public void addFP() {
    fp++;
  }

  public void addTarget() {
    target++;
  }

  public void addSentence() {
    senteces++;
  }

  public void addTP(String type) {
    if (!tpForOutcome.containsKey(type))
      tpForOutcome.put(type, new AtomicInteger());

    tpForOutcome.get(type).incrementAndGet();
  }

  public void addFP(String type) {
    if (!fpForOutcome.containsKey(type))
      fpForOutcome.put(type, new AtomicInteger());

    fpForOutcome.get(type).incrementAndGet();
  }

  public void addTarget(String type) {
    if (!targetForOutcome.containsKey(type)) {
      targetForOutcome.put(type, new AtomicInteger());
    }

    targetForOutcome.get(type).incrementAndGet();
  }

  @SuppressWarnings("deprecation")
  public void updateScores(List<Error> target, List<Error> predicted) {
    mGeneralFMeasure.updateScores(toArray(target), toArray(predicted));

    Map<String, Pair<List<Error>, List<Error>>> m = new HashMap<String, Pair<List<Error>, List<Error>>>();

    for (Error t : target) {
      if (!m.containsKey(t.getType())) {
        m.put(t.getType(), new Pair<List<Error>, List<Error>>(
            new ArrayList<Error>(), new ArrayList<Error>()));
      }
      m.get(t.getType()).a.add(t);
    }

    for (Error p : predicted) {
      if (!m.containsKey(p.getType())) {
        m.put(p.getType(), new Pair<List<Error>, List<Error>>(
            new ArrayList<Error>(), new ArrayList<Error>()));
      }
      m.get(p.getType()).b.add(p);
    }

    for (String type : m.keySet()) {
      if (!mFMeasureForOutcome.containsKey(type)) {
        mFMeasureForOutcome.put(type, new FMeasure());
      }
      mFMeasureForOutcome.get(type).updateScores(toArray(m.get(type).a),
          toArray(m.get(type).b));
    }
  }

  private Error[] toArray(List<Error> l) {
    return l.toArray(new Error[l.size()]);
  }

  public String toFP_TP_Table() {

    StringBuilder sb = new StringBuilder();

    SortedSet<String> set = new TreeSet<String>(new CompRule());
    // set.addAll(tpForOutcome.keySet());
    // set.addAll(fpForOutcome.keySet());

    set.addAll(Categories.getRules());

    sb.append(String.format(" %6s %3s %3s\n", "Regra", "TP", "FP"));

    for (String type : set) {
      sb.append(String.format(tpfp, type.replace("xml:", ""),
          zeroOrValue(tpForOutcome.get(type)),
          zeroOrValue(fpForOutcome.get(type))));
    }

    return sb.toString();

  }

  static String tpfp = " %6s %3d %3d\n";

  public String toFP_TP_Target_Table() {

    StringBuilder sb = new StringBuilder();

    SortedSet<String> set = new TreeSet<String>(new CompCat());
    // set.addAll(tpForOutcome.keySet());
    // set.addAll(fpForOutcome.keySet());
    // set.addAll(targetForOutcome.keySet());

    set.addAll(Categories.getCategories());

    sb.append(String.format("%8s %3s %3s %3s\n", "Regra", "TP", "FP", "Tar"));

    for (String type : set) {
      sb.append(String.format("%8s %3d %3d %3d\n", type,
          zeroOrValue(tpForOutcome.get(type)),
          zeroOrValue(fpForOutcome.get(type)),
          zeroOrValue(targetForOutcome.get(type))));
    }

    return sb.toString();

  }

  @Override
  public String toString() {
    StringBuilder ret = new StringBuilder();
    int found = tp + fp;
    ret.append("processed " + senteces + " sentences with " + target
        + " grammar errors; found: " + found + "; correct: " + tp + ".\n");

    ret.append(String.format(format, "TOTAL",
        zeroOrPositive(mGeneralFMeasure.getPrecisionScore() * 100),
        zeroOrPositive(mGeneralFMeasure.getRecallScore() * 100),
        zeroOrPositive(mGeneralFMeasure.getFMeasure() * 100)));
    ret.append("\n");
    SortedSet<String> set = new TreeSet<String>(new Comp());
    set.addAll(targetForOutcome.keySet());
    set.addAll(fpForOutcome.keySet());
    for (String type : set) {

      ret.append(String
          .format(
              format + extra,
              type,
              zeroOrPositive(mFMeasureForOutcome.get(type).getPrecisionScore() * 100),
              zeroOrPositive(mFMeasureForOutcome.get(type).getRecallScore() * 100),
              zeroOrPositive(mFMeasureForOutcome.get(type).getFMeasure() * 100),
              zeroOrValue(targetForOutcome.get(type)),
              zeroOrValue(tpForOutcome.get(type)),
              zeroOrValue(fpForOutcome.get(type))));
      ret.append("\n");
    }

    return ret.toString();
  }

  public Map<String, Data> getData() {
    SortedMap<String, Data> res = new TreeMap<String, Stats.Data>(new Comp());

    res.put("TOTAL", new Data("TOTAL", mGeneralFMeasure, target, tp, fp));
    SortedSet<String> set = new TreeSet<String>(new Comp());
    set.addAll(targetForOutcome.keySet());
    set.addAll(fpForOutcome.keySet());
    for (String type : set) {
      res.put(
          type,
          new Data(type, mFMeasureForOutcome.get(type),
              zeroOrValue(targetForOutcome.get(type)), zeroOrValue(tpForOutcome
                  .get(type)), zeroOrValue(fpForOutcome.get(type))));
    }

    return res;
  }

  private double zeroOrPositive(double v) {
    if (v < 0) {
      return 0;
    }
    return v;
  }

  private int zeroOrValue(AtomicInteger atomicInteger) {
    if (atomicInteger != null)
      return atomicInteger.get();
    return 0;
  }

  public int getSentences() {
    return senteces;
  }

  private String getAsPercentage(double d) {
    return MessageFormat.format("{0,number,#.##%}", d);
  }

  static String n = "%\u00207.2f%%";
  static String format = "%8s: precision: " + n + ";  recall: " + n + "; F1: "
      + n + ".";
  static String extra = " [target: %3d; tp: %3d; fp: %3d]";

  public static void main(String[] args) {
    double precision = 0.3181818181818182 * 100;
    double recall = 0.2692307692307692 * 100;
    double f = 0.2916666666666667 * 100;

    System.out.println(String.format(format, "ADVP", precision, recall, f,
        10000, 10, 0));
    precision *= -1;
    recall = 0;
    System.out.println(String.format(format, "ADVP", precision, recall, f));
    precision = -100;
    recall = 100;
    System.out.println(String.format(format, "ADVP", precision, recall, f));

    System.out.println(String.format(tpfp, "ADVP", 1000, 2000));
  }

  private class Comp implements Comparator<String> {
    public int compare(String o1, String o2) {
      if (o1.equals(o2))
        return 0;
      double t1 = 0;
      double t2 = 0;

      if (mFMeasureForOutcome.containsKey(o1))
        t1 += mFMeasureForOutcome.get(o1).getFMeasure();
      if (mFMeasureForOutcome.containsKey(o2))
        t2 += mFMeasureForOutcome.get(o2).getFMeasure();

      t1 = zeroOrPositive(t1);
      t2 = zeroOrPositive(t2);

      if (t1 + t2 > 0d) {
        if (t1 > t2)
          return -1;
        return 1;
      }
      return o1.compareTo(o2);
    }

  }

  private class Comp2 implements Comparator<String> {
    public int compare(String o1, String o2) {
      if (o1.equals(o2))
        return 0;

      double fp1 = 0;
      double fp2 = 0;

      if (fpForOutcome.get(o1) != null)
        fp1 = fpForOutcome.get(o1).get();
      if (fpForOutcome.get(o2) != null)
        fp2 = fpForOutcome.get(o2).get();

      double tp1 = 0;
      double tp2 = 0;

      if (tpForOutcome.get(o1) != null)
        tp1 = tpForOutcome.get(o1).get();
      if (tpForOutcome.get(o2) != null)
        tp2 = tpForOutcome.get(o2).get();

      double p1 = tp1 / (tp1 + fp1);
      double p2 = tp2 / (tp2 + fp2);

      if (p1 == p2) {
        return o1.compareTo(o2);
      } else if (p2 < p1) {
        return 1;
      }
      return -1;
    }

  }

  private class CompCat implements Comparator<String> {
    public int compare(String o1, String o2) {
      return o1.compareTo(o2);
    }

  }

  private class CompRule implements Comparator<String> {
    public int compare(String o1, String o2) {

      try {
        String r1 = o1.replace("xml:", "");
        String r2 = o2.replace("xml:", "");

        return new Integer(r1).compareTo(new Integer(r2));
      } catch (Exception e) {
        return o1.compareTo(o2);
      }
    }

  }

  public class Data {
    public final FMeasure f;
    public final int target;
    public final int tp;
    public final int fp;

    public final String cat;

    public Data(String cat, FMeasure f, int target, int tp, int fp) {
      super();
      this.cat = cat;
      this.f = f;
      this.target = target;
      this.tp = tp;
      this.fp = fp;
    }

    public FMeasure getF() {
      return f;
    }

    public double getPre() {
      return zeroOrPositive(f.getPrecisionScore() * 100);
    }

    public double getRec() {
      return zeroOrPositive(f.getRecallScore() * 100);
    }

    public int getTarget() {
      return target;
    }

    public int getSelected() {
      return tp + fp;
    }

    public int getTp() {
      return tp;
    }

    public int getFp() {
      return fp;
    }

    public String getCat() {
      return cat;
    }

  }
}
