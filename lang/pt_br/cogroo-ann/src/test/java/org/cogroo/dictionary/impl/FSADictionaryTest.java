package org.cogroo.dictionary.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class FSADictionaryTest {

  @Test
  public void testLemmatizerDict() throws IOException {
    String path = "/fsa_dictionaries/pos/pt_br_jspell";
    InputStream dict = FSADictionary.class.getResourceAsStream(path + ".dict");
    InputStream info = FSADictionary.class.getResourceAsStream(path + ".info");
    
    FSADictionary td = FSADictionary.create(dict, info);
    
    assertEquals("menino", td.getLemmas("meninos", "n")[0]);
    
  }
}
