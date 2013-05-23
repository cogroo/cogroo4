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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import opennlp.tools.formats.ad.ADSentenceStream;
import opennlp.tools.formats.ad.ADSentenceStream.Sentence;
import opennlp.tools.formats.ad.ADSentenceStream.SentenceParser.Leaf;
import opennlp.tools.formats.ad.ADSentenceStream.SentenceParser.Node;
import opennlp.tools.formats.ad.ADSentenceStream.SentenceParser.TreeElement;
import opennlp.tools.formats.ad.PortugueseContractionUtility;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;

import org.cogroo.ContractionUtility;

/**
 * Parser for Floresta Sita(c)tica Arvores Deitadas corpus, output to for the
 * Portuguese NER training.
 * <p>
 * The data contains common multiword expressions. The categories are:<br>
 * intj, spec, conj-s, num, pron-indef, n, prop, adj, prp, adv
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
public class ADContractionNameSampleStream implements ObjectStream<NameSample> {

  private final ObjectStream<ADSentenceStream.Sentence> adSentenceStream;

  /**
   * To keep the last left contraction part
   */
  private String leftContractionPart = null;
  private static final Pattern underlinePattern = Pattern.compile("[_]+");

  /**
   * The tags we are looking for
   */
  private Set<String> tags;

  /**
   * Creates a new {@link NameSample} stream from a line stream, i.e.
   * {@link ObjectStream}< {@link String}>, that could be a
   * {@link PlainTextByLineStream} object.
   * 
   * @param lineStream
   *          a stream of lines as {@link String}
   * @param tags
   *          the tags we are looking for, or null for all
   */
  public ADContractionNameSampleStream(ObjectStream<String> lineStream,
      Set<String> tags) {
    this.adSentenceStream = new ADSentenceStream(lineStream);
    this.tags = tags;
  }

  /**
   * Creates a new {@link NameSample} stream from a {@link InputStream}
   * 
   * @param in
   *          the Corpus {@link InputStream}
   * @param charsetName
   *          the charset of the Arvores Deitadas Corpus
   * @param tags
   *          the tags we are looking for, or null for all
   */
  public ADContractionNameSampleStream(InputStream in, String charsetName,
      Set<String> tags) {

    try {
      this.adSentenceStream = new ADSentenceStream(new PlainTextByLineStream(
          in, charsetName));
      this.tags = tags;
    } catch (UnsupportedEncodingException e) {
      // UTF-8 is available on all JVMs, will never happen
      throw new IllegalStateException(e);
    }
  }

  public NameSample read() throws IOException {

    Sentence paragraph;
    while ((paragraph = this.adSentenceStream.read()) != null) {
      Node root = paragraph.getRoot();
      List<String> sentence = new ArrayList<String>();
      List<Span> names = new ArrayList<Span>();
      process(root, sentence, names);

      return new NameSample(sentence.toArray(new String[sentence.size()]),
          names.toArray(new Span[names.size()]), true);
    }
    return null;
  }

  /**
   * Recursive method to process a node in Arvores Deitadas format.
   * 
   * @param node
   *          the node to be processed
   * @param sentence
   *          the sentence tokens we got so far
   * @param names
   *          the names we got so far
   */
  private void process(Node node, List<String> sentence, List<Span> names) {
    if (node != null) {
      for (TreeElement element : node.getElements()) {
        if (element.isLeaf()) {
          processLeaf((Leaf) element, sentence, names);
        } else {
          process((Node) element, sentence, names);
        }
      }
    }
  }

  /**
   * Process a Leaf of Arvores Detaitadas format
   * 
   * @param leaf
   *          the leaf to be processed
   * @param sentence
   *          the sentence tokens we got so far
   * @param names
   *          the names we got so far
   */
  private void processLeaf(Leaf leaf, List<String> sentence, List<Span> names) {

    if (leaf != null && leftContractionPart == null) {

      int startOfNamedEntity = -1;
      String leafTag = leaf.getSecondaryTag();

      if (leafTag != null) {
        if (leafTag.contains("<sam->")) {
          String[] lexemes = underlinePattern.split(leaf.getLexeme());
          if (lexemes.length > 1) {
            for (int i = 0; i < lexemes.length - 1; i++) {
              sentence.add(lexemes[i]);
              
              String[] conts = ContractionUtility.expand(lexemes[i]);
              if(conts != null) {
                int end = sentence.size();
                int start = end - 1;
                Span s = new Span(start, end, "default");
                names.add(s);
                {
                  Span[] ss = {s};
                  System.out.println(Arrays.toString(Span.spansToStrings(ss, sentence.toArray(new String[sentence.size()]))));
                }
              }
            }
          }
          leftContractionPart = lexemes[lexemes.length - 1];
          return;
        }
        // if (leaf.getLexeme().contains("_") && leaf.getLexeme().length() > 3)
        // {
        // String tag = leaf.getFunctionalTag();
        // if (tags != null) {
        // if (tags.contains(tag)) {
        // namedEntityTag = leaf.getFunctionalTag();
        // }
        // } else {
        // namedEntityTag = leaf.getFunctionalTag();
        // }
        // }
      }

      // if (contraction) {
      // startOfNamedEntity = sentence.size();
      // }
      //
      sentence.addAll(Arrays.asList(leaf.getLexeme()));// .split("_")
      //
      // if (contraction) {
      // names
      // .add(new Span(startOfNamedEntity, sentence.size()));
      // }

    } else {
      // will handle the contraction
      String tag = leaf.getSecondaryTag();
      String right = leaf.getLexeme();
      if (tag != null && tag.contains("<-sam>")) {
        String[] parts = underlinePattern.split(leaf.getLexeme());
        if(parts != null) {
          // try to join only the first
          String c = PortugueseContractionUtility.toContraction(
              leftContractionPart, parts[0]);

          if (c != null) {
            sentence.add(c);
            names.add(new Span(sentence.size() - 1, sentence.size(), "default"));
          }
          
          for (int i = 1; i < parts.length; i++) {
            sentence.add(parts[i]);
          }
        } else {
          right = leaf.getLexeme();
          String c = PortugueseContractionUtility.toContraction(
              leftContractionPart, right);

          if (c != null) {
            sentence.add(c);
            names.add(new Span(sentence.size() - 1, sentence.size(), "default"));
          } else {
            System.err.println("missing " + leftContractionPart + " + " + right);
            sentence.add(leftContractionPart);
            sentence.add(right);
          }
        }
      } else {
        System.err.println("unmatch" + leftContractionPart + " + " + right);
      }
      leftContractionPart = null;
    }

  }

  public void reset() throws IOException, UnsupportedOperationException {
    adSentenceStream.reset();
  }

  public void close() throws IOException {
    adSentenceStream.close();
  }

}
