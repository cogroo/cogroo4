#!/usr/bin/perl

use Time::HiRes;
use Data::Dumper;
use strict 'vars';
use File::Temp qw/ tempfile tempdir /;

use constant CORPUS_ROOT => $ENV{'CORPUS_ROOT'};

if ( CORPUS_ROOT eq '' ) {
	die "Env var CORPUS_ROOT undefined";
}

use constant {
	CF   => CORPUS_ROOT . "/Bosque/Bosque_CF_8.0.ad.txt",
	CP   => CORPUS_ROOT . "/Bosque_CP_8.0.ad.txt",
	CFCP => CORPUS_ROOT . "/Bosque_CFCP_8.0.ad.txt",
	VCF  => CORPUS_ROOT . "/FlorestaVirgem/FlorestaVirgem_CF_3.0_ad.txt",
	AMA  => CORPUS_ROOT . "/amazonia.ad",
	LIT  => CORPUS_ROOT . "/selva_lit.ad",
	CIE  => CORPUS_ROOT . "/selva_cie.ad",

	ENCODING   => "ISO-8859-1",
	MOD_PREFIX => "model/pt-",
	MOD_SUFIX  => ".model"
};

use vars qw/ %opt /;
my %extraOpt;

sub init() {
	use Getopt::Std;
	my $opt_string = 'hfet:a:c:o:i:p:d:';
	getopts( "$opt_string", \%opt ) or usage();
	usage() if $opt{h};
	
	open CONFIG, "scripts/options.properties" or die $!;
	
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

sub usage() {
	print STDERR << "EOF";
This program does...
	usage: $0 [-hvd] [-f file]
     -h        : this (help) message
     -t <tool> : the tool name (sent, toke etc)
     -d <data> : corpus (CF, CP...)
     -a <alg>  : MAXENT or PERCEPTRON
     -c <val>  : cutoff value
     -e <opt>  : evaluation options
     -o <opt>  : training options (separated by ,)
     -i <id>   : execution id, for logging
     -p <num>  : num of threads (8 is default)
     -f        : create a final model
     -e        : evaluate using Cross Validation
    example: $0 -h
EOF
	exit;
}

sub createParams {
	( my $pfh ) = @_;

	my $threads = $opt{p};
	$threads = 8 if ( $threads eq '' );

	my $algorithm = $opt{a};
	$algorithm = "MAXENT" if ( $algorithm eq '' );

	my $cutoff = $opt{c};
	$cutoff = 5 if ( $cutoff eq '' );

	my $params = "Threads=$threads
Iterations=100
Algorithm=$algorithm
Cutoff=$cutoff";
	print "Creating params: \n$params\n\n";
	print $pfh $params;
}

my %simpleF = ( sent => 1, );

sub filter {
	my %out;
	my @res     = @_;
	my $capture = 0;
	if ( $simpleF{ $opt{t} } ) {
		foreach (@res) {
			if ( !$capture ) {
				$capture = 1 if $_ =~ m/^Precision.*/;
			}
			if ($capture) {
				$_ =~ m/^([^:]+):\s+([\d\.\,]+)/;
				$out{$1} = $2;
			}
		}
	}
	else {
		die "don't know which filter to use for: -t $opt{t}";
	}
	return %out;
}

sub get_temp_filename {
	my $fh = File::Temp->new( SUFFIX => '.module', );
	my $filename = $fh->filename;
	chmod 0660, $filename;
	return $filename;
}

sub executeCV {
	my $command = shift;
	if($opt{e}) {
		print "Will execute command: $command\n";
	
		my $start_time = [ Time::HiRes::gettimeofday() ];
		my @res        = `sh $command 2>&1`;
		my $diff       = Time::HiRes::tv_interval($start_time);
	
		my %results = filter(@res);
	
		$results{"eval_time"} = $diff;
	
		return %results;		
	}
}

sub executeTr {
	my $command = shift;
	my $out     = "# results for $command\n";
	print "Will execute command: $command\n";

	my $start_time = [ Time::HiRes::gettimeofday() ];
	my @res        = `sh $command 2>&1`;
	my $diff       = Time::HiRes::tv_interval($start_time);

	print @res;
	my %results;
	$results{"tr_time"} = $diff;

	return %results;
}

init();

# Create the params file
my ( $paramsFileHandler, $paramsFileName ) = tempfile();
createParams($paramsFileHandler);

# Create model file name
my $model = '';
if($opt{f}) {
	$model = MOD_PREFIX . $opt{t} . MOD_SUFIX;
} else {
	$model = get_temp_filename();	
}

# create corpus args
my $data = $opt{d}->();
die "The parameter -d corpus name is mandatory." if $data eq '';

my $extraOption = '';
if ( $opt{o} ne "" ) {
	my @tokens = split(/,/, $opt{o});
	foreach my $token (@tokens) {
    	$extraOption .= $extraOpt{$token} . ' ';
	}
}

my $trCommand = '';
my $cvCommand = '';

if ( $opt{t} eq 'sent' ) {
	$trCommand .=
	    "scripts/opennlp SentenceDetectorTrainer.ad -lang pt -encoding "
	  . ENCODING
	  . " -data $data -model $model $extraOption";
	$cvCommand .=
	    "scripts/opennlp SentenceDetectorCrossValidator.ad -lang pt -encoding "
	  . ENCODING
	  . " -data $data $extraOption";
}

my %resCV = executeCV($cvCommand);

my %resTr     = executeTr($trCommand);
my $modelSize = -s $model;
$resTr{"model_size"} = $modelSize;

my %res = ( %resCV, %resTr );

#close the params file in the end of execution.
close $paramsFileHandler;

print Dumper( \%res );
