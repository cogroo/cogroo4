package cogroo.uima.eval;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import opennlp.tools.util.eval.FMeasure;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import cogroo.uima.Pair;
import cogroo.uima.ae.Categories;
import cogroo.uima.eval.Stats.Data;

public class HtmlWriter {

  private BufferedWriter mWriter;
  private STGroupFile mSTGroup;

  private String mHeader;
  private String mFooter;
  private String mTitle;
  private String mOverrall;
  private List<String> mOrderedCat = new ArrayList<String>();
  private Map<String, String> mIndexElements = new HashMap<String, String>();
  private List<String> mScripts = new ArrayList<String>();
  private Map<String, String> mCharts = new HashMap<String, String>();

  public HtmlWriter(String htmlFile, String type, String corpus)
      throws IOException {

    mWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
        htmlFile), "UTF-8"));
    mSTGroup = new STGroupFile(this.getClass().getClassLoader()
        .getResource("cogroo/uima/eval/templates.stg"), "UTF-8", '$', '$');

    this.mHeader = genHeader(corpus);
    this.mFooter = genFooter();
    this.mTitle = genTitle(type, corpus);

  }

  public void addData(String summary, List<Detail> details,
      Map<String, Data> results, int sentences) {

    Data overall = results.remove("TOTAL");
    mOverrall = genOverall(summary, details, overall, sentences,
        results.values());

    mScripts.add(genOverallCollScript(overall, results.values()));

    mScripts.add(genOverallPieScript(overall, results.values()));

    mScripts.add(genFPRatio(overall, results.values(), sentences));

    //
    // mScripts.add(genOverallCollScript(results.values()));
    //
    // mOverrall += genOverallPieChart();
    // mOverrall += genOverallCollChart();

    // results.remove("TOTAL");
    //
    // for (String cat : results.keySet()) {
    // mOrderedCat.add(cat);
    // mScripts.put(cat, genScript(results.get(cat)));
    // mIndexElements.put(cat, genIndex(cat));
    // mCharts.put(cat, genCharts(cat));
    // }

  }

  private String genChart(String id) {
    ST st = mSTGroup.getInstanceOf("chart");
    st.add("id", id);
    String result = st.render();

    return result;
  }

  private String genOverallPieScript(Data overall, Collection<Data> data) {
    final double grads = .02d;
    double limit = grads * overall.getTarget();

    Collection<Data> categoriesDistrib = new ArrayList<Stats.Data>();

    int covered = 0;
    int uncovered = 0;

    for (Data d : data) {
      if (!d.cat.equals("TOTAL")) {
        categoriesDistrib.add(d);
        if (Categories.isCategoryImplemented(d.cat)) {
          covered += d.target;
        } else {
          uncovered += d.target;
        }
      }
    }
    ST st = mSTGroup.getInstanceOf("pieScriptA");
    st.add("data", categoriesDistrib);
    st.add("sliceVisibilityThreshold", grads);
    String result = st.render();

    List<Pair<String, Integer>> coveredData = new ArrayList<Pair<String, Integer>>(
        2);
    coveredData.add(new Pair<String, Integer>("Erros cobertos", new Integer(
        covered)));

    coveredData.add(new Pair<String, Integer>("Erros descobertos", new Integer(
        uncovered)));

    ST stA = mSTGroup.getInstanceOf("pieScriptGeneral");
    // uniqueId, col1, col2, data, title
    stA.add("uniqueId", "coveredCat");
    stA.add("col1", "Erros cobertos");
    stA.add("col2", "Erros descobertos");
    stA.add("title", "Proporção de erros com categorias cobertas");
    stA.add("data", coveredData);
    result += stA.render();

    return result;

  }

  private String genOverallCollScript(Data overall, Collection<Data> collection) {
    Collection<Data> list = new ArrayList<Stats.Data>();
    for (Data d : collection) {
      if (!d.cat.equals("TOTAL") && d.getPre() + d.getRec() > 0) {
        list.add(d);
      }
    }
    ST st = mSTGroup.getInstanceOf("collAllScript");
    st.add("data", list);
    st.add("overall", overall);

    st.add("rows", list.size() + 1);

    String result = st.render();

    return result;
  }

  private String genFPRatio(Data overall, Collection<Data> values, int senteces) {
    // get fp / 1000 for each cat
    // final double ratio = 10000;
    List<Pair<String, Integer>> r = new ArrayList<Pair<String, Integer>>();

    r.add(new Pair<String, Integer>("TOTAL", overall.getFp()));

    for (Data d : values) {
      if (!d.cat.equals("TOTAL") && d.getFp() > 0) {
        r.add(new Pair<String, Integer>(d.cat, d.getFp()));
      }
    }
    ST st = mSTGroup.getInstanceOf("genFPRatio");
    st.add("r", r);

    st.add("rows", r.size());

    String result = st.render();

    return result;
  }

  private String genOverall(String summary, List<Detail> details,
      Data totalData, int sentences, Collection<Data> data) {

    // create list of categories
    SortedSet<String> categories = new TreeSet<String>();
    SortedSet<String> uncovered = new TreeSet<String>();

    for (Data d : data) {
      if (!d.cat.equals("TOTAL")) {
        categories.add(d.cat);

        if (!Categories.isCategoryImplemented(d.cat)) {
          uncovered.add(d.cat);
        }
      }
    }

    List<Pair<String, String>> categoriesDesc = new ArrayList<Pair<String, String>>();
    for (String c : categories) {
      categoriesDesc.add(new Pair<String, String>(c, Categories
          .getCategoryDescription(c)));
    }

    List<Pair<String, String>> uncoveredCategoriesDesc = new ArrayList<Pair<String, String>>();
    for (String c : uncovered) {
      uncoveredCategoriesDesc.add(new Pair<String, String>(c, Categories
          .getCategoryDescription(c)));
    }

    // summary, target, selected, tp
    ST st = mSTGroup.getInstanceOf("overall");
    st.add("summary", summary);
    st.add("details", details);
    st.add("sentences", sentences);
    st.add("categoriesDesc", categoriesDesc);
    st.add("uncovered", uncoveredCategoriesDesc);
    st.add("target", totalData.target);
    int selected = totalData.tp + totalData.fp;
    st.add("selected", selected);
    st.add("tp", totalData.tp);

    int min = totalData.target + totalData.tp + selected + 1;

    if (totalData.tp > 0 && totalData.tp < min) {
      min = totalData.tp;
    }

    if (totalData.target > 0 && totalData.target < min) {
      min = totalData.target;
    }

    if (selected > 0 && selected < min) {
      min = selected;
    }

    st.add("targetMin", totalData.target * 10 / min);
    st.add("selectedMin", (selected * 10) / min);
    st.add("tpMin", (totalData.tp * 10) / min);

    List<DataMin> d = new ArrayList<DataMin>();
    for (Data c : data) {
      if (c.getSelected() > 0 && c.getTarget() > 0) {
        d.add(new DataMin(c.getCat(), c.getSelected(), c.getTarget(), c.getTp()));
      }
    }

    st.add("data", d);
    String result = st.render();

    return result;
  }

  public class DataMin {
    private int selected;
    private int target;
    private int tp;

    private int selectedMin;
    private int targetMin;
    private int tpMin;

    private String cat;

    public DataMin(String cat, int selected, int target, int tp) {
      super();
      this.selected = selected;
      this.target = target;
      this.tp = tp;
      this.cat = cat;

      int min = selected + target + tp + 1;

      if (selected > 0 && selected < min) {
        min = selected;
      }

      if (target > 0 && target < min) {
        min = target;
      }

      if (tp > 0 && tp < min) {
        min = tp;
      }

      this.selectedMin = selected * 10 / min;
      this.targetMin = target * 10 / min;
      this.tpMin = tp * 10 / min;
    }

    public int getSelectedMin() {
      return selectedMin;
    }

    public int getTargetMin() {
      return targetMin;
    }

    public int getTpMin() {
      return tpMin;
    }

    public int getSelected() {
      return selected;
    }

    public int getTarget() {
      return target;
    }

    public String getCat() {
      return cat;
    }

    public int getTp() {
      return tp;
    }

  }

  private String genTitle(String type, String corpus) {
    ST st = mSTGroup.getInstanceOf("title");
    st.add("corpus", corpus);
    st.add("type", type);
    Object[] arguments = { new Date(System.currentTimeMillis()) };
    String date = MessageFormat.format("{0,time} - {0,date}", arguments);
    st.add("date", date);
    String result = st.render();

    return result;
  }

  private String genHeader(String corpus) {
    ST st = mSTGroup.getInstanceOf("header");
    st.add("corpus", corpus);
    String result = st.render();

    return result;
  }

  private String genFooter() {
    ST st = mSTGroup.getInstanceOf("close");
    String result = st.render();

    return result;
  }

  public void render() {

    write(mHeader);
    for (String s : mScripts) {
      write(s);
    }
    write(mTitle);

    write(mOverrall);

    write(mFooter);

    try {
      mWriter.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void write(String result) {
    System.out.println(result);
    try {
      mWriter.append(result);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static class Detail {
    private String id;
    private String desc;
    private String data;

    public Detail(String id, String desc, String data) {
      super();
      this.id = id;
      this.desc = desc;
      this.data = data;
    }

    public String getId() {
      return id;
    }

    public String getDesc() {
      return desc;
    }

    public String getData() {
      return data;
    }

  }

  public static void main(String[] args) throws IOException {
    HtmlWriter h = new HtmlWriter(
        "/Users/wcolen/Documents/Mestrado/resultados/html/metro.html",
        "Análise", "Metrô");
    Map<String, Data> m = new HashMap<String, Stats.Data>();
    FMeasure f = new FMeasure();
    Stats s = new Stats();

    h.addData("a summary", null, s.getData(), 1);

    h.render();
  }

}
