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