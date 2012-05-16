package br.ccsl.cogroo.analyzer;

import java.util.List;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.Span;

import br.ccsl.cogroo.text.Document;
import br.ccsl.cogroo.text.Sentence;
import br.ccsl.cogroo.text.Token;
import br.ccsl.cogroo.text.impl.TokenImpl;
import br.ccsl.cogroo.util.TextUtils;

public class NameFinder implements Analyzer {

  private NameFinderME nameFinder;

  public NameFinder(NameFinderME nameFinder) {
    this.nameFinder = nameFinder;
  }

  public void analyze(Document document) {
    List<Sentence> sentences = document.getSentences();

    for (Sentence sentence : sentences) {
      Span[] namesSpan = nameFinder.find(TextUtils.tokensToString(sentence
          .getTokens()));
      List<Token> newTokens = sentence.getTokens();

      for (int i = namesSpan.length - 1; i >= 0; i--) {
        int start = namesSpan[i].getStart(), end = namesSpan[i].getEnd();

        String name = newTokens.get(end - 1).getLexeme();
        newTokens.remove(end - 1);

        for (int j = end - 2; j >= start; j--) {
          String temp = newTokens.get(j).getLexeme();
          name = temp + "_" + name;
          newTokens.remove(j);
        }
        Span span = new Span(start, end);
        Token token = new TokenImpl(span, name);
        newTokens.add(start, token);
      }
      sentence.setTokens(newTokens);
    }
  }
}
