

/* First created by JCasGen Tue May 17 11:07:22 BRT 2011 */
package cogroo.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue May 17 11:07:22 BRT 2011
 * XML source: /Users/wcolen/Documents/wrks/corpuswrk/TypeSystem/src/main/resources/cogroo/uima/TypeSystem.xml
 * @generated */
public class GoldenSentence extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(GoldenSentence.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected GoldenSentence() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public GoldenSentence(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public GoldenSentence(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public GoldenSentence(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {}
     
 
    
  //*--------------*
  //* Feature: id

  /** getter for id - gets 
   * @generated */
  public String getId() {
    if (GoldenSentence_Type.featOkTst && ((GoldenSentence_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "cogroo.uima.GoldenSentence");
    return jcasType.ll_cas.ll_getStringValue(addr, ((GoldenSentence_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets  
   * @generated */
  public void setId(String v) {
    if (GoldenSentence_Type.featOkTst && ((GoldenSentence_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "cogroo.uima.GoldenSentence");
    jcasType.ll_cas.ll_setStringValue(addr, ((GoldenSentence_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: goldenGrammarErrors

  /** getter for goldenGrammarErrors - gets 
   * @generated */
  public FSArray getGoldenGrammarErrors() {
    if (GoldenSentence_Type.featOkTst && ((GoldenSentence_Type)jcasType).casFeat_goldenGrammarErrors == null)
      jcasType.jcas.throwFeatMissing("goldenGrammarErrors", "cogroo.uima.GoldenSentence");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((GoldenSentence_Type)jcasType).casFeatCode_goldenGrammarErrors)));}
    
  /** setter for goldenGrammarErrors - sets  
   * @generated */
  public void setGoldenGrammarErrors(FSArray v) {
    if (GoldenSentence_Type.featOkTst && ((GoldenSentence_Type)jcasType).casFeat_goldenGrammarErrors == null)
      jcasType.jcas.throwFeatMissing("goldenGrammarErrors", "cogroo.uima.GoldenSentence");
    jcasType.ll_cas.ll_setRefValue(addr, ((GoldenSentence_Type)jcasType).casFeatCode_goldenGrammarErrors, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for goldenGrammarErrors - gets an indexed value - 
   * @generated */
  public GoldenGrammarError getGoldenGrammarErrors(int i) {
    if (GoldenSentence_Type.featOkTst && ((GoldenSentence_Type)jcasType).casFeat_goldenGrammarErrors == null)
      jcasType.jcas.throwFeatMissing("goldenGrammarErrors", "cogroo.uima.GoldenSentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((GoldenSentence_Type)jcasType).casFeatCode_goldenGrammarErrors), i);
    return (GoldenGrammarError)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((GoldenSentence_Type)jcasType).casFeatCode_goldenGrammarErrors), i)));}

  /** indexed setter for goldenGrammarErrors - sets an indexed value - 
   * @generated */
  public void setGoldenGrammarErrors(int i, GoldenGrammarError v) { 
    if (GoldenSentence_Type.featOkTst && ((GoldenSentence_Type)jcasType).casFeat_goldenGrammarErrors == null)
      jcasType.jcas.throwFeatMissing("goldenGrammarErrors", "cogroo.uima.GoldenSentence");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((GoldenSentence_Type)jcasType).casFeatCode_goldenGrammarErrors), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((GoldenSentence_Type)jcasType).casFeatCode_goldenGrammarErrors), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    