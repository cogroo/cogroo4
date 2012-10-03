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

# expand_me & factory em todos

perl scripts/eval.pl -t feat -p 8 -e -v gp -a MAXENT -d cf -o FEAT_FLAGSNONE

perl scripts/eval.pl -t feat -p 8 -e -v gp -a MAXENT -d cf -o FEAT_FLAGSS

perl scripts/eval.pl -t feat -p 8 -e -v gp -a MAXENT -d cf -o FEAT_FLAGSCS

perl scripts/eval.pl -t feat -p 8 -e -v gp -a MAXENT -d cf -o FEAT_FLAGSNCS

perl scripts/eval.pl -t feat -p 8 -e -v gp -a MAXENT -d cf -o FEAT_FLAGSHNCS

perl scripts/eval.pl -t feat -p 8 -e -v gp -a MAXENT -d cf -o FEAT_FLAGSWHNCS

perl scripts/eval.pl -t feat -p 8 -e -v gp -a MAXENT -d cf -o FEAT_FLAGSHNCS,FEAT_DIC,FEAT_FSAFACTORY




perl scripts/eval.pl -t feat -p 8 -e -v gp -a PERCEPTRON -d cf -o FEAT_FLAGSNONE

perl scripts/eval.pl -t feat -p 8 -e -v gp -a PERCEPTRON -d cf -o FEAT_FLAGSNONE

perl scripts/eval.pl -t feat -p 8 -e -v gp -a PERCEPTRON -d cf -o FEAT_FLAGSS

perl scripts/eval.pl -t feat -p 8 -e -v gp -a PERCEPTRON -d cf -o FEAT_FLAGSCS

perl scripts/eval.pl -t feat -p 8 -e -v gp -a PERCEPTRON -d cf -o FEAT_FLAGSNCS

perl scripts/eval.pl -t feat -p 8 -e -v gp -a PERCEPTRON -d cf -o FEAT_FLAGSHNCS

perl scripts/eval.pl -t feat -p 8 -e -v gp -a PERCEPTRON -d cf -o FEAT_FLAGSWHNCS

perl scripts/eval.pl -t feat -p 8 -e -v gp -a PERCEPTRON -d cf -o FEAT_FLAGSHNCS,FEAT_DIC,FEAT_FSAFACTORY

