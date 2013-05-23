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
package cogroo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import br.usp.pcs.lta.cogroo.configuration.LegacyRuntimeConfiguration;
import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.entity.Token;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.MorphologicalTag;
import br.usp.pcs.lta.cogroo.entity.tree.Leaf;
import br.usp.pcs.lta.cogroo.entity.tree.Node;
import br.usp.pcs.lta.cogroo.entity.tree.TreeElement;
import br.usp.pcs.lta.cogroo.grammarchecker.CheckerResult;
import br.usp.pcs.lta.cogroo.grammarchecker.CogrooI;
import br.usp.pcs.lta.cogroo.tag.LegacyTagInterpreter;
import br.usp.pcs.lta.cogroo.tag.TagInterpreterI;

public class ProcessReport {

  CogrooI cogroo;
  private String report;
  private String output;

  public ProcessReport(String resources, String report, String output) {
    // UIMA will load modules using envvar!
    System.out.println("Using uima modules from: " + System.getenv("UIMA_DATAPATH"));
    // the rest we load normally
    this.cogroo = new MultiCogroo(new LegacyRuntimeConfiguration(resources));
    this.report = report;
    this.output = output;
  }

  private void processFile() throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(
        new FileInputStream(this.report), "utf-8"));

    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream(this.output), "utf-8"));
    String line = in.readLine();
    while (line != null) {
      out.append(line).append("\n");
      String text = getText(line);
      if (text != null) {
        out.append(process(text)).append("\n\n");
      }

      line = in.readLine();
    }
    in.close();
    out.close();
  }

  private String process(String text) {
    CheckerResult results = cogroo.analyseAndCheckText(text);
    StringBuilder sb = new StringBuilder();
    
    if(results == null) {
      return "Processing failed: " + text;
    }
    for (Sentence sentence : results.sentences) {
      sb.append("Flat structure for: ").append(sentence.getSentence())
          .append("\n");
      for (Token token : sentence.getTokens()) {
        // print the text
        sb.append(token.getLexeme());
        // print the lemma
        sb.append(" [" + token.getPrimitive() + " ");
        // print the morphological tag, we use a tag interpreter here
        
        if(token.getMorphologicalTag() == null) {
          throw new IllegalArgumentException("Morphological tag is missing! " + sentence.toString());
        }
        
        sb.append("" + mtagToStr(token.getMorphologicalTag()) + "] ");
      }
      sb.append("\n").append("Syntax tree: " + sentence.getSyntaxTree());
    }
    return sb.toString();
  }

  private String getText(String line) {

    if (line.startsWith("[")) {
      String[] parts = line.split("\\t+");
      return parts[parts.length - 1];
    }
    return null;
  }

  private static String printTree(Node root) {
    StringBuffer sb = new StringBuffer();
    // print itself and its children
    for (int i = 0; i < root.getLevel(); i++) {
      sb.append("\t");
    }
    sb.append(root.getSyntacticTag());
    sb.append("{");
    if (root.getMorphologicalTag() != null) {
      sb.append(root.getMorphologicalTag());
    }
    sb.append("\n");
    for (TreeElement element : root.getElements()) {
      sb.append(printTree(element));
    }
    sb.append("\n");
    for (int i = 0; i < root.getLevel(); i++) {
      sb.append("\t");
    }
    sb.append("}\n");
    return sb.toString();
  }

  private static String printTree(Leaf leaf) {
    StringBuffer sb = new StringBuffer();
    // print itself and its children
    for (int i = 0; i < leaf.getLevel(); i++) {
      sb.append("\t");
    }
    // print the text
    sb.append(leaf.getLexeme());
    // print the lemma
    sb.append("\tlemma[" + leaf.getLemma() + "] ");
    // print the morphological tag, we use a tag interpreter here
    sb.append("\ttag[" + leaf.getMorphologicalTag() + "] \n");
    return sb.toString();
  }

  private static String printTree(TreeElement element) {
    if (element instanceof Node)
      return printTree((Node) element);
    else
      return printTree((Leaf) element);
  }

  // tag interpreter is responsible for serializing and reading tags.
  // .. the LegacyTagInterpreter follow a variant of GC tagset:
  // .. http://beta.visl.sdu.dk/visl/pt/info/portsymbol.html
  private static final TagInterpreterI tagInterpreter = new LegacyTagInterpreter();

  private static String mtagToStr(MorphologicalTag tag) {
    return tagInterpreter.serialize(tag);
  }

  /**
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    System.out.println("Executing MultiCogroo ProcessReport...");
    System.out.println("  path: " + args[0]);
    System.out.println("    in: " + args[1]);
    System.out.println("   out: " + args[2]);
    ProcessReport pr = new ProcessReport(args[0], args[1], args[2]);

    pr.processFile();
  }

}
