#!/usr/bin/perl -s

# Please install the dictionary first (make install)
# Loads the ptbr dictionary word list, and creates the files 
# target/WordPrimitiveTag_ptBR
# target/PrimitiveWordTag_ptBR

use jspell::dict;
use Data::Dumper;

mkdir("target");

$dic = jspell::dict::init("../pt-br/ptbr.dic") or die "nao abriu port.dic";
$pt_dict = jspell::new("ptbr") or die "nao abriu";
open (PWT, '>', 'target/WordPrimitiveTag_ptBR.txt');
open (WPT,'>', 'target/PrimitiveWordTag_ptBR.txt');
open (TAG,'>', 'target/TAG_ptPT.txt');

my %wpt;
my %pwt;
my %tags;

$dic->foreach_word(
	sub {
		# gets each word from dictionary
		my $word = shift;
		# we first consume the begining of the document (comments and affixes)
		if($word !~ m/^\s/ && $word !~ m/^-e/)
		{
			# list of derived words
			my @der = $pt_dict->der($word);
			foreach $dword (@der) {
				# gets its analisis and put to a string
				my @fea = $pt_dict->fea($dword);
				my $analisis;
				my $rad;
				foreach $key (@fea) {
				
					while ( ($k,$v) = each %$key ) {
						if( $k eq "rad" ) {
							$rad = $v;
						}
						elsif($k ne "SEM" && $k ne "PREAO90") {
						    $analisis .= "$k:$v ";
						    $tags{"$k:$v"} = 1;
						}
					}
					$wpt{$dword}{"$rad\t$analisis"}=1;
					$pwt{$rad}{"$dword\t$analisis"}=1;
					$analisis = '';
				}
			}#foreach
		}
	}#sub
);

for my $dword ( sort keys %wpt ) {
	for my $a ( keys %{$wpt{$dword}} ) {
		print WPT "$dword\t$a\n";
	}
}

for my $primitive ( sort keys %pwt ) {
	for my $a ( keys %{$pwt{$primitive}} ) {
		print PWT "$primitive\t$a\n";
	}
}

for my $t ( sort keys %tags ) {
		print TAG "$t\n";
}


close WPT or die "bad WPT: $! $?";
close PWT or die "bad PWT: $! $?";
close TAG or die "bad TAG: $! $?";

