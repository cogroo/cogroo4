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
package uima;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import br.usp.pcs.lta.cogroo.configuration.LegacyRuntimeConfiguration;
import br.usp.pcs.lta.cogroo.grammarchecker.CheckerResult;
import br.usp.pcs.lta.cogroo.grammarchecker.Cogroo;
import cogroo.MultiCogroo;

public class ResultsTest {

  private static Cogroo baseline;
  private static MultiCogroo multi;

  // @BeforeClass
  public static void loadCogroo() {
    multi = new MultiCogroo(
        new LegacyRuntimeConfiguration(
            "/Users/wcolen/Documents/wrks/corpuswrk/BaselineCogrooAE/target/cogroo"));
    baseline = new Cogroo(
        new LegacyRuntimeConfiguration(
            "/Users/wcolen/Documents/wrks/corpuswrk/BaselineCogrooAE/target/cogroo"));
  }

  // @Test
  public void test01() {
    String text = "As menina vão chegar cedo.";

    System.out.println("*** Will process using BASELINE ***");
    CheckerResult baselineResult = baseline.analyseAndCheckText(text);

    System.out.println("*** Will process using MULTI ***");
    CheckerResult multiResult = multi.analyseAndCheckText(text);

    assertEquals(baselineResult.toString(), multiResult.toString());
  }

}
