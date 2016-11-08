package org.cogroo.tools.checker.rules.applier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.cogroo.util.FileUtils;
import org.junit.Test;

public class RulesXmlAccessTest {
 
  @Test
  public void testCanReadRule() throws IOException {
    String rule = getRule();
    assertNotNull(rule);
    
  }
    
  @Test
  public void testGetInstanceString() throws IOException {
    assertNotNull(RulesXmlAccess.getInstance(getRule()));
  }


  
  @Test
  public void testCanParseRule() throws IOException {
    RulesAccess access =  RulesXmlAccess.getInstance(getRule());
    assertEquals(1, access.getRules().getRule().size());
  }
  
  private String getRule() throws IOException {
    
    URL url = this.getClass().getResource("/org/cogroo/tools/checker/rules/applier/sinlgetonRule.xml");
    return FileUtils.readFile(new File(url.getFile()), StandardCharsets.UTF_8);
  }

}
