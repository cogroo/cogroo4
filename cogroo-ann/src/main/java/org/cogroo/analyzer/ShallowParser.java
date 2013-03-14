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

import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.SyntacticChunk;
import org.cogroo.text.Token;
import org.cogroo.text.impl.SyntacticChunkImpl;
import org.cogroo.tools.chunker2.ChunkerME;
import org.cogroo.util.TextUtils;

import opennlp.tools.chunker.ChunkSample;
import opennlp.tools.util.Span;


public class ShallowParser implements Analyzer {
  private ChunkerME shallowParser;

  public ShallowParser(ChunkerME shallowParser) {
    this.shallowParser = shallowParser;
  }

  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();

    for (Sentence sentence : sentences) {
      
      List<Token> tokens = sentence.getTokens();
      List<SyntacticChunk> syntChunks = new ArrayList<SyntacticChunk>();

      String[] tags = new String[tokens.size()];

      for (int i = 0; i < tokens.size(); i++)
        tags[i] = tokens.get(i).getPOSTag() + "|" + tokens.get(i).getChunkTag();

      String[] tokensString = TextUtils.tokensToString(tokens);
      
      String[] spTags = null;
      synchronized (this.shallowParser) {
        spTags = shallowParser.chunk(tokensString, tags);
      }

      for (int i = 0; i < spTags.length; i++) {
        tokens.get(i).setSyntacticTag(spTags[i]);
      }
      
      Span[] spSpans = ChunkSample.phrasesAsSpanList(tokensString, tags, spTags);
      
      for (Span span : spSpans) {
        SyntacticChunk st = new SyntacticChunkImpl(span.getType(),
            span.getStart(), span.getEnd(), sentence);
        syntChunks.add(st);
      }

      sentence.setSyntacticChunks(syntChunks);

    }
  }

}
