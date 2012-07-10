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
package cogroo.uima.ae;

import java.util.List;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.entity.Token;
import br.usp.pcs.lta.cogroo.tools.ProcessingEngine;

public class UimaFeaturizer extends AnnotationService implements
    ProcessingEngine {

  private Type tokenType;
  private Type sentenceType;
  private Feature posFeature;
  private Feature lexemeFeature;
  private Feature featFeature;
  
  
  public UimaFeaturizer() throws AnnotationServiceException {
    super("UimaFeaturizer");

  }

  public void process(Sentence text) {
    // ************************************
    // Add text to the CAS
    // ************************************
    updateCas(text, cas);
    // ************************************
    // Analyze text
    // ************************************
    try {
      ae.process(cas);
    } catch (Exception e) {
      throw new RuntimeException("Error processing a text.", e);
    }

    // ************************************
    // Extract the result using annotated CAS
    // ************************************

    FSIterator<Annotation> tokenIterator = cas.getAnnotationIndex(tokenType)
        .iterator();

    int index = 0;
    List<Token> tokens = text.getTokens();
    
    while (tokenIterator.hasNext()) {
      Annotation a = tokenIterator.next();
      String tag = a.getFeatureValueAsString(featFeature);
      if(!"-".equals(tag)) {
        tokens.get(index).setOriginalFeatures(tag);
      }
      //tokens.get(index).setMorphologicalTag(toMorphologicalTag(tag));
      index++;
    }
    

    cas.reset();
  }
  
  @Override
  protected void initTypes(TypeSystem typeSystem) {
    sentenceType = cas.getTypeSystem().getType("opennlp.uima.Sentence");
    tokenType = cas.getTypeSystem().getType("opennlp.uima.Token");
    posFeature = tokenType.getFeatureByBaseName("pos");
    lexemeFeature = tokenType.getFeatureByBaseName("lexeme");
    featFeature = tokenType.getFeatureByBaseName("feats");
  }

  private void updateCas(Sentence sentence, JCas cas) {
    cas.reset();
    cas.setDocumentText(sentence.getSentence());

    AnnotationFS a = cas.getCas().createAnnotation(sentenceType,
        sentence.getOffset(),
        sentence.getOffset() + sentence.getSentence().length());

    cas.getIndexRepository().addFS(a);

    for (Token t : sentence.getTokens()) {
      a = cas.getCas().createAnnotation(tokenType,
          t.getSpan().getStart() + sentence.getOffset(),
          t.getSpan().getEnd() + sentence.getOffset());
      
      if(t.getLexeme() == null) {
        System.out.println();
      }
      
      a.setStringValue(lexemeFeature, t.getLexeme());
      a.setStringValue(posFeature, t.getOriginalPOSTag());

      cas.getIndexRepository().addFS(a);
    }
  }

}
