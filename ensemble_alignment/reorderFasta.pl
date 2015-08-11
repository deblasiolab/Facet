#!/usr/bin/perl
use strict;

my $fname = shift;
my %sequences;
my $input_fname = shift;
my @names = `grep ">" $input_fname | sed "s/>//"`;
chomp @names;

open FILE, "$fname" or die("$!:$fname");
my $name = "";
while(<FILE>){
	chomp $_;
	if($_ =~ />(.*)/){
		$name = $1;
	}else{
		$sequences{$name} .= $_;
	}
}

foreach my $k(@names){
	print ">$k\n";
	for(my $j=0;$j<length($sequences{$k})+50;$j+=50){
		print substr($sequences{$k},$j,50)."\n";
	}
}
