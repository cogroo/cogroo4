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
import java.util.Arrays;

import org.apache.uima.resource.ResourceInitializationException;
import org.cogroo.analyzer.Pipe;
import org.cogroo.checker.CheckDocument;
import org.cogroo.entities.Sentence;
import org.cogroo.entities.Token;
import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.entities.tree.Leaf;
import org.cogroo.entities.tree.Node;
import org.cogroo.entities.tree.TreeElement;
import org.cogroo.interpreters.FlorestaTagInterpreter;
import org.cogroo.interpreters.TagInterpreterI;

import cogroo.uima.ae.NewTagsetBaselineCogrooAE;

public class ProcessReport {

  Pipe pipe;
  private String report;
  private String output;

  public ProcessReport(String resources, String report, String output)
      throws IllegalArgumentException, IOException, ResourceInitializationException {
    
    pipe = (Pipe) NewTagsetBaselineCogrooAE.createCogroo();
    
    
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
    CheckDocument doc = new CheckDocument();
    doc.setText(text);
    
    pipe.analyze(doc);
    
    StringBuilder sb = new StringBuilder();
    for (Sentence sentence : doc.getSentencesLegacy()) {
      sb.append("Flat structure for: ").append(sentence.getSentence())
          .append("\n");
      for (Token token : sentence.getTokens()) {
        // print the text
        sb.append(token.getLexeme());
        // print the lemma
        sb.append(" [" + Arrays.toString(token.getPrimitive()) + " ");
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
  private static final TagInterpreterI tagInterpreter = new FlorestaTagInterpreter();

  private static String mtagToStr(MorphologicalTag tag) {
    return tagInterpreter.serialize(tag);
  }

  /**
   * @param args
   * @throws IOException
   * @throws IllegalArgumentException 
   * @throws ResourceInitializationException 
   */
  public static void main(String[] args) throws IOException, ResourceInitializationException, IllegalArgumentException {
    System.out.println("Executing MultiCogroo ProcessReport...");
    System.out.println("  path: " + args[0]);
    System.out.println("    in: " + args[1]);
    System.out.println("   out: " + args[2]);
    ProcessReport pr = new ProcessReport(args[0], args[1], args[2]);

    pr.processFile();
  }

}
