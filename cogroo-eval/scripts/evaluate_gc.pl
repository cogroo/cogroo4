#!/usr/bin/perl
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


# Script will create a new model folder and uima repo from scratch execute evaluation
# It requires a list of configurations.
# Outputs a report in the eval folder
# 
# usage: perl evaluate_gc.pl sent-vcf

use FindBin qw($Bin);
use Test::Deep qw(cmp_deeply);
use Data::Dumper;
use File::Temp qw/ tempfile tempdir /;
use Cwd;
use File::Path qw(make_path rmtree);
use Storable qw(freeze thaw);
use File::Copy::Recursive qw(dircopy);
my $common = "$Bin/../../lang/pt_br/cogroo-res/scripts"; 

use lib "$Bin/../../lang/pt_br/cogroo-res/scripts";
require eval_unit;
 
require cpe;
require cpeNewTagset;

my %extraOpt;

sub init() {

	open CONFIG, "../../lang/pt_br/cogroo-res/scripts/options.properties" or die $!;

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

my $baselineModels;

sub createBaseline {
	# a list with the baseline configuration
	my $file = shift;
	
	# we will create a folder with the baseline models, they will be copied to the test folder latter
	
	open (LIST, $file)  or die("Could not open list file. $!");
	
	$baselineModels = tempdir;
	$ENV{'MODEL_ROOT'} = $baselineModels;
	
	my $dir = getcwd;
	chdir('../../lang/pt_br/cogroo-res');

	while (my $conf = <LIST>) {
    	$conf =~ s/^\s+|\s+$//g;
    	my @confs = split(/\n/,$conf);
    	foreach my $c (@confs) {
    	    print "will build baseline model: $c \n";
			my %opt = parseConfig($c);
			# faltam: c, p, f 
			$opt{'p'} = 8;
			$opt{'f'} = 1;
	
			# this will build the model
			eval_unit::exec(\%opt, \%extraOpt);
    	}
	}
	chdir($dir);	
	close LIST;
}

my %baselineVars;

sub evaluate {
	my $conf = shift;
	my $useNew = shift;
	
	my %opt = parseConfig($conf);
	# faltam: c, p, f 
	$opt{'p'} = 8;
	$opt{'f'} = 1;
	
	# create a temp folder and set the envvar REPO there.
	
	
	my $tempDir = tempdir;
	$ENV{'MODEL_ROOT'} = $tempDir;
	$ENV{'UIMA_DATAPATH'} = $tempDir;
	$ENV{'REPO_ROOT'} = tempdir;
	$ENV{'UIMA_JVM_OPTS'} = "-Duima.datapath=$tempDir -Xms512m -Xmx1024m -XX:PermSize=256m " . $ENV{'UIMA_JVM_OPTS'};
	$ENV{'MAVEN_OPTS'} = "-Duima.datapath=$tempDir -Xms256m -Xmx1024m -XX:PermSize=256m " . $ENV{'MAVEN_OPTS'};
	
	my $dir = getcwd;
	chdir('../../lang/pt_br/cogroo-res');
	# this will build the model
	eval_unit::exec(\%opt, \%extraOpt);
	chdir($dir);
	
	# copy baseline models to $tempdir
	if(defined $baselineModels) {
		my $num_of_files_and_dirs = dircopy($baselineModels,$tempDir);
		print "Copied $num_of_files_and_dirs models to $tempDir \n";		
	}
	
	my %r;
	if($useNew) {
		# copy models to resources
		rmtree("../NewTagsetBaselineCogrooAE/target/models");
		my $num_of_files_and_dirs = dircopy($tempDir,"../NewTagsetBaselineCogrooAE/target/models");
		print "Copied $num_of_files_and_dirs models to models \n";
		
		cpeNewTagset::install("../NewTagsetBaselineCogrooAE/pom.xml");
		%r = cpeNewTagset::evaluateUsingModel('eval', processName($conf), \%baselineVars);
	} else {
		
		cpe::installRequiredPears();
		%r = cpe::evaluateUsingModel('eval', processName($conf));		
	}

	
	return %r;
}

sub evaluateBaseline {
	my $name = shift;
	my $tempDir = tempdir;
	$ENV{'MODEL_ROOT'} = $tempDir;
	$ENV{'UIMA_DATAPATH'} = $tempDir;
	$ENV{'REPO_ROOT'} = tempdir;
	$ENV{'UIMA_JVM_OPTS'} = "-Duima.datapath=$tempDir " . $ENV{'UIMA_JVM_OPTS'};
	$ENV{'MAVEN_OPTS'} = "-Duima.datapath=$tempDir " . $ENV{'MAVEN_OPTS'};
	
		# this is executed first, so we can keep the vars
	
	$baselineVars{'MODEL_ROOT'} = $ENV{'MODEL_ROOT'};
	$baselineVars{'UIMA_HOME'} = $ENV{'UIMA_HOME'};
	$baselineVars{'CORPUS_ROOT'} = $ENV{'CORPUS_ROOT'};
	$baselineVars{'REPO_ROOT'} = $ENV{'REPO_ROOT'}; 
	
	# copy baseline models to $tempdir
	if(defined $baselineModels) {
		my $num_of_files_and_dirs = dircopy($baselineModels,$tempDir);
		print "Copied $num_of_files_and_dirs models to $tempDir \n";		
	}
	
	cpe::installRequiredPears();
	my %r = cpe::evaluateUsingModel('eval', $name);
	
	return %r;
}

sub loadOrderFromFile {
	my $file = shift;
	
	open (CONF, $file)  or die("Could not open list file. $!");
	
	my @order;
	push(@order, 'baseline');
	while (my $line = <CONF>) {
    	$line =~ s/^\s+|\s+$//g;
    	my @confs = split(/\n/,$line);
    	foreach my $c (@confs) {
			push(@order, processName($c));
    	}
	}
	
	close CONF;
	
	return @order;	
}

sub evaluateListFromFile {
	my $file = shift;
	my $useNew = shift;
	open (CONF, $file)  or die("Could not open list file. $!");
	
	my %results;

	while (my $line = <CONF>) {
    	$line =~ s/^\s+|\s+$//g;
    	my @confs = split(/\n/,$line);
    	foreach my $c (@confs) {
    	    print "will evaluate $c \n";
			my %newResult = evaluate($c, $useNew);
			%results = (%results, %newResult);	
    	}
	}
	
	close CONF;
	
	return %results;
}


sub printReport {
	my %table = %{$_[0]};
	my $file = $_[1];
	
	open (REP, ">$file") or die("Could not create report file. $!");;
	
	my @corpus = qw(metro probi bosque);
	my @col = qw(target tp fp);
	
	my @expNames = sort keys %table;
	
	# remove baseline and add it to the begin 
	my $removedBaseline = 0;
	for(my $i = 0; $i < @expNames; $i++) {
		if($expNames[$i] eq 'baseline') {
			splice(@expNames, $i,1);
			$removedBaseline = 1;
		}
	}
	if($removedBaseline) {
		unshift(@expNames, 'baseline')
	}
	
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

sub printDetailedReport {
	my %table = %{$_[0]};
	my @order = @{$_[1]};
	my $file = $_[2];
	
	open (REP, ">$file") or die("Could not create report file. $!");;
	
	my @corpus = qw(metro probi bosque);
	my @col = qw(target tp fp);
	my @signs = qw(+ -);
	
	my @expNames = @order;
	
	# remove baseline and add it to the begin 
	my $removedBaseline = 0;
	for(my $i = 0; $i < @expNames; $i++) {
		if($expNames[$i] eq 'baseline') {
			splice(@expNames, $i,1);
			$removedBaseline = 1;
		}
	}
	
	my @hNames = generateHumanNames(\@expNames);
	
	if($removedBaseline) {
		unshift(@expNames, 'baseline');
		unshift(@hNames, 'BASELINE');
	}
	
	
	my %translation;
	for(my $i; $i < @expNames; $i++) {
		$translation{$expNames[$i]} = $hNames[$i];
	}
	
	# combine similar lines...
	# name -> similar names
	my %combined;
	my %repeated;
	for(my $i = 0; $i < @expNames; $i++) {
		my $exp = $expNames[$i];
		if($exp ne 'baseline') {
			if(!$repeated{$exp}) {
				push(@{$combined{$exp}}, $exp);
				for(my $j = $i + 1; $j < @expNames; $j++) {
					my $next = $expNames[$j];
					if(!$repeated{$next} && cmp_deeply($table{$exp}, $table{$next})) {
						$repeated{$next} = 1;
						push(@{$combined{$exp}}, $next);
					}
				}
			}			
		} else {
			push(@{$combined{$exp}}, $exp);
		}
	}
	
	print Dumper(\%combined);

	my $r = 
'\begin{table}[h!]
 	\begin{center}
    	\begin{tabular}{c|r|r|r|r|r|r|r|}
        	\cline{2-8}
        	& \multicolumn{3}{|c|}{Metro} & \multicolumn{3}{|c|}{Probi} & Bosque \\\\ \\hline
        	\multicolumn{1}{|c|}{Experiment} & \multicolumn{1}{|c|}{Target} & \multicolumn{1}{|c|}{TP} & \multicolumn{1}{|c|}{FP} & \multicolumn{1}{|c|}{Target} & \multicolumn{1}{|c|}{TP} & \multicolumn{1}{|c|}{FP} & \multicolumn{1}{|c|}{FP} \\\\ \\hline \\hline
    		';
	my %first;
	for(my $i = 0; $i < @expNames; $i++) {
		my $exp = $expNames[$i];
		if($combined{$exp}) {
			if($expNames[$i] eq 'baseline') {
				$r .= "\\multicolumn{1}{|l|}{$hNames[$i]} ";	
			} else {
				$r .= "\\multicolumn{1}{|l|}{\\multirow{3}{7cm}{";
				my @arr;
				foreach(@{$combined{$exp}}) {
					push(@arr, $translation{$_});
				}
				$r .= join " \\newline ", @arr;
				
				$r .= "}} ";			
			}
	        
			foreach my $c (@corpus) {
				if($c eq 'bosque') {
					my $d = 'fp';
					$r .= "& $table{$exp}{$c}{$d} ";
				} else {
					foreach my $d (@col) {
						if($d eq 'target') {
							if(!defined $first{$c}){
								my $size = ((keys %combined) * 3) - 2;
								foreach(keys %combined) {
									my @arr = @{$combined{$_}};
									if(@arr > 3) {
										$size += @arr - 3;
									}
								}
								
								$r .= "& \\multicolumn{1}{|c|}{\\multirow{$size}{*}{$table{$exp}{$c}{$d}}} ";
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
			if($expNames[$i] ne 'baseline') {
				$r .= "\\\\ \n";
				foreach my $sign(@signs) {
					$r .= "    		\\multicolumn{1}{|c|}{} ";
					foreach my $c (@corpus) {
						my $c = $c . "-c";
						if($c eq 'bosque-c') {
							my $d = 'fp';
							if($table{$exp}{$c}{$d}{$sign} > 0) {
								$r .= "& \\textit{$sign$table{$exp}{$c}{$d}{$sign}} ";	
							} else {
								$r .= "& ";							
							}
						} else {
							foreach my $d (@col) {
								if($d eq 'target') {
									$r .= "& ";	
								} else {
									if($table{$exp}{$c}{$d}{$sign} > 0) {
										$r .= "& \\textit{$sign$table{$exp}{$c}{$d}{$sign}} ";	
									} else {
										$r .= "& ";
									}								
								}
							}
						}
					}
					if($sign ne $signs[@signs-1]) {
						$r .= "\\\\ \n";
					}		
				}
			}
			$r .= "\\\\ \n";
	
			if($expNames[$i] ne 'baseline') {
				for(my $i = 3; $i < @{$combined{$exp}}; $i++) {
					$r .= "    		\\multicolumn{1}{|c|}{} & & & & & & & \\\\ \n";
				}
			}
			
			if($i < (keys %combined)) {
				$r .= "    		\\cline{1-1} \\cline{3-4} \\cline{6-8} \n    		";	
			} else {
				$r .= "    		\\hline \n    		";
			}
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
	
	$name =~ s/SENT_//g;
	$name =~ s/TOK_//g;
	
	$name =~ s/_//g;
	$name =~ s/,/./g;
	$name =~ s/-/./g;
	$name =~ s/GP //g;
	$name =~ s/SENT //g;
	$name =~ s/TOK //g;
	$name =~ s/POS //g;
	$name =~ s/FEAT //g;
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

my %data;
my @forder;


push(@forder, 'sent-cf-maxent-sd_factory,sd_abb-gp-4');
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'metro'}{'target'} = 333;
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'metro'}{'tp'} = 10;
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'metro'}{'fp'} = 100;
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'tp'}{'-'} = 10;
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'tp'}{'+'} = 10;
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'fp'}{'-'} = 10;
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'fp'}{'+'} = 10;


$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'probi'}{'target'} = 333;
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'probi'}{'tp'} = 10;
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'probi'}{'fp'} = 100;
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'tp'}{'-'} = 10;
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'tp'}{'+'} = 10;
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'fp'}{'-'} = 10;
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'fp'}{'+'} = 10;

$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'bosque'}{'target'} = 333;
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'bosque'}{'tp'} = 10;
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'bosque'}{'fp'} = 100;
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'tp'}{'-'} = 10;
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'tp'}{'+'} = 10;
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'fp'}{'-'} = 10;
$data{'sent-cf-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'fp'}{'+'} = 10;


push(@forder, 'sent-vcf-maxent-sd_factory,sd_abb-gp-4');
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'metro'}{'target'} = 333;
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'metro'}{'tp'} = 10;
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'metro'}{'fp'} = 100;
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'tp'}{'-'} = 10;
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'tp'}{'+'} = 10;
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'fp'}{'-'} = 10;
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'fp'}{'+'} = 10;


$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'probi'}{'target'} = 333;
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'probi'}{'tp'} = 10;
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'probi'}{'fp'} = 100;
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'tp'}{'-'} = 10;
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'tp'}{'+'} = 10;
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'fp'}{'-'} = 10;
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'fp'}{'+'} = 10;

$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'bosque'}{'target'} = 333;
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'bosque'}{'tp'} = 10;
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'bosque'}{'fp'} = 100;
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'tp'}{'-'} = 10;
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'tp'}{'+'} = 10;
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'fp'}{'-'} = 10;
$data{'sent-vcf-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'fp'}{'+'} = 10;


push(@forder, 'sent-ama-maxent-sd_factory,sd_abb-gp-4');
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'metro'}{'target'} = 333;
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'metro'}{'tp'} = 10;
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'metro'}{'fp'} = 100;
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'tp'}{'-'} = 10;
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'tp'}{'+'} = 10;
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'fp'}{'-'} = 10;
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'fp'}{'+'} = 10;


$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'probi'}{'target'} = 333;
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'probi'}{'tp'} = 10;
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'probi'}{'fp'} = 100;
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'tp'}{'-'} = 10;
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'tp'}{'+'} = 10;
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'fp'}{'-'} = 10;
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'fp'}{'+'} = 10;

$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'bosque'}{'target'} = 333;
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'bosque'}{'tp'} = 10;
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'bosque'}{'fp'} = 100;
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'tp'}{'-'} = 10;
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'tp'}{'+'} = 10;
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'fp'}{'-'} = 10;
$data{'sent-ama-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'fp'}{'+'} = 11;

#
push(@forder, 'tok-ama-maxent-sd_factory,sd_abb-gp-4');
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'metro'}{'target'} = 333;
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'metro'}{'tp'} = 10;
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'metro'}{'fp'} = 100;
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'tp'}{'-'} = 10;
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'tp'}{'+'} = 10;
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'fp'}{'-'} = 10;
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'fp'}{'+'} = 10;


$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'probi'}{'target'} = 333;
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'probi'}{'tp'} = 10;
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'probi'}{'fp'} = 100;
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'tp'}{'-'} = 10;
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'tp'}{'+'} = 10;
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'fp'}{'-'} = 10;
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'fp'}{'+'} = 10;

$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'bosque'}{'target'} = 333;
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'bosque'}{'tp'} = 10;
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'bosque'}{'fp'} = 100;
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'tp'}{'-'} = 10;
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'tp'}{'+'} = 10;
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'fp'}{'-'} = 10;
$data{'tok-ama-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'fp'}{'+'} = 11;
#

#
push(@forder, 'tok-a1-maxent-sd_factory,sd_abb-gp-4');
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'metro'}{'target'} = 333;
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'metro'}{'tp'} = 10;
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'metro'}{'fp'} = 100;
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'tp'}{'-'} = 10;
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'tp'}{'+'} = 10;
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'fp'}{'-'} = 10;
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'fp'}{'+'} = 10;


$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'probi'}{'target'} = 333;
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'probi'}{'tp'} = 10;
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'probi'}{'fp'} = 100;
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'tp'}{'-'} = 10;
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'tp'}{'+'} = 10;
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'fp'}{'-'} = 10;
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'fp'}{'+'} = 10;

$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'bosque'}{'target'} = 333;
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'bosque'}{'tp'} = 10;
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'bosque'}{'fp'} = 100;
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'tp'}{'-'} = 10;
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'tp'}{'+'} = 10;
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'fp'}{'-'} = 10;
$data{'tok-a1-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'fp'}{'+'} = 11;
#

#
push(@forder, 'tok-a2-maxent-sd_factory,sd_abb-gp-4');
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'metro'}{'target'} = 333;
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'metro'}{'tp'} = 10;
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'metro'}{'fp'} = 100;
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'tp'}{'-'} = 10;
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'tp'}{'+'} = 10;
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'fp'}{'-'} = 10;
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'metro-c'}{'fp'}{'+'} = 10;


$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'probi'}{'target'} = 333;
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'probi'}{'tp'} = 10;
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'probi'}{'fp'} = 100;
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'tp'}{'-'} = 10;
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'tp'}{'+'} = 10;
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'fp'}{'-'} = 10;
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'probi-c'}{'fp'}{'+'} = 10;

$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'bosque'}{'target'} = 333;
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'bosque'}{'tp'} = 10;
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'bosque'}{'fp'} = 100;
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'tp'}{'-'} = 10;
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'tp'}{'+'} = 10;
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'fp'}{'-'} = 10;
$data{'tok-a2-maxent-sd_factory,sd_abb-gp-4'}{'bosque-c'}{'fp'}{'+'} = 11;
#

push(@forder, 'baseline');
$data{'baseline'}{'metro'}{'target'} = 333;
$data{'baseline'}{'metro'}{'tp'} = 10;
$data{'baseline'}{'metro'}{'fp'} = 100;
$data{'baseline'}{'metro-c'}{'tp'}{'-'} = 10;
$data{'baseline'}{'metro-c'}{'tp'}{'+'} = 10;
$data{'baseline'}{'metro-c'}{'fp'}{'-'} = 10;
$data{'baseline'}{'metro-c'}{'fp'}{'+'} = 10;


$data{'baseline'}{'probi'}{'target'} = 333;
$data{'baseline'}{'probi'}{'tp'} = 10;
$data{'baseline'}{'probi'}{'fp'} = 100;
$data{'baseline'}{'probi-c'}{'tp'}{'-'} = 10;
$data{'baseline'}{'probi-c'}{'tp'}{'+'} = 10;
$data{'baseline'}{'probi-c'}{'fp'}{'-'} = 10;
$data{'baseline'}{'probi-c'}{'fp'}{'+'} = 10;

$data{'baseline'}{'bosque'}{'target'} = 333;
$data{'baseline'}{'bosque'}{'tp'} = 10;
$data{'baseline'}{'bosque'}{'fp'} = 100;
$data{'baseline'}{'bosque-c'}{'tp'}{'-'} = 10;
$data{'baseline'}{'bosque-c'}{'tp'}{'+'} = 10;
$data{'baseline'}{'bosque-c'}{'fp'}{'-'} = 10;
$data{'baseline'}{'bosque-c'}{'fp'}{'+'} = 10;

# printDetailedReport(\%data, \@forder, "table_teste.txt");


$ENV{'REPO_ROOT'} = tempdir;

init();
cpe::init();

my @order = loadOrderFromFile($ARGV[0]);

print "order: \n" . Dumper(\%order);

my $reportsPath = 'eval';
my %eval;

if(0) {
	# use this to evaluate baseline x new modules
	if(@ARGV > 1 && $ARGV[1] ne '') {
		createBaseline($ARGV[1]);
	}
	my %baselineEval = evaluateBaseline('baseline');
	my %fromfileEval = evaluateListFromFile($ARGV[0], 0);
	%eval = (%baselineEval, %fromfileEval);
} elsif(1) {
	my %baselineEval;
	# use this to evaluate baseline x new modules with new tagset
	if(@ARGV > 1 && $ARGV[1] ne '') {
		createBaseline($ARGV[1]);
	}
	if(0) {
		%baselineEval = evaluateBaseline('baseline');
	} else {
		%baselineEval = cpe::evaluateCogroo3($reportsPath, 'baseline');
	}
	my %fromfileEval = evaluateListFromFile($ARGV[0], 1);
	%eval = (%baselineEval, %fromfileEval);
} else {
	# use this to evaluate cogroo3 x new baseline (code changes)
	
	my %baselineEval = cpe::evaluateCogroo3($reportsPath, 'baseline');
	my %fromfileEval = evaluateBaseline('new');
	%eval = (%baselineEval, %fromfileEval);
}

print Dumper(\%eval);

printReport(\%eval, "$reportsPath/".$ARGV[0].'.txt');
printDetailedReport(\%eval, \@order, "$reportsPath/".$ARGV[0].'_detailed.txt');