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
package cogroo.uima.readers.probi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import opennlp.tools.util.ObjectStream;

public class ProbiParser implements ObjectStream<ProbiEntry> {

  private BufferedReader probiReader;

  public ProbiParser(File file, String encoding) throws FileNotFoundException,
      UnsupportedEncodingException {
    InputStreamReader inputStreamReader = new InputStreamReader(
        new FileInputStream(file), encoding);
    probiReader = new BufferedReader(inputStreamReader);

  }

  public List<ProbiEntry> getEntries() throws Exception {

    String line = probiReader.readLine();

    List<ProbiEntry> entries = new ArrayList<ProbiEntry>(11600);
    while (line != null) {

      ProbiEntry entry = ProbiEntry.fromString(line);
      entries.add(entry);

      line = probiReader.readLine();
    }

    return Collections.unmodifiableList(entries);
  }

  public ProbiEntry read() throws IOException {
    String line = probiReader.readLine();
    if (line == null) {
      return null;
    }
    return ProbiEntry.fromString(line);
  }

  public void reset() throws IOException, UnsupportedOperationException {
    probiReader.reset();
  }

  public void close() throws IOException {
    probiReader.close();
  }

}
