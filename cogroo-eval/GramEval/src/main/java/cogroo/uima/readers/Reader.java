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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cogroo.uima.readers.entities.Paragraph;
import cogroo.uima.readers.entities.SentenceEx;
import cogroo.uima.readers.entities.Text;

import opennlp.tools.formats.ad.ADSentenceStream;
import opennlp.tools.formats.ad.ADSentenceStream.Sentence;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class Reader implements ObjectStream<Text> {

  private ADSentenceStream sentenceStream;

  private int text = -1;
  private int para = -1;
  private boolean isSameText;
  private boolean isSamePara;
  private Sentence sent;

  private Pattern metaPattern = Pattern
      .compile("^[a-zA-Z]+(\\d+)-(\\d+)\\w?\\s+p=(\\d+).*");

  public Reader(InputStream in, String charset) throws IOException {
    this.sentenceStream = new ADSentenceStream(new PlainTextByLineStream(in,
        charset));
    sent = this.sentenceStream.read();
    updateMeta();
  }

  public Text read() throws IOException {

    if (sent == null) {
      return null;
    }
    int thisText = text;
    List<Paragraph> paragraphs = new ArrayList<Paragraph>();
    do {
      int thisPara = para;
      List<SentenceEx> sentences = new ArrayList<SentenceEx>();
      do {
        SentenceEx se = new SentenceEx(sent);
        sentences.add(se);
        sent = sentenceStream.read();

        updateMeta();
      } while (isSamePara);

      paragraphs.add(new Paragraph(sentences, thisPara));
    } while (isSameText);

    return new Text(paragraphs, thisText);

  }

  private void updateMeta() {
    if (this.sent != null) {
      String meta = this.sent.getMetadata();
      Matcher m = metaPattern.matcher(meta);
      int currentText;
      int currentPara;
      if (m.matches()) {
        currentText = Integer.parseInt(m.group(1));
        currentPara = Integer.parseInt(m.group(3));
      } else {
        throw new RuntimeException("Invalid metadata: " + meta);
      }
      isSamePara = isSameText = false;
      if (currentText == text)
        isSameText = true;

      if (currentPara == para)
        isSamePara = true;

      text = currentText;
      para = currentPara;

    } else {
      this.isSamePara = this.isSameText = false;
    }
  }

  public void reset() throws IOException, UnsupportedOperationException {
    this.sentenceStream.reset();
  }

  public void close() throws IOException {
    this.sentenceStream.close();

  }

  /**
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {

    FileInputStream in = new FileInputStream(
        "/Users/wcolen/Documents/wrks/corpus/Bosque_CF_8.0.ad.txt");
    Reader r = new Reader(in, "ISO-8859-1");

    Text t = r.read();
    while (t != null) {
      System.out.println(t.getId());
      for (Paragraph p : t.getParagraphs()) {
        System.out.println(" > " + p.getId());
        for (SentenceEx s : p.getSentences()) {
          System.out.println("   > " + s.getSentence().getMetadata());
        }
      }

      t = r.read();
    }
  }
}
