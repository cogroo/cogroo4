#!/usr/bin/perl

# This script will install required Maven projects and UIMA pears 
# to execute grammar checking evaluation. Also has methods to execute the evaluation
# Everything is cleared before each test to make sure we are using the right models and code

package cpe;

use Data::Dumper;
use File::Path qw(make_path);
use strict 'vars';

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

sub install {
	my $pom = shift;
	printToLog("Will install pom: $pom\n");

	my $command = "mvn -e -q -f $pom install -Dmaven.test.skip";

	my @res = `$command 2>&1`;

	my $err = 0;
	foreach my $line (@res) {
		if($line =~ m/\[ERROR\] For more information about the errors/) {
			$err = 1;
		}
	}
	
	if($err) {
		my $log = "=== Failed to execute command ===\n\n";
		$log .= join "\t", @res;
		printToLog($log . "\n\n");
		
		die "Failed to execute command";
	}
}

sub installPearByPath {
	my $pear = shift;
	printToLog("Will install pear: $pear\n");
	
	my $command = 'mvn -f ../UIMAAutomation/pom.xml -e -q clean install exec:java -Dmaven.test.skip "-Dexec.mainClass=uima.Installer" "-Dexec.args=' . $pear . ' ' . $ENV{'REPO_ROOT'} . '"';

	printToLog("command: $command\n");
	
	my @res = `$command 2>&1`;

	my $err = 0;
	foreach my $line (@res) {
		if($line =~ m/Verification of \w+ failed/) {
			$err = 1;
		}
		if($line =~ m/file not found/) {
			$err = 1;
		}
	}
	
	if($err) {
		my $log = "=== Failed to execute command ===\n\n";
		$log .= join "\t", @res;
		printToLog($log . "\n\n");
		
		die "Failed to execute command";
	}
}

sub installPearByName {
	my $tool = shift;
	printToLog("Will install pear: $tool\n");
	
	my $pear = "../UIMAWrappers/UIMA$tool/target/UIMA$tool.pear";
	
	installPearByPath($pear);
}

sub prepareCPE {
	# obter path corpus
	my $corpus = shift;
	# obter path casProcessor
	my $cas = shift;
	# obter path report
	my $report = shift;
	my $corpusName = shift;
	
	make_path($report);
	
	# abrir CPE_AD.filter, processar e salvar em target
	my %replace = ( 
		'corpus' =>	$ENV{'CORPUS_ROOT'} . '/' . $corpus, 
		'gcPear' => $ENV{'REPO_ROOT'} . "/$cas/$cas\_pear.xml",
		'report' => $report,
		'corpusName' => $corpusName );
		
	# abrir Probi.filter e salvar em target
	filterFile("../GramEval/desc/ProbiCollectionReaderDescriptor.filter.xml", "../GramEval/desc/ProbiCollectionReaderDescriptor.svnignore.xml", \%replace);
	filterFile("../GramEval/desc/CPE_Probi.filter.xml", "../GramEval/desc/CPE_Probi.svnignore.xml", \%replace);
	filterFile("../GramEval/desc/CPE_AD.filter.xml", "../GramEval/desc/CPE_AD.svnignore.xml", \%replace);
}

sub executeCPE {
	
	my $name = shift;
	
	printToLog("Will execute CPE: $name \n");
	
	my $desc = "../GramEval/desc/$name.svnignore.xml";
	
	my $command = 'mvn -f ../GramEval/pom.xml -e -q exec:java -Dmaven.test.skip -DsystemProperties="file.encoding=UTF-8" "-Dexec.mainClass=cogroo.uima.SimpleRunCPE" "-Dexec.args=' . $desc . '"';

	my @res = `$command 2>&1`;

	my $err = 0;
	foreach my $line (@res) {
		if($line =~ m/An exception occured while executing/) {
			$err = 1;
		}
	}
	
	if($err) {
		my $log = "=== Failed to execute command ===\n\n";
		$log .= join "\t", @res;
		printToLog($log . "\n\n");
		
		die "Failed to execute command";
	}
	
}

sub evaluate {
	my $gc = shift;
	my $reportPath = shift;
	# will prepare CPE files and execute the evaluation
	
	my %res;

	my $report =  $reportPath . '/probi';
	prepareCPE("Probi_From_MDB.txt", $gc, $report, 'PROBI');
	executeCPE("CPE_Probi");
	$res{'probi'} = readCPEResults("$report/PROBI-FMeasure.txt");
	
	$report =  $reportPath . '/metro';
	prepareCPE("Metro", $gc, $report, 'Metro');
	executeCPE("CPE_AD");
	$res{'metro'} = readCPEResults("$report/Metro-FMeasure.txt");
	
	$report =  $reportPath . '/bosque';
	prepareCPE("Bosque", $gc, $report, 'Bosque');
	executeCPE("CPE_AD");
	$res{'bosque'} = readCPEResults("$report/Bosque-FMeasure.txt");
	
	return \%res;
}

sub configureMultiProperties {
	
	my $useModels = shift;
	
	# we check the avalilable models and configure multi.properties to use it
	my $filterFile = '../BaselineCogrooAE/src/main/resources/cogroo/multi.filter.properties';
	my $target = '../BaselineCogrooAE/src/main/resources/cogroo/multi.properties';
	
	my $modelRoot = $ENV{'MODEL_ROOT'};
	my %replace = (
		'sent' => 'false',
		'tok' => 'false',
		'pre' => 'false',
		'chunker' => 'false',
		'sp' => 'false',
	);
	
	if($useModels) {
		if (-e "$modelRoot/pt-sent.model") {
	 		$replace{'sent'} = 'true';
	 	}		
	}
	
 	filterFile($filterFile, $target, \%replace);
}

sub readCPEResults {
	my $reportPath = shift;
	
	open REPORT, "$reportPath" or die $!;

	my $buf = <REPORT>;
	my %result;
	
	if($buf =~ m/with (\d+) grammar errors; found: (\d+); correct: (\d+)/) {
		$result{'target'} = $1;
		$result{'selected'} = $2;
		$result{'tp'} = $3;
	} else {
		die "could not extract data from report: $reportPath";
	}
	
	close REPORT;
	
	return \%result;
}

sub filterFile {
	# origem
	my $from = shift;
	# destino
	my $to = shift;
	# dados
	my %replace = %{$_[0]};
	undef $/;
	
	open IN, "$from" or die $!;
	open OUT, ">", "$to" or die $!;
	
	my $buf = <IN>;
	$buf =~ s/\$\{$_\}/$replace{$_}/g for keys %replace;
	print OUT $buf;
	
	close OUT;
	close IN;
}

sub printToLog {
	my $info = shift;
	print $info;
	#if($fhLog) {
	#	print $fhLog $info;
	#}
}

sub evaluateCogroo3 {
	my $evalPath = shift;
	my %res;
	$res{'cogroo3'} = evaluate("Cogroo3AE", "$evalPath/cogroo3");
	return %res;
}

sub evaluateBaseline {
	my $evalPath = shift;
	my %res;
	configureMultiProperties(0);
	install("../BaselineCogrooAE/pom.xml");
	installPearByPath("../BaselineCogrooAE/target/BaselineCogrooAE.pear");
	$res{'baseline'} = evaluate("BaselineCogrooAE", "$evalPath/baseline");
	printToLog Dumper( \%res );
	return %res;
}

sub evaluateUsingModel {
	my $evalPath = shift;
	my $name = shift;
	my %res;
	configureMultiProperties(1);
	install("../BaselineCogrooAE/pom.xml");
	installPearByPath("../BaselineCogrooAE/target/BaselineCogrooAE.pear");
	$res{$name} = evaluate("BaselineCogrooAE", "$evalPath/$name");
	printToLog Dumper( \%res );
	return %res;
}

sub init() {
	checkVars();
	install("../../../cogroo3/pom.xml");
	install("../UIMAAutomation/pom-evaluators.xml");
	installPearByPath("../Cogroo3AE/target/Cogroo3AE.pear");
	
	installPearByName('SentenceDetector');
}
1;
