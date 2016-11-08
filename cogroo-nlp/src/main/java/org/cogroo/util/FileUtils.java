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

package org.cogroo.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {

  public static final String readFile(String fileName, Charset charset) throws IOException {
    return new String(Files.readAllBytes(Paths.get(fileName)), charset);
  }

  public static final String readFile(File file, Charset charset) throws IOException {
    return new String(Files.readAllBytes(file.toPath()), charset);
  }
  
  public static final void copy(InputStream initialStream, OutputStream outStream) throws IOException {
      byte[] buffer = new byte[8 * 1024];
      int bytesRead;
      while ((bytesRead = initialStream.read(buffer)) != -1) {
          outStream.write(buffer, 0, bytesRead);
      }
  }
}
