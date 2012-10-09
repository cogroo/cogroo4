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
package org.cogroo.formats.ad;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.chunker.ChunkSample;
import opennlp.tools.formats.ad.ADChunkSampleStream;
import opennlp.tools.formats.ad.ADSentenceStream;
import opennlp.tools.formats.ad.ADSentenceStream.Sentence;
import opennlp.tools.formats.ad.ADSentenceStream.SentenceParser.Leaf;
import opennlp.tools.formats.ad.ADSentenceStream.SentenceParser.Node;
import opennlp.tools.formats.ad.ADSentenceStream.SentenceParser.TreeElement;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

/**
 * Parser for Floresta Sita(c)tica Arvores Deitadas corpus, output to for the
 * Portuguese Chunker training.
 * <p>
 * The heuristic to extract chunks where based o paper 'A Machine Learning
 * Approach to Portuguese Clause Identification', (Eraldo Fernandes, Cicero
 * Santos and Ruy Milidiú).<br>
 * <p>
 * Data can be found on this web site:<br>
 * http://www.linguateca.pt/floresta/corpus.html
 * <p>
 * Information about the format:<br>
 * Susana Afonso.
 * "Árvores deitadas: Descrição do formato e das opções de análise na Floresta Sintáctica"
 * .<br>
 * 12 de Fevereiro de 2006.
 * http://www.linguateca.pt/documentos/Afonso2006ArvoresDeitadas.pdf
 * <p>
 * Detailed info about the NER tagset:
 * http://beta.visl.sdu.dk/visl/pt/info/portsymbol.html#semtags_names
 * <p>
 * <b>Note:</b> Do not use this class, internal use only!
 */
public class ADChunkBasedHeadFinderSampleStream extends ADChunkSampleStream {

  public ADChunkBasedHeadFinderSampleStream(InputStream in, String charsetName) {
    super(in, charsetName);
  }

  public ADChunkBasedHeadFinderSampleStream(ObjectStream<String> lineStream) {
    super(lineStream);
  }
  
  protected void processLeaf(Leaf leaf, boolean isIntermediate, String phraseTag,
      List<String> sentence, List<String> tags, List<String> target) {
    super.processLeaf(leaf, isIntermediate, phraseTag, sentence, tags, target);
    
    int i = target.size() - 1;
    // change the tags
    tags.set(i, tags.get(i) + "|" + target.get(i));
    
    if (/*!isInherited &&*/ ("H".equals(leaf.getSyntacticTag()) || "MV".equals(leaf.getSyntacticTag())) 
        && !OTHER.equals(phraseTag)) {
      target.set(i, "B-H");
    } else {
      target.set(i, OTHER);
    }
  }
  
  protected boolean isIntermediate(List<String> tags, List<String> target, String phraseTag) {
    return tags.size() > 0 && tags.get(tags.size() - 1).endsWith("-" + phraseTag);
  }
}
