package br.ccsl.cogroo.analyzer;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.chunker.ChunkSample;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.util.Span;
import br.ccsl.cogroo.text.Chunk;
import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.Token;
import br.ccsl.cogroo.text.impl.ChunkImpl;
import br.ccsl.cogroo.util.TextUtils;

public class Chunker implements AnalyzerI {
  private ChunkerME chunker;

  public Chunker(ChunkerME chunker) {
    this.chunker = chunker;
  }

  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();

    for (Sentence sentence : sentences) {
      List<Token> tokens = sentence.getTokens();
      
      String[] tags = new String[tokens.size()];

      for (int i = 0; i < tokens.size(); i++)
        tags[i] = tokens.get(i).getPOSTag();
      
      String[] tokensString = TextUtils.tokensToString(tokens);
      List<Chunk> chunks = new ArrayList<Chunk>();
      String[] chunkTags = chunker.chunk(tokensString, tags);
    
      
      for (int i = 0; i < chunkTags.length; i++) {
        tokens.get(i).setChunkTag(chunkTags[i]);
      }
      
      Span[] chunksSpans = ChunkSample.phrasesAsSpanList(tokensString, tags, chunkTags);
      
      for (Span span : chunksSpans) {
        Chunk chunk = new ChunkImpl(span.getType(), span.getStart(), span.getEnd(), sentence);
        chunks.add(chunk);
      }
      sentence.setChunks(chunks);
      
    }
  }
}
