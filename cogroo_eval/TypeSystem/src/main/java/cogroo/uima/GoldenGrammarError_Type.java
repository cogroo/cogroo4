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
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/**
 * Updated by JCasGen Tue May 17 11:07:22 BRT 2011
 * 
 * @generated
 */
public class GoldenGrammarError_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {
    return fsGenerator;
  }

  /** @generated */
  private final FSGenerator fsGenerator = new FSGenerator() {
    public FeatureStructure createFS(int addr, CASImpl cas) {
      if (GoldenGrammarError_Type.this.useExistingInstance) {
        // Return eq fs instance if already created
        FeatureStructure fs = GoldenGrammarError_Type.this.jcas
            .getJfsFromCaddr(addr);
        if (null == fs) {
          fs = new GoldenGrammarError(addr, GoldenGrammarError_Type.this);
          GoldenGrammarError_Type.this.jcas.putJfsFromCaddr(addr, fs);
          return fs;
        }
        return fs;
      } else
        return new GoldenGrammarError(addr, GoldenGrammarError_Type.this);
    }
  };
  /** @generated */
  public final static int typeIndexID = GoldenGrammarError.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  public final static boolean featOkTst = JCasRegistry
      .getFeatOkTst("cogroo.uima.GoldenGrammarError");

  /** @generated */
  final Feature casFeat_category;
  /** @generated */
  final int casFeatCode_category;

  /** @generated */
  public String getCategory(int addr) {
    if (featOkTst && casFeat_category == null)
      jcas.throwFeatMissing("category", "cogroo.uima.GoldenGrammarError");
    return ll_cas.ll_getStringValue(addr, casFeatCode_category);
  }

  /** @generated */
  public void setCategory(int addr, String v) {
    if (featOkTst && casFeat_category == null)
      jcas.throwFeatMissing("category", "cogroo.uima.GoldenGrammarError");
    ll_cas.ll_setStringValue(addr, casFeatCode_category, v);
  }

  /** @generated */
  final Feature casFeat_error;
  /** @generated */
  final int casFeatCode_error;

  /** @generated */
  public String getError(int addr) {
    if (featOkTst && casFeat_error == null)
      jcas.throwFeatMissing("error", "cogroo.uima.GoldenGrammarError");
    return ll_cas.ll_getStringValue(addr, casFeatCode_error);
  }

  /** @generated */
  public void setError(int addr, String v) {
    if (featOkTst && casFeat_error == null)
      jcas.throwFeatMissing("error", "cogroo.uima.GoldenGrammarError");
    ll_cas.ll_setStringValue(addr, casFeatCode_error, v);
  }

  /** @generated */
  final Feature casFeat_replace;
  /** @generated */
  final int casFeatCode_replace;

  /** @generated */
  public String getReplace(int addr) {
    if (featOkTst && casFeat_replace == null)
      jcas.throwFeatMissing("replace", "cogroo.uima.GoldenGrammarError");
    return ll_cas.ll_getStringValue(addr, casFeatCode_replace);
  }

  /** @generated */
  public void setReplace(int addr, String v) {
    if (featOkTst && casFeat_replace == null)
      jcas.throwFeatMissing("replace", "cogroo.uima.GoldenGrammarError");
    ll_cas.ll_setStringValue(addr, casFeatCode_replace, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   * 
   * @generated
   */
  public GoldenGrammarError_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType,
        getFSGenerator());

    casFeat_category = jcas.getRequiredFeatureDE(casType, "category",
        "uima.cas.String", featOkTst);
    casFeatCode_category = (null == casFeat_category) ? JCas.INVALID_FEATURE_CODE
        : ((FeatureImpl) casFeat_category).getCode();

    casFeat_error = jcas.getRequiredFeatureDE(casType, "error",
        "uima.cas.String", featOkTst);
    casFeatCode_error = (null == casFeat_error) ? JCas.INVALID_FEATURE_CODE
        : ((FeatureImpl) casFeat_error).getCode();

    casFeat_replace = jcas.getRequiredFeatureDE(casType, "replace",
        "uima.cas.String", featOkTst);
    casFeatCode_replace = (null == casFeat_replace) ? JCas.INVALID_FEATURE_CODE
        : ((FeatureImpl) casFeat_replace).getCode();

  }
}
