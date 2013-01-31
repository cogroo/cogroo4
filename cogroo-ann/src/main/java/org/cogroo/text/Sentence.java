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
package org.cogroo.text;

import java.util.List;

import org.cogroo.text.tree.Node;


public interface Sentence {

  /**
   * @return the <code>String</code> of the sentence
   */
  public String getText();

  public int getStart();
  
  public int getEnd();
  
  public void setBoundaries(int start, int end);

  public List<Token> getTokens();
  
  public double getTokensProb();

  public void setTokens(List<Token> tokens);
  
  public List<Chunk> getChunks();
  
  public void setChunks(List<Chunk> chunks);
  
  public List<SyntacticChunk> getSyntacticChunks();

  public void setSyntacticChunks(List<SyntacticChunk> syntacticChunks);
  
  public Node asTree ();

  public void setTokensProb(double prob);
}