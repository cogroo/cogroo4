package org.cogroo.tools.checker.rules.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cogroo.entities.Mistake;
import org.cogroo.entities.impl.MistakeImpl;
import org.junit.Test;
import static org.junit.Assert.*;
public class MistakeComparatorTest {

  @Test
  public void testSort() {
    int start = 0;
    int end = 10;
    
    List<Mistake> mistakes = new ArrayList<Mistake>();
    
    mistakes.add(createMistake("11", 1000, start, end));
    mistakes.add(createMistake("10", 1000, start, end));
    mistakes.add(createMistake("15", 1000, start, end));
    
    Collections.sort(mistakes, new MistakeComparator());
    
    assertEquals("10", mistakes.get(0).getRuleIdentifier());
    assertEquals("11", mistakes.get(1).getRuleIdentifier());
    assertEquals("15", mistakes.get(2).getRuleIdentifier());
  }
  
  @Test
  public void testSortXML() {
    int start = 0;
    int end = 10;
    
    List<Mistake> mistakes = new ArrayList<Mistake>();
    
    mistakes.add(createMistake("xml:11", 1000, start, end));
    mistakes.add(createMistake("xml:3", 1000, start, end));
    mistakes.add(createMistake("xml:100", 1000, start, end));
    
    Collections.sort(mistakes, new MistakeComparator());
    
    assertEquals("xml:3", mistakes.get(0).getRuleIdentifier());
    assertEquals("xml:11", mistakes.get(1).getRuleIdentifier());
    assertEquals("xml:100", mistakes.get(2).getRuleIdentifier());
  }
  
  @Test
  public void testPriority() {
    int start = 0;
    int end = 10;
    
    List<Mistake> mistakes = new ArrayList<Mistake>();
    
    mistakes.add(createMistake("11", 150, start, end));
    mistakes.add(createMistake("10", 50, start, end));
    mistakes.add(createMistake("15", 100, start, end));
    
    Collections.sort(mistakes, new MistakeComparator());
    
    assertEquals("11", mistakes.get(0).getRuleIdentifier());
    assertEquals("15", mistakes.get(1).getRuleIdentifier());
    assertEquals("10", mistakes.get(2).getRuleIdentifier());
  }
  
  private Mistake createMistake(String id, int priority, int start, int end) {
    String message = "";
    String shortMessage = "";
    return new MistakeImpl(id, priority, message, shortMessage, null, start, end, null, null);

  }

}
