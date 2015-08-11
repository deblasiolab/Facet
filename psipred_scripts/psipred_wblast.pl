#!/usr/bin/perl
use strict;

our $PSIPRED_LOCATION = "psipred32";
our $TEMP_FILE_LOCATION = ".";

our $file = "";
our $ftype = "fasta";
our $repl_name = "Blossom65";
our $num_seq = -1;
our @names;
our @sequences;

require "file_inp.pl";


my $folder = `pwd`;
chomp($folder);
$folder .= "";
foreach my $seq (@sequences){
	open FILE,">$TEMP_FILE_LOCATION/temp$$.fasta" or die "Cannot open $TEMP_FILE_LOCATION/temp$$.fasta file\n";
	print FILE ">$TEMP_FILE_LOCATION/temp_seq\n";
	print FILE "$seq\n";
	close(FILE);
	#print "psipred32/runpsipred $folder/temp$$.fasta\n";
  system("$PSIPRED_LOCATION/runpsipred $TEMP_FILE_LOCATION/temp$$.fasta 2>&1 >/dev/null");


	open INP,"$TEMP_FILE_LOCATION/temp$$.ss2" or die("$TEMP_FILE_LOCATION/temp$$.ss2: $!");
	my $i=0;
	my ($c, $h, $e) = ("","","");
	while (<INP>) {
		while(substr($seq,$i,1) =~ /[\.-]/){
			#print "-";
			$i++;
		}	
		if (/[0-9]+\s([A-Z])\s([A-Z])\s+([0-9\.]+)\s+([0-9\.]+)\s+([0-9\.]+)/) {
			printf "%s",$2;
			$c .= " $3";
			$h .= " $4";
			$e .= " $5";
			$i++;
		}
	  
	}
	while(substr($seq,$i,1) =~ /[\.-]/){
		#print "-";
		$i++;
	}
	close INP;
	print "\n";

	print STDERR "$c\n$h\n$e\n";
	
	system ("rm $TEMP_FILE_LOCATION/temp$$.ss* $TEMP_FILE_LOCATION/temp$$.horiz $TEMP_FILE_LOCATION/temp$$.fasta");
}
