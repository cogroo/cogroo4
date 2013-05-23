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
package org.cogroo.entities.impl;

import java.util.List;

import org.cogroo.entities.Sentence;
import org.cogroo.entities.Token;


/**
 * Adds some {@link String} realization to {@link Sentence} methods.
 * 
 * @author William Colen
 * 
 */
public class SentenceCogroo extends Sentence {

  /**
   * Id for serialization.
   */
  private static final long serialVersionUID = -7980890706769539538L;

  public SentenceCogroo(List<Token> tokens) {
    this.tokens = tokens;
  }

  public SentenceCogroo(String sentence) {
    this.sentence = sentence;
  }

  /**
   * Returns the grammar check analysis as a string like as follows:
   * 
   * <pre>
   * [VERB][B-VP*] Fomos --> {}_V_PS_1P_IND_VFIN_
   * [-   ][I-VP ] levados --> {levar}_V_PCP_M_P_
   * [-   ][O    ] a --> {a}_PRP_
   * [-   ][B-NP ] a --> {}_DET_F_S_
   * [-   ][I-NP*] crer --> {crer}_V_INF_3S_
   * [-   ][O    ] . --> {}_-PNT_ABS
   * </pre>
   * 
   * @return the grammar check analysis as a string.
   */
  public String getAnalysisAsString() {
    if (this.tokens.size() > 0
        && this.tokens.get(0).getMorphologicalTag() == null) {
      throw new IllegalStateException("The sentence was not analyzed yet.");
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < this.tokens.size(); i++) {
      sb.append("[").append(this.tokens.get(i).getSyntacticTag()).append("]");
      sb.append("[").append(this.tokens.get(i).getChunkTag()).append("]");
      sb.append(" ").append(this.tokens.get(i)).append(" -> ");
      sb.append("{").append(this.tokens.get(i).getPrimitive()).append("}");
      sb.append("_").append(this.tokens.get(i).getMorphologicalTag());
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * Returns the grammar check analysis as a table. Each row is composed of the
   * analysis for one token of the sentence. The column order is Syntactic Tag,
   * Chunk Tag, Lexeme, Primitive and Morphological Tag.
   * 
   * @return the analysis table
   */
  // public List<List<String>> getAnalysisAsTable() {
  // if (this.tokens.size() > 0 && this.tokens.get(0).getMorphologicalTag() ==
  // null) {
  // throw new IllegalStateException("The sentence was not analyzed yet.");
  // }
  //
  // List<List<String>> analysis = new ArrayList<List<String>>(5);
  // for (int i = 0; i < this.tokens.size(); i++) {
  // List<String> row = new ArrayList<String>();
  // row.add(this.tokens.get(i).getSyntacticTag().toString());
  // row.add(this.tokens.get(i).getChunkTag().toString());
  // row.add(this.tokens.get(i).getLexeme());
  // row.add(this.tokens.get(i).getPrimitive());
  // row.add(this.tokens.get(i).getMorphologicalTag().toString());
  // analysis.add(row);
  // }
  // return analysis;
  // }

  /**
   * An implementation of toPlainText method.
   */
  @Override
  public String toPlainText() {
    return this.sentence;
  }

}
