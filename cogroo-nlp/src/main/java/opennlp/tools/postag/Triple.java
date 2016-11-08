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
package opennlp.tools.postag;

import java.util.Objects;

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
      return Objects.equals(this.clazz, other.clazz) &&
          Objects.equals(this.lemma, other.lemma) &&
          Objects.equals(this.feats, other.feats);
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(clazz, lemma, feats);
  }

  public int compareTo(Triple o) {
    return this.lemma.compareTo(o.getLemma());
  }
}
