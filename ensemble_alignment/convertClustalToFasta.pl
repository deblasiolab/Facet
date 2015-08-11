#!/usr/bin/perl
use strict;

my $fname = shift;
my %sequences;
my $input_fname = shift;
my @names = `grep ">" $input_fname | sed "s/>//"`;
chomp @names;

open FILE, "$fname" or die("$!:$fname");
while(<FILE>){
	chomp $_;
	my @spl = split(/\s+/,$_);
	if(scalar(@spl)==2 && $spl[1] =~ /^[A-Z\-a-z]*$/){
		if($sequences{$spl[0]} eq ""){
			#push @names, $spl[0];
		}
		$sequences{$spl[0]} .= $spl[1];
	}
}

foreach my $k(@names){
	print ">$k\n";
	$sequences{$k} =~ s/[ \t]+//g;
	for(my $j=0;$j<length($sequences{$k})+50;$j+=50){
		print substr($sequences{$k},$j,50)."\n" if substr($sequences{$k},$j,50) ne "";
	}
}
