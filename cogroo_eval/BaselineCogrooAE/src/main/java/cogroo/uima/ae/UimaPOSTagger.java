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

import java.util.ArrayList;
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
import br.usp.pcs.lta.cogroo.entity.impl.runtime.MorphologicalTag;
import br.usp.pcs.lta.cogroo.tools.ProcessingEngine;
import cogroo.util.EntityUtils;
import cogroo.util.TypedSpan;

public class UimaPOSTagger extends AnnotationService implements
    ProcessingEngine {

  private Type tokenType;
  private Type sentenceType;
  private Feature posFeature;
  private Feature additionalContextFeature;
  private Feature lexemeFeature;

  public UimaPOSTagger() throws AnnotationServiceException {
    super("UimaPOSTagger");

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
      String tag = a.getFeatureValueAsString(posFeature);
      tokens.get(index).setOriginalPOSTag(tag);
      //tokens.get(index).setMorphologicalTag(toMorphologicalTag(tag));
      index++;
    }
    
    text.setTokens(EntityUtils.groupTokens(text.getSentence(), text.getTokens(), createSpanList(toTokensArray(tokens), toTagsArray(tokens))));

    cas.reset();
  }

  // this is from opennlp
  public static List<TypedSpan> createSpanList(String[] toks, String[] tags) {

    // initialize with the list maximum size
    List<TypedSpan> phrases = new ArrayList<TypedSpan>(toks.length); 
    String startTag = "";
    int startIndex = 0;
    boolean foundPhrase = false;

    for (int ci = 0, cn = tags.length; ci < cn; ci++) {
      String pred = tags[ci];
      if(!tags[ci].startsWith("B-") && !tags[ci].startsWith("I-")) {
        pred = "O";
      }
      if (pred.startsWith("B-")
          || (!pred.equals("I-" + startTag) && !pred.equals("O"))) { // start
        if (foundPhrase) { // handle the last
          phrases.add(new TypedSpan(startIndex, ci, startTag));
        }
        startIndex = ci;
        startTag = pred.substring(2);
        foundPhrase = true;
      } else if (pred.equals("I-" + startTag)) { // middle
        // do nothing
      } else if (foundPhrase) {// end
        phrases.add(new TypedSpan(startIndex, ci, startTag));
        foundPhrase = false;
        startTag = "";
      }
    }
    if (foundPhrase) { // leftover
      phrases.add(new TypedSpan(startIndex, tags.length, startTag));
    }

    return phrases;
  }

  @Override
  protected void initTypes(TypeSystem typeSystem) {
    sentenceType = cas.getTypeSystem().getType("opennlp.uima.Sentence");
    tokenType = cas.getTypeSystem().getType("opennlp.uima.Token");
    posFeature = tokenType.getFeatureByBaseName("pos");
    additionalContextFeature = tokenType.getFeatureByBaseName("additionalContext");
    lexemeFeature = tokenType.getFeatureByBaseName("lexeme");
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
      
      a.setStringValue(additionalContextFeature, t.getAdditionalContext());
      a.setStringValue(lexemeFeature, t.getLexeme());

      cas.getIndexRepository().addFS(a);
    }
  }
  
  
  private String[] toTagsArray(List<Token> tokens) {
    String[] tag = new String[tokens.size()];
    for (int i = 0; i < tokens.size(); i++) {
      tag[i] = tokens.get(i).getOriginalPOSTag();
    }
    return tag;
  }

  private String[] toTokensArray(List<Token> tokens) {
    String[] toks = new String[tokens.size()];
    for (int i = 0; i < tokens.size(); i++) {
      toks[i] = tokens.get(i).getLexeme();
    }
    return toks;
  }
}
