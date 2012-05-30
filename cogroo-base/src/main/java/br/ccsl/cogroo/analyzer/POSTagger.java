package br.ccsl.cogroo.analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.Span;
import br.ccsl.cogroo.config.Analyzers;
import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.Token;
import br.ccsl.cogroo.text.impl.TokenImpl;
import br.ccsl.cogroo.util.EntityUtils;
import br.ccsl.cogroo.util.TextUtils;

/**
 * The <code>POSTagger</code> class analyzes each token of a sentence and
 * classifies it grammatically.
 * 
 */
public class POSTagger implements AnalyzerI {
  private POSTaggerME tagger;

  public POSTagger(POSTaggerME tagger) {
    this.tagger = tagger;
  }

  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();

    for (Sentence sentence : sentences) {
      List<Token> tokens = sentence.getTokens();
      String[] tags = tagger.tag(
          TextUtils.tokensToString(sentence.getTokens()), TextUtils
              .additionalContext(tokens, Arrays.asList(
                  Analyzers.CONTRACTION_FINDER, Analyzers.NAME_FINDER)));

      for (int i = 0; i < tags.length; i++) {
        tokens.get(i).setPOSTag(tags[i]);
      }
      
      EntityUtils.groupTokens(sentence.getText(), tokens, createSpanList(toTokensArray(tokens), toTagsArray(tokens)));
    }
  }
  
  private String[] toTokensArray(List<Token> tokens) {
    String[] arr = new String[tokens.size()];
    for (int i = 0; i < tokens.size(); i++) {
      arr[i] = tokens.get(i).getLexeme();
    }
    return arr;
  }

  private String[] toTagsArray(List<Token> tokens) {
    String[] arr = new String[tokens.size()];
    for (int i = 0; i < tokens.size(); i++) {
      arr[i] = tokens.get(i).getPOSTag();
    }
    return arr;
  }

  // this is from opennlp
  public static List<Span> createSpanList(String[] toks, String[] tags) {

    // initialize with the list maximum size
    List<Span> phrases = new ArrayList<Span>(toks.length); 
    String startTag = "";
    int startIndex = 0;
    boolean foundPhrase = false;

    for (int ci = 0, cn = tags.length; ci < cn; ci++) {
      String pred = tags[ci];
      if(!tags[ci].startsWith("B-") && !tags[ci].startsWith("I-")) {
        pred = "O";
      }
      if (pred.startsWith("B-")
          || (!pred.equals("I-" + startTag) && !pred.equals("O"))) { // start
        if (foundPhrase) { // handle the last
          phrases.add(new Span(startIndex, ci, startTag));
        }
        startIndex = ci;
        startTag = pred.substring(2);
        foundPhrase = true;
      } else if (pred.equals("I-" + startTag)) { // middle
        // do nothing
      } else if (foundPhrase) {// end
        phrases.add(new Span(startIndex, ci, startTag));
        foundPhrase = false;
        startTag = "";
      }
    }
    if (foundPhrase) { // leftover
      phrases.add(new Span(startIndex, tags.length, startTag));
    }

    return phrases;
  }
}
