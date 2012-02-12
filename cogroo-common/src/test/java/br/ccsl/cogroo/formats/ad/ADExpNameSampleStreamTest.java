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

import opennlp.tools.formats.ad.ADParagraphStreamTest;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;

import org.junit.Before;
import org.junit.Test;

import br.ccsl.cogroo.formats.ad.ADExpNameSampleStream;

public class ADExpNameSampleStreamTest {

  List<NameSample> samples = new ArrayList<NameSample>();
  List<NameSample> samples1 = new ArrayList<NameSample>();

  @Test
  public void testSimpleCount() throws IOException {
    assertEquals(4, samples.size());
  }

  @Test
  public void testCheckMergedContractions() throws IOException {

    assertEquals("no", samples.get(0).getSentence()[1]);
    assertEquals("no", samples.get(0).getSentence()[11]);
    assertEquals("Com", samples.get(1).getSentence()[0]);
    assertEquals("relação", samples.get(1).getSentence()[1]);
    assertEquals("à", samples.get(1).getSentence()[2]);
    assertEquals("mais", samples.get(2).getSentence()[4]);
    assertEquals("de", samples.get(2).getSentence()[5]);
    assertEquals("da", samples.get(2).getSentence()[8]);
    assertEquals("num", samples.get(3).getSentence()[26]);

  }

  @Test
  public void testSize() throws IOException {
    assertEquals(25, samples.get(0).getSentence().length);
    assertEquals(12, samples.get(1).getSentence().length);
    assertEquals(59, samples.get(2).getSentence().length);
    assertEquals(33, samples.get(3).getSentence().length);
  }

  @Test
  public void testAll() throws IOException {

    assertEquals(4, samples.get(0).getNames().length);
    assertEquals(new Span(8, 10, "prop"), samples.get(0).getNames()[0]);
    assertEquals(new Span(12, 14, "prop"), samples.get(0).getNames()[1]);
    assertEquals(new Span(15, 17, "prop"), samples.get(0).getNames()[2]);
    assertEquals(new Span(20, 22, "prop"), samples.get(0).getNames()[3]);

    assertEquals(0, samples.get(1).getNames().length);

    assertEquals(12, samples.get(2).getNames().length);
    assertEquals(new Span(4, 6, "adv"), samples.get(2).getNames()[0]);
    assertEquals(new Span(22, 24, "prop"), samples.get(2).getNames()[1]);

    assertEquals(1, samples.get(3).getNames().length);
    assertEquals(new Span(8, 10, "adv"), samples.get(3).getNames()[0]);

  }
  
  @Test
  public void testAdv() throws IOException {

    assertEquals(0, samples1.get(0).getNames().length);

    assertEquals(0, samples1.get(1).getNames().length);

    assertEquals(1, samples1.get(2).getNames().length);
    assertEquals(new Span(4, 6, "adv"), samples1.get(2).getNames()[0]);

    assertEquals(1, samples1.get(3).getNames().length);
    assertEquals(new Span(8, 10, "adv"), samples1.get(3).getNames()[0]);

  }

  @Before
  public void setup() throws IOException {
    InputStream in = ADParagraphStreamTest.class
        .getResourceAsStream("/opennlp/tools/formats/ad.sample");

    ADExpNameSampleStream stream = new ADExpNameSampleStream(
        new PlainTextByLineStream(in, "UTF-8"), null);

    NameSample sample = stream.read();

    while (sample != null) {
      samples.add(sample);
      sample = stream.read();
    }

    in = ADParagraphStreamTest.class
        .getResourceAsStream("/opennlp/tools/formats/ad.sample");

    Set<String> tags = new HashSet<String>();
    tags.add("adv");
    stream = new ADExpNameSampleStream(new PlainTextByLineStream(in, "UTF-8"),
        tags);

    sample = stream.read();

    while (sample != null) {
      samples1.add(sample);
      sample = stream.read();
    }
  }

}
