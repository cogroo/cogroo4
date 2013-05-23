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

import opennlp.tools.util.Span;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.jcas.tcas.Annotation;

import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.tools.sentencedetector.SentenceDetectorI;

public class UimaSentenceDetector extends AnnotationService implements
    SentenceDetectorI {

  private Type sentenceType;

  public UimaSentenceDetector() throws AnnotationServiceException {
    super("UIMASentenceDetector");

  }

  public List<Sentence> process(String text) {
    // ************************************
    // Add text to the CAS
    // ************************************
    cas.setDocumentText(text);
    List<Sentence> sentences = new ArrayList<Sentence>();
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

    FSIterator<Annotation> iterator = cas.getAnnotationIndex(sentenceType)
        .iterator();
    while (iterator.hasNext()) {
      Annotation a = iterator.next();
      Sentence s = new Sentence();
      s.setSpan(new Span(a.getBegin(), a.getEnd()));
      s.setSentence(a.getCoveredText());
      s.setOffset(a.getBegin());
      sentences.add(s);
    }

    cas.reset();

    return sentences;
  }

  @Override
  protected void initTypes(TypeSystem typeSystem) {
    sentenceType = cas.getTypeSystem().getType("opennlp.uima.Sentence");
  }

  public static void main(String[] args) throws AnnotationServiceException {
    UimaSentenceDetector sd = new UimaSentenceDetector();
    System.out.println(sd.process("O sr. José chegou. Vamos sair."));
  }

}
