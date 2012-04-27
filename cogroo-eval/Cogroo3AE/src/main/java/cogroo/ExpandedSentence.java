package cogroo;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.Span;
import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.entity.Token;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.TokenCogroo;

public class ExpandedSentence {

  private static final long serialVersionUID = 1L;
  private String expandedSentence;
  private Sentence theSentence;
  private int[] offsets;
  private List<Token> tokens;

  public ExpandedSentence(Sentence sentence) {
    theSentence = sentence;

    if (expandedSentence == null) {
      synchronized (this) {
        if (expandedSentence == null) {

          int offset = 0;
          int lastStart = -1;
          offsets = new int[theSentence.getTokens().size()];
          StringBuilder expSent = new StringBuilder(theSentence.getSentence()
              .length());
          tokens = new ArrayList<Token>();
          for (int i = 0; i < theSentence.getTokens().size(); i++) {
            Token t = theSentence.getTokens().get(i);
            String lexeme = t.getLexeme();
            if (lexeme.length() < t.getSpan().length()
                && t.getSpan().getStart() != lastStart) {
              lexeme = t.getSpan().getCoveredText(theSentence.getSentence())
                  .replace(" ", "_");
            }
            if (t.getSpan().getStart() == lastStart) {
              // oops! a contraction!
              // we configure the offset and append the string

              // compute the skip
              int skip = theSentence.getTokens().get(i - 1).getSpan().getEnd() + 1;
              append(offset + skip, lexeme, expSent);
              offset += lexeme.length() + 1;
            } else {
              append(offset + t.getSpan().getStart(), lexeme, expSent);
            }
            offsets[i] = offset;
            lastStart = t.getSpan().getStart();

            Token newToken = new TokenCogroo(t.getLexeme(), offset
                + t.getSpan().getStart());
            tokens.add(newToken);
          }
          expandedSentence = expSent.toString();
        }
      }
    }
  }

  public String getExtendedSentence() {
    return expandedSentence;
  }

  public Sentence getSent() {
    return theSentence;
  }

  private void append(int i, String string, StringBuilder expSent) {
    while (expSent.length() < i) {
      expSent.append(" ");
    }
    expSent.append(string);
  }

  public Span getTokenSpan(int i) {
    Span s = theSentence.getTokens().get(i).getSpan();
    return new Span(s.getStart() + offsets[i], s.getEnd() + offsets[i]);
  }

}
