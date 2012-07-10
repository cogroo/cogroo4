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
package cogroo.uima.readers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.ObjectStream;
import cogroo.uima.readers.entities.Paragraph;
import cogroo.uima.readers.entities.SentenceEx;
import cogroo.uima.readers.entities.Text;

/**
 * Not threadsafe.
 * 
 */
public class MultiReader implements ObjectStream<Text> {

  private final List<File> mFiles;
  private final String mEncoding;
  private int mCurrentIndex;

  private Reader mCurrentReader;

  public MultiReader(List<File> aFiles, String aEncoding) {
    this.mFiles = aFiles;
    this.mEncoding = aEncoding;

    this.mCurrentIndex = 0;

  }

  public Text read() throws IOException {
    if (this.mCurrentReader == null) {
      updateReader();
    }
    Text ret = this.mCurrentReader.read();
    if (ret != null)
      return ret;

    this.mCurrentIndex++;
    if (this.mCurrentIndex < this.mFiles.size()) {
      this.updateReader();
      return read();
    }

    return null;
  }

  public void reset() throws IOException, UnsupportedOperationException {
    this.mCurrentReader.close();
    this.mCurrentIndex = 0;
    this.updateReader();
  }

  public void close() throws IOException {
    this.mCurrentReader.close();
    this.mCurrentIndex = 0;
  }

  private void updateReader() throws FileNotFoundException, IOException {
    if (this.mCurrentReader != null)
      this.mCurrentReader.close();
    this.mCurrentReader = new Reader(new FileInputStream(
        this.mFiles.get(this.mCurrentIndex)), this.mEncoding);
  }

  public static void main(String[] args) throws FileNotFoundException,
      IOException {
    List<File> fs = new ArrayList<File>();
    fs.add(new File(
        "/Users/wcolen/Documents/wrks/corpus/Bosque/Bosque_CF_8.0.ad.txt"));
    // fs.add(new
    // File("/Users/wcolen/Documents/wrks/corpuswrk/Corpus/Metro/Metro6.txt"));

    MultiReader r = new MultiReader(fs, "ISO-8859-1");

    int tcount = 1;
    Text t = r.read();
    while (t != null) {
      String text = t.getText();
      System.out.println(tcount++);
      int pcount = 1;
      for (Paragraph p : t.getParagraphs()) {
        System.out.println(" > " + pcount++);
        System.out.println("[" + text.subSequence(p.getStart(), p.getEnd())
            + "]");
        for (SentenceEx s : p.getSentences()) {

          System.out.println("   > " + s.getSentence().getMetadata());
          System.out.println("[" + text.subSequence(s.getStart(), s.getEnd())
              + "]");
          s.getGrammarErrors();
        }
      }

      t = r.read();
    }
  }
}
