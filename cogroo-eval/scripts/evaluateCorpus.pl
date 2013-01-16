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


# Script to evaluate the current GC (does not train models!!)
# 
# usage: perl evaluateCorpus.pl Metro|Bosque|Probi

use FindBin qw($Bin);
use Test::Deep qw(cmp_deeply);
use Data::Dumper;
use File::Temp qw/ tempfile tempdir /;
use Cwd;
use File::Path qw(make_path rmtree);
use Storable qw(freeze thaw);
use File::Copy::Recursive qw(dircopy);
my $common = "$Bin/../../lang/pt_br/cogroo-res/scripts"; 

use lib "$Bin/../../lang/pt_br/cogroo-res/scripts";
require eval_unit;
 
require cpe;
require cpeNewTagset;

# override models path
# ../../lang/pt_br/cogroo-res/models

$ENV{'MODEL_ROOT'} = "../../lang/pt_br/cogroo-res/models";


sub evaluate {
	my $corpus = shift;
	my %baselineVars;
	
	$baselineVars{'MODEL_ROOT'} = $ENV{'MODEL_ROOT'};
	$baselineVars{'UIMA_HOME'} = $ENV{'UIMA_HOME'};
	$baselineVars{'CORPUS_ROOT'} = $ENV{'CORPUS_ROOT'};
	$baselineVars{'REPO_ROOT'} = $ENV{'REPO_ROOT'}; 

	$ENV{'UIMA_JVM_OPTS'} = "-Duima.datapath=$tempDir -Xms512m -Xmx1024m -XX:PermSize=256m " . $ENV{'UIMA_JVM_OPTS'};
	$ENV{'MAVEN_OPTS'} = "-Duima.datapath=$tempDir -Xms256m -Xmx1024m -XX:PermSize=256m " . $ENV{'MAVEN_OPTS'};
	
	
	my %r; 

	# copy models to resources
	rmtree("../NewTagsetBaselineCogrooAE/target/models");
	my $num_of_files_and_dirs = dircopy("../../lang/pt_br/cogroo-res/models","../NewTagsetBaselineCogrooAE/target/models");
	print "Copied $num_of_files_and_dirs models to models \n";
		
	# cpeNewTagset::install("../NewTagsetBaselineCogrooAE/pom.xml");
	%r = cpeNewTagset::evaluateUsingModel('eval', "default", \%baselineVars, $corpus);

	
	return %r;
}

my $corpus;
if(@ARGV == 1 && $ARGV[0] ne '') {
	$corpus = $ARGV[0];
}

my %res = evaluate($corpus);
print Dumper(\%res); 


