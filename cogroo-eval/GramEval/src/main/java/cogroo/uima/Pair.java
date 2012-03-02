package cogroo.uima;

public final class Pair<A, B> {
  public final A a;
  public final B b;

  public Pair(A a, B b) {
    this.a = a;
    this.b = b;
  }

  public String toString() {
    return "[" + a + "/" + b + "]";
  }
}