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

package org.cogroo.formats.ad;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.chunker.ChunkSample;
import opennlp.tools.formats.ad.ADChunkSampleStream;
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
 * Portuguese Chunker training.
 * <p>
 * The heuristic to extract chunks where based o paper 'A Machine Learning
 * Approach to Portuguese Clause Identification', (Eraldo Fernandes, Cicero
 * Santos and Ruy Milidiú).<br>
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
public class ADChunkBasedShallowParserSampleStream implements
    ObjectStream<ChunkSample> {

  private final ObjectStream<ADSentenceStream.Sentence> adSentenceStream;

  private int start = -1;
  private int end = -1;

  private int index = 0;

  private boolean useCGTags;

  private boolean expandME;

  private final Set<String> functTagSet;

  private String[] defaultFunctTags = { "SUBJ", "ACC", "DAT", "PIV", "ADVS",
      "ADVO", "SC", "OC", "P", "MV", "PMV", "AUX", "PAUX", "NPHR" };

  private boolean isIncludePOSTags;
  
  public ADChunkBasedShallowParserSampleStream(ObjectStream<String> lineStream, String commaSeparatedFunctTags,
      boolean isIncludePOSTags, boolean useCGTag, boolean expandME) {
    this.useCGTags = useCGTag;
    this.expandME = expandME;
      this.adSentenceStream = new ADSentenceStream(lineStream);
      this.isIncludePOSTags = isIncludePOSTags;

      if (commaSeparatedFunctTags == null
          || commaSeparatedFunctTags.trim().isEmpty()) {
        Set<String> functTagsSet = new HashSet<String>();
        functTagsSet.addAll(Arrays.asList(defaultFunctTags));
        functTagSet = Collections.unmodifiableSet(functTagsSet);
      } else {
        String[] tags = commaSeparatedFunctTags.split(",");
        Set<String> functTagsSet = new HashSet<String>();
        functTagsSet.addAll(Arrays.asList(tags));
        functTagSet = Collections.unmodifiableSet(functTagsSet);
      }
  }
  

  /**
   * Creates a new {@link NameSample} stream from a {@link InputStream}
   * 
   * @param in
   *          the Corpus {@link InputStream}
   * @param charsetName
   *          the charset of the Arvores Deitadas Corpus
   */
  public ADChunkBasedShallowParserSampleStream(InputStream in,
      String charsetName, String commaSeparatedFunctTags,
      boolean isIncludePOSTags, boolean useCGTag, boolean expandME) {
    this.useCGTags = useCGTag;
    this.expandME = expandME;
    try {
      this.adSentenceStream = new ADSentenceStream(new PlainTextByLineStream(
          in, charsetName));
      this.isIncludePOSTags = isIncludePOSTags;

      if (commaSeparatedFunctTags == null
          || commaSeparatedFunctTags.trim().isEmpty()) {
        Set<String> functTagsSet = new HashSet<String>();
        functTagsSet.addAll(Arrays.asList(defaultFunctTags));
        functTagSet = Collections.unmodifiableSet(functTagsSet);
      } else {
        String[] tags = commaSeparatedFunctTags.split(",");
        Set<String> functTagsSet = new HashSet<String>();
        functTagsSet.addAll(Arrays.asList(tags));
        functTagSet = Collections.unmodifiableSet(functTagsSet);
      }
    } catch (UnsupportedEncodingException e) {
      // UTF-8 is available on all JVMs, will never happen
      throw new IllegalStateException(e);
    }
  }

  public ChunkSample read() throws IOException {

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
        List<String> tags = new ArrayList<String>();
        List<String> target = new ArrayList<String>();

        processRoot(root, sentence, tags, target);

        if (sentence.size() > 0) {
          index++;
          return new ChunkSample(sentence, tags, target);
        }

      }

    }
    return null;
  }

  private void processRoot(Node root, List<String> sentence, List<String> tags,
      List<String> target) {
    if (root != null) {
      TreeElement[] elements = root.getElements();
      for (int i = 0; i < elements.length; i++) {
        if (elements[i].isLeaf()) {
          processLeaf((Leaf) elements[i], false, "O", false, "O", sentence,
              tags, target);
        } else {
          // allways pass isInt false when it is a new node
          processNode((Node) elements[i], sentence, tags, target, "O", false,
              null);
        }
      }
    }
  }

  private void processNode(Node node, List<String> sentence, List<String> tags,
      List<String> target, String functTagParent, boolean isIntermediateFunct,
      String inheritedTag) {

    String phraseTag = getChunkTag(node.getSyntacticTag());
    String funcTag = getFunctionTag(node.getSyntacticTag());

    boolean inherited = false;
    if (phraseTag.equals("O") && inheritedTag != null) {
      phraseTag = inheritedTag;
      inherited = true;
    }

    if (funcTag.equals("O") && functTagParent != null) {
      // keep parent funct tag
      funcTag = functTagParent;
    }

    if (!funcTag.equals(functTagParent)) {
      // it is a new tag... reset isInt
      isIntermediateFunct = false;
    }

    TreeElement[] elements = node.getElements();
    for (int i = 0; i < elements.length; i++) {

      if (elements[i].isLeaf()) {

        boolean isIntermediatePhrase = false;
        if (i > 0 && elements[i - 1].isLeaf() && phraseTag != null
            && !phraseTag.equals("O")) {
          isIntermediatePhrase = true;
        }
        if (inherited && target.size() > 0
            && target.get(target.size() - 1).endsWith(phraseTag)) {
          isIntermediatePhrase = true;
        }

        processLeaf((Leaf) elements[i], isIntermediatePhrase, phraseTag,
            isIntermediateFunct, funcTag, sentence, tags, target);
      } else {
        processNode((Node) elements[i], sentence, tags, target, funcTag,
            isIntermediateFunct, phraseTag);
      }
      if (!funcTag.equals("O")) {
        isIntermediateFunct = true;
      }
    }
  }

  private void processLeaf(Leaf leaf, boolean isIntermediatePhrase,
      String phraseTag, boolean isIntermediateFunct, String functTag,
      List<String> sentence, List<String> tags, List<String> target) {

    if (leaf.getFunctionalTag() != null && phraseTag.equals("O")) {
      if (leaf.getFunctionalTag().equals("v-fin")) {
        phraseTag = "VP";
      } else if (leaf.getFunctionalTag().equals("n")) {
        phraseTag = "NP";
      }
    }

    phraseTag = ADChunkSampleStream.convertPhraseTag(phraseTag);

    if (leaf.getSyntacticTag() != null && functTag.equals("O")
        && functTagSet.contains(leaf.getSyntacticTag())) {
      functTag = leaf.getSyntacticTag();
    }

    if (!phraseTag.equals("O")) {
      if (isIntermediatePhrase) {
        phraseTag = "I-" + phraseTag;
      } else {
        phraseTag = "B-" + phraseTag;
      }
    }

    if (!functTag.equals("O")) {
      if (isIntermediateFunct) {
        functTag = "I-" + functTag;
      } else {
        functTag = "B-" + functTag;
      }
    }

    sentence.add(leaf.getLexeme());

    // if ("H".equals(leaf.getSyntacticTag())) {
    // phraseTag = "*" + phraseTag;
    // }

    if (leaf.getSyntacticTag() == null) {
      tags.add(getTag(leaf.getLexeme(), phraseTag));
    } else {
      tags.add(getTag(ADChunkSampleStream.convertFuncTag(
          leaf.getFunctionalTag(), this.useCGTags), phraseTag));
    }

    target.add(functTag);
  }

  private String getTag(String functTag, String phraseTag) {
    if (isIncludePOSTags) {
      return functTag + "|" + phraseTag;
    }
    return phraseTag;
  }

  private String getFunctionTag(String tag) {

    String funcTag = tag.substring(0, tag.lastIndexOf(":"));

    if (!functTagSet.contains(funcTag)) {
      funcTag = "O";
    }
    return funcTag;
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
