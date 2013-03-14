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
package org.cogroo.gc.cmdline.dictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.cmdline.ArgumentParser.OptionalParameter;
import opennlp.tools.cmdline.ArgumentParser.ParameterDescription;
import opennlp.tools.cmdline.BasicCmdLineTool;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.TerminateToolException;
import opennlp.tools.postag.Triple;
import opennlp.tools.util.featuregen.StringPattern;

import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.formats.ad.ADFeaturizerSampleStream;
import org.cogroo.interpreters.FlorestaTagInterpreter;
import org.cogroo.interpreters.JspellTagInterpreter;
import org.cogroo.interpreters.TagInterpreter;
import org.cogroo.tools.featurizer.FeatureSample;

public class TabSeparatedPOSDictionaryBuilderTool extends
BasicCmdLineTool {

  interface Params extends POSDictionaryBuilderParams {

    @ParameterDescription(valueName = "includeFetures", description = "include features")
    @OptionalParameter(defaultValue = "false")
    Boolean getIsIncludeFeatures();

    @ParameterDescription(valueName = "includeFromCorpus", description = "include from corpus")
    @OptionalParameter(defaultValue = "false")
    Boolean getIncludeFromCorpus();

    @ParameterDescription(valueName = "expandME", description = "include from corpus")
    @OptionalParameter(defaultValue = "false")
    Boolean getExpandME();
  }

  public String getShortDescription() {
    return "builds a new tab separated lexical dictionary to be used with FSA builder";
  }

  public String getHelp() {
    return getBasicHelp(Params.class);
  }

  public void run(String[] args) {
    Params params = validateAndParseParams(args, Params.class);

    File dictInFile = params.getInputFile();
    File dictOutFile = params.getOutputFile();
    File corpusFile = params.getCorpus();
    Charset encoding = params.getEncoding();

    CmdLineUtil.checkInputFile("dictionary input file", dictInFile);
    CmdLineUtil.checkOutputFile("dictionary output file", dictOutFile);
    CmdLineUtil.checkInputFile("corpus input file", corpusFile);

    InputStreamReader in = null;
    OutputStreamWriter out = null;
    try {

      // load corpus tags

      ADFeaturizerSampleStream sentenceStream = new ADFeaturizerSampleStream(
          new FileInputStream(corpusFile), "ISO-8859-1", params.getExpandME());
      Set<String> knownFeats = new HashSet<String>();
      Set<String> knownPostags = new HashSet<String>();
      FeatureSample sample = sentenceStream.read();
      while (sample != null) {
        Collections.addAll(knownFeats, sample.getFeatures());
        Collections.addAll(knownPostags, sample.getTags());
        sample = sentenceStream.read();
      }

      sentenceStream.close();

      in = new InputStreamReader(new FileInputStream(dictInFile), encoding);
      
      SortedMap<String, Set<Triple>> entries = new TreeMap<String, Set<Triple>>();
      
//      Multimap<String, Triple> entries = ArrayListMultimap.create(500000, 1);
//      TreeMultimap<String, Triple> entries = TreeMultimap.create();
//      Multimap<String, Triple> entries = HashMultimap.create(500000, 1);

      parseOneEntryPerLine(in, entries, new JspellTagInterpreter(),
          new FlorestaTagInterpreter(), knownFeats, knownPostags,
          params.getAllowInvalidFeats(), params.getIsIncludeFeatures());

      in.close();

      if (params.getIncludeFromCorpus()) {
        sentenceStream = new ADFeaturizerSampleStream(new FileInputStream(
            corpusFile), "ISO-8859-1", params.getExpandME());
        sample = sentenceStream.read();
        while (sample != null) {
          String[] toks = sample.getSentence();
          String[] lemmas = sample.getLemmas();
          String[] tags = sample.getTags();
          String[] feats = sample.getFeatures();

          for (int i = 0; i < toks.length; i++) {
            String tok;
            if(!"prop".equals(tags[i])) {
              tok = toks[i].toLowerCase();
            } else {
              tok = toks[i];
            }
            
            if (isValid(entries.get(tok), tok, tags[i], lemmas[i], feats[i],
                params.getIsIncludeFeatures())) {
              Triple t = asTriple(tags[i], lemmas[i], feats[i],
                  params.getIsIncludeFeatures());
              put(tok, t, entries);
              System.err.println("  added: " + tok + ": " + t);
            }
          }

          sample = sentenceStream.read();
        }
        sentenceStream.close();
      }

      out = new OutputStreamWriter(new FileOutputStream(dictOutFile), "UTF-8");

      for (String token : entries.keySet()) {
        for (Triple triple : entries.get(token)) {
          out.append(toString(token, triple));
        }
      }

      out.close();

    } catch (IOException e) {
      throw new TerminateToolException(-1,
          "IO error while reading training data or indexing data: "
              + e.getMessage());
    }  catch (Exception e) {
      throw new TerminateToolException(-1,
          "Exception: "
              + e.getMessage());
    } finally {
      try {
        in.close();
        out.close();
      } catch (IOException e) {
        // sorry that this can fail
      }
    }

  }

  private static void put(String tok, Triple t, SortedMap<String, Set<Triple>> entries) {
    if(!entries.containsKey(tok)) {
      entries.put(tok, new HashSet<Triple>());
    }
    entries.get(tok).add(t);
  }

  private boolean isValid(Collection<Triple> knownTriples, String tok, String clazz,
      String lemma, String feats, boolean includeFeatures) {
    if(StringPattern.recognize(tok).containsDigit())
      return false; //no numbers...
    
    // no B- I-
    if(clazz.startsWith("B-") || clazz.startsWith("I-")) {
      return false;
    }
    
    if (knownTriples != null && knownTriples.size() > 0) {
      // check if we already have this entry ignoring the lemma...
      Set<String> entries = new HashSet<String>();
      for (Triple t : knownTriples) {
        String tFeat = null;
        if (includeFeatures) {
          tFeat = t.getFeats();
        }
        entries.add(t.getClazz() + "|" + tFeat);
      }

      String f = null;
      if (includeFeatures) {
        f = feats;
      }

      if (entries.contains(clazz + "|" + f)) {
        return false;
      }
    }
    return true;
  }

  public static void parseOneEntryPerLine(Reader in,
      SortedMap<String, Set<Triple>> entries, TagInterpreter tago,
      TagInterpreter tagd, Set<String> knownFeats, Set<String> knownPostags,
      boolean allowInvalidFeats, boolean includeFeatures) throws IOException {

    knownFeats = new TreeSet<String>(knownFeats);
    
    if(!includeFeatures) {
      // force ignore unknown features
      allowInvalidFeats = true;
    }

    BufferedReader lineReader = new BufferedReader(in);

    String line;

    Set<String> unknownTags = new TreeSet<String>();

    while ((line = lineReader.readLine()) != null) {
      StringTokenizer whiteSpaceTokenizer = new StringTokenizer(line, " ");

      String word = whiteSpaceTokenizer.nextToken();

      while (whiteSpaceTokenizer.hasMoreTokens()) {
        String data = whiteSpaceTokenizer.nextToken();
        String[] lemmaTag = data.split(">");

        if (lemmaTag.length != 2) {
          System.err.println("** Invalid lemmatag. " + word + " -> " + data);
        } else {

          // convert the jspell tag to floresta tag
          MorphologicalTag completeTag = tago
              .parseMorphologicalTag(lemmaTag[1]);

          if (completeTag == null || completeTag.getClazzE() == null) {
            System.err.println("-- Missing class tag. " + word + " -> " + data);
          } else {
            MorphologicalTag classMT = new MorphologicalTag();
            classMT.setClazz(completeTag.getClazzE());
            String classString = tagd.serialize(classMT);
            if(classString == null) {
              System.out.println("erro :(");
            }

            MorphologicalTag featsMT = completeTag.clone();
            featsMT.setClazz(null);
            
            String featsString = null;
            
            if(!featsMT.isEmpty()) {
            	featsString = tagd.serialize(featsMT);
            }
            
            if (featsString == null || featsString.length() == 0) {
              featsString = "-";
            }
            
            if(classString.startsWith("v-") && word.contains("-")) {
              // don't add
//              System.err.println("ignore " + word);
            } else if ("pron".equals(classString)) {
              // change to pron-det and pron-indp
              if (knownFeats.contains(featsString) || allowInvalidFeats) {
                put(
                    word,
                    asTriple("pron-det", lemmaTag[0], featsString,
                        includeFeatures), entries);
                put(
                    word,
                    asTriple("pron-indp", lemmaTag[0], featsString,
                        includeFeatures), entries);
              }
            } else if (classString != null
                && knownPostags.contains(classString)
                && (knownFeats.contains(featsString) || allowInvalidFeats)) {
              put(
                  word,
                  asTriple(classString, lemmaTag[0], featsString,
                      includeFeatures), entries);
            } else {
              if ("pnt".equals(classString) && knownPostags.contains(word)) {
                put(word, asTriple(word, word, null, includeFeatures), entries);
              } else if (!classString.startsWith("v-"))
                System.err.println("unknown - "
                    + word
                    + " -> "
                    + new Triple(classString, lemmaTag[0], classString + "_"
                        + featsString));
              unknownTags.add(classString + "_" + featsString);
            }
          }
        }
      }

    }

    if (knownFeats.size() > 0) {
      System.err.print("Known tags:");
      for (String tag : knownFeats) {
        System.err.print(" " + tag);
      }
      System.err.println();
    }

    if (unknownTags.size() > 0) {
      System.err.print("Found unknown tags:");
      for (String tag : unknownTags) {
        System.err.print(" " + tag);
      }
      System.err.println();
    }
  }

  private static Triple asTriple(String clazz, String lemma, String feats,
      boolean includeFeatures) {
    if (includeFeatures)
      return new Triple(clazz, lemma, feats);
    return new Triple(clazz, lemma, null);
  }

  private static final char HT = '\t';
  private static final char NL = '\n';

  private static String toString(String word, Triple t) {
    StringBuilder sb = new StringBuilder();
    sb.append(word).append(HT).append(t.getLemma()).append(HT)
        .append(t.getClazz());
    if (t.getFeats() != null && t.getFeats().length() > 0) {
      sb.append("#").append(t.getFeats());
    }
    sb.append(NL);
    return sb.toString();
  }
}
