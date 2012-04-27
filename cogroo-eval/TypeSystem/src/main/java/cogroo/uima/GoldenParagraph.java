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
public class GoldenParagraph extends Annotation {
  /**
   * @generated
   * @ordered
   */
  public final static int typeIndexID = JCasRegistry
      .register(GoldenParagraph.class);
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
  protected GoldenParagraph() {
  }

  /**
   * Internal - constructor used by generator
   * 
   * @generated
   */
  public GoldenParagraph(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }

  /** @generated */
  public GoldenParagraph(JCas jcas) {
    super(jcas);
    readObject();
  }

  /** @generated */
  public GoldenParagraph(JCas jcas, int begin, int end) {
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
  // * Feature: id

  /**
   * getter for id - gets
   * 
   * @generated
   */
  public int getId() {
    if (GoldenParagraph_Type.featOkTst
        && ((GoldenParagraph_Type) jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "cogroo.uima.GoldenParagraph");
    return jcasType.ll_cas.ll_getIntValue(addr,
        ((GoldenParagraph_Type) jcasType).casFeatCode_id);
  }

  /**
   * setter for id - sets
   * 
   * @generated
   */
  public void setId(int v) {
    if (GoldenParagraph_Type.featOkTst
        && ((GoldenParagraph_Type) jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "cogroo.uima.GoldenParagraph");
    jcasType.ll_cas.ll_setIntValue(addr,
        ((GoldenParagraph_Type) jcasType).casFeatCode_id, v);
  }
}
