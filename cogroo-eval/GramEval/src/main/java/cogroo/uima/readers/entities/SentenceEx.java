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
package cogroo.uima.readers.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.formats.ad.ADSentenceStream.Sentence;

public class SentenceEx {

  private int start;
  private final Sentence sentence;

  public SentenceEx(Sentence sentence) {
    super();
    this.sentence = sentence;
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return this.start + this.sentence.getText().length();
  }

  public Sentence getSentence() {
    return sentence;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public String getId() {
    return sentence.getMetadata().substring(0,
        sentence.getMetadata().indexOf(" "));
  }

  private Pattern erPattern = Pattern
      .compile("c=(.*?)\\s+err=\"(.*?)\"\\s+rep=\"(.*?)\"");

  public List<GrEr> getGrammarErrors() {
    ArrayList<GrEr> errors = new ArrayList<SentenceEx.GrEr>();
    String meta = sentence.getMetadata();
    Matcher m = erPattern.matcher(meta);
    while (m.find()) {

      String text = this.sentence.getText();
      String err = m.group(2);
      int b = text.indexOf(err);
      if (b < 0)
        throw new RuntimeException();
      errors.add(new GrEr(m.group(1), m.group(2), m.group(3), this.start + b,
          this.start + b + err.length()));
    }
    return errors;
  }

  public class GrEr {

    private int start;
    private int end;

    private String cat;
    private String err;
    private String rep;

    public GrEr(String cat, String err, String rep, int start, int end) {
      super();
      this.cat = cat;
      this.err = err;
      this.rep = rep;
      this.start = start;
      this.end = end;
    }

    public String getCat() {
      return cat;
    }

    public String getErr() {
      return err;
    }

    public String getRep() {
      return rep;
    }

    public int getStart() {
      return start;
    }

    public int getEnd() {
      return end;
    }
  }
}
