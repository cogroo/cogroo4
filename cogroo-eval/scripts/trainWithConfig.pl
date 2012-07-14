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
use Data::Dumper;
use File::Temp qw/ tempfile tempdir /;
use Cwd;
use File::Path qw(make_path);
use Storable qw(freeze thaw);
use File::Copy::Recursive qw(dircopy);
my $common = "$Bin/../../lang/pt_br/cogroo-res/scripts"; 

use lib "$Bin/../../lang/pt_br/cogroo-res/scripts";
require eval_unit;
 
require cpe;

my %extraOpt;

my $evaluate = 0;

sub init() {
	
	checkVars();

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
	
	#cpe::install("../../cogroo/pom.xml");
}

sub checkVars {
	
	if($ENV{'MODEL_ROOT'} eq '') {
		die "envvar MODEL_ROOT required by UIMA pears";
	}
	
	if($ENV{'UIMA_HOME'} eq '') {
		die "envvar UIMA_HOME required by CPE";
	}
	
	if($ENV{'CORPUS_ROOT'} eq '') {
		die "envvar CORPUS_ROOT required by Evaluators";
	}
	
	if($ENV{'REPO_ROOT'} eq '') {
		die "envvar REPO_ROOT required by Evaluators";
	}
	
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



sub train {
	my $conf = shift;
	my %opt = parseConfig($conf);
	# faltam: c, p, f 
	$opt{'p'} = 8;
	$opt{'f'} = 1;
	if($evaluate) {
		$opt{'e'} = 1;
	}
	
	
	# create a temp folder and set the envvar REPO there.
	
	my $dir = getcwd;
	chdir('../../lang/pt_br/cogroo-res');
	# this will build the model
	my %result = eval_unit::exec(\%opt, \%extraOpt);
	chdir($dir);
	return %result; 
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

sub trainListFromFile {
	my $file = shift;
	my $log = shift;

	my $output = '';
	
	
	open (CONF, $file)  or die("Could not open list file. $!");
	
	my %results;

	while (my $line = <CONF>) {
    	$line =~ s/^\s+|\s+$//g;
    	my @confs = split(/\n/,$line);
    	foreach my $c (@confs) {
    	    $output .= "### $c \n";
    	    print "will train $c \n";
			my %res = train($c);
			my @keys = keys %res;
			
			foreach my $k (@keys) {
				if( $k ne '' ) {
					$output .= " * $k: " . $res{$k} . "  \n";
				}
			}
			$output .= "\n";
    	}
	}
	
	if($log ne '') {
		open (LOG, ">" . $log);
		print LOG $output;
		close LOG;
	}
	close CONF;
}



if(@ARGV < 1) {
	die "pass in a configuration file and 1 to create and install the UIMA pears, and a report file. \n";
}
print "Will train with argument " . $ARGV[0] . "\n";

init();


if(@ARGV == 3) {
	$report = $ARGV[2];
	$evaluate = 1;
}

trainListFromFile($ARGV[0], $report);

if($ARGV[1]) {
	print "will also install pears...\n";
	cpe::installRequiredPears();
}