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
package org.cogroo.tools.postag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cogroo.dictionary.FeatureDictionaryI;
import org.cogroo.dictionary.impl.FSADictionary;
import org.cogroo.dictionary.impl.FSAFeatureDictionary;
import org.cogroo.util.PairWordPOSTag;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.postag.DefaultPOSContextGenerator;

public class PortuguesePOSContextGenerator extends DefaultPOSContextGenerator {

  private FSADictionary trans;
  private FeatureDictionaryI feat;

  public PortuguesePOSContextGenerator(Dictionary dict) {
    this(0, dict);
  }

  public PortuguesePOSContextGenerator(int cacheSize, Dictionary dict) {
    super(cacheSize, dict);
    
    // load transitivity dic
    try {
      this.trans = FSADictionary.createFromResources("/fsa_dictionaries/pos/pt_br_trans.dict");
      this.feat = FSAFeatureDictionary.createFromResources("/fsa_dictionaries/featurizer/pt_br_feats.dict");
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }

  public String[] getContext(final int index, String[] sequence,
      String[] priorDecisions, Object[] additionalContext) {
    String[] context = super.getContext(index, sequence, priorDecisions,
        additionalContext);
    
    List<String> modContext = new ArrayList<String>(Arrays.asList(context));
    
    if (additionalContext != null && additionalContext.length > 0) {
      String[][] ac = (String[][]) additionalContext;
        
        for (int i = 0; i < ac.length; i++) {
          if (ac[i][index] != null) {
            modContext.add("ac_" + i + "=" + ac[i][index]);
          }
        
      }
    }
    
    
    // Check transitivity
    if(index > 0)
      addTransitivity("prev_", sequence[index-1], modContext);
    
    addTransitivity("", sequence[index], modContext);
    
    if(index < sequence.length - 1)
      addTransitivity("nxt_", sequence[index+1], modContext);
    
    // specific features:
    // 'a'
    if("a".equals(sequence[index].toLowerCase())) {
      
      if(index > 0 && isVerbTag(priorDecisions[index-1])) {
        modContext.add("spec_a=v+a");
        addVerbTransitivityAndLemma("spec_a", sequence[index-1], modContext);
      } else if(index > 1 && isVerbTag(priorDecisions[index-2])) {
        addVerbTransitivityAndLemma2("spec_pa", sequence[index-2], priorDecisions[index-1], modContext);
      }
      
      if(index < sequence.length - 1) {
        String next = sequence[index + 1];
        if(next.length() > 2) {
          if(matchFeature(next, "n", "F")) {
            modContext.add("spec_af");
            if(index > 0) modContext.add("spec_af|" + priorDecisions[index-1]);
          } else if(matchFeature(next, "n", "M")) {
            modContext.add("spec_am");
            if(index > 0) modContext.add("spec_am|" + priorDecisions[index-1]);  
          }
        }
      }
    }
    
    // 'que'
    if("que".equals(sequence[index].toLowerCase())) {
      if(index < sequence.length - 1) { // at least one before and one after
        boolean nextIsVerb = false;
        if(isCanBeAVerb(sequence[index+1])) {
          modContext.add("spec_que=nv");
          nextIsVerb = true;
        }
        if(index > 0) {
          modContext.add("spec_que_prev=" + priorDecisions[index-1]);
          if(nextIsVerb) {
            modContext.add("spec_que_prev_nv=" + priorDecisions[index-1]);
          }
        }
        if(index > 1) {
          modContext.add("spec_que_pprev=" + priorDecisions[index-2] + "|" + priorDecisions[index-1]);
          if(nextIsVerb)
            modContext.add("spec_que_pprev_nv=" + priorDecisions[index-2] + "|" + priorDecisions[index-1]);
        }
      }
    }
    

    context = modContext.toArray(new String[modContext.size()]);
    
    return context;
  }

  private boolean matchFeature(String next, String tag, String feature) {
    String[] features = feat.getFeatures(next, null);
    if(features == null)
      features = feat.getFeatures(next.toLowerCase(), null);
    if(features != null) {
      for (String f : features) {
        if(f.contains(feature)) {
          return true;
        }
      } 
    }
    
    return false;
  }

  private boolean isCanBeAVerb(String candidate) {
    return trans.getTags(candidate) != null;
  }

  private void addVerbTransitivityAndLemma2(String prefix, String ppVerb, String prevTag,
      List<String> modContext) {
    
    List<PairWordPOSTag> tagsAndLemmas = trans.getTagsAndLemms(ppVerb);
    if(tagsAndLemmas != null && tagsAndLemmas.size() > 0) {
      for (PairWordPOSTag pairWordPOSTag : tagsAndLemmas) {
        modContext.add(prefix + "_lm=" + prevTag + "|" + pairWordPOSTag.getWord()); // adds the lemma
        modContext.add(prefix + "_tr=" + prevTag + "|" + pairWordPOSTag.getPosTag()); // adds the transitivity
        modContext.add(prefix + "_lmtr=" + prevTag + "|" + pairWordPOSTag.getWord() + "|" + pairWordPOSTag.getPosTag()); // adds the transitivity
      }
    }
    
  }

  private void addVerbTransitivityAndLemma(String prefix, String verb,
      List<String> modContext) {
    List<PairWordPOSTag> tagsAndLemmas = trans.getTagsAndLemms(verb);
    if(tagsAndLemmas != null && tagsAndLemmas.size() > 0) {
      for (PairWordPOSTag pairWordPOSTag : tagsAndLemmas) {
        modContext.add(prefix + "_lm=" + pairWordPOSTag.getWord()); // adds the lemma
        modContext.add(prefix + "_tr=" + pairWordPOSTag.getPosTag()); // adds the transitivity
        modContext.add(prefix + "_lmtr=" + pairWordPOSTag.getWord() + "|" + pairWordPOSTag.getPosTag()); // adds the transitivity
      }
    }
  }

  private boolean isVerbTag(String string) {
    return string.startsWith("v-");
  }

  private void addTransitivity(String prefix, String tok,
      List<String> modContext) {
    if(trans != null) {
      String[] tags = trans.getTags(tok);
      if(tags != null) {
        if(tags.length > 1) {
          Set<String> trans = new HashSet<String>(Arrays.asList(tags));
          for (String t : trans) {
            modContext.add(prefix + "trans=" + t);
          }
        } else {
          modContext.add(prefix + "trans=" + tags[0]);
        }
      }
    }
  }
}
