/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.ccsl.cogroo.formats.ad;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

import opennlp.tools.formats.ad.ADSentenceStream;
import opennlp.tools.formats.ad.ADSentenceStream.Sentence;
import opennlp.tools.formats.ad.ADSentenceStream.SentenceParser.Leaf;
import opennlp.tools.formats.ad.ADSentenceStream.SentenceParser.Node;
import opennlp.tools.formats.ad.ADSentenceStream.SentenceParser.TreeElement;
import opennlp.tools.postag.POSSample;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.StringUtil;

/**
 * <b>Note:</b> Do not use this class, internal use only!
 */
public class ADExPOSSampleStream implements ObjectStream<POSSample> {

  private final ObjectStream<ADSentenceStream.Sentence> adSentenceStream;
  private boolean expandME;
  private boolean isIncludeFeatures;
  private boolean additionalContext;
  
  private static final Pattern hyphenPattern = Pattern.compile("((\\p{L}+)-$)|(^-(\\p{L}+)(.*))|((\\p{L}+)-(\\p{L}+)(.*))");

  /**
   * Creates a new {@link POSSample} stream from a line stream, i.e.
   * {@link ObjectStream}< {@link String}>, that could be a
   * {@link PlainTextByLineStream} object.
   * 
   * @param lineStream
   *          a stream of lines as {@link String}
   * @param expandME
   *          if true will expand the multiword expressions, each word of the
   *          expression will have the POS Tag that was attributed to the
   *          expression plus the prefix B- or I- (CONLL convention)
   * @param includeFeatures
   *          if true will combine the POS Tag with the feature tags
   */
  public ADExPOSSampleStream(ObjectStream<String> lineStream, boolean expandME,
      boolean includeFeatures, boolean additionalContext) {
    this.adSentenceStream = new ADSentenceStream(lineStream);
    this.expandME = expandME;
    this.isIncludeFeatures = includeFeatures;
    this.additionalContext = additionalContext;
  }


  public POSSample read() throws IOException {
    Sentence paragraph;
    while ((paragraph = this.adSentenceStream.read()) != null) {
      Node root = paragraph.getRoot();
      List<String> sentence = new ArrayList<String>();
      List<String> tags = new ArrayList<String>();
      List<String> contractions = new ArrayList<String>();
      List<String> prop = new ArrayList<String>();
      process(root, sentence, tags, contractions, prop);

      if (sentence.size() != contractions.size()
          || sentence.size() != prop.size()) {
        throw new IllegalArgumentException(
            "There must be exactly same number of tokens and additional context!");
      }

      if(this.additionalContext) {
        String[][] ac = new String[sentence.size()][2];
        for (int i = 0; i < ac.length; i++) {
          if (contractions.get(i) != null) {
            ac[i][0] = contractions.get(i);
            // if(contractions.get(i) != null) {
            // System.out.println(contractions.get(i) + ": " + sentence.get(i));
            // }
          }
          if (prop.get(i) != null) {
            ac[i][1] = prop.get(i);
            // if(prop.get(i) != null) {
            // System.out.println(prop.get(i) + ": " + sentence.get(i));
            // }
          }
        }
        // System.out.println();
        return new POSSample(sentence, tags, ac);
      } else {
        return new POSSample(sentence, tags);
      }
    }
    return null;
  }

  private void process(Node node, List<String> sentence, List<String> tags,
      List<String> con, List<String> prop) {
    if (node != null) {
      for (TreeElement element : node.getElements()) {
        if (element.isLeaf()) {
          processLeaf((Leaf) element, sentence, tags, con, prop);
        } else {
          process((Node) element, sentence, tags, con, prop);
        }
      }
    }
  }

  private void processLeaf(Leaf leaf, List<String> sentence, List<String> tags,
      List<String> con, List<String> prop) {
    if (leaf != null) {
      String lexeme = leaf.getLexeme();
      String tag = leaf.getFunctionalTag();

      String contraction = null;
      if (leaf.getSecondaryTag() != null) {
        if (leaf.getSecondaryTag().contains("<sam->")) {
          contraction = "B";
        } else if (leaf.getSecondaryTag().contains("<-sam>")) {
          contraction = "E";
        }
      }

      if (tag == null) {
        tag = leaf.getLexeme();
      }

      if (isIncludeFeatures && leaf.getMorphologicalTag() != null) {
        tag += " " + leaf.getMorphologicalTag();
      }
      tag = tag.replaceAll("\\s+", "=");

      if (tag == null)
        tag = lexeme;

      if (expandME && lexeme.contains("_")) {
        StringTokenizer tokenizer = new StringTokenizer(lexeme, "_");

        if ("prop".equals(tag)) {
          sentence.add(lexeme);
          tags.add(tag);
          con.add(null);
          prop.add("P");
        } else if (tokenizer.countTokens() > 0) {
          List<String> toks = new ArrayList<String>(tokenizer.countTokens());
          List<String> tagsWithCont = new ArrayList<String>(
              tokenizer.countTokens());
          toks.add(tokenizer.nextToken());
          tagsWithCont.add("B-" + tag);
          while (tokenizer.hasMoreTokens()) {
            toks.add(tokenizer.nextToken());
            tagsWithCont.add("I-" + tag);
          }
          if (contraction != null) {
            con.addAll(Arrays.asList(new String[toks.size() - 1]));
            con.add(contraction);
          } else {
            con.addAll(Arrays.asList(new String[toks.size()]));
          }

          sentence.addAll(toks);
          tags.addAll(tagsWithCont);
          prop.addAll(Arrays.asList(new String[toks.size()]));
        } else {
          sentence.add(lexeme);
          tags.add(tag);
          prop.add(null);
          con.add(contraction);
        }

      } else if(lexeme.contains("-") && lexeme.length() > 1) {
        Matcher matcher = hyphenPattern.matcher(lexeme);

        String firstTok = null;
        String hyphen = "-";
        String secondTok = null;
        String rest = null;

        if (matcher.matches()) {
          if (matcher.group(1) != null) {
            firstTok = matcher.group(2);
          } else if (matcher.group(3) != null) {
            secondTok = matcher.group(4);
            rest = matcher.group(5);
          } else if (matcher.group(6) != null) {
            firstTok = matcher.group(7);
            secondTok = matcher.group(8);
            rest = matcher.group(9);
          } else {
            throw new IllegalStateException("wrong hyphen pattern");
          }

          if (!Strings.isNullOrEmpty(firstTok)) {
            sentence.add(firstTok);
            tags.add(tag);
            prop.add(null);
            con.add(contraction);
          }
          
          if (!Strings.isNullOrEmpty(hyphen)) {
            sentence.add(hyphen);
            tags.add("-");
            prop.add(null);
            con.add(contraction);
          }
          if (!Strings.isNullOrEmpty(secondTok)) {
            sentence.add(secondTok);
            tags.add(tag);
            prop.add(null);
            con.add(contraction);
          }
          if (!Strings.isNullOrEmpty(rest)) {
            sentence.add(rest);
            tags.add(tag);
            prop.add(null);
            con.add(contraction);
          }
        } else {
          sentence.add(lexeme);
          tags.add(tag);
          prop.add(null);
          con.add(contraction);
        }
      } else {
        sentence.add(lexeme);
        tags.add(tag);
        prop.add(null);
        con.add(contraction);
      }
    }

  }

  public void reset() throws IOException, UnsupportedOperationException {
    adSentenceStream.reset();
  }

  public void close() throws IOException {
    adSentenceStream.close();
  }
}
