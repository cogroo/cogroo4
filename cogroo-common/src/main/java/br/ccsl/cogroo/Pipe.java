package br.ccsl.cogroo;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import opennlp.tools.chunker.ChunkSample;
import opennlp.tools.chunker.Chunker;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

public class Pipe {

  public static void main(String[] args) throws FileNotFoundException {
    long start = System.nanoTime();

    LanguageLoader loader = new RuntimeLanguageLoader();

    SentenceDetector sentdetect = loader.getSentenceDetector();

    Tokenizer tokenizer = loader.getTokenizer();

    TokenNameFinder propFinder = loader.getProperNameFinder();

    TokenNameFinder expFinder = loader.getExpressionFinder();

    TokenNameFinder contFinder = loader.getContractionFinder();

    POSTagger tagger = loader.getPOSTagger();

    Chunker chunker = loader.getChunker();

    Chunker shallowParser = loader.getShallowParser();

    System.out.println("Loading time ["
        + ((System.nanoTime() - start) / 1000000) + "ms]");

    Scanner kb = new Scanner(System.in);
    System.out.print("Enter the sentence: ");
    String input = kb.nextLine();
    while (!input.equals("q")) {
      if (input.equals("0")) {
        input = "Quem\n\nQueen é uma banda.\nO túnel começava no banheiro da alfaiataria e se estendia por 10,5 metros sob o prédio, avançando 0,5 metro no pátio externo rumo à muralha, distante 19,5 m.";
      }

      Span[] sentPosArr = sentdetect.sentPosDetect(input);
      for (Span sentPos : sentPosArr) {
        CharSequence sentence = sentPos.getCoveredText(input);
        System.out.println("SENT: " + sentence);

        Span[] tokPosArr = tokenizer.tokenizePos(sentence.toString());
        System.out.print("TOKS: ");
        for (Span tokPos : tokPosArr) {
          System.out.print(tokPos.getCoveredText(sentence) + " ");
        }
        System.out.println();

        String[] toks = Span.spansToStrings(tokPosArr, sentence);
        Span[] props = propFinder.find(toks);
        System.out.println("PROP: "
            + new NameSample(toks, props, true).toString());

        Span[] exps = expFinder.find(toks);
        System.out.println("EXPS: "
            + new NameSample(toks, exps, true).toString());

        Span[] merged = merge(props, exps);
        toks = groupTokens(toks, merged);
        System.out.println("MERG: "
            + new NameSample(toks, merged, true).toString());
        System.out.println("MERG: " + Arrays.toString(toks));

        Span[] contractions = contFinder.find(toks);
        toks = expandContractions(toks, contractions);
        System.out.println("CONT: " + Arrays.toString(toks));

        String[] posTags = tagger.tag(toks);
        System.out.println("POST: " + new POSSample(toks, posTags));

        String[] chunks = chunker.chunk(toks, posTags);
        System.out.println("CHKS: "
            + new ChunkSample(toks, filterPOS(posTags), chunks));

        String[] shallowParseChunks = shallowParser.chunk(toks,
            getShallowParserInTags(filterPOS(posTags), chunks));
        System.out.println("SHAL: "
            + new ChunkSample(toks, posTags, shallowParseChunks));

        System.out.println();
      }

      input = kb.nextLine();
    }
  }

  private static String[] filterPOS(String[] posTags) {
    for (int i = 0; i < posTags.length; i++) {
      if ("art".equals(posTags[i]) || "pron-det".equals(posTags[i])) {
        posTags[i] = "det";
      }
    }
    return posTags;
  }

  private static String[] getShallowParserInTags(String[] posTags,
      String[] chunks) {
    String[] tags = new String[posTags.length];
    for (int i = 0; i < chunks.length; i++) {
      tags[i] = posTags[i] + "|" + chunks[i];
    }
    return tags;
  }

  private static String[] expandContractions(String[] toks, Span[] contractions) {
    List<String> expanded = new LinkedList<String>(Arrays.asList(toks));
    for (int i = contractions.length - 1; i >= 0; i--) {
      String[] c = ContractionUtility.expand(toks[contractions[i].getStart()]);
      if (c != null) {
        expanded.remove(contractions[i].getStart());
        expanded.addAll(contractions[i].getStart(), Arrays.asList(c));
      }
    }
    return expanded.toArray(new String[expanded.size()]);
  }

  private static String[] groupTokens(String[] toks, Span[] spans) {
    if (spans == null || spans.length == 0) {
      return toks;
    }
    List<String> grouped = new ArrayList<String>(toks.length);
    int sindex = 0;
    StringBuilder merging = null;
    for (int i = 0; i < toks.length; i++) {
      if (sindex < spans.length) {
        if (i == spans[sindex].getStart()) {
          merging = new StringBuilder(toks[i]);
        } else if (merging != null) {
          if (i < spans[sindex].getEnd() - 1) {
            merging.append('_').append(toks[i]);
          } else if (i == spans[sindex].getEnd() - 1) {
            merging.append('_').append(toks[i]);
            grouped.add(merging.toString());
            merging = null;
            sindex++;
          }
        } else {
          grouped.add(toks[i]);
        }
      } else {
        grouped.add(toks[i]);
      }
    }
    return grouped.toArray(new String[grouped.size()]);
  }

  private static Span[] merge(Span[] first, Span[] second) {
    List<Span> merged = new ArrayList<Span>(first.length + second.length);
    // add all of the first
    merged.addAll(Arrays.asList(first));

    for (Span s : second) {
      boolean addS = true;
      for (Span f : first) {
        if (s.intersects(f)) {
          addS = false;
          break;
        }
      }
      if (addS) {
        merged.add(s);
      }
    }
    Collections.sort(merged);
    return merged.toArray(new Span[merged.size()]);
  }

}
