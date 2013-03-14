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
package org.cogroo.analyzer;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.chunker.ChunkSample;
import opennlp.tools.util.Span;

import org.cogroo.text.Chunk;
import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.Token;
import org.cogroo.text.impl.ChunkImpl;
import org.cogroo.tools.chunker2.ChunkerME;
import org.cogroo.util.TextUtils;

public class Chunker implements Analyzer {
  private ChunkerME chunker;

  public Chunker(ChunkerME chunker) {
    this.chunker = chunker;
  }

  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();

    for (Sentence sentence : sentences) {
      List<Token> tokens = sentence.getTokens();
      
      String[] tags = new String[tokens.size()];

      for (int i = 0; i < tokens.size(); i++)
        tags[i] = tokens.get(i).getPOSTag();
      
      String[] tokensString = TextUtils.tokensToString(tokens);
      List<Chunk> chunks = new ArrayList<Chunk>();
      String[] chunkTags;
      
      synchronized (chunker) {
        chunkTags = chunker.chunk(tokensString, tags);
      }
      
      for (int i = 0; i < chunkTags.length; i++) {
        tokens.get(i).setChunkTag(chunkTags[i]);
      }
      
      Span[] chunksSpans = ChunkSample.phrasesAsSpanList(tokensString, tags, chunkTags);
      
      for (Span span : chunksSpans) {
        Chunk chunk = new ChunkImpl(span.getType(), span.getStart(), span.getEnd(), sentence);
        chunks.add(chunk);
      }
      sentence.setChunks(chunks);
      
    }
  }
}
