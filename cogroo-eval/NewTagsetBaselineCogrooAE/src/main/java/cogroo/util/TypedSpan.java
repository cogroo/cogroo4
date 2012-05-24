package cogroo.util;

import opennlp.tools.util.Span;

public class TypedSpan extends Span {

  private String type;

  public TypedSpan(int s, int e, String type) {
    super(s, e);
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
