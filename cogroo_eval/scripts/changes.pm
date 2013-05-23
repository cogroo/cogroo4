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


package changes;

use strict 'vars';
use Data::Dumper;

# conta FP, FN e TP de um arquivo

sub trim($)
{
	my $string = shift;
	$string =~ s/^\s+//;
	$string =~ s/\s+$//;
	return $string;
}

sub count {
	my $file = shift;
	my %data;
	open REPORT, '<:encoding(UTF-8)', $file or die $!;
	foreach my $line (<REPORT>) {
		my @lines = split(/\n/,$line);
    	foreach my $l (@lines) {
    		if($l =~ m/^([FPNT]{2})\s+(\S+)\s+.*/) {
				my $type = $1;
				my $id = $2;
				# ${$data{$type}}{$id} = trim($l);
				push(@{${$data{$type}}{$id}}, trim($l));			
			}
    	}
	}
	close REPORT;
	
	return %data;
}

sub my_sort {
	$a =~ m/(\d+)-(\d+)/;
	my $aDoc = int($1);
	my $aPara = int($2);
	
	$b =~ m/(\d+)-(\d+)/;
	my $bDoc = int($1);
	my $bPara = int($2);
	
	if($aDoc ne $bDoc) {
		return $aDoc <=> $bDoc;
	}
	
	return $aPara <=> $bPara;
}

sub getMissing {
	my %ref = %{$_[0]};
	my %pred = %{$_[1]};
	
	my @missingInPred;
	foreach my $k (keys %ref) {
		if($k =~ m/CF438-5/) {
			print "aqui";
		}
		if(!exists $pred{$k}) {
			my $count = 0;
			foreach (@{$ref{$k}}) {
				push(@missingInPred, $k . ' ' . $count);
				$count++;					
			}
		} elsif(@{$pred{$k}} ne @{$ref{$k}}) {
			my $count = 0;
			foreach my $r (@{$ref{$k}}) {
				my $contains = 0;
				foreach my $p (@{$pred{$k}}) {
					if($r eq $p) {
						$contains = 1;
					}
				}
				if(!$contains) {
					push(@missingInPred, $k . ' ' . $count);
				}
				$count++;			
			}
		}
	}
	
	return @missingInPred;
}

sub countEntries {
	my %ref = %{$_[0]};
	my $count = 0;
	foreach my $k (keys %ref) {
		$count += @{$ref{$k}};
	}	
	return $count;
}

sub compare {
	my %ref = %{$_[0]};
	my %pred = %{$_[1]};
	my $r;
	
	my @missingInPred = getMissing(\%ref, \%pred);
	
	my @missingInRef = getMissing(\%pred, \%ref);
	#my $dif = keys( %ref ) - keys( %pred );
	my $mPredCount = @missingInPred;
	my $mRefCount = @missingInRef;
	
	${$_[2]}{'-'} = $mPredCount;
	${$_[2]}{'+'} = $mRefCount;

	$r .= "In baseline: " . countEntries( %ref ) . " In new: " . countEntries( %pred ) . "\n\n";
	$r .= "There are $mPredCount removed entries: \n\n";
	foreach(sort my_sort @missingInPred) {
		my @part = split('\s', $_);
		$r .= "[baseline] $ref{$part[0]}[$part[1]]\n"; 
	}
	
	$r .= "\nThere are $mRefCount new entries: \n\n";
	foreach(sort my_sort @missingInRef) {
		my @part = split('\s', $_);
		$r .= "[new] $pred{$part[0]}[$part[1]]\n"; 
	}
	$r .= "\n";
	
	return $r;
}

sub compareReport {
	my %ref = %{$_[0]};
	my %pred = %{$_[1]};
	my $r;
	
	my %fp;
	my %fn;
	my %tp;
	
	${$_[2]}{'fp'} = \%fp;
	${$_[2]}{'fn'} = \%fn;
	${$_[2]}{'tp'} = \%tp;
		
	# print the differences
	$r .= header("False Positives");
	$r .= compare($ref{'FP'},$pred{'FP'},\%fp);
	$r .= header("False Negatives");
	$r .= compare($ref{'FN'},$pred{'FN'},\%fn);
	$r .= header("True Positives");
	$r .= compare($ref{'TP'},$pred{'TP'},\%tp);
	
	return $r;
}

sub header {
	my $name = shift;
	my $r .= "*******************************************************************************
  === $name ===
*******************************************************************************\n\n";
	return $r; 
}

sub changes {
	my $ref = shift;
	my $pred = shift;
	if(-e $ref && -e $pred) {
		my $out = shift;
		
		my %baseline = count($ref);
		my %other = count($pred);
		my %summary;
		my $r = compareReport(\%baseline, \%other, \%summary);
		
		open REPORT, '>:encoding(UTF-8)', $out or die $!;
		print REPORT $r;
		close REPORT;
		
		return \%summary;	
	}
}

#my %summary;
#$summary{'teste'} = changes("eval/baseline/bosque/Bosque-Details.txt", "eval/tok.cf.maxent.ACP.gp.2/bosque/Bosque-Details.txt", "report_teste.txt");
 
1;
