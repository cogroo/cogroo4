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
public class GoldenGrammarError extends Annotation {
  /**
   * @generated
   * @ordered
   */
  public final static int typeIndexID = JCasRegistry
      .register(GoldenGrammarError.class);
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
  protected GoldenGrammarError() {
  }

  /**
   * Internal - constructor used by generator
   * 
   * @generated
   */
  public GoldenGrammarError(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }

  /** @generated */
  public GoldenGrammarError(JCas jcas) {
    super(jcas);
    readObject();
  }

  /** @generated */
  public GoldenGrammarError(JCas jcas, int begin, int end) {
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
  // * Feature: category

  /**
   * getter for category - gets
   * 
   * @generated
   */
  public String getCategory() {
    if (GoldenGrammarError_Type.featOkTst
        && ((GoldenGrammarError_Type) jcasType).casFeat_category == null)
      jcasType.jcas.throwFeatMissing("category",
          "cogroo.uima.GoldenGrammarError");
    return jcasType.ll_cas.ll_getStringValue(addr,
        ((GoldenGrammarError_Type) jcasType).casFeatCode_category);
  }

  /**
   * setter for category - sets
   * 
   * @generated
   */
  public void setCategory(String v) {
    if (GoldenGrammarError_Type.featOkTst
        && ((GoldenGrammarError_Type) jcasType).casFeat_category == null)
      jcasType.jcas.throwFeatMissing("category",
          "cogroo.uima.GoldenGrammarError");
    jcasType.ll_cas.ll_setStringValue(addr,
        ((GoldenGrammarError_Type) jcasType).casFeatCode_category, v);
  }

  // *--------------*
  // * Feature: error

  /**
   * getter for error - gets
   * 
   * @generated
   */
  public String getError() {
    if (GoldenGrammarError_Type.featOkTst
        && ((GoldenGrammarError_Type) jcasType).casFeat_error == null)
      jcasType.jcas.throwFeatMissing("error", "cogroo.uima.GoldenGrammarError");
    return jcasType.ll_cas.ll_getStringValue(addr,
        ((GoldenGrammarError_Type) jcasType).casFeatCode_error);
  }

  /**
   * setter for error - sets
   * 
   * @generated
   */
  public void setError(String v) {
    if (GoldenGrammarError_Type.featOkTst
        && ((GoldenGrammarError_Type) jcasType).casFeat_error == null)
      jcasType.jcas.throwFeatMissing("error", "cogroo.uima.GoldenGrammarError");
    jcasType.ll_cas.ll_setStringValue(addr,
        ((GoldenGrammarError_Type) jcasType).casFeatCode_error, v);
  }

  // *--------------*
  // * Feature: replace

  /**
   * getter for replace - gets
   * 
   * @generated
   */
  public String getReplace() {
    if (GoldenGrammarError_Type.featOkTst
        && ((GoldenGrammarError_Type) jcasType).casFeat_replace == null)
      jcasType.jcas.throwFeatMissing("replace",
          "cogroo.uima.GoldenGrammarError");
    return jcasType.ll_cas.ll_getStringValue(addr,
        ((GoldenGrammarError_Type) jcasType).casFeatCode_replace);
  }

  /**
   * setter for replace - sets
   * 
   * @generated
   */
  public void setReplace(String v) {
    if (GoldenGrammarError_Type.featOkTst
        && ((GoldenGrammarError_Type) jcasType).casFeat_replace == null)
      jcasType.jcas.throwFeatMissing("replace",
          "cogroo.uima.GoldenGrammarError");
    jcasType.ll_cas.ll_setStringValue(addr,
        ((GoldenGrammarError_Type) jcasType).casFeatCode_replace, v);
  }
}
