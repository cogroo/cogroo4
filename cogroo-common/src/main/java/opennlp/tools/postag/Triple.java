package opennlp.tools.postag;

import com.google.common.base.Objects;


public class Triple implements Comparable<Triple>{
  private final String clazz;
  private final String lemma;
  private final String feats;

  public Triple(String clazz, String lemma, String feats) {
    this.clazz = clazz;
    this.lemma = lemma;
    this.feats = feats;
  }

  public String getClazz() {
    return clazz;
  }

  public String getLemma() {
    return lemma;
  }

  public String getFeats() {
    return feats;
  }

  @Override
  public String toString() {
    return lemma + ": " + clazz + " " + feats;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof Triple) {
      Triple other = (Triple) o;
      return Objects.equal(this.clazz, other.clazz) &&
          Objects.equal(this.lemma, other.lemma) &&
          Objects.equal(this.feats, other.feats);
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    return Objects.hashCode(clazz, lemma, feats);
  }

  public int compareTo(Triple o) {
    return this.lemma.compareTo(o.getLemma());
  }
}
