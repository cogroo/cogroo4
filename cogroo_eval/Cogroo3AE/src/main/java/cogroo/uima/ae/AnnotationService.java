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

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

public abstract class AnnotationService {

  protected static final Logger logger = Logger
      .getLogger(AnnotationService.class);

  /** The TextAnnotator Analyzer */
  protected AnalysisEngine ae = null;

  /** The Common Analysis System to hold our documents */
  protected JCas cas = null;

  public AnnotationService(String descriptor) throws AnnotationServiceException {
    try {
      loadDescriptor(new File(descriptor));
    } catch (Exception e) {
      throw new AnnotationServiceException("Error loading descriptor: "
          + descriptor, e);
    }
  }

  private void loadDescriptor(File descriptorFile) throws IOException,
      InvalidXMLException, ResourceInitializationException {
    XMLInputSource in = new XMLInputSource(descriptorFile);
    ResourceSpecifier specifier = UIMAFramework.getXMLParser()
        .parseResourceSpecifier(in);
    ResourceManager manager = UIMAFramework.newDefaultResourceManager();

    this.ae = UIMAFramework.produceAnalysisEngine(specifier, manager, null);
    this.cas = this.ae.newJCas();

    // ************************************
    // Load the types and features
    // ************************************
    initTypes(cas.getTypeSystem());

  }

  protected abstract void initTypes(TypeSystem typeSystem);

}
