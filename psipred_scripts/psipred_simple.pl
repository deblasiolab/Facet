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
	open FILE,">$TEMP_FILE_LOCATION/temp.fasta" or die "Cannot open $TEMP_FILE_LOCATION/temp.fasta file\n";
	print FILE ">$TEMP_FILE_LOCATION/temp_seq\n";
	print FILE "$seq\n";
	close(FILE);
  system("$PSIPRED_LOCATION/runpsipred_single $TEMP_FILE_LOCATION/temp.fasta 2>&1 >/dev/null");


	open INP,"$TEMP_FILE_LOCATION/temp.ss2";
	my $i=0;
	my $struc = "";
	my $conf = "";
	my ($c, $h, $e) = ("","","");
	while (<INP>) {
		#while(substr($seq,$i,1) =~ /[\.-]/){
			#print "-";
		#	$i++;
		#}	
		if (/[0-9]+\s([A-Z])\s([A-Z])\s+([0-9\.]+)\s+([0-9\.]+)\s+([0-9\.]+)/) {
			printf "%s",$2;
			$c .= " $3";
			$h .= " $4";
			$e .= " $5";
			$i++;
		}

		#chomp($_);
                #if(/Pred: ([CHE]+)/){
                #        $struc .= $1;
                #}
		#elsif(/Conf: ([0-9]+)/){
                #        $conf .= $1;
                #}

	  
	}
	while(substr($seq,$i,1) =~ /[\.-]/){
		#print "-";
		$i++;
	}
	close INP;
	#print $struc."\n";
	#print STDERR $conf."\n";
	#my @conf = ((3.5,3,3),(4,2.75,2.75),(5.5,2.25,2.25),(6,1.75,1.75),(6.5,1.25,1.25),(7,1,1),(7.5,.75,.75),(8,.5,.5),(8.5,.25,.25),(9,0,0));
	#for(my $i=0;$i<length($struc);$i++){
	#	my $S = substr($struc,$i,1);
	#		my $C = int(substr($conf,$i,1));
	#	$C += (9-$C)/3;
	#	$C = int($C+1);
	#	my $O = (9-$C+1)/2-0.5;
	#	$O = int($O);
	#	if($S=="C"){
	#		$c.= " $C.000";
	#		$e.= " $O.000";
	#		$h.= " $O.000";
	#	}if($S=="E"){
	#                     $e.= " $C.000";
	#                     $h.= " $O.000";
	#                     $c.= " $O.000";
	#             }if($S=="H"){
	#                     $h.= " $C.000";
	#                     $c.= " $O.000";
	#                     $e.= " $O.000";
	#             }
	#}
	print "\n";

	print STDERR "$c\n$h\n$e\n";
	
	system ("rm $TEMP_FILE_LOCATION/temp.ss* $TEMP_FILE_LOCATION/temp.horiz $TEMP_FILE_LOCATION/temp.fasta");
}
