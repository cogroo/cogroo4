/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package cogroo.uima.readers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import cogroo.uima.GoldenGrammarError;
import cogroo.uima.GoldenParagraph;
import cogroo.uima.GoldenSentence;
import cogroo.uima.readers.entities.Paragraph;
import cogroo.uima.readers.entities.SentenceEx;
import cogroo.uima.readers.entities.SentenceEx.GrEr;
import cogroo.uima.readers.entities.Text;

/**
 * A simple collection reader that reads documents from a directory in the
 * filesystem. It can be configured with the following parameters:
 * <ul>
 * <li><code>InputDirectory</code> - path to directory containing files</li>
 * <li><code>Encoding</code> (optional) - character encoding of the input files</li>
 * <li><code>Language</code> (optional) - language of the input documents</li>
 * </ul>
 * 
 * 
 */
public class ADCollectionReader extends CollectionReader_ImplBase {
  /**
   * Name of configuration parameter that must be set to the path of a directory
   * containing input files.
   */
  public static final String PARAM_INPUTDIR = "InputDirectory";

  /**
   * Name of configuration parameter that contains the character encoding used
   * by the input files. If not specified, the default system encoding will be
   * used.
   */
  public static final String PARAM_ENCODING = "Encoding";

  /**
   * Name of optional configuration parameter that contains the language of the
   * documents in the input directory. If specified this information will be
   * added to the CAS.
   */
  public static final String PARAM_LANGUAGE = "Language";

  /**
   * Name of optional configuration parameter that indicates including the
   * subdirectories (recursively) of the current input directory.
   */
  public static final String PARAM_SUBDIR = "BrowseSubdirectories";

  private ArrayList<File> mFiles;

  private String mEncoding;

  private String mLanguage;

  private Boolean mRecursive;

  private int mCurrentIndex;

  private MultiReader mMultiReader;

  private Text mNextText;

  private int mCurrentText;

  /**
   * @see org.apache.uima.collection.CollectionReader_ImplBase#initialize()
   */
  public void initialize() throws ResourceInitializationException {

    // check encoding
    // if( !Charset.defaultCharset().equals(Charset.forName("UTF-8")) )
    // {
    // System.out.println("invalid charset: " + Charset.defaultCharset());
    // throw new ResourceInitializationException();
    // }

    File directory = new File(
        ((String) getConfigParameterValue(PARAM_INPUTDIR)).trim());
    mEncoding = (String) getConfigParameterValue(PARAM_ENCODING);
    mLanguage = (String) getConfigParameterValue(PARAM_LANGUAGE);
    mRecursive = (Boolean) getConfigParameterValue(PARAM_SUBDIR);
    if (null == mRecursive) { // could be null if not set, it is optional
      mRecursive = Boolean.FALSE;
    }
    mCurrentIndex = 0;

    // if input directory does not exist or is not a directory, throw exception
    if (!directory.exists() || !directory.isDirectory()) {
      throw new ResourceInitializationException(
          ResourceConfigurationException.DIRECTORY_NOT_FOUND,
          new Object[] { PARAM_INPUTDIR, this.getMetaData().getName(),
              directory.getPath() });
    }

    // get list of files in the specified directory, and subdirectories if the
    // parameter PARAM_SUBDIR is set to True
    mFiles = new ArrayList<File>();
    addFilesFromDir(directory);

    this.mMultiReader = new MultiReader(mFiles, mEncoding);
  }

  /**
   * This method adds files in the directory passed in as a parameter to mFiles.
   * If mRecursive is true, it will include all files in all subdirectories
   * (recursively), as well.
   * 
   * @param dir
   */
  private void addFilesFromDir(File dir) {
    File[] files = dir.listFiles();
    for (int i = 0; i < files.length; i++) {
      if (!files[i].isDirectory()) {
        mFiles.add(files[i]);
      } else if (mRecursive) {
        addFilesFromDir(files[i]);
      }
    }
  }

  /**
   * @see org.apache.uima.collection.CollectionReader#hasNext()
   */
  public boolean hasNext() {
    if (mNextText == null) {
      try {
        mNextText = mMultiReader.read();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return mNextText != null;
  }

  /**
   * @see org.apache.uima.collection.CollectionReader#getNext(org.apache.uima.cas.CAS)
   */
  public void getNext(CAS aCAS) throws IOException, CollectionException {
    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new CollectionException(e);
    }

    // put document in CAS
    jcas.setDocumentText(mNextText.getText());

    for (Paragraph para : mNextText.getParagraphs()) {
      GoldenParagraph p = new GoldenParagraph(jcas);

      for (SentenceEx sent : para.getSentences()) {
        GoldenSentence s = new GoldenSentence(jcas);
        s.setId(sent.getId());
        s.setBegin(sent.getStart());
        s.setEnd(sent.getEnd());

        List<GrEr> grers = sent.getGrammarErrors();
        if (grers.size() > 0) {
          FSArray fsarr = new FSArray(jcas, grers.size());
          for (int j = 0; j < grers.size(); j++) {
            GoldenGrammarError ge = new GoldenGrammarError(jcas);
            ge.setBegin(grers.get(j).getStart());
            ge.setEnd(grers.get(j).getEnd());
            ge.setCategory(grers.get(j).getCat());
            ge.setError(grers.get(j).getErr());
            ge.setReplace(grers.get(j).getRep());
            ge.addToIndexes();
            fsarr.set(j, ge);
          }
          s.setGoldenGrammarErrors(fsarr);
        }

        s.addToIndexes();
      }

      p.setId(para.getId());
      p.setBegin(para.getStart());
      p.setEnd(para.getEnd());
      p.addToIndexes();
    }

    // set language if it was explicitly specified as a configuration parameter
    if (mLanguage != null) {
      ((DocumentAnnotation) jcas.getDocumentAnnotationFs())
          .setLanguage(mLanguage);
    }

    // Also store location of source document in CAS. This information is
    // critical
    // if CAS Consumers will need to know where the original document contents
    // are located.
    // For example, the Semantic Search CAS Indexer writes this information into
    // the
    // search index that it creates, which allows applications that use the
    // search index to
    // locate the documents that satisfy their semantic queries.
    // SourceDocumentInformation srcDocInfo = new
    // SourceDocumentInformation(jcas);
    // srcDocInfo.setUri(file.getAbsoluteFile().toURL().toString());
    // srcDocInfo.setOffsetInSource(0);
    // srcDocInfo.setDocumentSize((int) file.length());
    // srcDocInfo.setLastSegment(mCurrentIndex == mFiles.size());
    // srcDocInfo.addToIndexes();

    mCurrentText++;
    mNextText = mMultiReader.read();
  }

  /**
   * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#close()
   */
  public void close() throws IOException {
  }

  /**
   * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#getProgress()
   */
  public Progress[] getProgress() {
    return new Progress[] { new ProgressImpl(mCurrentText, -1,
        Progress.ENTITIES, true)
    /* , new ProgressImpl(mCurrentIndex,mFiles.size(),Progress.ENTITIES) */};
  }

}
