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
      String fName = getRepoPath() + "/" + descriptor + "/" + descriptor
          + "_pear.xml";
      System.out.println("Will load pear from " + fName);
      loadDescriptor(new File(fName));
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

  private static String getRepoPath() {
    return System.getenv("REPO_ROOT");
  }

  protected abstract void initTypes(TypeSystem typeSystem);

}
