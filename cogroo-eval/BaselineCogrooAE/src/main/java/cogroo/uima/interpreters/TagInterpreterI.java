package cogroo.uima.interpreters;

import br.usp.pcs.lta.cogroo.entity.impl.runtime.ChunkTag;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.MorphologicalTag;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.SyntacticTag;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Case;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.ChunkFunction;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Class;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Gender;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Mood;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Number;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Person;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Punctuation;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.SyntacticFunction;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Tense;

public interface TagInterpreterI {

  MorphologicalTag parseMorphologicalTag(String tagString);

  ChunkTag parseChunkTag(String tagString);

  SyntacticTag parseSyntacticTag(String tagString);

  String serialize(MorphologicalTag tag);

  String serialize(ChunkTag tag);

  String serialize(SyntacticTag tag);

  String serialize(SyntacticFunction tag);

  String serialize(ChunkFunction tag);

  String serialize(Class tag);

  String serialize(Gender tag);

  String serialize(Number tag);

  String serialize(Case tag);

  String serialize(Person tag);

  String serialize(Tense tag);

  String serialize(Mood tag);

  String serialize(Punctuation tag);

}
