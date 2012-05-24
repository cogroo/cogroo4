package cogroo.util;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.Span;
import br.usp.pcs.lta.cogroo.entity.Token;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.TokenCogroo;

public class EntityUtils {

  public static List<Token> groupTokensChar(String text, List<Token> toks, List<Span> charSpans, String additionalContext) {
    if (charSpans == null || charSpans.size() == 0) {
      return toks;
    }
    
    int lastVisitedTok = 0;
    List<Span> spans = new ArrayList<Span>(charSpans.size());
    
    for (Span ch : charSpans) {
//      System.out.println("looking for: " + ch.getCoveredText(text));
      Token aToken = toks.get(lastVisitedTok);
      while (aToken.getSpan().getStart() < ch.getStart()) {
        lastVisitedTok++;
        aToken = toks.get(lastVisitedTok);
      }
      int start = lastVisitedTok;
      while (aToken.getSpan().getEnd() < ch.getEnd()) {
        lastVisitedTok++;
        aToken = toks.get(lastVisitedTok);
      }
      int end = lastVisitedTok + 1;
      Span tokSpan = new Span(start, end);
      spans.add(tokSpan);
    }
    
    return groupTokens(text, toks, spans, additionalContext);
  }
  
  public static List<Token> groupTokens(String text, List<Token> toks, List<? extends Span> spans) {
    return groupTokens(text, toks, spans, null);
  }
  
  public static List<Token> groupTokens(String text, List<Token> toks, List<? extends Span> spans, String additionalContext) {
    for(int i = spans.size() - 1; i >=0; i--) {
      Span span = spans.get(i);
      if(span.length() > 0) {
        int s = toks.get(span.getStart()).getSpan().getStart();
        int e = toks.get(span.getEnd() - 1).getSpan().getEnd();
        StringBuilder lexeme = new StringBuilder();
        for(int j = span.getStart(); j < span.getEnd() - 1; j++) {
          lexeme.append(toks.get(j).getLexeme()).append("_");
        }
        lexeme.append(toks.get(span.getEnd() - 1).getLexeme()); 
        
        for(int j = span.getEnd() - 1; j >= span.getStart(); j--) {
          toks.remove(j);
        }
        Token t = new TokenCogroo(lexeme.toString(), new Span(s,e));
        if(span instanceof TypedSpan) {
          t.setOriginalPOSTag(((TypedSpan) span).getType());
        }
        
        if(additionalContext != null) {
          t.setAdditionalContext(additionalContext);
        }
        
        toks.add(span.getStart(), t);
      }
    }
    return toks;
  }

}
