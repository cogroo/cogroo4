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
package org.cogroo.formats.ad;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.cogroo.tools.featurizer.FeatureSample;

import opennlp.tools.formats.ad.ADSentenceStream;
import opennlp.tools.formats.ad.ADSentenceStream.Sentence;
import opennlp.tools.formats.ad.ADSentenceStream.SentenceParser.Leaf;
import opennlp.tools.formats.ad.ADSentenceStream.SentenceParser.Node;
import opennlp.tools.formats.ad.ADSentenceStream.SentenceParser.TreeElement;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

/**
 * Parser for Floresta Sita(c)tica Arvores Deitadas corpus, output to for the
 * Portuguese Featurizer training.
 * <p>
 * Data can be found on this web site:<br>
 * http://www.linguateca.pt/floresta/corpus.html
 * <p>
 * Information about the format:<br>
 * Susana Afonso.
 * "Árvores deitadas: Descrição do formato e das opções de análise na Floresta Sintáctica"
 * .<br>
 * 12 de Fevereiro de 2006.
 * http://www.linguateca.pt/documentos/Afonso2006ArvoresDeitadas.pdf
 * <p>
 * Detailed info about the NER tagset:
 * http://beta.visl.sdu.dk/visl/pt/info/portsymbol.html#semtags_names
 * <p>
 * <b>Note:</b> Do not use this class, internal use only!
 */
public class ADFeaturizerSampleStream implements ObjectStream<FeatureSample> {

  private final ObjectStream<ADSentenceStream.Sentence> adSentenceStream;

  private int start = -1;
  private int end = -1;

  private int index = 0;

  private boolean expandME;
  
  // this is used to control changing aspas representation, some sentences we keep as original, others we change to " 
  private int callsCount = 0;

  /**
   * Creates a new {@link NameSample} stream from a line stream, i.e.
   * {@link ObjectStream}< {@link String}>, that could be a
   * {@link PlainTextByLineStream} object.
   * 
   * @param lineStream
   *          a stream of lines as {@link String}
   */
  public ADFeaturizerSampleStream(ObjectStream<String> lineStream,
      boolean expandME) {
    this.expandME = expandME;
    this.adSentenceStream = new ADSentenceStream(lineStream);
  }

  /**
   * Creates a new {@link NameSample} stream from a {@link InputStream}
   * 
   * @param in
   *          the Corpus {@link InputStream}
   * @param charsetName
   *          the charset of the Arvores Deitadas Corpus
   */
  public ADFeaturizerSampleStream(InputStream in, String charsetName,
      boolean expandME) {

    try {
      this.expandME = expandME;
      this.adSentenceStream = new ADSentenceStream(new PlainTextByLineStream(
          in, charsetName));
    } catch (UnsupportedEncodingException e) {
      // UTF-8 is available on all JVMs, will never happen
      throw new IllegalStateException(e);
    }
  }

  public FeatureSample read() throws IOException {

    callsCount++;
    Sentence paragraph;
    while ((paragraph = this.adSentenceStream.read()) != null) {

      if (end > -1 && index >= end) {
        // leave
        return null;
      }

      if (start > -1 && index < start) {
        index++;
        // skip this one
      } else {
        Node root = paragraph.getRoot();
        List<String> sentence = new ArrayList<String>();
        List<String> lemma = new ArrayList<String>();
        List<String> tags = new ArrayList<String>();
        List<String> target = new ArrayList<String>();

        processRoot(root, sentence, lemma, tags, target);

        if (sentence.size() > 0) {
          index++;
          return new FeatureSample(sentence, lemma, tags, target);
        }

      }

    }
    return null;
  }

  private void processRoot(Node root, List<String> sentence,List<String> lemmas, List<String> tags,
      List<String> target) {
    if (root != null) {
      TreeElement[] elements = root.getElements();
      for (int i = 0; i < elements.length; i++) {
        if (elements[i].isLeaf()) {
          processLeaf((Leaf) elements[i], false, "O", sentence, lemmas, tags, target);
        } else {
          processNode((Node) elements[i], sentence, lemmas, tags, target, null);
        }
      }
    }
  }

  private void processNode(Node node, List<String> sentence, List<String> lemmas, List<String> tags,
      List<String> target, String inheritedTag) {
    String phraseTag = getChunkTag(node.getSyntacticTag());

    boolean inherited = false;
    if (phraseTag.equals("O") && inheritedTag != null) {
      phraseTag = inheritedTag;
      inherited = true;
    }

    TreeElement[] elements = node.getElements();
    for (int i = 0; i < elements.length; i++) {
      if (elements[i].isLeaf()) {
        boolean isIntermediate = false;
        if (i > 0 && elements[i - 1].isLeaf() && phraseTag != null
            && !phraseTag.equals("O")) {
          isIntermediate = true;
        }
        if (inherited && target.size() > 0
            && target.get(target.size() - 1).endsWith(phraseTag)) {
          isIntermediate = true;
        }
        processLeaf((Leaf) elements[i], isIntermediate, phraseTag, sentence, lemmas,
            tags, target);
      } else {
        processNode((Node) elements[i], sentence, lemmas, tags, target, phraseTag);
      }
    }
  }

  private void processLeaf(Leaf leaf, boolean isIntermediate, String phraseTag,
      List<String> sentence, List<String> lemmas, List<String> tags, List<String> target) {

    String featureTag;
    String lemma = leaf.getLemma();
    String lexeme = leaf.getLexeme();
    featureTag = leaf.getMorphologicalTag();
    
    // this will change half of the aspas 
    if("«".equals(lexeme) || "»".equals(lexeme)) {
      if(callsCount % 2 == 0) {
        lexeme = "\"";
      }
    }

    if (featureTag == null) {
      featureTag = "-";
    } else {
      featureTag = featureTag.replace(" ", "=");
    }

    String postag;

    if (leaf.getSyntacticTag() == null) {
      postag = lexeme;
      lemma =  lexeme;
    } else {
      postag = ADFeaturizerSampleStream.convertFuncTag(leaf.getFunctionalTag());
    }

    if(postag == null) {
      return;
    }
    
    if (expandME && lexeme.contains("_") && !"prop".equals(postag)) {
      StringTokenizer tokenizer = new StringTokenizer(lexeme, "_");

      /*
       * if(postag.startsWith("prop")) { sentence.add(tokenizer.nextToken());
       * target.add(featureTag); tags.add(postag); } else
       */if (tokenizer.countTokens() > 0) {
        List<String> toks = new ArrayList<String>(tokenizer.countTokens());
        List<String> tagsWithCont = new ArrayList<String>(
            tokenizer.countTokens());
        toks.add(tokenizer.nextToken());
        tagsWithCont.add("B-" + postag);
        target.add(featureTag);
        while (tokenizer.hasMoreTokens()) {
          toks.add(tokenizer.nextToken());
          tagsWithCont.add("I-" + postag);
          target.add(featureTag);
        }

        lemmas.addAll(toks);
        sentence.addAll(toks);
        tags.addAll(tagsWithCont);
      } else {
        sentence.add(lexeme);
        lemmas.add(lemma);
        target.add(featureTag);
        tags.add(postag);
      }
    } else {
      sentence.add(lexeme);
      lemmas.add(lemma);
      target.add(featureTag);
      tags.add(postag);
    }

  }

  private static String convertFuncTag(String t) {
    // XXX: this should be removed when using Floresta tagger !
    // if("art".equals(t) || "pron-det".equals(t) || "pron-indef".equals(t)) {
    // t = "det";
    // }
    return t;
  }

  private String getChunkTag(String tag) {

    String phraseTag = tag.substring(tag.lastIndexOf(":") + 1);

    // maybe we should use only np, vp and pp, but will keep ap and advp.
    if (phraseTag.equals("np") || phraseTag.equals("vp")
        || phraseTag.equals("pp") || phraseTag.equals("ap")
        || phraseTag.equals("advp")) {
      phraseTag = phraseTag.toUpperCase();
    } else {
      phraseTag = "O";
    }
    return phraseTag;
  }

  public void setStart(int aStart) {
    this.start = aStart;
  }

  public void setEnd(int aEnd) {
    this.end = aEnd;
  }

  public void reset() throws IOException, UnsupportedOperationException {
    adSentenceStream.reset();
  }

  public void close() throws IOException {
    adSentenceStream.close();
  }

}
