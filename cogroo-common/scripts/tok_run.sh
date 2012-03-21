perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d cf -o SD_ABB
perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d cf

perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d cf -o SD_ABB
perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d cf

perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d vcf -o SD_ABB
perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d vcf

perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d vcf -o SD_ABB
perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d vcf


# can't train with AMA because it never finishes
# perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d AMA -o SD_ABB
# perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d AMA

# perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d AMA -o SD_ABB
# perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d AMA