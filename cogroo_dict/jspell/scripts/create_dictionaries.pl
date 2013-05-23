#!/usr/bin/perl -s

# Please install the dictionary first (make install)
# Loads the ptbr dictionary word list, and creates the files 
# target/WordPrimitiveTag_ptBR
# target/PrimitiveWordTag_ptBR

use strict;
use Lingua::Jspell;
use Lingua::Jspell::DictManager;

use Data::Dumper;
use locale;
use POSIX 'locale_h';

my $isCollectTags = 0;
my $isCollectContractions = 0;

# ptbr.dic for production, sample.dic for test

# this is to iterate the dictionaries
my $dic = init("../pt-br/ptbr.dic") or die "nao abriu port.dic";

# this is to analyze
my $pt_dict = Lingua::Jspell->new("ptbr") or die "nao abriu";

# list of entries
open (SIMPLE,	'>:encoding(UTF-8)', '../../target/tagdict.txt');


if($isCollectTags) {
	# list of tags
	open (TAG,		'>:encoding(UTF-8)', '../../target/TAG_ptPT.txt');
}

if($isCollectContractions) {
	# the contractions
	open (CON,		'>:encoding(UTF-8)', '../../target/contractions.txt');
}

# hash to remove duplicates and sort... is it necessary for simple? maybe we should serialize it directly to make it faster

my %tags;
my %con;

# features to add. Only if 1 will add
my %features = (
        ABR => 0,
        SEM => 0,
        PREAO90 => 0,
        EQAO90 => 0,
        AG => 1,
        PFSEM => 0,
        FSEM => 0,
        ORIG => 0,
        Adv => 0,
        Art => 0,
        BRAS => 0,
        CAR => 0,
        GR => 0,
        I => 0,
        LA => 0,
        PT => 0,
        PFSEM => 0,
        Pind => 0,
        Pdem => 0,
        Ppes => 0,  
        SEM => 0,
        SUBCAT => 0, 
        guess => 0,
        unknown => 0,
        AN => 0,
        unknown => 0,
        CLA => 0,
        ORIT => 0,
        Pdem2 => 0,
        Prep2 => 0,
        Prep => 0,
        TR => 0,
        
    );

$dic->foreach_word(
	sub {
		# gets each word from dictionary
		my $word = shift;
		# list of derived words
		my @der = $pt_dict->der($word);
		foreach my $dword (@der) {
				# gets the analysis a 
				my @fea = $pt_dict->fea($dword);
				foreach my $key (@fea) {
					my $analisis;
					my $rad;
					if(!($dword =~ m/\S-\S/ && ${$key}{'CAT'} eq 'v')) { #avoid amar-lhe, amo-lha-ei etc
						while ( my ($k,$v) = each %$key ) {
							if( $k eq "rad" ) {
								$rad = $v;
							}
						    elsif( !defined($features{$k}) ) {
							    $analisis .= "$k:$v|";
							    if($isCollectTags) {
							    	$tags{"$k:$v"} = 1; # enable to create a log of tags
							    }
							}
						}
						$rad =~ s/ /_/g;
						print SIMPLE "$dword $rad>$analisis\n";
						#$simple{$dword}{"$rad>$analisis"} = 1;
						if($isCollectContractions && ${$key}{'CAT'} eq 'cp') {
							$con{$dword} = 1;
						}			
					}
				}
		}#foreach
	}#sub
);

close SIMPLE or die "bad SIMPLE: $! $?";

if($isCollectTags) {
	for my $t ( sort keys %tags ) {
			print TAG "$t\n";
	}
	
	close TAG or die "bad TAG: $! $?";
}


if($isCollectContractions) {
	for my $t ( sort keys %con ) {
			print CON "$t\n";
	}
	
	close CON or die "bad CON: $! $?";
}


