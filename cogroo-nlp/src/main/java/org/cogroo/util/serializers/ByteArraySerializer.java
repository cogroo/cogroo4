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
package org.cogroo.util.serializers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.model.ArtifactSerializer;
import opennlp.tools.util.model.ModelUtil;

public class ByteArraySerializer implements ArtifactSerializer<byte[]> {

  public byte[] create(InputStream in) throws IOException,
      InvalidFormatException {

    return ModelUtil.read(in);
  }

  public void serialize(byte[] artifact, OutputStream out) throws IOException {
    out.write(artifact);
  }
}