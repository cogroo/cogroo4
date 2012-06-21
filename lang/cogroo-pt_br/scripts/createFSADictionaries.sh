
# vars
BASE_PARAM="-inputFile ../../../jspell.br/out/cogroo/tagdict.txt -corpus /Users/wcolen/Documents/wrks/corpus/Bosque/Bosque_CF_8.0.ad.txt -encoding "UTF-8" -allowInvalidFeats false"

TS="sh ../../cogroo-gc/scripts/cogroo TabSeparatedPOSDictionaryBuilder"
SYNTH="awk -f scripts/synthesis.awk"

mkdir target/tmp

## Features
 
OUT=target/tmp/feats
FINAL=fsa_dictionaries/featurizer/pt_br_feats
 
$TS -isIncludeFeatures true -includeFromCorpus false -outputFile ${OUT}.txt $BASE_PARAM

$SYNTH ${OUT}.txt > ${OUT}_synth.txt
mvn -e -o -q exec:java "-Dexec.mainClass=morfologik.tools.Launcher" "-Dexec.args=tab2morph -inf -i ${OUT}.txt -o ${OUT}-enc.txt"
mvn -e -o -q exec:java "-Dexec.mainClass=morfologik.tools.Launcher" "-Dexec.args=fsa_build -f CFSA2 -i ${OUT}-enc.txt -o ${FINAL}.dict"
mvn -e -o -q exec:java "-Dexec.mainClass=morfologik.tools.Launcher" "-Dexec.args=tab2morph -inf -i ${OUT}_synth.txt -o ${OUT}-enc_synth.txt"
mvn -e -o -q exec:java "-Dexec.mainClass=morfologik.tools.Launcher" "-Dexec.args=fsa_build -f CFSA2 -i ${OUT}-enc_synth.txt -o ${FINAL}_synth.dict"


## POS - jspell

OUT=target/tmp/jspell
FINAL=fsa_dictionaries/pos/pt_br_jspell

$TS -isIncludeFeatures false -includeFromCorpus false -outputFile ${OUT}.txt $BASE_PARAM

$SYNTH ${OUT}.txt > ${OUT}_synth.txt
mvn -e -o -q exec:java "-Dexec.mainClass=morfologik.tools.Launcher" "-Dexec.args=tab2morph -inf -i ${OUT}.txt -o ${OUT}-enc.txt"
mvn -e -o -q exec:java "-Dexec.mainClass=morfologik.tools.Launcher" "-Dexec.args=fsa_build -f CFSA2 -i ${OUT}-enc.txt -o ${FINAL}.dict"
mvn -e -o -q exec:java "-Dexec.mainClass=morfologik.tools.Launcher" "-Dexec.args=tab2morph -inf -i ${OUT}_synth.txt -o ${OUT}-enc_synth.txt"
mvn -e -o -q exec:java "-Dexec.mainClass=morfologik.tools.Launcher" "-Dexec.args=fsa_build -f CFSA2 -i ${OUT}-enc_synth.txt -o ${FINAL}_synth.dict"


## POS - jspell + corpus

OUT=target/tmp/jspell_corpus
FINAL=fsa_dictionaries/pos/pt_br_jspell_corpus

$TS -isIncludeFeatures false -includeFromCorpus true -outputFile ${OUT}.txt $BASE_PARAM

$SYNTH ${OUT}.txt > ${OUT}_synth.txt
mvn -e -o -q exec:java "-Dexec.mainClass=morfologik.tools.Launcher" "-Dexec.args=tab2morph -inf -i ${OUT}.txt -o ${OUT}-enc.txt"
mvn -e -o -q exec:java "-Dexec.mainClass=morfologik.tools.Launcher" "-Dexec.args=fsa_build -f CFSA2 -i ${OUT}-enc.txt -o ${FINAL}.dict"
mvn -e -o -q exec:java "-Dexec.mainClass=morfologik.tools.Launcher" "-Dexec.args=tab2morph -inf -i ${OUT}_synth.txt -o ${OUT}-enc_synth.txt"
mvn -e -o -q exec:java "-Dexec.mainClass=morfologik.tools.Launcher" "-Dexec.args=fsa_build -f CFSA2 -i ${OUT}-enc_synth.txt -o ${FINAL}_synth.dict"


## POS - corpus

# ??

## Transitividade

## POS - jspell + corpus

OUT=target/tmp/trans
FINAL=fsa_dictionaries/pos/pt_br_trans

mvn -e -o -q exec:java "-Dexec.mainClass=morfologik.tools.Launcher" "-Dexec.args=tab2morph -inf -i /Users/wcolen/Documents/jspell-git/master/out/cogroo/trans.txt -o ${OUT}-enc.txt"
mvn -e -o -q exec:java "-Dexec.mainClass=morfologik.tools.Launcher" "-Dexec.args=fsa_build -f CFSA2 -i ${OUT}-enc.txt -o ${FINAL}.dict"

## XML

XML=/Users/wcolen/Documents/wrks/cogroo4/cogroo4/cogroo-dict/res/tagdict.xml/Users/wcolen/Documents/wrks/cogroo4/cogroo4/cogroo-dict/res/tagdict.xml
sh scripts/cogroo POSDictionaryBuilder -isIncludeFeatures false -includeFromCorpus false -outputFile ${"XML} $BASE_PARAM
