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
		$o{'o'} = $tokens[3]; 		
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
	
	$ENV{'MODEL_ROOT'} = tempdir;
	
	
	my $dir = getcwd;
	chdir('../../cogroo-common');
	eval_unit::exec(\%opt, \%extraOpt);
	chdir($dir);
	
	#my %baseline = cpe::evaluateBaseline('eval');
	my %r = cpe::evaluateUsingModel('eval', $conf);
	
	return %r;
}

sub evaluateListFromFile {
	my $file = shift;
	
	open (LIST, $file)  or die("Could not open list file. $!");
	
	my %results;

	foreach my $line (<LIST>) {
    	chomp($line);
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
	
	my @corpus = qw(bosque probi metro);
	my @col = qw(target tp selected);

	my $r = "; Bosque; ; ; Probi; ; ; Metro; ; \n";
	foreach (@corpus) {
		$r .= '; target; tp; selected';
	}
	$r .= "\n";
	
	for my $exp (keys %table) {
        $r .= "$exp";
		foreach my $c (@corpus) {
			foreach my $d (@col) {
				$r .= '; ' . $table{$exp}{$c}{$d};
			}
		}
		$r .= "\n";
    }
	
	print REP $r;
	
	close REP;
}


$ENV{'REPO_ROOT'} = tempdir;

init();
cpe::init();

my $reportsPath = 'eval';

my %eval = (evaluateListFromFile($ARGV[0]), cpe::evaluateBaseline($reportsPath));

print Dumper(\%eval);

printReport(\%eval, "$reportsPath/".$ARGV[0].'.txt');