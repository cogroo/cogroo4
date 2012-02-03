#!/usr/bin/perl -s

# Please install the dictionary first (make install)
# Loads the ptbr dictionary word list, and creates the files 
# target/WordPrimitiveTag_ptBR
# target/PrimitiveWordTag_ptBR

use jspell::dict;
use Data::Dumper;
use locale;
use POSIX 'locale_h';


# ptbr.dic for production, sample.dic for test

$dic = jspell::dict::init("../pt-br/ptbr.dic") or die "nao abriu port.dic";
$pt_dict = jspell::new("ptbr") or die "nao abriu";

open (SIMPLE, '>:encoding(UTF-8)', '../../target/tagdict.txt');
#open (WPT, '>:encoding(UTF-8)', '../../target/wpt.txt');
#open (PWT,'>', '../../target/PrimitiveWordTag_ptBR.txt');
open (TAG,'>', '../../target/TAG_ptPT.txt');

my %simple;
#my %wpt;
#my %pwt;
my %tags;

# features to add. Only if 1 will add
%features = (
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
		# we first consume the begining of the document (comments and affixes)
		if($word !~ m/^\s/ && $word !~ m/^-e/)
		{
			# list of derived words
			my @der = $pt_dict->der($word);
			foreach $dword (@der) {
				#if($dword !~ /\w-\w/) {
					# gets its analisis and put to a string
					my @fea = $pt_dict->fea($dword);
					my $analisis;
					my $rad;
					foreach $key (@fea) {
					
						while ( ($k,$v) = each %$key ) {
							if( $k eq "rad" ) {
								$rad = $v;
							}
						    elsif( !defined($features{$k}) ) {
							#else {
							    $analisis .= "$k:$v|";
							    $tags{"$k:$v"} = 1;
							}
						}
						#if($analisis =~ m/CAT:a_nc/ ) {
						#	$analisis =~ s/CAT:a_nc/CAT:nc/;
						#	
						#	#$wpt{$dword}{"$rad\t$analisis"}=1;
						#	#$pwt{$rad}{"$dword\t$analisis"}=1;
						#	$simple{$dword}{$analisis} = 1;
						#	
						#	$analisis =~ s/CAT:nc/CAT:adj/;
						#} 
						
						$rad =~ s/ /_/g;
						
						#$wpt{$dword}{"$rad\t$analisis"}=1;
						#$pwt{$rad}{"$dword\t$analisis"}=1;
						$simple{$dword}{"$rad>$analisis"} = 1;
						
						$analisis = '';
					}
				#}
			}#foreach
		}
	}#sub
);

for my $dword ( sort keys %simple ) {
	print SIMPLE "$dword";
	for my $a ( keys %{$simple{$dword}} ) {
		print SIMPLE " $a";
	}
	print SIMPLE "\n";	
}

#for my $dword ( sort keys %wpt ) {
#	for my $a ( keys %{$wpt{$dword}} ) {
#		print WPT "$dword\t$a\n";
#	}
#}

#for my $primitive ( sort keys %pwt ) {
#	for my $a ( keys %{$pwt{$primitive}} ) {
#		print PWT "$primitive\t$a\n";
#	}
#}

for my $t ( sort keys %tags ) {
		print TAG "$t\n";
}

close SIMPLE or die "bad WPT: $! $?";
#close WPT or die "bad WPT: $! $?";
#close PWT or die "bad PWT: $! $?";
#close TAG or die "bad TAG: $! $?";

