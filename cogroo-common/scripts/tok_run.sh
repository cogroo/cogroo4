perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d CF -o SD_ABB
perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d CF

perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d CF -o SD_ABB
perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d CF

perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d VCF -o SD_ABB
perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d VCF

perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d VCF -o SD_ABB
perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d VCF


# can't train with AMA because it never finishes
# perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d AMA -o SD_ABB
# perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d AMA

# perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d AMA -o SD_ABB
# perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d AMA