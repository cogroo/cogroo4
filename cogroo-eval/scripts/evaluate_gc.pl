#!/usr/bin/perl

# Script will create a new model folder and uima repo from scratch execute evaluation
# It requires a list of configurations.
# Outputs a report in the eval folder
# 
# usage: perl evaluate_gc.pl sent-vcf

use FindBin qw($Bin);
use Data::Dumper;
use File::Temp qw/ tempfile tempdir /;
use Cwd;
use File::Path qw(make_path);
use Storable qw(freeze thaw);
my $common = "$Bin/../../cogroo-common/scripts"; 

use lib "$Bin/../../cogroo-common/scripts";
require eval_unit;
 
require cpe;

my %extraOpt;

sub init() {

	open CONFIG, "../../cogroo-common/scripts/options.properties" or die $!;

	while (<CONFIG>) {
		chomp;       # no newline
		s/#.*//;     # no comments
		s/^\s+//;    # no leading white
		s/\s+$//;    # no trailing white
		next unless length;    # anything left?
		my ( $var, $value ) = split( /\s*=\s*/, $_, 2 );
		$extraOpt{$var} = $value;
	}

	close CONFIG;
}

sub parseConfig {
	my $str = shift;
	my @tokens = split(/-/, $str);
	
	my %o = (
		't' => $tokens[0],
		'd' => $tokens[1],
		'a' => uc($tokens[2]),
		# 'v' => $tokens[4],
		'c' => $tokens[5],
	);
	if($tokens[3] ne 'NONE') {
		$o{'o'} = uc($tokens[3]); 		
	}
	
	
	return %o;	
}

sub evaluate {
	my $conf = shift;
	my %opt = parseConfig($conf);
	# faltam: c, p, f 
	$opt{'p'} = 8;
	$opt{'f'} = 1;
	
	# create a temp folder and set the envvar REPO there.
	
	
	my $tempDir = tempdir;
	$ENV{'MODEL_ROOT'} = $tempDir;
	$ENV{'UIMA_DATAPATH'} = $tempDir;
	$ENV{'UIMA_JVM_OPTS'} = "-Duima.datapath=$tempDir " . $ENV{'UIMA_JVM_OPTS'};
	$ENV{'MAVEN_OPTS'} = "-Duima.datapath=$tempDir " . $ENV{'MAVEN_OPTS'};
	
	my $dir = getcwd;
	chdir('../../cogroo-common');
	eval_unit::exec(\%opt, \%extraOpt);
	chdir($dir);
	
	#my %baseline = cpe::evaluateBaseline('eval');
	cpe::installRequiredPears();
	my %r = cpe::evaluateUsingModel('eval', processName($conf));
	
	return %r;
}

sub evaluateListFromFile {
	my $file = shift;
	
	open (LIST, $file)  or die("Could not open list file. $!");
	
	my %results;

	foreach my $line (<LIST>) {
    	$line =~ s/^\s+|\s+$//g;
    	print "will evaluate $line \n";
		my %newResult = evaluate($line);
		%results = (%results, %newResult);
	}
	
	close LIST;
	
	return %results;
}

sub printReport {
	my %table = %{$_[0]};
	my $file = $_[1];
	
	open (REP, ">$file") or die("Could not create report file. $!");;
	
	my @corpus = qw(metro probi bosque);
	my @col = qw(target tp fp);
	
	my @expNames = sort keys %table;
	
	my @hNames = generateHumanNames(\@expNames);

	my $r = 
'\begin{table}[h!]
 	\begin{center}
    	\begin{tabular}{c|c|c|c|c|c|c|c|}
        	\cline{2-8}
        	& \multicolumn{3}{|c|}{Metro} & \multicolumn{3}{|c|}{Probi} & Bosque \\\\ \\hline
        	\multicolumn{1}{|c|}{Experiment} & Target & TP & FP & Target & TP & FP & FP \\\\ \\hline \\hline
    		';
	my %first;
	for(my $i = 0; $i < @expNames; $i++) {
		my $exp = $expNames[$i];
        $r .= "\\multicolumn{1}{|c|}{$hNames[$i]} ";
		foreach my $c (@corpus) {
			if($c eq 'bosque') {
				my $d = 'fp';
				$r .= "& $table{$exp}{$c}{$d} ";
			} else {
				foreach my $d (@col) {
					if($d eq 'target') {
						if(!defined $first{$c}){
							my $size = @expNames;
							$r .= "& \\multirow{$size}{*}{$table{$exp}{$c}{$d}} ";
							$first{$c} = 1;							
						} else {
							$r .= "& ";	
						}
					} else {
						$r .= "& $table{$exp}{$c}{$d} ";
					}
				}
			}
		}
		if($i < @expNames - 1) {
			$r .= "\\\\ \\cline{1-1} \\cline{3-4} \\cline{6-8} \n    		";	
		} else {
			$r .= "\\\\ \\hline \n    		";
		}
		
    }
    
    $r .= 
'    	\end{tabular}
    \caption{TODO}
   \label{tb:TODO}
	\end{center}
\end{table}';
	
	print REP $r;
	
	close REP;
}

sub processName {
	my $name = shift;
	$name =~ s/sd_//g;
	$name =~ s/-gp$//g;
	$name =~ s/-ap$//g;
	$name =~ s/_//g;
	$name =~ s/,/./g;
	$name =~ s/-/./g;
	$name =~ s/^\s+|\s+$//g;
	return $name;
}

sub generateHumanNames {
	my @names = @{$_[0]};
	# we need to create a copy of it...
	my $store = freeze(\@names);
	@names = @{ thaw($store) };
	
	#sent-cf-maxent-sd_factory,sd_abb-gp-4
	
	# replace dots
	for(my $i = 0; $i < @names; $i++) {
		$names[$i] = processName($names[$i]);
		$names[$i] =~ s/\./ /g;
	}
	
	# now we look for common parts to remove.
	if(@names > 0) {
		my @parts = split(/\s+/,$names[0]);
		foreach my $p (@parts) {
			my $isCommon = 1;
			$p = quotemeta($p);
			for(my $i = 1; $i < @names; $i++) {
				if($names[$i] !~ m/\b$p\b/) {
					$isCommon = 0;
				}
			}
			if($isCommon) {
				for(my $i = 0; $i < @names; $i++) {
					$names[$i] =~ s/\b$p\b//g;
				}
			}
		}
	}
	
	for(my $i = 0; $i < @names; $i++) {
		$names[$i] =~ s/NONE//g;
		$names[$i] =~ s/^\s+//g;
		$names[$i] =~ s/\s+$//g;
		$names[$i] =~ s/\s+/ /g;
		$names[$i] =~ s/^\s+|\s+$//g;
		$names[$i] = uc($names[$i]);
	}
	
	return @names;
}


$ENV{'REPO_ROOT'} = tempdir;

init();
cpe::init();

my $reportsPath = 'eval';

my %eval = (evaluateListFromFile($ARGV[0]), cpe::evaluateBaseline($reportsPath));

print Dumper(\%eval);

printReport(\%eval, "$reportsPath/".$ARGV[0].'.txt');