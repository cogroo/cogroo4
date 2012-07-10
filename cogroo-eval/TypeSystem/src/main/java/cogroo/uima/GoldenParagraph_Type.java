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
public class GoldenParagraph_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {
    return fsGenerator;
  }

  /** @generated */
  private final FSGenerator fsGenerator = new FSGenerator() {
    public FeatureStructure createFS(int addr, CASImpl cas) {
      if (GoldenParagraph_Type.this.useExistingInstance) {
        // Return eq fs instance if already created
        FeatureStructure fs = GoldenParagraph_Type.this.jcas
            .getJfsFromCaddr(addr);
        if (null == fs) {
          fs = new GoldenParagraph(addr, GoldenParagraph_Type.this);
          GoldenParagraph_Type.this.jcas.putJfsFromCaddr(addr, fs);
          return fs;
        }
        return fs;
      } else
        return new GoldenParagraph(addr, GoldenParagraph_Type.this);
    }
  };
  /** @generated */
  public final static int typeIndexID = GoldenParagraph.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  public final static boolean featOkTst = JCasRegistry
      .getFeatOkTst("cogroo.uima.GoldenParagraph");

  /** @generated */
  final Feature casFeat_id;
  /** @generated */
  final int casFeatCode_id;

  /** @generated */
  public int getId(int addr) {
    if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "cogroo.uima.GoldenParagraph");
    return ll_cas.ll_getIntValue(addr, casFeatCode_id);
  }

  /** @generated */
  public void setId(int addr, int v) {
    if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "cogroo.uima.GoldenParagraph");
    ll_cas.ll_setIntValue(addr, casFeatCode_id, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   * 
   * @generated
   */
  public GoldenParagraph_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType,
        getFSGenerator());

    casFeat_id = jcas.getRequiredFeatureDE(casType, "id", "uima.cas.Integer",
        featOkTst);
    casFeatCode_id = (null == casFeat_id) ? JCas.INVALID_FEATURE_CODE
        : ((FeatureImpl) casFeat_id).getCode();

  }
}
