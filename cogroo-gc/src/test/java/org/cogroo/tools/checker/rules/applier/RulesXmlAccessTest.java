package org.cogroo.tools.checker.rules.applier;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

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
    String text = CharStreams.toString(
        CharStreams.newReaderSupplier(Files.newInputStreamSupplier(new File(url.getFile())) , Charsets.UTF_8));
  
    return text;
  }

}
