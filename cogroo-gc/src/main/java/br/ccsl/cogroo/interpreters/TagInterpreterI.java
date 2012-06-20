package br.ccsl.cogroo.interpreters;

import br.ccsl.cogroo.entities.impl.ChunkTag;
import br.ccsl.cogroo.entities.impl.MorphologicalTag;
import br.ccsl.cogroo.entities.impl.SyntacticTag;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Case;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.ChunkFunction;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Class;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Gender;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Mood;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Number;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Person;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Punctuation;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.SyntacticFunction;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Tense;

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
