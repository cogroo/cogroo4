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

import java.util.List;

import org.cogroo.text.Chunk;
import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.Token;
import org.cogroo.tools.chunker2.ChunkerME;
import org.cogroo.util.TextUtils;

public class HeadFinder implements Analyzer {
  private ChunkerME headFinder;

  public HeadFinder(ChunkerME headFinder) {
    this.headFinder = headFinder;
  }
  
  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();
    
    for (Sentence sentence : sentences) {
      List<Token> tokens = sentence.getTokens();
      List<Chunk> chunks = sentence.getChunks();

      String[] tags = new String[tokens.size()];

      for (int i = 0; i < tokens.size(); i++)
        tags[i] = tokens.get(i).getPOSTag() + "|" + tokens.get(i).getChunkTag();
      
      String[] tokensString = TextUtils.tokensToString(tokens);
      String[] heads;
      synchronized (this.headFinder) {
        heads = headFinder.chunk(tokensString, tags);
      }
      
      for (Chunk chunk : chunks)
        for (int i = chunk.getStart(); i < chunk.getEnd(); i++)
          if (heads[i].equals("B-H")) {
            tokens.get(i).isChunkHead(true);
            chunk.setHeadIndex(i);
          }
    }
  }

}
