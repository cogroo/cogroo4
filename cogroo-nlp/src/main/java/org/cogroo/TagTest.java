///**
// * Copyright (C) 2012 cogroo <cogroo@cogroo.org>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *         http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.cogroo;
//
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.SortedSet;
//import java.util.TreeSet;
//
//import org.cogroo.formats.ad.ADExPOSSampleStream;
//
//import opennlp.tools.postag.POSSample;
//import opennlp.tools.util.ObjectStream;
//import opennlp.tools.util.PlainTextByLineStream;
//
//public class TagTest {
//
//  public static void main(String[] args) throws Exception {
//
//    InputStream in = new FileInputStream(
//        "/Users/wcolen/Documents/wrks/corpus/Bosque/Bosque_CF_8.0.ad.txt");
//        //"/Users/wcolen/Documents/wrks/corpus/FlorestaVirgem/FlorestaVirgem_CF_3.0_ad.txt");
//
//    String encoding = "ISO-8859-1";
//
//    ObjectStream<POSSample> sampleStream = new ADExPOSSampleStream(
//        new PlainTextByLineStream(new InputStreamReader(in, encoding)), false,
//        true, false);
//
//    SortedSet<String> tags = new TreeSet<String>();
//    POSSample sample = sampleStream.read();
//    while(sample != null) {
//      for (String tag : sample.getTags()) {
//        tags.add(tag);
//      }
//      sample = sampleStream.read();
//    }
//
//    for (String string : tags) {
//      System.out.println(string);
//    }
//  }
//
//}
