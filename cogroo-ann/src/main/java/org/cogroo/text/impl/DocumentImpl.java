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
package org.cogroo.text.impl;

import java.util.List;

import org.cogroo.text.Document;
import org.cogroo.text.Sentence;



/**
 * The <code>Document</code> class contains a text given by the user and also
 * its sentences separately in a list.
 */
public class DocumentImpl implements Document {

  /** the <code>String</code> which contains the whole text */
  private String text;

  /** the list of every sentence in <code>text</code> */
  private List<Sentence> sentences;

  public DocumentImpl() {
  }
  
  public DocumentImpl(String text) {
    setText(text);
  }
  
  /* (non-Javadoc)
   * @see org.cogroo.text.Document#getText()
   */
  public String getText() {
    return text;
  }

  /* (non-Javadoc)
   * @see org.cogroo.text.Document#setText(java.lang.String)
   */
  public void setText(String text) {
    this.text = text;
  }

  /* (non-Javadoc)
   * @see org.cogroo.text.Document#getSentences()
   */
  public List<Sentence> getSentences() {
    return sentences;
  }

  /* (non-Javadoc)
   * @see org.cogroo.text.Document#setSentences(java.util.List)
   */
  public void setSentences(List<Sentence> sentences) {
    this.sentences = sentences;
  }
}
