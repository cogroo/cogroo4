#!/usr/bin/perl

use lib "scripts";
require eval_unit;

use File::Path qw(make_path remove_tree);
use Data::Dumper;

# criar função q varia o corte de 0 até 320

# criar função q varia o corte de x até y

# criar função que imprime a tabela

sub init() {
	use Getopt::Std;
	my $opt_string = 'hfet:a:c:o:i:p:d:v:s:b:';
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
     -o <opt>  : training options (separated by ,)
     -p <num>  : num of threads (8 is default)
     -v <vmode>: how to change the cutoff: ap or gp
     -b <bound>: comma separated boundaries of cutoff. ie: 10,30
     -s <stp>  : cutoff step
    example: $0 -h
EOF
	exit;
}

sub evaluate() {
	
	# create the basic command
	# it is the same, but the -c is missing 

	# create the folder
	if($opt{t} eq '') {
		die "the tool name is missing";
	} 
	
	if($opt{v} ne 'ap' && $opt{v} ne 'gp') {
		die "the -v arg is missing or invalid \n";
	}
	
	if($opt{v} eq 'ap' && $opt{s} <= 0 ) {
		die "the step should be positive \n";
	}
	
	my ($begin,$end, $step);
	($begin,$end) = split(/,/, $opt{b});
	$step = $opt{s};
	if($opt{v} eq 'ap' ) {
		if(!($begin >= 0 && $end > $begin )) {
			die "invalid boundaries \n";
		}
		if($step <= 0) {
			die "invalid step \n";
		}
	}
	
	
	my $folder = "eval/". createBaseReportName();
	
	if (-e $folder) {
 		print "Folder exists, will use it: $folder";
 	}  else {
		print "will create eval folder: $folder \n";
		make_path($folder) || die "Unable to create directory <$!>\n";
 	}
	
	open (LOG, ">$folder/log.txt");
	open (TABLE, ">$folder/table.txt");
	
	# print the command to a log file inside the folder
	
	print LOG "Option hash in eval.pl\n";
	print LOG Dumper( \%opt );
	print LOG "\n";

	$opt{e} = 1;
	
	my @cutoffArr;
	if($opt{v} eq 'gp') {
		push(@cutoffArr, 0);
		for(my $i = 2; $i <= 512; $i *= 2) {
			push(@cutoffArr, $i);	
		}
	} else {
		die "not implemented \n";
	}
	
	my @keys;
	
	foreach my $cut (@cutoffArr) {
		$opt{c} = $cut;
		
		my %result = eval_unit::exec(\%opt, \%extraOpt, *LOG);
	
		if(@keys == 0) {
			@keys = keys %result;
			my $hLine = join "\t", @keys;
			print TABLE $hLine . "\n";
		}

		my @line;	
		foreach my $k (@keys) {
			push(@line, $result{$k});
		}
		my $l = join "\t", @line;
		print TABLE $l . "\n";
	}
	
	close LOG;
	close TABLE;
}

sub createBaseReportName() {
	my $o;
	if($opt{o}) {
		$o = $opt{o};
		# $o =~ s/,/:/g;
	} else {
		$o = 'NONE';
	}
		# sent_MAXENT_DIC,FAC_pa_120228-070420
	my $name = $opt{t} . '/' . $opt{a} . '-' . $o . '-' . $opt{v};# . '-' . getTime();
}

sub getTime() {
	my @t = localtime(time);
	my $ret;
	$t[5] = $t[5] - 100;
	$t[4] = $t[4] + 1;
	for(my $i = 5; $i >= 3; $i--) {
		if($t[$i] < 10) {
			$ret .= "0$t[$i]";
		} else {
			$ret .= $t[$i];
		}
	}
	$ret .= '-';
	for(my $i = 0; $i < 3; $i++) {
		if($t[$i] < 10) {
			$ret .= "0$t[$i]";
		} else {
			$ret .= $t[$i];
		}
	}
	
	return $ret;
}

init();

evaluate();
