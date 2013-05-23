#!/usr/bin/perl -s

# Load JSpell ptbr dictionary for queries
# Please install the dictionary first (make install)

use Lingua::Jspell;
use strict;

use open ':encoding(utf8)';
use open ':std';

my $dict = Lingua::Jspell->new( "ptbr") || die "could not open ptbr dict";   # select portuguese dictionary
$dict->setmode({flags => 1});    # show  feature "flag" in output

while(<>) {
	chop;
	
	my @fea = $dict->fea($_);
	
	print Lingua::Jspell::any2str ( [@fea] , 1) . "\n";
}
