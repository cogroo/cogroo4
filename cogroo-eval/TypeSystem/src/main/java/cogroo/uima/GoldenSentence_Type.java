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
public class GoldenSentence_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {
    return fsGenerator;
  }

  /** @generated */
  private final FSGenerator fsGenerator = new FSGenerator() {
    public FeatureStructure createFS(int addr, CASImpl cas) {
      if (GoldenSentence_Type.this.useExistingInstance) {
        // Return eq fs instance if already created
        FeatureStructure fs = GoldenSentence_Type.this.jcas
            .getJfsFromCaddr(addr);
        if (null == fs) {
          fs = new GoldenSentence(addr, GoldenSentence_Type.this);
          GoldenSentence_Type.this.jcas.putJfsFromCaddr(addr, fs);
          return fs;
        }
        return fs;
      } else
        return new GoldenSentence(addr, GoldenSentence_Type.this);
    }
  };
  /** @generated */
  public final static int typeIndexID = GoldenSentence.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  public final static boolean featOkTst = JCasRegistry
      .getFeatOkTst("cogroo.uima.GoldenSentence");

  /** @generated */
  final Feature casFeat_id;
  /** @generated */
  final int casFeatCode_id;

  /** @generated */
  public String getId(int addr) {
    if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "cogroo.uima.GoldenSentence");
    return ll_cas.ll_getStringValue(addr, casFeatCode_id);
  }

  /** @generated */
  public void setId(int addr, String v) {
    if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "cogroo.uima.GoldenSentence");
    ll_cas.ll_setStringValue(addr, casFeatCode_id, v);
  }

  /** @generated */
  final Feature casFeat_goldenGrammarErrors;
  /** @generated */
  final int casFeatCode_goldenGrammarErrors;

  /** @generated */
  public int getGoldenGrammarErrors(int addr) {
    if (featOkTst && casFeat_goldenGrammarErrors == null)
      jcas.throwFeatMissing("goldenGrammarErrors", "cogroo.uima.GoldenSentence");
    return ll_cas.ll_getRefValue(addr, casFeatCode_goldenGrammarErrors);
  }

  /** @generated */
  public void setGoldenGrammarErrors(int addr, int v) {
    if (featOkTst && casFeat_goldenGrammarErrors == null)
      jcas.throwFeatMissing("goldenGrammarErrors", "cogroo.uima.GoldenSentence");
    ll_cas.ll_setRefValue(addr, casFeatCode_goldenGrammarErrors, v);
  }

  /** @generated */
  public int getGoldenGrammarErrors(int addr, int i) {
    if (featOkTst && casFeat_goldenGrammarErrors == null)
      jcas.throwFeatMissing("goldenGrammarErrors", "cogroo.uima.GoldenSentence");
    if (lowLevelTypeChecks)
      return ll_cas
          .ll_getRefArrayValue(
              ll_cas.ll_getRefValue(addr, casFeatCode_goldenGrammarErrors), i,
              true);
    jcas.checkArrayBounds(
        ll_cas.ll_getRefValue(addr, casFeatCode_goldenGrammarErrors), i);
    return ll_cas.ll_getRefArrayValue(
        ll_cas.ll_getRefValue(addr, casFeatCode_goldenGrammarErrors), i);
  }

  /** @generated */
  public void setGoldenGrammarErrors(int addr, int i, int v) {
    if (featOkTst && casFeat_goldenGrammarErrors == null)
      jcas.throwFeatMissing("goldenGrammarErrors", "cogroo.uima.GoldenSentence");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(
          ll_cas.ll_getRefValue(addr, casFeatCode_goldenGrammarErrors), i, v,
          true);
    jcas.checkArrayBounds(
        ll_cas.ll_getRefValue(addr, casFeatCode_goldenGrammarErrors), i);
    ll_cas.ll_setRefArrayValue(
        ll_cas.ll_getRefValue(addr, casFeatCode_goldenGrammarErrors), i, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   * 
   * @generated
   */
  public GoldenSentence_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType,
        getFSGenerator());

    casFeat_id = jcas.getRequiredFeatureDE(casType, "id", "uima.cas.String",
        featOkTst);
    casFeatCode_id = (null == casFeat_id) ? JCas.INVALID_FEATURE_CODE
        : ((FeatureImpl) casFeat_id).getCode();

    casFeat_goldenGrammarErrors = jcas.getRequiredFeatureDE(casType,
        "goldenGrammarErrors", "uima.cas.FSArray", featOkTst);
    casFeatCode_goldenGrammarErrors = (null == casFeat_goldenGrammarErrors) ? JCas.INVALID_FEATURE_CODE
        : ((FeatureImpl) casFeat_goldenGrammarErrors).getCode();

  }
}
