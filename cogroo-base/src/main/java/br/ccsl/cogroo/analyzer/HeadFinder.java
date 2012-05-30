package br.ccsl.cogroo.analyzer;

import java.util.List;

import opennlp.tools.chunker.ChunkerME;

import br.ccsl.cogroo.text.Chunk;
import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.Token;
import br.ccsl.cogroo.util.TextUtils;

public class HeadFinder implements AnalyzerI {
  private ChunkerME headFinder;

  public HeadFinder(ChunkerME headFinder) {
    this.headFinder = headFinder;
  }
  
  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();
    
    for (Sentence sentence : sentences) {
      String[] heads = new String[sentence.getTokens().size()];
      List<Token> tokens = sentence.getTokens();
      List<Chunk> chunks = sentence.getChunks();

      String[] tags = new String[tokens.size()];

      for (int i = 0; i < tokens.size(); i++)
        tags[i] = tokens.get(i).getPOSTag() + "|" + tokens.get(i).getChunkTag();
      
      String[] tokensString = TextUtils.tokensToString(tokens);
      heads = headFinder.chunk(tokensString, tags);
      
      for (Chunk chunk : chunks)
        for (int i = chunk.getSpan().getStart(); i < chunk.getSpan().getEnd(); i++)
          if (heads[i].equals("B-H"))
            chunk.setHeadIndex(i);
    }
  }

}
