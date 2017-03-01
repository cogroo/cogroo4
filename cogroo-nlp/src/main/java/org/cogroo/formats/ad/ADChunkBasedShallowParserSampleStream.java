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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.cogroo.tools.featurizer.WordTag;
import org.cogroo.tools.shallowparser.ShallowParserSequenceValidator;

import opennlp.tools.chunker.ChunkSample;
import opennlp.tools.formats.ad.ADSentenceStream.Sentence;
import opennlp.tools.formats.ad.ADSentenceStream.SentenceParser.Leaf;
import opennlp.tools.formats.ad.ADSentenceStream.SentenceParser.Node;
import opennlp.tools.formats.ad.ADSentenceStream.SentenceParser.TreeElement;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.Span;

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
public class ADChunkBasedShallowParserSampleStream extends ADChunk2SampleStream {

  private final Set<String> functTagSet;

  private String[] defaultFunctTags = { "SUBJ", "ACC", "DAT", "PIV", "ADVS",
      "ADVO", "SC", "OC", "P",  "NPHR", "SA", "ADVL", "APP",
      // "MV","PMV", "PAUX", "AUX",
      };

  private boolean readChunk;

  private ShallowParserSequenceValidator sv = new ShallowParserSequenceValidator();

  private ArrayList<String> chunks;

  private SubjectTypes subjectTypes = new SubjectTypes();
  
  public ADChunkBasedShallowParserSampleStream(ObjectStream<String> lineStream, String commaSeparatedFunctTags,
      boolean isIncludePOSTags, boolean useCGTag, boolean expandME) {
    
    super(lineStream);
    

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
  public ADChunkBasedShallowParserSampleStream(InputStreamFactory in,
      String charsetName, String commaSeparatedFunctTags,
      boolean isIncludePOSTags, boolean useCGTag, boolean expandME) throws IOException {
    
    super(in, charsetName);
    
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

  public ChunkSample read() throws IOException {
    
    
    Sentence paragraph;
    while ((paragraph = this.adSentenceStream.read()) != null) {

        this.readChunk = true;
        Node root = paragraph.getRoot();
        List<String> sentence = new ArrayList<String>();
        List<String> tags = new ArrayList<String>();
        chunks = new ArrayList<String>();

        processRoot(root, sentence, tags, chunks);
        
        this.readChunk = false;
        
        sentence.clear();
        tags.clear();
        List<String> target = new ArrayList<String>();
        processRoot(root, sentence, tags, target);
        
        for (int i = 0; i < tags.size(); i++) {
          tags.set(i, tags.get(i) + "|" + chunks.get(i));
        }
        
        if (sentence.size() > 0) {
          ChunkSample cs = new ChunkSample(sentence, tags, target);
//          System.out.println(cs);
          for (int i = 0; i < sentence.size(); i++) {
            String[] outcomes;
            if(i > 0) {
              outcomes = target.subList(0, i).toArray(new String[i]);
            } else {
              outcomes = new String[0];
            }
            if(!sv.validSequence(i, WordTag.create(cs), outcomes, target.get(i))) {
              //sv.validSequence(i, WordTag.create(cs), outcomes, target.get(i));
              System.out.println("failed, invalid outcome: " + target.get(i));
            }
          }
          
          
//          System.out.println(cs);
//          this.subjectTypes.add(cs);
          return cs;
        }
        

      }

//    this.subjectTypes.print();
    return null;
  }
  
  protected String getChunkTag(Leaf leaf) {
    if(this.readChunk)
      return super.getChunkTag(leaf);
    
    String tag = leaf.getSyntacticTag(); 
    if(functTagSet.contains(tag)) {
      return tag;
    }
    return null;
  }


  @Override
  protected String getChunkTag(Node node, String parent, int index) {
    if(this.readChunk)
      return super.getChunkTag(node, parent, index);
    else {
      String tag = node.getSyntacticTag();
      String funcTag = tag.substring(0, tag.lastIndexOf(":"));

      if (!functTagSet.contains(funcTag)) {
        funcTag = "O";
      }
      
      if(funcTag.equals(parent))
        return "O";
      
      if(funcTag.equals("O")) 
        return funcTag;
      
      // check for nested...
      // we can check the index, and the size of this node (number of leafs)
      int leafs = countLeafs(node);
      // check if we have a complete chunk group inside
      String s = chunks.get(index);
      boolean valid = s.equals("O") || s.startsWith("B-");
      
      if(valid) {
        if(chunks.size() == index + leafs) {
          // last chunk...
          return funcTag;
        }
        String end1 = chunks.get(index + leafs);
        valid = end1.equals("O") || end1.startsWith("B-");
      }
      if(valid) 
        return funcTag;
      return "O";
    }
  }
  
  private int countLeafs(Node node) {
    int counter = 0;
    for (TreeElement element : node.getElements()) {
      if(element.isLeaf()) {
        counter++;
      } else {
        counter += countLeafs((Node)element);
      }
    }
    return counter;
  }


  protected String getPhraseTagFromPosTag(String functionalTag) {
    return OTHER;
  }

  @Override
  protected boolean isIncludePunctuations() {
    if(this.readChunk)
      return super.isIncludePunctuations();
    return true;
  }
  
  static class SubjectTypes {
    private Map<String, AtomicInteger> subjects = new HashMap<String, AtomicInteger>();
    private Map<String, String> examples = new HashMap<String, String>();
    
    public void add(ChunkSample sample) {
      for (Span subj : sample.getPhrasesAsSpanList()) {
        if(subj.getType().equals("SUBJ")) {
          String[] chunks = extractChunk(Arrays.copyOfRange(sample.getTags(), subj.getStart(), subj.getEnd()));
          Span[] c = ChunkSample.phrasesAsSpanList(chunks, chunks, chunks);
          StringBuilder sb = new StringBuilder();
          for (Span span : c) {
            sb.append(span.getType()).append(" ");
          }
          
          String value = sb.toString().trim();
          
          if(!subjects.containsKey(value)) {
            subjects.put(value, new AtomicInteger(1));
            examples.put(value, Arrays.toString(Arrays.copyOfRange(sample.getSentence(), subj.getStart(), subj.getEnd())));
          } else {
            subjects.get(value).incrementAndGet();
          }
        }
      }
    }
    
    public void print() {
      Set<String> chunks = new TreeSet<String>(new Comparator<String>() {

        @Override
        public int compare(String arg0, String arg1) {
          if(arg0.equals(arg1)) return 0;
          return subjects.get(arg0).intValue() - subjects.get(arg1).intValue();
        }
        
      });
      
      chunks.addAll(subjects.keySet());
      
      for (String string : chunks) {
        System.out.println(string + " -> " + subjects.get(string) + "->" + examples.get(string));
      }
      
    }

    private String[] extractChunk(String[] postags) {
      String[] out = new String[postags.length];
      for (int i = 0; i < postags.length; i++) {
        out[i] = extractChunk(postags[i]);
      }
      return out;
    }
    
    private String extractChunk(String postag) {
      int i = postag.indexOf('|');
      return postag.substring(i + 1);
    }
  }
  
}
