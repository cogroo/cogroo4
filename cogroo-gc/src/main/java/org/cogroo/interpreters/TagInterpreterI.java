package org.cogroo.interpreters;

import org.cogroo.entities.impl.ChunkTag;
import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.entities.impl.SyntacticTag;

import org.cogroo.tools.checker.rules.model.TagMask.Case;
import org.cogroo.tools.checker.rules.model.TagMask.ChunkFunction;
import org.cogroo.tools.checker.rules.model.TagMask.Class;
import org.cogroo.tools.checker.rules.model.TagMask.Gender;
import org.cogroo.tools.checker.rules.model.TagMask.Mood;
import org.cogroo.tools.checker.rules.model.TagMask.Number;
import org.cogroo.tools.checker.rules.model.TagMask.Person;
import org.cogroo.tools.checker.rules.model.TagMask.Punctuation;
import org.cogroo.tools.checker.rules.model.TagMask.SyntacticFunction;
import org.cogroo.tools.checker.rules.model.TagMask.Tense;

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
