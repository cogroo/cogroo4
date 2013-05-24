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

import org.cogroo.config.Analyzers;

/**
 * The <code>Token</code> interface is responsible for obtaining each component
 * of a token
 */
public interface Token {

  public int getStart();
  
  public int getEnd();

  public String[] getLemmas();

  public String getLexeme();

  public String getPOSTag();
  
  public double getPOSTagProb();

  public String getFeatures();
  
  public String getChunkTag();
  
  public String getSyntacticTag();
  
  public boolean isChunkHead();
  
  public void isChunkHead(boolean ch);

  public void setFeatures(String features);

  public void setLemmas(String[] lemmas);

  public void setLexeme(String lexeme);

  public void setPOSTag(String tag);

  public void setBoundaries(int start, int end);
  
  public void setChunkTag(String string);
  
  public void setSyntacticTag(String string);

  public void addContext(Analyzers contractionFinder, String value);

  public Object getAdditionalContext(Analyzers analyzers);

  public void setPOSTagProb(double d);

}