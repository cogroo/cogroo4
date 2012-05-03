# expand_me & factory em todos
perl scripts/eval.pl -t pos -p 8 -e -v gp -a MAXENT -d cf -o POS_EXPANDME,POS_FACTORY

perl scripts/eval.pl -t pos -p 8 -e -v gp -a PERCEPTRON -d cf -o POS_EXPANDME,POS_FACTORY

perl scripts/eval.pl -t pos -p 8 -e -v gp -a MAXENT -d cf -o POS_EXPANDME,POS_FACTORY,POS_DIC

perl scripts/eval.pl -t pos -p 8 -e -v gp -a PERCEPTRON -d cf -o POS_EXPANDME,POS_FACTORY,POS_DIC

perl scripts/eval.pl -t pos -p 8 -e -v gp -a MAXENT -d vcf -o POS_EXPANDME,POS_FACTORY

perl scripts/eval.pl -t pos -p 8 -e -v gp -a PERCEPTRON -d vcf -o POS_EXPANDME,POS_FACTORY

perl scripts/eval.pl -t pos -p 8 -e -v gp -a MAXENT -d vcf -o POS_EXPANDME,POS_FACTORY,POS_DIC

perl scripts/eval.pl -t pos -p 8 -e -v gp -a PERCEPTRON -d vcf -o POS_EXPANDME,POS_FACTORY,POS_DIC