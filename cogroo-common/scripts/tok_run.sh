# SEM ALPHA

perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d cf -o SD_ABB
perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d cf

perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d cf -o SD_ABB
perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d cf

perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d vcf -o SD_ABB
perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d vcf

perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d vcf -o SD_ABB
perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d vcf


perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d cf -o SD_ABB,TOK_ACP
perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d cf -o TOK_ACP

perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d cf -o SD_ABB,TOK_ACP
perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d cf -o TOK_ACP

perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d vcf -o SD_ABB,TOK_ACP
perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d vcf -o TOK_ACP

perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d vcf -o SD_ABB,TOK_ACP
perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d vcf -o TOK_ACP

# TOK_ALPHA

perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d cf -o SD_ABB,TOK_ALPHAOPT 
perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d cf -o TOK_ALPHAOPT

perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d cf -o SD_ABB,TOK_ALPHAOPT
perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d cf -o TOK_ALPHAOPT

perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d vcf -o SD_ABB,TOK_ALPHAOPT
perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d vcf -o TOK_ALPHAOPT

perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d vcf -o SD_ABB,TOK_ALPHAOPT
perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d vcf -o TOK_ALPHAOPT


perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d cf -o SD_ABB,TOK_ALPHAOPT,TOK_ACP 
perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d cf -o TOK_ALPHAOPT,TOK_ACP

perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d cf -o SD_ABB,TOK_ALPHAOPT,TOK_ACP
perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d cf -o TOK_ALPHAOPT,TOK_ACP

perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d vcf -o SD_ABB,TOK_ALPHAOPT,TOK_ACP
perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d vcf -o TOK_ALPHAOPT,TOK_ACP

perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d vcf -o SD_ABB,TOK_ALPHAOPT,TOK_ACP
perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d vcf -o TOK_ALPHAOPT,TOK_ACP


# can't train with AMA because it never finishes
# perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d AMA -o SD_ABB
# perl scripts/eval.pl -t tok -p 8 -e -v gp -a MAXENT -d AMA

# perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d AMA -o SD_ABB
# perl scripts/eval.pl -t tok -p 8 -e -v gp -a PERCEPTRON -d AMA