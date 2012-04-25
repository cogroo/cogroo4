
	open (LIST, "tok")  or die("Could not open list file. $!");
	
	my %results;

	while (my $line = <LIST>) {
    	$line =~ s/^\s+|\s+$//g;
    	print "will evaluate $line \n";
	}
	
	close LIST;
