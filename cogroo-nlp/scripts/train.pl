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


use lib ".";
use lib "scripts";
require eval_unit;

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
     -o <opt>  : training options (separated by ,)
     -i <id>   : execution id, for logging
     -p <num>  : num of threads (8 is default)
     -f        : create a final model
     -e        : evaluate using Cross Validation
    example: $0 -h
EOF
	exit;
}

init();

eval_unit::exec(\%opt, \%extraOpt);