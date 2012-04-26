# perl scripts/eval.pl -t con -p 8 -e -v gp -a MAXENT -d cf -o CON_DIC
# perl scripts/eval.pl -t con -p 8 -e -v gp -a MAXENT -d cf

# perl scripts/eval.pl -t con -p 8 -e -v gp -a PERCEPTRON -d cf -o CON_DIC
# perl scripts/eval.pl -t con -p 8 -e -v gp -a PERCEPTRON -d cf

perl scripts/eval.pl -t con -p 8 -e -v gp -a MAXENT -d vcf -o CON_DIC
perl scripts/eval.pl -t con -p 8 -e -v gp -a MAXENT -d vcf

perl scripts/eval.pl -t con -p 8 -e -v gp -a PERCEPTRON -d vcf -o CON_DIC
perl scripts/eval.pl -t con -p 8 -e -v gp -a PERCEPTRON -d vcf

perl scripts/eval.pl -t con -p 8 -e -v gp -a MAXENT -d ama -o CON_DIC
perl scripts/eval.pl -t con -p 8 -e -v gp -a MAXENT -d ama

perl scripts/eval.pl -t con -p 8 -e -v gp -a PERCEPTRON -d ama -o CON_DIC
perl scripts/eval.pl -t con -p 8 -e -v gp -a PERCEPTRON -d ama