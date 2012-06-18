#!/usr/bin/perl

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
use File::Path qw(make_path);
use Storable qw(freeze thaw);
use File::Copy::Recursive qw(dircopy);
my $common = "$Bin/../../cogroo-common/scripts"; 

use lib "$Bin/../../cogroo-common/scripts";
require eval_unit;
 
require cpe;

my %extraOpt;

sub init() {
	
	checkVars();

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
	
	# create a temp folder and set the envvar REPO there.
	
	my $dir = getcwd;
	chdir('../../cogroo-common');
	# this will build the model
	eval_unit::exec(\%opt, \%extraOpt);
	chdir($dir);
	
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



if(@ARGV != 1) {
	die "pass in an configuration argument\n";
}
print "Will train with argument " . $ARGV[0] . "\n";

init();

train($ARGV[0]);

print "will also install pears...\n";
cpe::installRequiredPears();