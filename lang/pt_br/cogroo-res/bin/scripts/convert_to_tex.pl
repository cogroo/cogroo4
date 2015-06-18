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


open IN, $ARGV[0] or die $!;

my @tableCol = qw/cutoff Precision Recall F-Measure eval_time tr_time predicates model_size/;


my $experiment = '';
my %data;
printHeader();
foreach my $line (<IN>) {
	if( $line =~ m/===\s+(.*?)\s+===/) {
		printData(\%data, $experiment) if %data;
		$experiment = $1;
		undef @col;
		undef %data;
	} elsif( $line =~ m/cutoff/ ) {
		@col = split(/\s+/, $line);
	} elsif($line =~ m/^\d/) {
		my @d = split(/\s+/, $line);
		for(my $i = 0; $i < @col; $i++) {
			my $val = $d[$i];
			push(@{$data{$col[$i]}}, $val);
		}
	}
}
printData(\%data, $experiment);

printFooter(); 

close IN;

sub printData {
	
	my %theData = %{$_[0]};
	my $name = processName( $_[1] );
	my $size = @{$theData{'cutoff'}};
	# first line we print the name
	print "\\hline\n";
	print '\multicolumn{' . @tableCol . '}{|c|}{' . "\\textbf\{Experiment = $name\}" . '} \\\\ ';
	print "\\hline\n";
	print "\\hline\n";
	for(my $i = 0; $i < $size; $i++) {
		my @data;
		foreach my $c (@tableCol) {
			my $v = $theData{$c}[$i];
			if($c eq 'Precision' || $c eq 'Recall' || $c eq 'F-Measure') {
				$v = $v * 100;
				$v = sprintf("%.3f", $v);
			}
			if($c eq 'eval_time' || $c eq 'tr_time') {
				$v = sprintf("%.3f", $v);
			}
			push(@data,$v);
		}
		print join(' & ', @data);
		print " \\\\\n";
	}
	print "\\hline\n";
}

sub processName {
	my $name = shift;
	$name =~ s/_//g;
	$name =~ s/,/ /g;
	$name =~ s/-/ /g;
	return $name;
}

sub printHeader {
		print '\begin{center}
 		\begin{longtable}{|r|r|r|r|r|r|r|r|}
			\hline 
			Cutoff & $P$ (\%) & $R$ (\%) & $F_{1}$ (\%) & TT (s) & ET (s) & Preds & M (bytes) \\\\
			\hline
';
}

sub printFooter {
		print '\end{longtable}
\end{center}
';
}