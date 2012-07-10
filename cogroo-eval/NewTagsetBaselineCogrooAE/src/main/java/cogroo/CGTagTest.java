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
//package cogroo;
//
//import java.io.File;
//import java.util.SortedSet;
//import java.util.TreeSet;
//
//import br.usp.pcs.lta.cogroo.entity.Sentence;
//import br.usp.pcs.lta.cogroo.entity.Token;
//import br.usp.pcs.lta.cogroo.entity.impl.training.RandomAccessFileCorpusCortado;
//import br.usp.pcs.lta.cogroo.tag.LegacyTagInterpreter;
//import br.usp.pcs.lta.cogroo.tag.TagInterpreterI;
//import cogroo.uima.interpreters.FlorestaTagInterpreter;
//
//public class CGTagTest {
//  public static void main(String[] args) throws Exception {
//    TagInterpreterI ti = new LegacyTagInterpreter();
//    TagInterpreterI floresta = new FlorestaTagInterpreter();
//    RandomAccessFileCorpusCortado cr = new RandomAccessFileCorpusCortado(
//        new File("/Users/wcolen/Documents/wrks/corpus/corpus_cut"), false, ti);
//    System.out.println("Sentence number: " + cr.getCurrentPosition());
//    SortedSet<String> tags = new TreeSet<String>();
//    while(cr.hasNext()) {
//      Sentence s = cr.getSentence();
//      for (Token t : s.getTokens()) {
//        String str = floresta.serialize(t.getMorphologicalTag());
//        if(str != null) {
//          tags.add(str);
//        } else {
//          System.out.println("Nao leu: " + t.getMorphologicalTag());
//        }
//        
//      }
//    }
//
//    for (String string : tags) {
//      System.out.println(string);
//    }
//  }
//}
