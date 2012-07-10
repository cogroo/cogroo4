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
package cogroo.uima.readers.probi;

import java.io.File;
import java.io.IOException;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import cogroo.uima.GoldenGrammarError;
import cogroo.uima.GoldenParagraph;
import cogroo.uima.GoldenSentence;

public class ProbiCollectionReader extends CollectionReader_ImplBase {

  public static final String PARAM_INPUT = "InputFile";

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

  private String mEncoding;

  private String mLanguage;

  private ProbiParser mParser;

  private int mDocs;

  private ProbiEntry mLastEntry;
  private int mDocCount = 0;

  private String mCat;

  /**
   * @see org.apache.uima.collection.CollectionReader_ImplBase#initialize()
   */
  public void initialize() throws ResourceInitializationException {
    File input = new File(
        ((String) getConfigParameterValue(PARAM_INPUT)).trim());
    mEncoding = (String) getConfigParameterValue(PARAM_ENCODING);
    mLanguage = (String) getConfigParameterValue(PARAM_LANGUAGE);
    try {
      mParser = new ProbiParser(input, mEncoding);
      mLastEntry = mParser.read();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
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

    int paragraphs = 0;

    // mLastEntry = mParser.read();
    StringBuilder text = new StringBuilder();
    String idPrefix = "PROBI_";
    if (mCat != null) {
      idPrefix += mCat + "_";
    }
    idPrefix += mDocCount + "-";
    while (mLastEntry != null && sameCat(mLastEntry.getCategory(), mCat)
        && paragraphs < 100) {

      int start = text.length();
      text.append(mLastEntry.getSentence());
      int end = text.length();
      text.append("\n\n");
      GoldenParagraph p = new GoldenParagraph(jcas);
      p.setId(paragraphs);
      p.setBegin(start);
      p.setEnd(end);
      p.addToIndexes();

      GoldenSentence s = new GoldenSentence(jcas);
      s.setId(idPrefix + paragraphs);
      s.setBegin(start);
      s.setEnd(end);

      if (mLastEntry.isContainsError()) {
        FSArray fsarr = new FSArray(jcas, 1);
        GoldenGrammarError ge = new GoldenGrammarError(jcas);
        ge.setBegin(start);
        ge.setEnd(end);
        ge.setCategory(mLastEntry.getCategory());
        // ge.setError("");
        // ge.setReplace(grers.get(j).getRep());
        ge.addToIndexes();
        fsarr.set(0, ge);
        s.setGoldenGrammarErrors(fsarr);
      }

      s.addToIndexes();

      paragraphs++;
      mLastEntry = mParser.read();
    }

    // put document in CAS
    jcas.setDocumentText(text.toString());

    // set language if it was explicitly specified as a configuration parameter
    if (mLanguage != null) {
      ((DocumentAnnotation) jcas.getDocumentAnnotationFs())
          .setLanguage(mLanguage);
    }
    if (mLastEntry != null) {
      mDocCount++;
      // mLastEntry = mParser.read();
      mCat = mLastEntry.getCategory();
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

  }

  /**
   * @see org.apache.uima.collection.CollectionReader#hasNext()
   */
  public boolean hasNext() {

    return mLastEntry != null;
  }

  private boolean sameCat(String category, String cat) {
    if (cat == null && category == null) {
      return true;
    }
    if (cat != null) {
      return cat.equals(category);
    }
    return false;
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
    return new Progress[] { new ProgressImpl(mDocs, -1, Progress.ENTITIES, true)
    /* , new ProgressImpl(mCurrentIndex,mFiles.size(),Progress.ENTITIES) */};
  }

}
