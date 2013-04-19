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


package eval_unit;

use Time::HiRes;
use Data::Dumper;
use strict 'vars';
use Cwd;
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
	MOD_PREFIX => "pt-",
	MOD_SUFIX  => ".model"
};

my %opt;
my %extraOpt;

my $fhLog;

sub getCorpusPath {
	my $name = shift;
	if($name eq 'cf') {
		return CF;
	} elsif($name eq 'cp') {
		return CP;
	} elsif($name eq 'cfcp') {
		return CFCP;
	} elsif($name eq 'vcf') {
		return VCF;
	} elsif($name eq 'ama') {
		return AMA;
	} elsif($name eq 'lit') {
		return LIT;
	} elsif($name eq 'cie') {
		return CIE;
	} else {
		die "invalid corpus name\n";
	}
}

sub printToLog {
	my $info = shift;
	print $info;
	if($fhLog) {
		print $fhLog $info;
	}
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
	printToLog( "Creating params: \n$params\n\n");
	print $pfh $params;
}

my %simpleF = ( 'sent' => 1, 'tok' => 1, 'con' => 1, 'prop' => 1, 'chunk' => 1, 'hf' => 1, 'sp' => 1 );
my %simpleA = ( 'pos' => 1, 'feat' => 1 );

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
	} elsif( $simpleA{$opt{t}}) {
		foreach (@res) {
			if ( !$capture ) {
				$capture = 1 if $_ =~ m/^Accuracy.*/;
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

sub checkError {
	my $err = 0;
	my @res     = @_;
	foreach my $line (@res) {
		if($line =~ m/\[ERROR\] For more information about the errors/) {
			$err = 1;
		}
	}
	
	if($err) {
		my $log = "=== Failed to execute command ===\n\n";
		$log .= join "\t", @res;
		printToLog $log . "\n\n";
		
		die "Failed to execute command";
	}
	#[ERROR] For more information about the errors
}

sub getNumberOfPreds {
	my %out;
	my @res     = @_;
	my $capture = 0;
	my $preds;
		foreach (@res) {
			if ( $_ =~ m/^\s+Number of Predicates:\s+([\d\.\,]+)/ ) {
				$preds = $1;
			}
		}
	
	return $preds;
}

sub get_temp_filename {
	my $fh = File::Temp->new( SUFFIX => '.module', );
	my $filename = $fh->filename;
	chmod 0660, $filename;
	return $filename;
}

sub executeCV {
	my $command = shift;
	if ( $opt{e} ) {
		printToLog "Will execute command: $command\n";

		my $start_time = [ Time::HiRes::gettimeofday() ];
		my @res        = `$command 2>&1`;
		my $diff       = Time::HiRes::tv_interval($start_time);

		checkError(@res);

		my %results = filter(@res);

		$results{"eval_time"} = $diff;

		return %results;
	}
}

sub executeTr {
	my $command = shift;
	my $out     = "# results for $command\n";
	printToLog "Will execute command: $command\n";
	printToLog "At: " . cwd() . "\n";

	my $start_time = [ Time::HiRes::gettimeofday() ];
	
	print $command;
	
	my @res        = `$command 2>&1`;
	my $diff       = Time::HiRes::tv_interval($start_time);

	print @res;
	checkError(@res);
	my %results;
	$results{"tr_time"} = $diff;
	$results{"predicates"} = getNumberOfPreds(@res);

	return %results;
}

sub createCommand {
	my $cliTool = shift;
	my $execArgs = shift;
	my $properties = shift;

	$ENV{'MAVEN_OPTS'} = "-Xms512m -Xmx4000m -XX:PermSize=256m";
	
	my $command = 'mvn -e -q exec:java "-Dexec.mainClass=';
	
	if($cliTool eq "opennlp") {
		$command .= 'opennlp.tools.cmdline.CLI';
	}  else {
		$command .= 'org.cogroo.cmdline.CLI';
	}
	
	$command .= '" "-Dexec.args=' . $execArgs . '"';
	
	if(length($properties) > 0) {
		$command .= ' '. $properties;
	}
	
	return $command;
}


sub exec() {
	%opt = %{$_[0]};
	%extraOpt = %{$_[1]};
	$fhLog = $_[2];

	# Create the params file
	my ( $paramsFileHandler, $paramsFileName ) = tempfile();
	createParams($paramsFileHandler);
	
	# Create model file name
	my $model = '';
	if ( $opt{f} ) {
		$model = $ENV{'MODEL_ROOT'} . '/' . MOD_PREFIX . $opt{t} . MOD_SUFIX;
	}
	else {
		$model = get_temp_filename();
	}
	
	# create corpus args
	my $data = getCorpusPath($opt{d});
	die "The parameter -d corpus name is mandatory." if $data eq '';
	
	my $extraOption = '';
	my $extraProperties = '';
	if ( $opt{o} ne "" ) {
		my @tokens = split( /,/, $opt{o} );
		foreach my $token (@tokens) {
			my $val = $extraOpt{$token};
			if($val eq '') {
				die "Could not load extraOpt: $token\n";
			}
			if($val =~ m/-D/) {
				$extraProperties .= $val . ' ';
			} else {
				$extraOption .= $val . ' ';	
			}
		}
	}
	
	printToLog "Extra options: $extraOption \n\n";
	
	my $basicCommand = " -params $paramsFileName -lang pt $extraOption -encoding ";
	my $trCommand = '';
	my $cvCommand = '';
	
	if ( $opt{t} eq 'sent' ) {
		$trCommand .=
			createCommand('opennlp', "SentenceDetectorTrainer.ad $basicCommand "
		  . ENCODING
		  . " -data $data -model $model ", $extraProperties);
		$cvCommand .=
		    createCommand('opennlp', "SentenceDetectorCrossValidator.ad $basicCommand "
		  . ENCODING
		  . " -data $data ", $extraProperties);
	}
	
	if ( $opt{t} eq 'tok' ) {
		$trCommand .=
		    createCommand('opennlp', "TokenizerTrainer.ad "
		  . "-detokenizer /Users/wcolen/Documents/wrks/cogroo4/opennlp/opennlp-tools/lang/pt/tokenizer/pt-detokenizer.xml "
		  . "$basicCommand "
		  . ENCODING
		  . " -data $data -model $model ", $extraProperties);
		$cvCommand .=
		    createCommand('opennlp', "TokenizerCrossValidator.ad "
		  . "-detokenizer /Users/wcolen/Documents/wrks/cogroo4/opennlp/opennlp-tools/lang/pt/tokenizer/pt-detokenizer.xml "
		  . "$basicCommand "
		  . ENCODING 
		  . " -data $data ", $extraProperties);
	}
	
	
	
	if ( $opt{t} eq 'con' ) {
		$trCommand .=
		    createCommand('cogroo', " TokenNameFinderTrainer.adcon "
		  . "$basicCommand "
		  . ENCODING
		  . " -data $data -model $model ", $extraProperties);
		$cvCommand .=
		    createCommand('cogroo', " TokenNameFinderCrossValidator.adcon "
		  . "$basicCommand "
		  . ENCODING 
		  . " -data $data ", $extraProperties);
	}
	
	if ( $opt{t} eq 'prop' ) {
		$trCommand .=
		    createCommand('cogroo', " TokenNameFinderTrainer.adexp "
		  . "$basicCommand "
		  . ENCODING
		  . " -tags prop -data $data -model $model ", $extraProperties);
		$cvCommand .=
		    createCommand('cogroo', " TokenNameFinderCrossValidator.adexp "
		  . "$basicCommand "
		  . ENCODING 
		  . " -tags prop -data $data ", $extraProperties);
	}
	
	if ( $opt{t} eq 'pos' ) {
		my $base = "$basicCommand "
		  . ENCODING
		  . " -data $data ";
		$trCommand .=
		    createCommand('cogroo', " POSTaggerTrainer.adex -model $model $base -expandME true", $extraProperties);
		$cvCommand .=
		    createCommand('cogroo', " POSTaggerCrossValidator.adex $base -expandME true", $extraProperties);
	}
	
	if ( $opt{t} eq 'feat' ) {
		my $base = "$basicCommand "
		  . ENCODING
		  . " -data $data ";
		$trCommand .=
		    createCommand('cogroo', " FeaturizerTrainerME.ad -model $model $base", $extraProperties);
		$cvCommand .=
		    createCommand('cogroo', " FeaturizerCrossValidator.ad $base", $extraProperties);
	}
	
	if ( $opt{t} eq 'chunk' ) {
		my $base = "$basicCommand "
		  . ENCODING
		  . " -data $data ";
		$trCommand .=
		    createCommand('cogroo', " Chunker2Trainer.ad2 -model $model $base", $extraProperties);
		$cvCommand .=
		    createCommand('cogroo', " Chunker2CrossValidator.ad2 $base", $extraProperties);
	}
	
	if ( $opt{t} eq 'hf' ) {
		my $base = "$basicCommand "
		  . ENCODING
		  . " -data $data ";
		$trCommand .=
		    createCommand('cogroo', " Chunker2Trainer.adheadfinder -factory org.cogroo.tools.headfinder.HeadFinderFactory -model $model $base", $extraProperties);
		$cvCommand .=
		    createCommand('cogroo', " Chunker2CrossValidator.adheadfinder -factory org.cogroo.tools.headfinder.HeadFinderFactory $base", $extraProperties);
	}
	
	if ( $opt{t} eq 'sp' ) {
		my $base = "$basicCommand "
		  . ENCODING
		  . " -data $data ";
		$trCommand .=
		    createCommand('cogroo', " Chunker2Trainer.adshallowparser -factory org.cogroo.tools.shallowparser.ShallowParserFactory -model $model $base", $extraProperties);
		$cvCommand .=
		    createCommand('cogroo', " Chunker2CrossValidator.adshallowparser -factory org.cogroo.tools.shallowparser.ShallowParserFactory $base", $extraProperties);
	}
	
	my %resCV = executeCV($cvCommand);
	
	my %resTr = executeTr($trCommand);
	my $modelSize = -s $model;
	$resTr{"model_size"} = $modelSize;
	
	my %res = ( %resCV, %resTr );
	
	#close the params file in the end of execution.
	close $paramsFileHandler;
	
	printToLog Dumper( \%res );

	return %res;
}
1;
