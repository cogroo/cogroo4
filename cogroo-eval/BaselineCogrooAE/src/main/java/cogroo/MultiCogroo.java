/**
 * Copyright (C) 2008 CoGrOO Team (cogroo AT gmail DOT com)
 * 
 * CoGrOO Team (cogroo AT gmail DOT com)
 * LTA, PCS (Computer and Digital Systems Engineering Department),
 * Escola Politécnica da Universidade de São Paulo
 * Av. Prof. Luciano Gualberto, trav. 3, n. 380
 * CEP 05508-900 - São Paulo - SP - BRAZIL
 * 
 * http://cogroo.sourceforge.net/
 * 
 * This file is part of CoGrOO.
 * 
 * CoGrOO is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public as published by 
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CoGrOO is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with CoGrOO. If not, see <http://www.gnu.org/licenses/>.
 */

package cogroo;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import br.usp.pcs.lta.cogroo.configuration.LegacyRuntimeConfiguration;
import br.usp.pcs.lta.cogroo.configuration.RuntimeConfigurationI;
import br.usp.pcs.lta.cogroo.entity.Mistake;
import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.entity.Token;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.MistakeImpl;
import br.usp.pcs.lta.cogroo.grammarchecker.CheckerResult;
import br.usp.pcs.lta.cogroo.grammarchecker.CogrooI;
import br.usp.pcs.lta.cogroo.tools.ProcessingEngine;
import br.usp.pcs.lta.cogroo.tools.checker.Checker;
import br.usp.pcs.lta.cogroo.tools.dictionary.CogrooTagDictionary;
import br.usp.pcs.lta.cogroo.tools.dictionary.impl.Merger;
import br.usp.pcs.lta.cogroo.tools.sentencedetector.SentenceDetectorI;

public class MultiCogroo implements CogrooI {

    protected SentenceDetectorI sentDetect;

    protected ProcessingEngine tokenizer;
    
    protected ProcessingEngine pretagger;

//    protected ProcessingEngine nameFind;

    protected ProcessingEngine tagger;

    protected ProcessingEngine chunker;

    protected ProcessingEngine shallowParser;
    
    protected CogrooTagDictionary tagDictionary;
    
    protected Checker checker;
    
    protected Merger merger;

    private PostPOSTagger postPOSTagger;

    protected static final Logger LOGGER = Logger.getLogger(MultiCogroo.class);
    
    public MultiCogroo() {
      
    }

    public MultiCogroo(RuntimeConfigurationI config) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(">>> init()");
        }
        LOGGER.info("Loading Resources...");
        
        try {
          
            // Loads dictionaries.
            LOGGER.info("Loading Dictionaries...");
            long start = System.nanoTime();
            this.tagDictionary = config.getTagDictionary();
            LOGGER.info("Dictionaries loaded in " + (System.nanoTime() - start)
                    / 1000000 + "ms");
          
            this.pretagger = new MultiPretagger(config);
            // Loads models.
            LOGGER.info("Loading Models...");
            long modelLoadingStart = System.nanoTime();
            start = modelLoadingStart;
            this.sentDetect = new MultiSentenceDetector(config);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("   [Sentence Detector]\t\tmodel loaded in\t["
                        + (System.nanoTime() - start) / 1000000 + "ms]");
            }
            
            start = System.nanoTime();
            this.tokenizer = new MultiTokenizer(config);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("   [Tokenizer]\t\tmodel loaded in\t["
                        + (System.nanoTime() - start) / 1000000 + "ms]");
            }
            
//            start = System.nanoTime();
//            this.nameFind = config.getNameFinder();
//            if (LOGGER.isDebugEnabled()) {
//                LOGGER.debug("   [Namefinder]\t\tmodel loaded in\t["
//                        + (System.nanoTime() - start) / 1000000 + "ms]");
//            }
            
            start = System.nanoTime();
            this.tagger = config.getPOSTagger();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("   [Tagger]\t\tmodel loaded in\t["
                        + (System.nanoTime() - start) / 1000000 + "ms]");
            }
            
            
            postPOSTagger = new PostPOSTagger();
            
            start = System.nanoTime();
            this.chunker = new MultiChunker(config);
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("   [Chunker]\t\tmodel loaded in\t["
                        + (System.nanoTime() - start) / 1000000 + "ms]");
            }
            
            
            start = System.nanoTime();
            this.shallowParser = new MultiShallowParser(config);
            
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("   [Shallow parser]\tmodel loaded in\t["
                        + (System.nanoTime() - start) / 1000000 + "ms]");
            }
            LOGGER.info("Models loaded in "
                    + (System.nanoTime() - modelLoadingStart) / 1000000 + "ms");
        } catch (Exception e) {
            throw new RuntimeException("CoGrOO loading failed", e);
        }
        // Forces initialization of the rules subsystem.
            LOGGER.info("Loading Rules...");
            long start = System.nanoTime();
            //this.rulesApplier = config.getRulesApplier();
            this.checker = config.getChecker();
            LOGGER.info("Rules loaded in " + (System.nanoTime() - start)
                    / 1000000 + "ms");
        LOGGER.info("Loading completed!");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("<<< init()");
        }
    }

    /* (non-Javadoc)
     * @see br.usp.pcs.lta.cogroo.grammarchecker.CogrooI#checkText(java.lang.String)
     */
    public List<Mistake> checkText(String text) {
      List<Mistake> mistakes = null;
      try {
        mistakes =analyseAndCheckText(text).mistakes;  
      } catch (IndexOutOfBoundsException e) {
        LOGGER.fatal("Failed to process text: " + text, e);
        throw new RuntimeException(e);
      }
      return mistakes;
        
    }
    
    public int checkFirstSentence(String paraText, List<Mistake> outMistakes) {
        CheckerResult res = analyseAndCheckText(paraText, true);
        outMistakes.addAll(res.mistakes);
        return res.sentences.get(0).getSentence().length();
    }
    
    public CheckerResult analyseAndCheckText(String text) {
        return analyseAndCheckText(text, false);
    }

    private CheckerResult analyseAndCheckText(String text, boolean isFirsSentenceOnly) {
        long start = System.nanoTime();
        /*
         * If an exception occurs when processing the sentence, simply returns
         * an empty mistakes list. CoGrOO must never die because of a bad user
         * entry, since its setup time is very expensive.
         */
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(">>> checkAndGetSentence()");
            LOGGER.debug("Text entered: " + text);
        }
        List<Sentence> sentences = null;
        List<Mistake> mistakes = new ArrayList<Mistake>();
        if (text != null && !"".equals(text)) { // Protect
            // against bad user entries.
            try {
                sentences = this.sentDetect.process(text);
                for (Sentence sentence : sentences) {
                    // Prepares the sentence to apply rules.
                    this.tokenizer.process(sentence);

//                    this.nameFind.process(sentence);
                    this.pretagger.process(sentence);
                    try {
                      this.tagger.process(sentence);
                    } catch (ArrayIndexOutOfBoundsException e) {
                      System.out.println("tagger failed");
                      return null;
                    }
                    
                    if(MultiCogrooSettings.TOK) {
                      this.postPOSTagger.process(sentence);
                    }
//                    this.merger.generalizePOSTags(sentence, this.tagDictionary);
                    
                    this.chunker.process(sentence);
                    this.shallowParser.process(sentence);

                    // Just verifying.
                    if (LOGGER.isDebugEnabled()) {
                        StringBuilder trace = new StringBuilder();
                        trace.append("Show tree [" + sentence.getSentence()
                                + "]: \n");
                        List<Token> tokens = sentence.getTokens();
                        for (int i = 0; i < tokens.size(); i++) {
                            trace.append("\t["
                                    + tokens.get(i).getSyntacticTag() + "]["
                                    + tokens.get(i).getChunkTag() + "] "
                                    + tokens.get(i) + " --> {"
                                    + tokens.get(i).getPrimitive() + "}_"
                                    + tokens.get(i).getMorphologicalTag()
                                    + "\n");
                        }
                        LOGGER.debug(trace.toString());
                    }

                    // Mistakes to be returned.
                    mistakes.addAll(this.checker.check(sentence));
                    // Just verifying.
                    if (LOGGER.isDebugEnabled()) {
                        for (Mistake mistake : mistakes) {
                            LOGGER.debug("rule["
                                    + ((MistakeImpl) mistake).getRuleIdentifier()
                                    + "], span["
                                    + ((MistakeImpl) mistake).getStart() + ", "
                                    + ((MistakeImpl) mistake).getEnd() + "]");
                        }
                    }
                    if(isFirsSentenceOnly) {
                        break;
                    }
                }
            } catch (Exception e) {
              LOGGER.error("Error processing text: " + text + " sentences: " + sentences, e);
            }
            LOGGER.debug("Check sentence time: " + (System.nanoTime() - start)
                    / 1000 + "us");

        }
        return new CheckerResult(sentences, mistakes);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        long start = System.nanoTime();
        CogrooI cogroo = new MultiCogroo(new LegacyRuntimeConfiguration("/Users/wcolen/Documents/wrks/corpuswrk/cogroo3/CoGrOOBase/target/CoGrOOBase-3.1.3a-SNAPSHOT-bin")); // THE CoGrOO!
        System.out.println("Loading time ["
                + ((System.nanoTime() - start) / 1000000) + "ms]");
        System.out.println("Default: " + Charset.defaultCharset());
        Scanner kb = new Scanner(System.in);
        System.out.print("Enter the sentence: ");
        String input = kb.nextLine();
        while (!input.equals("q")) {
            if (input.equals("0")) {
                //input = "couves-flores, amores-perfeitos, gentis-homens, quintas-feiras, guarda-roupas, alto-falantes, reco-recos, águas-de-colônia, cavalos-vapor, palavras-chave, bota-fora, saca-rolhas, louva-a-deus";
//                input = "Os olhos das meninas são bonitos nas estrelas.";
//              input = "Os inimigos que eram fácil derrotar estão próximo.";
              //input = "A construção do trecho inicial da Linha 5-Lilás.";
              input = "problemas político-econômicos";
              //114: Jamais ocorreu-nos tal idéia.
              //115: Júlio namorou com Marina durante três anos.
              //
            }
            try {
                CheckerResult cr = cogroo.analyseAndCheckText(input);
                for (Mistake mistake : cr.mistakes) {
                    System.out.println("["
                            + mistake.getStart()
                            + ".."
                            + mistake.getEnd()
                            + "] = ["
                            + input.substring(((MistakeImpl) mistake).getStart(),
                                    ((MistakeImpl) mistake).getEnd()) + "]");
                    System.out.println(mistake.toString());
                }
                for (Sentence s : cr.sentences) {
                    System.out.println(s.getSentence());
                    System.out.println(s.getSyntaxTree());
                    System.out.println(s);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            System.out.print("Enter the sentence: ");
            input = kb.nextLine();
        }
    }

    public CogrooTagDictionary getTagDictionary() {
        return this.tagDictionary;
    }

}
