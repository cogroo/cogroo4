/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.ccsl.cogroo.formats.ad;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;

import org.junit.Before;
import org.junit.Test;

public class ADContractionNameSampleStreamTest {

  List<NameSample> samples = new ArrayList<NameSample>();
  List<NameSample> samples1 = new ArrayList<NameSample>();

  @Test
  public void testSimpleCount() throws IOException {
    assertEquals(6, samples.size());
  }

  @Test
  public void testCheckMergedContractions() throws IOException {
    assertEquals("no", samples.get(0).getSentence()[1]);
    assertEquals("no", samples.get(0).getSentence()[10]);
    assertEquals("Com", samples.get(1).getSentence()[0]);
    // assertEquals("relação", samples.get(1).getSentence()[1]);
    // assertEquals("à", samples.get(1).getSentence()[2]);
    assertEquals("mais_de", samples.get(2).getSentence()[4]);
    assertEquals("da", samples.get(2).getSentence()[7]);
    assertEquals("num", samples.get(3).getSentence()[25]);

  }

  @Test
  public void testSize() throws IOException {
    assertEquals(21, samples.get(0).getSentence().length);
    assertEquals(12, samples.get(1).getSentence().length);
    assertEquals(46, samples.get(2).getSentence().length);
    assertEquals(32, samples.get(3).getSentence().length);
  }

  @Test
  public void testAll() throws IOException {

    assertEquals(2, samples.get(0).getNames().length);
    assertEquals(new Span(1, 2), samples.get(0).getNames()[0]);
    assertEquals(new Span(10, 11), samples.get(0).getNames()[1]);

    assertEquals(1, samples.get(1).getNames().length);
    assertEquals(new Span(2, 3), samples.get(1).getNames()[0]);

    assertEquals(2, samples.get(2).getNames().length);
    assertEquals(new Span(7, 8), samples.get(2).getNames()[0]);
    assertEquals(new Span(9, 10), samples.get(2).getNames()[1]);

    assertEquals(2, samples.get(3).getNames().length);
    assertEquals(new Span(25, 26), samples.get(3).getNames()[0]);
    assertEquals(new Span(29, 30), samples.get(3).getNames()[1]);

  }

  @Before
  public void setup() throws IOException {
    InputStream in = ADContractionNameSampleStreamTest.class
        .getResourceAsStream("/br/ccsl/cogroo/formats/ad/ad.sample");

    ADContractionNameSampleStream stream = new ADContractionNameSampleStream(
        new PlainTextByLineStream(in, "UTF-8"), null);

    NameSample sample = stream.read();

    while (sample != null) {
      samples.add(sample);
      sample = stream.read();
    }

    in = ADContractionNameSampleStreamTest.class
        .getResourceAsStream("/br/ccsl/cogroo/formats/ad/ad.sample");

    Set<String> tags = new HashSet<String>();
    tags.add("adv");
    stream = new ADContractionNameSampleStream(new PlainTextByLineStream(in,
        "UTF-8"), tags);

    sample = stream.read();

    while (sample != null) {
      samples1.add(sample);
      sample = stream.read();
    }
  }

}
