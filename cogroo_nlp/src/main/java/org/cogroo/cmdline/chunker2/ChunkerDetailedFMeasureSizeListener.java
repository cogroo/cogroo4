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

package org.cogroo.cmdline.chunker2;

import java.util.Arrays;
import java.util.Random;

import opennlp.tools.chunker.ChunkSample;
import opennlp.tools.util.Span;

import org.cogroo.tools.chunker2.ChunkerEvaluationMonitor;
import org.cogroo.tools.featurizer.WordTag;

public class ChunkerDetailedFMeasureSizeListener extends
    DetailedFMeasureForSizeListener<ChunkSample> implements ChunkerEvaluationMonitor{

  @Override
  protected Span[] asSpanArray(ChunkSample sample) {
    
    int size = sample.getSentence().length;
    String[] headTags = new String[size];
    String[] chunkTags = new String[size];
    String[] posTags = new String[size];
    String[] lexemes = new String[size];
    WordTag.extract(WordTag.create(sample), lexemes, posTags, headTags);
    
    for (int i = 0; i < chunkTags.length; i++) {
      chunkTags[i] = posTags[i].substring(posTags[i].indexOf('|') + 1);
    }
    
    Span[] out = asHeadSpan(ChunkSample.phrasesAsSpanList(lexemes, posTags, chunkTags), headTags); 
    
    return out;
  }

  private void print(Span[] out, String[] lexemes) {
    String[] chunks = Span.spansToStrings(out, lexemes);
    for (int i = 0; i < chunks.length; i++) {
      chunks[i] = out[i].getType() + ": " + chunks[i];
    }
    System.out.println(Arrays.toString(chunks));
  }
  
  Random randomGenerator = new Random();

  private Span[] asHeadSpan(Span[] chunks, String[] headTags) {
    Span[] out = new Span[chunks.length];
    for (int i = 0; i < chunks.length; i++) {
      Span c = chunks[i];
      int head = -1;
      for (int j = c.getStart(); j < c.getEnd(); j++) {
        // find the head
        if(isHead(headTags[j])) {
          head = j - c.getStart();
        }
      }
      if(head == -1) {
        if(c.length() == 1) {
          head = 0;
        } else {
          head = randomGenerator.nextInt(100000) + 8;
        }
      }
       
      out[i] = new Span(c.getStart(), c.getEnd(), Integer.toString(head));
    }
    
    return out;
  }
  
  private boolean isHead(String outcome) {
    return !outcome.equals("O");
  }


}
