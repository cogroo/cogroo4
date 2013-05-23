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
/* First created by JCasGen Tue May 17 11:07:22 BRT 2011 */
package cogroo.uima;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;

/**
 * Updated by JCasGen Tue May 17 11:07:22 BRT 2011 XML source:
 * /Users/wcolen/Documents
 * /wrks/corpuswrk/TypeSystem/src/main/resources/cogroo/uima/TypeSystem.xml
 * 
 * @generated
 */
public class GrammarError extends Annotation {
  /**
   * @generated
   * @ordered
   */
  public final static int typeIndexID = JCasRegistry
      .register(GrammarError.class);
  /**
   * @generated
   * @ordered
   */
  public final static int type = typeIndexID;

  /** @generated */
  public int getTypeIndexID() {
    return typeIndexID;
  }

  /**
   * Never called. Disable default constructor
   * 
   * @generated
   */
  protected GrammarError() {
  }

  /**
   * Internal - constructor used by generator
   * 
   * @generated
   */
  public GrammarError(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }

  /** @generated */
  public GrammarError(JCas jcas) {
    super(jcas);
    readObject();
  }

  /** @generated */
  public GrammarError(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }

  /**
   * <!-- begin-user-doc --> Write your own initialization here <!--
   * end-user-doc -->
   * 
   * @generated modifiable
   */
  private void readObject() {
  }

  // *--------------*
  // * Feature: ruleId

  /**
   * getter for ruleId - gets
   * 
   * @generated
   */
  public String getRuleId() {
    if (GrammarError_Type.featOkTst
        && ((GrammarError_Type) jcasType).casFeat_ruleId == null)
      jcasType.jcas.throwFeatMissing("ruleId", "cogroo.uima.GrammarError");
    return jcasType.ll_cas.ll_getStringValue(addr,
        ((GrammarError_Type) jcasType).casFeatCode_ruleId);
  }

  /**
   * setter for ruleId - sets
   * 
   * @generated
   */
  public void setRuleId(String v) {
    if (GrammarError_Type.featOkTst
        && ((GrammarError_Type) jcasType).casFeat_ruleId == null)
      jcasType.jcas.throwFeatMissing("ruleId", "cogroo.uima.GrammarError");
    jcasType.ll_cas.ll_setStringValue(addr,
        ((GrammarError_Type) jcasType).casFeatCode_ruleId, v);
  }

  // *--------------*
  // * Feature: category

  /**
   * getter for category - gets
   * 
   * @generated
   */
  public String getCategory() {
    if (GrammarError_Type.featOkTst
        && ((GrammarError_Type) jcasType).casFeat_category == null)
      jcasType.jcas.throwFeatMissing("category", "cogroo.uima.GrammarError");
    return jcasType.ll_cas.ll_getStringValue(addr,
        ((GrammarError_Type) jcasType).casFeatCode_category);
  }

  /**
   * setter for category - sets
   * 
   * @generated
   */
  public void setCategory(String v) {
    if (GrammarError_Type.featOkTst
        && ((GrammarError_Type) jcasType).casFeat_category == null)
      jcasType.jcas.throwFeatMissing("category", "cogroo.uima.GrammarError");
    jcasType.ll_cas.ll_setStringValue(addr,
        ((GrammarError_Type) jcasType).casFeatCode_category, v);
  }

  // *--------------*
  // * Feature: error

  /**
   * getter for error - gets
   * 
   * @generated
   */
  public String getError() {
    if (GrammarError_Type.featOkTst
        && ((GrammarError_Type) jcasType).casFeat_error == null)
      jcasType.jcas.throwFeatMissing("error", "cogroo.uima.GrammarError");
    return jcasType.ll_cas.ll_getStringValue(addr,
        ((GrammarError_Type) jcasType).casFeatCode_error);
  }

  /**
   * setter for error - sets
   * 
   * @generated
   */
  public void setError(String v) {
    if (GrammarError_Type.featOkTst
        && ((GrammarError_Type) jcasType).casFeat_error == null)
      jcasType.jcas.throwFeatMissing("error", "cogroo.uima.GrammarError");
    jcasType.ll_cas.ll_setStringValue(addr,
        ((GrammarError_Type) jcasType).casFeatCode_error, v);
  }

  // *--------------*
  // * Feature: replace

  /**
   * getter for replace - gets
   * 
   * @generated
   */
  public String getReplace() {
    if (GrammarError_Type.featOkTst
        && ((GrammarError_Type) jcasType).casFeat_replace == null)
      jcasType.jcas.throwFeatMissing("replace", "cogroo.uima.GrammarError");
    return jcasType.ll_cas.ll_getStringValue(addr,
        ((GrammarError_Type) jcasType).casFeatCode_replace);
  }

  /**
   * setter for replace - sets
   * 
   * @generated
   */
  public void setReplace(String v) {
    if (GrammarError_Type.featOkTst
        && ((GrammarError_Type) jcasType).casFeat_replace == null)
      jcasType.jcas.throwFeatMissing("replace", "cogroo.uima.GrammarError");
    jcasType.ll_cas.ll_setStringValue(addr,
        ((GrammarError_Type) jcasType).casFeatCode_replace, v);
  }
}
