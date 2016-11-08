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
package org.cogroo.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.entities.impl.SyntacticTag;
import org.cogroo.tools.checker.rules.model.TagMask.Class;
import org.cogroo.tools.checker.rules.model.TagMask.Gender;
import org.cogroo.tools.checker.rules.model.TagMask.Number;
import org.cogroo.tools.checker.rules.model.TagMask.SyntacticFunction;
import org.cogroo.util.ToStringHelper;

/**
 * Initially a subject or verb group of Chunks
 */
public class SyntacticChunk implements Serializable {

  private static final long serialVersionUID = 4768788694700581906L;

  protected List<Chunk> chunks;

  protected SyntacticTag syntacticTag;

  private static final SyntacticTag SUBJ;
  private static final SyntacticTag MV;
  private static final SyntacticTag NONE;
  private static final SyntacticTag SUBJECT_PREDICATIVE;
  
  static {
    SUBJ = new SyntacticTag();
    SUBJ.setSyntacticFunction(SyntacticFunction.SUBJECT);

    MV = new SyntacticTag();
    MV.setSyntacticFunction(SyntacticFunction.VERB);

    NONE = new SyntacticTag();
    NONE.setSyntacticFunction(SyntacticFunction.NONE);

    SUBJECT_PREDICATIVE = new SyntacticTag();
    SUBJECT_PREDICATIVE.setSyntacticFunction(SyntacticFunction.SUBJECT_PREDICATIVE);
  }

  public SyntacticChunk(List<Chunk> childChunks) {
    this.chunks = childChunks;
  }

  public String toPlainText() {
    StringBuilder chunkAsString = new StringBuilder();
    for (int i = 0; i < this.chunks.size(); i++) {
      chunkAsString.append(this.chunks.get(i).toPlainText());
      if (i + 1 != this.chunks.size()) {
        chunkAsString.append(' ');
      }
    }
    return chunkAsString.toString();
  }

  private MorphologicalTag tag = null;

  public MorphologicalTag getMorphologicalTag() {
    if(tag == null) {
        // here we try to guess a mtag for the syntactic chunk.
        if(syntacticTag.match(NONE)) {
            tag = getChildChunks().get(0).getMainToken().morphologicalTag;
        } else if(syntacticTag.match(MV)) {
            for (Chunk verbChunk : getChildChunks()) {
                if(verbChunk.getMainToken() != null) {
                    tag = verbChunk.getMainToken().getMorphologicalTag();
                    break;
                }
            }
        } else if(syntacticTag.match(SUBJ) || syntacticTag.match(SUBJECT_PREDICATIVE)) {
            boolean hasMale = false;
            boolean hasFemale = false;
            boolean hasSingular = false;
            boolean hasPlural = false;
            
            Gender lastGender = null;
            
            boolean multipleNP = false;
            
            // for now we can not handle complex cases... so lets handle simple ones at least
            boolean canHandle = false;
            List<Chunk> childChunks = filterPP(getChildChunks());
            
            if(childChunks != null && 
                !containsLemma(childChunks, "cujo", "que", "como") && 
                !containsClass(childChunks, Class.NUMERAL) &&
                !endsWithPunct(childChunks)) {
              
              // 1) we can handle trivial cases, with one NP (but there are exceptions that cause false positives!!)
              if(childChunks.size() == 1 && "NP".equals(childChunks.get(0).getType())) {
                canHandle = true;
              }
              
              // 2) we can handle (NP, NP)* "e" NP (a menina e a bola)
              else if(childChunks.size() > 1) {
                boolean gotE = false;
                // now we check 
                for (int i = 0; i < childChunks.size(); i = i+2) {
                  if(i == childChunks.size() - 1 && checkType(childChunks.get(i), "NP")) {
                    if(gotE) {
                      canHandle = true;
                      multipleNP = true;
                    }
                  } else if(checkType(childChunks.get(i), "NP") && checkType(childChunks.get(i+1), null) 
                      && checkLexeme(childChunks.get(i+1), ",", "e")) {
                    if(checkLexeme(childChunks.get(i+1), "e")) {
                      gotE = true;
                    }
                    // keep going... until last one that should be a NP
                  } else {
                    break;
                  }
                  
                }
              }
              
            }
            
            if(canHandle) {
              for (Chunk subjChunk : childChunks) {
              if(tag == null && subjChunk.getMainToken() != null) {
                  tag = subjChunk.getMainToken().getMorphologicalTag();
              }
              
              if(subjChunk.getMainToken() != null) {
                  MorphologicalTag mt = subjChunk.getMainToken().getMorphologicalTag();
                  if((!hasFemale || !hasMale) && mt.getGenderE() != null && mt.getGenderE().equals(Gender.NEUTRAL)) {
                      hasFemale = true; hasMale = true;
                      lastGender = Gender.NEUTRAL;
                  } else if(!hasFemale && mt.getGenderE() != null && mt.getGenderE().equals(Gender.FEMALE)) {
                      hasFemale = true;
                      lastGender = Gender.FEMALE;
                  } else if(!hasMale && mt.getGenderE() != null && mt.getGenderE().equals(Gender.MALE)) {
                      hasMale = true;
                      lastGender = Gender.MALE;
                  }
  
                  if((!hasSingular || !hasPlural) && mt.getNumberE() != null && mt.getNumberE().equals(Number.NEUTRAL)) {
                      hasSingular = true; hasPlural = true;
                  } else if(!hasSingular && mt.getNumberE() != null && mt.getNumberE().equals(Number.SINGULAR)) {
                      hasSingular = true;
                  } else if(!hasPlural && mt.getNumberE() != null && mt.getNumberE().equals(Number.PLURAL)) {
                      hasPlural = true;
                  }
              }
          }
              
            } else {
              tag = createNeutralNoun();
            }
              

            tag = tag.clone();
            if(hasFemale && hasMale) {
                if(Objects.equals(lastGender, Gender.MALE)) {
                  tag.setGender(Gender.MALE);
                } else {
                  tag.setGender(Gender.NEUTRAL); // could be male, but sometimes one can opt to agree with latter only
                }
            } else if(hasFemale) {
                tag.setGender(Gender.FEMALE);
            } else if(hasMale) {
                tag.setGender(Gender.MALE);
            }
            
            if(syntacticTag.match(SUBJ) && multipleNP) {
              tag.setNumber(Number.PLURAL);
            } else if(syntacticTag.match(SUBJECT_PREDICATIVE) && childChunks.size() > 1) {
              tag.setNumber(Number.NEUTRAL);
            } else {
              if(hasSingular && hasPlural) {
                tag.setNumber(Number.NEUTRAL);
              } else if(hasSingular) {
                  tag.setNumber(Number.SINGULAR);
              } else if(hasPlural) {
                  tag.setNumber(Number.PLURAL);
              }              
            }

        } else {
          if(getChildChunks().size() == 1) {
            tag = getChildChunks().get(0).getMainToken().getMorphologicalTag();
          } else {
            tag = createNeutralNoun();
          }
        }
    }
    
    if(tag.getNumberE() == null) tag.setNumber(Number.NEUTRAL);
    if(tag.getGenderE() == null) tag.setGender(Gender.NEUTRAL);
    return tag;
}
  
  private boolean endsWithPunct(List<Chunk> childChunks) {
    if(childChunks.size() > 0) {
      Chunk lastChunk = childChunks.get(childChunks.size() - 1);
      List<Token> tokens = lastChunk.getTokens();
      if(tokens.size() > 0) {
        return Class.PUNCTUATION_MARK.equals(tokens.get(tokens.size() - 1).getMorphologicalTag().getClazzE());
      }
    }
    return false;
  }

  private List<Chunk> filterPP(List<Chunk> childChunks) {
    List<Chunk> out = new ArrayList<Chunk>();
    // if we find a PP, we skip it and the following NP
    // it it fails, we simply skip the chunks...
    if(childChunks.size() == 1) return childChunks;
    
    for (int i = 0; i < childChunks.size(); i++) {
      Chunk c = childChunks.get(i);
      
      if(i < childChunks.size() - 1 && Objects.equals(c.getType(), "PP") && Objects.equals(childChunks.get(i+1).getType(), "NP")) {
        i++;
      } else {
        out.add(c);
      }
      
    }
    return out;
  }

  private boolean containsClass(List<Chunk> childChunks, Class... classes) {
    boolean match = false;
    for (Chunk chunk : childChunks) {
      for (Token t : chunk.getTokens()) {
        for (Class c : classes) {
          if (t.getMorphologicalTag() != null
              && c.equals(t.getMorphologicalTag().getClazzE())) {
            match = true;
            break;
          }
        }
      }
    }
    return match;
  }

  private boolean containsLemma(List<Chunk> childChunks, String ... arr) {
    boolean match = false;
    for (Chunk chunk : childChunks) {
      for (Token t : chunk.getTokens()) {
        for (String lexeme : arr) {
          for (String p : t.getPrimitive()) {
            if(lexeme.equalsIgnoreCase(p)) {
              match = true;
              break;
            } 
          }
            
        }
      }
    }
    return match;
  }

  private MorphologicalTag createNeutralNoun() {
    MorphologicalTag t = new MorphologicalTag();
    t.setClazz(Class.NOUN);
    t.setGender(Gender.NEUTRAL);
    t.setNumber(Number.NEUTRAL);
    return t;
  }

  private boolean checkLexeme(Chunk chunk, String ... arr) {
    boolean match = false;
    if(chunk.getTokens().size() == 1) {
      for (String lexeme : arr) {
        if(Objects.equals(chunk.getTokens().get(0).getLexeme(), lexeme)) {
          match = true;
          break;
        }
      }
    }
    return match;
  }

  private boolean checkType(Chunk chunk, String string) {
    return Objects.equals(chunk.getType(), string);
  }

  /**
   * @return the morphologicalTag
   */
  public MorphologicalTag getMorphologicalTag2xx() {
    if (tag == null) {
      // here we try to guess a mtag for the syntactic chunk.
      if (syntacticTag.match(NONE)) {
        tag = getChildChunks().get(0).getMainToken().morphologicalTag;
      } else if (syntacticTag.match(MV)) {
        for (Chunk verbChunk : getChildChunks()) {
          if (verbChunk.getMainToken() != null) {
            tag = verbChunk.getMainToken().getMorphologicalTag();
            break;
          }
        }
      } else if (syntacticTag.match(SUBJ)) {
        
        Gender gender = null;
        Number number = null;
        
        List<Chunk> childChunks = filterNP(getChildChunks());
        
        MorphologicalTag mtag = childChunks.get(0).getMorphologicalTag().clone();
        gender = mtag.getGenderE();
        number = mtag.getNumberE();
        
        for (int i = 1; i < childChunks.size(); i++) {
          Chunk cc = childChunks.get(i);
          if(cc.getMorphologicalTag().getClazzE().equals(Class.PREPOSITION)) {
            break;
          }
          number = Number.PLURAL;
          Gender otherGender = cc.getMorphologicalTag().getGenderE();
          gender = getStronger(gender, otherGender);
        }
        
        mtag.setGender(gender);
        mtag.setNumber(number);
        
        tag = mtag;
      }
    }
        
    return tag;
  }

  private List<Chunk> filterNP(List<Chunk> childChunks) {
    List<Chunk> filtered = new ArrayList<Chunk>();
    for (Chunk c : childChunks) {
      if(c.getMorphologicalTag().getClazzE().equals(Class.PREPOSITION)) {
        break;
      }
      if(Objects.equals(c.getType(), "NP")) {
        filtered.add(c);
      }
    }
    if(filtered.size() > 0) {
      return filtered;
    }
    
    return childChunks;
  }

  private Number getStronger(Number a, Number b) {
    if(Number.PLURAL.equals(a) || Number.PLURAL.equals(b)) return Number.PLURAL;
    
    if(Number.NEUTRAL.equals(a)) return b;
    if(Number.NEUTRAL.equals(b)) return a;
    
    if(a == null) return b;
    if(b == null) return a;
    
    return a;
  }

  private Gender getStronger(Gender a, Gender b) {
    if(Gender.MALE.equals(a) || Gender.MALE.equals(b)) return Gender.MALE;
    
    if(Gender.NEUTRAL.equals(a)) return b;
    if(Gender.NEUTRAL.equals(b)) return a;
    
    if(a == null) return b;
    if(b == null) return a;
        
    return a;
  }

  public SyntacticTag getSyntacticTag() {
    return this.syntacticTag;
  }

  public void setSyntacticTag(SyntacticTag syntacticTag) {
    this.syntacticTag = syntacticTag;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SyntacticChunk) {
      SyntacticChunk that = (SyntacticChunk) obj;
      return /*
              * Objects.equals(this.tokens, that.tokens) &&
              * Objects.equals(this.firstToken, that.firstToken) &&
              */Objects.equals(this.getChildChunks(), that.getChildChunks())
          && Objects.equals(this.syntacticTag, that.syntacticTag);
    }
    return false;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.getChildChunks(), this.syntacticTag);
  }

  public List<Chunk> getChildChunks() {
    return this.chunks;
  }

  @Override
  public String toString() {
    
    

    return ToStringHelper.toStringHelper(this).add("tag", syntacticTag)
        .add("mtag", this.getMorphologicalTag())
        .add("toks", toPlainText()).toString();
  }

  public int getFirstToken() {
    return chunks.get(0).getFirstToken();
  }

  private List<Token> tokens = null;

  public List<Token> getTokens() {

    if (tokens == null) {
      List<Token> tks = new ArrayList<Token>();
      for (Chunk c : chunks) {
        tks.addAll(c.getTokens());
      }
      tokens = Collections.unmodifiableList(tks);
    }

    return tokens;
  }

}
