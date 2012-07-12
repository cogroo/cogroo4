#
# Copyright (C) 2012 cogroo <cogroo@cogroo.org>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


# vars
BASE_PARAM="-inputFile ../../../../jspell.br/out/cogroo/tagdict.txt -corpus ${CORPUS_ROOT}/Bosque/Bosque_CF_8.0.ad.txt -encoding "UTF-8" -allowInvalidFeats false"

TS="sh scripts/cogroo TabSeparatedPOSDictionaryBuilder"
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

mvn -e -o -q exec:java "-Dexec.mainClass=morfologik.tools.Launcher" "-Dexec.args=tab2morph -inf -i ../../../../jspell.br/out/cogroo/trans.txt -o ${OUT}-enc.txt"
mvn -e -o -q exec:java "-Dexec.mainClass=morfologik.tools.Launcher" "-Dexec.args=fsa_build -f CFSA2 -i ${OUT}-enc.txt -o ${FINAL}.dict"

## XML

XML=../../../cogroo-dict/res/tagdict.xml
mkdir ../../../cogroo-dict/res 
sh scripts/cogroo POSDictionaryBuilder -outputFile ${XML} $BASE_PARAM
