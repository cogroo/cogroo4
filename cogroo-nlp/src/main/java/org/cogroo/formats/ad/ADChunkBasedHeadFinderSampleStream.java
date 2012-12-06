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
import java.util.List;

import opennlp.tools.chunker.ChunkSample;
import opennlp.tools.formats.ad.ADSentenceStream.Sentence;
import opennlp.tools.formats.ad.ADSentenceStream.SentenceParser.Leaf;
import opennlp.tools.formats.ad.ADSentenceStream.SentenceParser.Node;
import opennlp.tools.util.ObjectStream;

public class ADChunkBasedHeadFinderSampleStream extends ADChunk2SampleStream {
  
  List<String> newTags = null;
  List<String> headTags = null;
  
  public ChunkSample read() throws IOException {

    Sentence paragraph;
    while ((paragraph = this.adSentenceStream.read()) != null) {

            Node root = paragraph.getRoot();
            List<String> sentence = new ArrayList<String>();
            List<String> tags = new ArrayList<String>();
            List<String> target = new ArrayList<String>();
            
            newTags = new ArrayList<String>();
            headTags = new ArrayList<String>();
            
            processRoot(root, sentence, tags, target);

            if (sentence.size() > 0) {
                return new ChunkSample(sentence, newTags, headTags);
            }
    }

    return null;
}
  
  public ADChunkBasedHeadFinderSampleStream(InputStream in, String charsetName) {
    super(in, charsetName);
  }

  public ADChunkBasedHeadFinderSampleStream(ObjectStream<String> lineStream) {
    super(lineStream);
  }
  
  protected void processLeaf(Leaf leaf, boolean isIntermediate, String phraseTag,
      List<String> sentence, List<String> tags, List<String> target) {
    super.processLeaf(leaf, isIntermediate, phraseTag, sentence, tags, target);
    
    int i = target.size() - 1;

    // check the previous... if the current chunk is B- or O, and the previous is B-, that should be the head...
    if(i > 0 && ( target.get(i).startsWith("B-") || OTHER.equals(target.get(i)))) {
      String prev = target.get(i-1).substring(target.get(i-1).indexOf('|') + 1);
      if(prev.startsWith("B-") && !headTags.get(i-1).equals("B-H")) {
        headTags.set(i-1, "B-H");
      }
    }
    
    // change the tags
    newTags.add(tags.get(i) + "|" + target.get(i));
//    tags.set(i, tags.get(i) + "|" + target.get(i));
    
    if (/*!isInherited &&*/ ("H".equals(leaf.getSyntacticTag()) || "MV".equals(leaf.getSyntacticTag())) 
        && !OTHER.equals(phraseTag)
        && isFirstHead(target, headTags)) {
      headTags.add("B-H");
//      target.set(i, "B-H");
    } else {
//      target.set(i, OTHER);
      headTags.add(OTHER);
    }
  }

  private boolean isFirstHead(List<String> target, List<String> heads) {
    // look back for existing heads
    // trivial case: this is a boundary
    
    if(target.get(target.size() - 1).startsWith("B-"))
      return true;
    
    for(int i = target.size() - 2; i >= 0; i--) {
      if(target.get(i).startsWith("I-")) {
        if(heads.get(i).equals("B-H")) 
          return false;
      } else if(target.get(i).startsWith("B-")) {
        if(heads.get(i).equals("B-H")) 
          return false;
        else return true;
      }
    }
    return true;
  }
}
