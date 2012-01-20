#!/usr/bin/perl -s

# Load JSpell ptbr dictionary for queries
# Please install the dictionary first (make install)

use jspell;

jspell_dict("ptbr") || die "could not open ptbr dict";   # select portuguese dictionary
setmode("+flags");     # show  feature "flag" in output

while(<>){
 chop;
 if($tag){  print join(" ",featags($_)). "\n"}
 else    {  print any2str([fea($_)],1) . "\n"}
}