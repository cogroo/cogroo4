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


# This script will install required Maven projects and UIMA pears 
# to execute grammar checking evaluation. Also has methods to execute the evaluation
# Everything is cleared before each test to make sure we are using the right models and code

package cpeNewTagset;

use FindBin qw($Bin);
use Data::Dumper;
use File::Path qw(make_path);
use strict 'vars';
use Switch;
require changes;


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

	my $command = "mvn -o -e -nsu -q -f $pom install -DskipTests";

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
	
	my $command = 'mvn -o -f ../UIMAAutomation/pom.xml -e -q clean install exec:java -DskipTests "-Dexec.mainClass=uima.Installer" "-Dexec.args=' . $pear . ' ' . $ENV{'REPO_ROOT'} . '" -DUIMA_DATAPATH';

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
	printToLog("Will create pear: $tool\n");
	install("../UIMAWrappers/UIMA$tool/pom.xml");
	
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
	my $encoding = shift;
	
	make_path($report);
	
	# abrir CPE_AD.filter, processar e salvar em target
	my %replace = ( 
		'corpus' =>	$ENV{'CORPUS_ROOT'} . '/' . $corpus, 
		'gcPear' => $ENV{'REPO_ROOT'} . "/$cas/$cas\_pear.xml",
		'report' => $report,
		'corpusName' => $corpusName,
		'encoding' => $encoding );
		
	# abrir Probi.filter e salvar em target
	filterFile("../GramEval/desc/ProbiCollectionReaderDescriptor.filter.xml", "../GramEval/desc/ProbiCollectionReaderDescriptor.svnignore.xml", \%replace);
	filterFile("../GramEval/desc/CPE_Probi.filter.xml", "../GramEval/desc/CPE_Probi.svnignore.xml", \%replace);
	filterFile("../GramEval/desc/CPE_AD.filter.xml", "../GramEval/desc/CPE_AD.svnignore.xml", \%replace);
}

sub executeCPE {
	
	my $name = shift;
	
	printToLog("Will execute CPE: $name \n");
	
	my $desc = "../GramEval/desc/$name.svnignore.xml";
	
	my $command = 'mvn -o -f ../GramEval/pom.xml -e -q exec:java -DskipTests -DsystemProperties="file.encoding=UTF-8" "-Dexec.mainClass=cogroo.uima.SimpleRunCPE" "-Dexec.args=' . $desc . '"';

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

sub generateCogrooReportNew {
	my $data = shift;
	
	if (-e $data) {
		my $report = shift;	
		my $path = "../NewTagsetBaselineCogrooAE/target/cogroo";
		configureMultiProperties(1);
			
		my $desc = "$path $data $report";
			
		printToLog("Will process report using MultiCogroo (new tagset) \n");
		
		my $command = 'mvn -o -f ../NewTagsetBaselineCogrooAE/pom.xml -e -q install exec:java -DskipTests -Dexec.classpathScope="compile" "-Dexec.mainClass=cogroo.ProcessReport" "-Dexec.args=' . $desc . '"';
	
		my @res = `$command 2>&1`;
	
		my $err = 0;
		foreach my $line (@res) {
			if($line =~ m/An exception occured while executing/) {
				$err = 1;
			}
		}
		
		if($err) {
			my $log = "=== Failed to execute command ===\n\n";
			$log .= "Command: $command\n\n";
			$log .= join "\t", @res;
			printToLog($log . "\n\n");
			
			die "Failed to execute command";
		}
	}
	
}

sub generateCogrooReport {
	my $data = shift;
	if (-e $data) {
		my $report = shift;	
		my $path = "../BaselineCogrooAE/target/cogroo";
		configureMultiPropertiesBaseline(1);
			
		my $desc = "$path $data $report";
			
		printToLog("Will process report using MultiCogroo \n");
		
		my $command = 'mvn -o -f ../BaselineCogrooAE/pom.xml -e -q install exec:java -DskipTests -Dexec.classpathScope="compile" "-Dexec.mainClass=cogroo.ProcessReport" "-Dexec.args=' . $desc . '"';
	
		my @res = `$command 2>&1`;
	
		my $err = 0;
		foreach my $line (@res) {
			if($line =~ m/An exception occured while executing/) {
				$err = 1;
			}
		}
		
		if($err) {
			my $log = "=== Failed to execute command ===\n\n";
			$log .= "Command: $command\n\n";
	
			$log .= join "\t", @res;
			printToLog($log . "\n\n");
			
			die "Failed to execute command";
		}
	}
	
}

my %baselineVars;

sub evaluate {
	my $gc = shift;
	my $reportPath = shift;
	my $corpus = shift;
	
	my $testProbi = 0;
	my $testMetro = 0;
	my $testBosque = 0;
	my $testComunidade = 0;
	
	if($corpus) {
		$corpus = lc($corpus);
		
		switch ($corpus) {
			case "probi"	{ $testProbi = 1 }
			case "metro"	{ $testMetro = 1 }
			case "bosque"	{ $testBosque = 1 }
			case "comunidade"	{ $testComunidade = 1 }
			else { die "corpus invalido !!" }
		}
	} else {
		$testProbi = 1;
		$testMetro = 1;
		$testBosque = 1;
		$testComunidade = 1;
	}
	
	
	# will prepare CPE files and execute the evaluation
	
	my %res;

	my $report;
	
	if($testProbi) {
		$report =  $reportPath . '/probi';
		prepareCPE("Probi_From_MDB.txt", $gc, $report, 'PROBI', 'UTF-8');
		executeCPE("CPE_Probi");
		$res{'probi'} = readCPEResults("$report/PROBI-FMeasure.txt");
	}
	
	if($testMetro) {
		$report =  $reportPath . '/metro';
		prepareCPE("Metro", $gc, $report, 'Metro', 'UTF-8');
		executeCPE("CPE_AD");
		$res{'metro'} = readCPEResults("$report/Metro-FMeasure.txt");
	}
	
	if($testBosque) {
		$report =  $reportPath . '/bosque';
		prepareCPE("Bosque", $gc, $report, 'Bosque', 'ISO-8859-1');
		executeCPE("CPE_AD");
		$res{'bosque'} = readCPEResults("$report/Bosque-FMeasure.txt");
	}
	
	if($testComunidade) {
		$report =  $reportPath . '/comunidade';
		prepareCPE("Comunidade", $gc, $report, 'Comunidade', 'UTF-8');
		executeCPE("CPE_AD");
		$res{'Comunidade'} = readCPEResults("$report/Comunidade-FMeasure.txt");
	}
	
	if($reportPath !~ m/baseline/) {
		print " will include new cogroo output to report ...\n";
		printVars();
		
		# compare results
		$res{'probi-c'} = changes::changes("eval/baseline/probi/PROBI-Details.txt", "$reportPath/probi/PROBI-Details.txt", "$reportPath/probi/diff.txt");
		$res{'metro-c'} = changes::changes("eval/baseline/metro/Metro-Details.txt", "$reportPath/metro/Metro-Details.txt", "$reportPath/metro/diff.txt");
		$res{'bosque-c'} = changes::changes("eval/baseline/bosque/Bosque-Details.txt", "$reportPath/bosque/Bosque-Details.txt", "$reportPath/bosque/diff.txt");
		$res{'comunidade-c'} = changes::changes("eval/baseline/comunidade/Comunidade-Details.txt", "$reportPath/comunidade/Comunidade-Details.txt", "$reportPath/comunidade/diff.txt");
		
		# change to 0 to evaluate cogroo3, to 1 to evaluate new
		if(1) {
			# execute new cogroo to get the reports...
			generateCogrooReportNew("$reportPath/probi/diff.txt", "$reportPath/probi/diff-new.txt");
			generateCogrooReportNew("$reportPath/metro/diff.txt", "$reportPath/metro/diff-new.txt");
			generateCogrooReportNew("$reportPath/bosque/diff.txt", "$reportPath/bosque/diff-new.txt");
			generateCogrooReportNew("$reportPath/comunidade/diff.txt", "$reportPath/comunidade/diff-new.txt");
			
			# restore baseline and execute
			$ENV{'MODEL_ROOT'} = $baselineVars{'MODEL_ROOT'};
			$ENV{'UIMA_HOME'} = $baselineVars{'UIMA_HOME'};
			$ENV{'CORPUS_ROOT'} = $baselineVars{'CORPUS_ROOT'};
			$ENV{'REPO_ROOT'} = $baselineVars{'REPO_ROOT'};
			
			print " will include baseline output to report ...\n";
			printVars();
			
			generateCogrooReport("$reportPath/probi/diff.txt", "$reportPath/probi/diff-baseline.txt");
			generateCogrooReport("$reportPath/metro/diff.txt", "$reportPath/metro/diff-baseline.txt");
			generateCogrooReport("$reportPath/bosque/diff.txt", "$reportPath/bosque/diff-baseline.txt");
			generateCogrooReport("$reportPath/comunidade/diff.txt", "$reportPath/comunidade/diff-baseline.txt");
		}
		
	} else {
		print "Saving baseline models vars...\n";
		printVars();
		$baselineVars{'MODEL_ROOT'} = $ENV{'MODEL_ROOT'};
		$baselineVars{'UIMA_HOME'} = $ENV{'UIMA_HOME'};
		$baselineVars{'CORPUS_ROOT'} = $ENV{'CORPUS_ROOT'};
		$baselineVars{'REPO_ROOT'} = $ENV{'REPO_ROOT'}; 
	}
	
	return \%res;
}

sub configureMultiPropertiesBaseline {
	
	my $useModels = shift;
	
	# we check the avalilable models and configure multi.properties to use it
	my $filterFile = '../BaselineCogrooAE/src/main/resources/cogroo/multi.filter.properties';
	my $target = '../BaselineCogrooAE/src/main/resources/cogroo/multi.properties';
	
	my $modelRoot = $ENV{'MODEL_ROOT'};
	my %replace = (
		'sent' => 'false',
		'tok' => 'false',
		'prop' => 'false',
		'con' => 'false',
		'pos' => 'false',
		'chunker' => 'false',
		'sp' => 'false',
	);
	
	if($useModels) {
		if (-e "$modelRoot/pt-sent.model") {
			print "found sentence detector model\n";
	 		$replace{'sent'} = 'true';
	 	}		
	}
	
	if($useModels) {
		if (-e "$modelRoot/pt-tok.model") {
			print "found tokenizer model\n";
	 		$replace{'tok'} = 'true';
	 	}		
	}
	
	if($useModels) {
		if (-e "$modelRoot/pt-prop.model") {
			print "found proper name finder model\n";
	 		$replace{'prop'} = 'true';
	 	}		
	}
	
	if($useModels) {
		if (-e "$modelRoot/pt-con.model") {
			print "found contraction finder model\n";
	 		$replace{'con'} = 'true';
	 	}		
	}
	
	#if($useModels) {
	#	if (-e "$modelRoot/pt-pos.model" && -e "$modelRoot/pt-feat.model") {
	#		print "found POS Tagger (class) and Featurizer models\n";
	# 		$replace{'pos'} = 'true';
	# 	}		
	#}
	
 	filterFile($filterFile, $target, \%replace);
}

sub configureMultiProperties {
	
	my $useModels = shift;
	
	# we check the avalilable models and configure multi.properties to use it
	my $filterFile = '../NewTagsetBaselineCogrooAE/src/main/resources/models.xml.filter';
	my $target = '../NewTagsetBaselineCogrooAE/src/main/resources/models.xml';
	my $root = '../NewTagsetBaselineCogrooAE/target/models';
	my $modelRoot = $ENV{'MODEL_ROOT'};
	my %replace = (
		'sent' => '',
		'tok' => '',
		'prop' => '',
		'con' => '',
		'pos' => '',
		'chunker' => '',
		'sp' => '',
	);
	
	if($useModels) {
		if (-e "$modelRoot/pt-sent.model") {
			print "found sentence detector model\n";
	 		$replace{'sent'} = '<analyzer>sentenceDetector</analyzer>';
	 	}		
	}
	
	if($useModels) {
		if (-e "$modelRoot/pt-tok.model") {
			print "found tokenizer model\n";
	 		$replace{'tok'} = '<analyzer>tokenizer</analyzer>';
	 	}		
	}
	
	if($useModels) {
		if (-e "$modelRoot/pt-prop.model") {
			print "found proper name finder model\n";
	 		$replace{'prop'} = '<analyzer>nameFinder</analyzer>';
	 	}		
	}
	
	if($useModels) {
		if (-e "$modelRoot/pt-con.model") {
			print "found contraction finder model\n";
	 		$replace{'con'} = '<analyzer>contractionFinder</analyzer>';
	 	}		
	}
	
	if($useModels) {
		if (-e "$modelRoot/pt-pos.model" && -e "$modelRoot/pt-feat.model") {
			print "found POS Tagger (class) and Featurizer models\n";
	 		$replace{'pos'} = '<analyzer>posTagger</analyzer><analyzer>featurizer</analyzer><analyzer>lemmatizer</analyzer>';
	 	}		
	}
	
	if($useModels) {
		if (-e "$modelRoot/pt-chunk.model" && -e "$modelRoot/pt-hf.model") {
			print "found Chunker and Chunker HF models\n";
	 		$replace{'chunker'} = '<analyzer>chunker</analyzer><analyzer>headFinder</analyzer>';
	 	}		
	}
	
	
	
	if($useModels) {
		if (-e "$modelRoot/pt-sp.model") {
			print "found SP models\n";
	 		$replace{'sp'} = '<analyzer>shallowParser</analyzer>';
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
		$result{'fp'} = $result{'selected'} - $result{'tp'};
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
	my $name = shift;
	my %res;
	$res{$name} = evaluate("Cogroo3AE", "$evalPath/$name");
	return %res;
}

#sub evaluateBaseline {#
#	my $evalPath = shift;
#	my %res;
#	configureMultiProperties(0);
#	install("../NewTagsetBaselineCogrooAE/pom.xml");
#	installPearByPath("../NewTagsetBaselineCogrooAE/target/NewTagsetBaselineCogrooAE.pear");
#	$res{'baseline'} = evaluate("NewTagsetBaselineCogrooAE", "$evalPath/baseline");
#	printToLog Dumper( \%res );
#	return %res;
#}

sub evaluateUsingModel {
	my $evalPath = shift;
	my $name = shift;
	%baselineVars =  %{$_[0]};
	shift;
	my $corpus = shift;

	my %res;
	configureMultiProperties(1);
	install("../NewTagsetBaselineCogrooAE/pom.xml");
	installPearByPath("../NewTagsetBaselineCogrooAE/target/NewTagsetBaselineCogrooAE.pear");
	$res{$name} = evaluate("NewTagsetBaselineCogrooAE", "$evalPath/$name", $corpus);
	printToLog Dumper( \%res );
	return %res;
}

sub printVars {
	print "... Using this vars ... \n";
	
	print "MODEL_ROOT: " . $ENV{'MODEL_ROOT'} . "\n";
	print "UIMA_HOME: " . $ENV{'UIMA_HOME'} . "\n";
	print "CORPUS_ROOT: " . $ENV{'CORPUS_ROOT'} . "\n";
	print "REPO_ROOT: " . $ENV{'REPO_ROOT'} . "\n"; 
	print "COGROO_3: " . $ENV{'COGROO_3'} . "\n";
}

sub init() {
	checkVars();
	install("../../cogroo/pom.xml");
	install($ENV{'COGROO_3'} ."/pom.xml");
	install("../UIMAAutomation/pom-evaluators.xml");
	installPearByPath("../Cogroo3AE/target/Cogroo3AE.pear");
}
1;
