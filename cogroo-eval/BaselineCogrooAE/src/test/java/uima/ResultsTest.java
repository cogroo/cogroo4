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

  @BeforeClass
  public static void loadCogroo() {
    multi = new MultiCogroo(new LegacyRuntimeConfiguration("/Users/wcolen/Documents/wrks/corpuswrk/BaselineCogrooAE/target/cogroo"));
    baseline = new Cogroo(new LegacyRuntimeConfiguration("/Users/wcolen/Documents/wrks/corpuswrk/BaselineCogrooAE/target/cogroo"));
  }
  
  @Test
  public void test01() {
    String text = "As menina vão chegar cedo.";
    
    System.out.println("*** Will process using BASELINE ***");
    CheckerResult baselineResult = baseline.analyseAndCheckText(text);
    
    System.out.println("*** Will process using MULTI ***");
    CheckerResult multiResult = multi.analyseAndCheckText(text);
    
    assertEquals(baselineResult.toString(), multiResult.toString());
  }

}
