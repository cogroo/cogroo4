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

import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.TokenTag;
import opennlp.tools.util.TrainingParameters;



/**
 * The class represents a maximum-entropy-based chunker. Such a chunker can be
 * used to find flat structures based on sequence inputs such as noun phrases or
 * named entities.
 */
public class FeaturizerME implements Featurizer {

  public static final int DEFAULT_BEAM_SIZE = 10;

  private final FeaturizerContextGenerator contextGenerator;
  private final SequenceValidator<TokenTag> sequenceValidator;

  private Sequence bestSequence;

  /**
   * The model used to assign chunk tags to a sequence of tokens.
   */
  protected SequenceClassificationModel<TokenTag> model;

  /**
   * Initializes the current instance with the specified model. The default beam
   * size is used.
   * 
   * @param model
   */
  public FeaturizerME(FeaturizerModel model) {
    FeaturizerFactory factory = model.getFactory();
    this.model = model.getChunkerSequenceModel();
    this.contextGenerator = model.getFactory().getFeaturizerContextGenerator();
    this.sequenceValidator = model.getFactory().getSequenceValidator();
  }

  public String[] featurize(String[] toks, String[] tags) {
    bestSequence = model.bestSequence(TokenTag.create(toks,tags), null, contextGenerator, sequenceValidator);
    List<String> c = bestSequence.getOutcomes();
    return c.toArray(new String[c.size()]);
  }

  public Sequence[] topKSequences(String[] sentence, String[] tags) {
    return model.bestSequences(DEFAULT_BEAM_SIZE, TokenTag.create(sentence, tags), new Object[] { },
        contextGenerator, sequenceValidator);
  }

  public Sequence[] topKSequences(String[] sentence, String[] tags,
      double minSequenceScore) {
    return model.bestSequences(DEFAULT_BEAM_SIZE, TokenTag.create(sentence, tags), new Object[] { }, minSequenceScore,
        contextGenerator, sequenceValidator);
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

    ObjectStream<Event> es = new FeaturizerEventStream(in, factory.getFeaturizerContextGenerator());

    EventTrainer trainer = TrainerFactory.getEventTrainer(
            mlParams, manifestInfoEntries);

    MaxentModel maxentModel = trainer.train(es);

    return new FeaturizerModel(lang, maxentModel, manifestInfoEntries, factory);
  }
}
