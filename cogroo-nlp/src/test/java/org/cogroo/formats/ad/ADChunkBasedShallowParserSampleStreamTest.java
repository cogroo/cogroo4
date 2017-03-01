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
package org.cogroo.formats.ad;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cogroo.tools.ResourceAsStreamFactory;
import org.junit.Before;
import org.junit.Test;

import opennlp.tools.chunker.ChunkSample;

import static org.junit.Assert.assertEquals;

public class ADChunkBasedShallowParserSampleStreamTest {

  List<ChunkSample> samples = new ArrayList<ChunkSample>();

  @Test
  public void testSimpleCount() throws IOException {
    assertEquals(6, samples.size());
  }

  @Test
  public void testChunks() throws IOException {

    assertEquals("Inicia", samples.get(0).getSentence()[0]);
    assertEquals("v-fin|B-VP", samples.get(0).getTags()[0]);
    assertEquals("B-P", samples.get(0).getPreds()[0]);

    assertEquals("em", samples.get(0).getSentence()[1]);
    assertEquals("prp|B-PP", samples.get(0).getTags()[1]);
    assertEquals("O", samples.get(0).getPreds()[1]);

    // assertEquals("galpão", samples.get(3).getSentence()[2]);
    assertEquals("n|B-NP", samples.get(3).getTags()[2]);
    assertEquals("B-SUBJ", samples.get(3).getPreds()[2]);

  }

  @Before
  public void setup() throws IOException {
    ResourceAsStreamFactory in = new ResourceAsStreamFactory(this.getClass(), "/br/ccsl/cogroo/formats/ad/ad.sample");


    ADChunkBasedShallowParserSampleStream stream = new ADChunkBasedShallowParserSampleStream(
        in, "UTF-8", "SUBJ,P", false, false, false);

    ChunkSample sample = stream.read();

    while (sample != null) {
//      System.out.println(sample);
      samples.add(sample);
      sample = stream.read();
    }
  }

}
