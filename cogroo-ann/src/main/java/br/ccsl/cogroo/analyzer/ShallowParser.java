package br.ccsl.cogroo.analyzer;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.util.Span;

import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.SyntacticChunk;
import br.ccsl.cogroo.text.Token;
import br.ccsl.cogroo.text.impl.SyntacticChunkImpl;
import br.ccsl.cogroo.util.TextUtils;

public class ShallowParser implements AnalyzerI {
  private ChunkerME shallowParser;

  public ShallowParser(ChunkerME shallowParser) {
    this.shallowParser = shallowParser;
  }

  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();

    for (Sentence sentence : sentences) {
      Span[] parsers = null;
      List<Token> tokens = sentence.getTokens();
      List<SyntacticChunk> syntChunks = new ArrayList<SyntacticChunk>();

      String[] tags = new String[tokens.size()];

      for (int i = 0; i < tokens.size(); i++)
        tags[i] = tokens.get(i).getPOSTag() + "|" + tokens.get(i).getChunkTag();

      String[] tokensString = TextUtils.tokensToString(tokens);
      parsers = shallowParser.chunkAsSpans(tokensString, tags);

      for (Span span : parsers) {
        SyntacticChunk st = new SyntacticChunkImpl(span.getType(),
            span.getStart(), span.getEnd(), sentence);
        syntChunks.add(st);
      }

      sentence.setSyntacticChunks(syntChunks);

    }
  }

}
