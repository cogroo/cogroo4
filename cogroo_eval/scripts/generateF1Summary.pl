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

use File::Path qw(make_path);
use Cwd 'abs_path';
use Storable qw(freeze thaw);

die "2 arguments required." if(@ARGV != 2);

open IN, $ARGV[0] or die $!;
open OUT, ">$ARGV[1]" or die $!;

# in is a tex file with a fixed format...
# lets create the data structure to print..

my %experiments;
my @experimentsNames;

my @corpus = ("Metro","Probi");

#search for the baseline, which also includes the target values

foreach my $line (<IN>) {
	if( $line =~ m/BASELINE.*?\*\}\{(\d+).*?&\s*(\d+)\s*&\s*(\d+).*?\*\}\{(\d+).*?&\s*(\d+)\s*&\s*(\d+)\s*/) {
		# 
		$experiments{'Metro'}{'BASELINE'}{'target'} = $1;
		$experiments{'Metro'}{'BASELINE'}{'tp'} = $2;
		$experiments{'Metro'}{'BASELINE'}{'fp'} = $3;
		
		&includeF($experiments{'Metro'}{'BASELINE'}{'target'}, ($experiments{'Metro'}{'BASELINE'}));
		
		$experiments{'Probi'}{'BASELINE'}{'target'} = $4;
		$experiments{'Probi'}{'BASELINE'}{'tp'} = $5;
		$experiments{'Probi'}{'BASELINE'}{'fp'} = $6;
		
		&includeF($experiments{'Probi'}{'BASELINE'}{'target'}, ($experiments{'Probi'}{'BASELINE'}));
		
		push(@experimentsNames, 'BASELINE');
	} elsif($line =~ /cm\}\{(.*?)\}\}\s*&\s*&\s*(\d+)\s*&\s*(\d+)\s*&\s*&\s*(\d+)\s*&\s*(\d+)/) {
		$experiments{'Metro'}{$1}{'tp'} = $2;
		$experiments{'Metro'}{$1}{'fp'} = $3;
		
		&includeF($experiments{'Metro'}{'BASELINE'}{'target'}, ($experiments{'Metro'}{$1}));
		
		$experiments{'Probi'}{$1}{'tp'} = $4;
		$experiments{'Probi'}{$1}{'fp'} = $5;
		
		&includeF($experiments{'Probi'}{'BASELINE'}{'target'}, ($experiments{'Probi'}{$1}));
		
		push(@experimentsNames, $1);
	}
}

# now we should have it all... lets print it

my $header = 
'\begin{table}[h!]
	\begin{center}
    \begin{tabular}{|l|r|r|r|}
        \hline
        \multicolumn{1}{|c|}{Experiment} & \multicolumn{1}{|c|}{Precision} & \multicolumn{1}{|c|}{Recall} & \multicolumn{1}{|c|}{$F_1$}    \\\\ \hline \hline
';

foreach my $c (@corpus) {
	print OUT "\% Summary for $c \n\n";
	
	print OUT $header;
	
	# find maximun
	my $max_p = max($experiments{$c}, 'p');
	my $max_r = max($experiments{$c}, 'r');
	my $max_f = max($experiments{$c}, 'f');
	
	foreach my $e (@experimentsNames) {
		my $p_str = tostr($experiments{$c}{$e}{'p'}, $max_p);
		my $r_str = tostr($experiments{$c}{$e}{'r'}, $max_r);
		my $f_str = tostr($experiments{$c}{$e}{'f'}, $max_f);
		
		print OUT "        $e & $p_str & $r_str & $f_str \\\\ \\hline \n";
	}
	
	my $footer = "
       \\hline
    \\end{tabular}
    \\caption{Summary of the grammar checker evaluation against $c corpus. The maximun value for precision, recall and $F_1$ are in bold.}
    \\label{tb:todo}
	\\end{center}
\\end{table}\n\n\n";

	print OUT $footer;
}

close OUT;



sub includeF {
	my $target = shift;
	my $tableref = shift;
	
	my $tp = $tableref->{'tp'};
	my $fp = $tableref->{'fp'};
	
	my $p;
	if($tp + $fp != 0) {
		$p = $tp / ($tp + $fp);
	} else {
		$p = 0;
	}

	my $r;
	if($target != 0) {
		$r = $tp / ($target);	
	} else {
		$r = 0;
	}
	 
	my $f;
	if($p + $r != 0) {
		$f = 2*$p*$r/($p+$r);	
	} else {
		$f = 0;
	}
	
	
	$tableref->{'p'} = $p;
	$tableref->{'r'} = $r;
	$tableref->{'f'} = $f;
}

sub max {
	my %table = %{$_[0]};
	my $key = $_[1];
	
	my $max = -1.0;
	
	foreach $exp (keys %table) {
		my $v = $table{$exp}{$key};
		if($v > $max) {
			$max = $v;
		}
	}
	
	return $max;
}

sub tostr {
	my $v = shift;
	my $max = shift;
	
	if($v eq $max) {
		return sprintf("\\textbf{%.2f}", $v*100);
	}
	return sprintf("%.2f", $v*100);
}
