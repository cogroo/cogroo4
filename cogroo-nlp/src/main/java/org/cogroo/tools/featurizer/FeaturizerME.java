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
package org.cogroo.tools.featurizer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.model.AbstractModel;
import opennlp.model.EventStream;
import opennlp.model.MaxentModel;
import opennlp.model.TrainUtil;
import opennlp.tools.postag.POSSample;
import opennlp.tools.util.BeamSearch;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.TrainingParameters;

/**
 * The class represents a maximum-entropy-based chunker. Such a chunker can be
 * used to find flat structures based on sequence inputs such as noun phrases or
 * named entities.
 */
public class FeaturizerME implements Featurizer {

  public static final int DEFAULT_BEAM_SIZE = 10;

  /**
   * The beam used to search for sequences of chunk tag assignments.
   */
  protected BeamSearch<WordTag> beam;

  private Sequence bestSequence;

  /**
   * The model used to assign chunk tags to a sequence of tokens.
   */
  protected MaxentModel model;

  /**
   * Initializes the current instance with the specified model and the specified
   * beam size.
   * 
   * @param model
   *          The model for this featurizer.
   * @param beamSize
   *          The size of the beam that should be used when decoding sequences.
   */
  public FeaturizerME(FeaturizerModel model, int beamSize) {
    FeaturizerFactory factory = model.getFactory();
    this.model = model.getFeaturizerModel();
    beam = new BeamSearch<WordTag>(beamSize,
        factory.getFeaturizerContextGenerator(), this.model,
        factory.getSequenceValidator(), 0);
  }

  /**
   * Initializes the current instance with the specified model. The default beam
   * size is used.
   * 
   * @param model
   */
  public FeaturizerME(FeaturizerModel model) {
    this(model, DEFAULT_BEAM_SIZE);
  }

  public String[] featurize(String[] toks, String[] tags) {
    bestSequence = beam.bestSequence(WordTag.create(toks, tags),
        new Object[] {});
    if(bestSequence != null) {
      List<String> c = bestSequence.getOutcomes();
      return c.toArray(new String[c.size()]);
    } else
      throw new RuntimeException("Could not find best sequence for " + new POSSample(toks, tags));
  }

  public Sequence[] topKSequences(String[] sentence, String[] tags) {
    return beam.bestSequences(DEFAULT_BEAM_SIZE,
        WordTag.create(sentence, tags), new Object[] {});
  }

  public Sequence[] topKSequences(String[] sentence, String[] tags,
      double minSequenceScore) {
    return beam.bestSequences(DEFAULT_BEAM_SIZE,
        WordTag.create(sentence, tags), null, minSequenceScore);
  }

  /**
   * Populates the specified array with the probabilities of the last decoded
   * sequence. The sequence was determined based on the previous call to
   * <code>chunk</code>. The specified array should be at least as large as the
   * numbe of tokens in the previous call to <code>chunk</code>.
   * 
   * @param probs
   *          An array used to hold the probabilities of the last decoded
   *          sequence.
   */
  public void probs(double[] probs) {
    bestSequence.getProbs(probs);
  }

  /**
   * Returns an array with the probabilities of the last decoded sequence. The
   * sequence was determined based on the previous call to <code>chunk</code>.
   * 
   * @return An array with the same number of probabilities as tokens were sent
   *         to <code>chunk</code> when it was last called.
   */
  public double[] probs() {
    return bestSequence.getProbs();
  }

  public static FeaturizerModel train(String lang,
      ObjectStream<FeatureSample> in,
      TrainingParameters mlParams,
      FeaturizerFactory factory) throws IOException {

    Map<String, String> manifestInfoEntries = new HashMap<String, String>();

    EventStream es = new FeaturizerEventStream(in, factory.getFeaturizerContextGenerator());

    AbstractModel maxentModel = TrainUtil.train(es, mlParams.getSettings(),
        manifestInfoEntries);

    return new FeaturizerModel(lang, maxentModel, manifestInfoEntries, factory);
  }
}
