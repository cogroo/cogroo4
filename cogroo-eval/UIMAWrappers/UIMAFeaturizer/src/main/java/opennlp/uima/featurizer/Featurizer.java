/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreemnets.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opennlp.uima.featurizer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import opennlp.uima.util.AnnotationComboIterator;
import opennlp.uima.util.AnnotationIteratorPair;
import opennlp.uima.util.AnnotatorUtil;
import opennlp.uima.util.UimaUtil;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.CasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

import br.ccsl.cogroo.tools.featurizer.FeaturizerME;
import br.ccsl.cogroo.tools.featurizer.FeaturizerModel;

public final class Featurizer extends CasAnnotator_ImplBase {
  
  /**
   * The chunk tag feature parameter
   */
  public static final String FEATURIZER_TAG_FEATURE_PARAMETER = 
      "opennlp.uima.FeaturizerFeature";
  
  private Type mTokenType;

  private Feature mPosFeature;

  private FeaturizerME mFeaturizer;
  
  private UimaContext context;
  
  private Logger mLogger;

  private Feature mFeaturizerFeature;

  private Type mSentenceType;

  private Feature mLexemeFeature;
  
  /**
   * Initializes a new instance.
   *
   * Note: Use {@link #initialize(UimaContext) } to initialize 
   * this instance. Not use the constructor.
   */
  public Featurizer() {
    // must not be implemented !
  }
  
  /**
   * Initializes the current instance with the given context.
   * 
   * Note: Do all initialization in this method, do not use the constructor.
   */
  public void initialize(UimaContext context)
      throws ResourceInitializationException {

    super.initialize(context);
    
	this.context = context;
	  
    mLogger = context.getLogger();  
    
    if (mLogger.isLoggable(Level.INFO)) {
      mLogger.log(Level.INFO, "Initializing the OpenNLP Featurizer annotator.");
    }    
    
    FeaturizerModel model;
    
    try {
      FeaturizerModelResource modelResource = 
            (FeaturizerModelResource) context.getResourceObject(UimaUtil.MODEL_PARAMETER);
        
        model = modelResource.getModel();
    }
    catch (ResourceAccessException e) {
        throw new ResourceInitializationException(e);
    }
    
    mFeaturizer = new FeaturizerME(model);
  }

  /**
   * Initializes the type system.
   */
  public void typeSystemInit(TypeSystem typeSystem)
      throws AnalysisEngineProcessException {
    
    // sentence type
    mSentenceType = AnnotatorUtil.getRequiredTypeParameter(this.context, typeSystem,
        UimaUtil.SENTENCE_TYPE_PARAMETER);
    
    // token type
    mTokenType = AnnotatorUtil.getRequiredTypeParameter(context, typeSystem,
        UimaUtil.TOKEN_TYPE_PARAMETER);

    // pos feature
    mPosFeature = AnnotatorUtil.getRequiredFeatureParameter(context, mTokenType, UimaUtil.POS_FEATURE_PARAMETER, 
    		CAS.TYPE_NAME_STRING);
    
    mLexemeFeature = AnnotatorUtil.getRequiredFeatureParameter(this.context, mTokenType,
        "opennlp.uima.LexemeFeature", CAS.TYPE_NAME_STRING);
    
    // featurizer feature
    mFeaturizerFeature = AnnotatorUtil.getRequiredFeatureParameter(context, mTokenType,
            FEATURIZER_TAG_FEATURE_PARAMETER, CAS.TYPE_NAME_STRING);
  }

  /**
   * Performs featurizer on the given tcas object.
   */
  public void process(CAS tcas) {
    
    final AnnotationComboIterator comboIterator = new AnnotationComboIterator(tcas,
        mSentenceType, mTokenType);

    for (AnnotationIteratorPair annotationIteratorPair : comboIterator) {
      
      final List<AnnotationFS> sentenceTokenAnnotationList = new LinkedList<AnnotationFS>();

      final List<String> sentenceTokenList = new LinkedList<String>();
      final List<String> sentenceTagsList = new LinkedList<String>();

      for (AnnotationFS tokenAnnotation : annotationIteratorPair.getSubIterator()) {

        sentenceTokenAnnotationList.add(tokenAnnotation);

        sentenceTokenList.add(tokenAnnotation.getFeatureValueAsString(mLexemeFeature));
        
        sentenceTagsList.add(tokenAnnotation.getFeatureValueAsString(mPosFeature));
      }
      
      String[] toks = sentenceTokenList.toArray(new String[sentenceTokenList.size()]);
      String[] tags = sentenceTagsList.toArray(new String[sentenceTagsList.size()]);
      
      String[] featureTag = mFeaturizer.featurize(toks, tags);

      final Iterator<AnnotationFS> sentenceTokenIterator = sentenceTokenAnnotationList.iterator();

      int index = 0;
      while (sentenceTokenIterator.hasNext()) {
        final AnnotationFS tokenAnnotation = sentenceTokenIterator.next();
        tokenAnnotation.setStringValue(mFeaturizerFeature, featureTag[index]);

        index++;
      }
    }
    
  }

  /**
   * Releases allocated resources.
   */
  public void destroy() {
    // dereference model to allow garbage collection 
    mFeaturizer = null;
  }
}