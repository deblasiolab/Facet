our $file = "";
our $sFile = "";
our $ftype = "fasta";
our $repl_name = "Blossom65";
our $equiv_class = "20";
our $core_threshold = .9;
our $small_group_cutoff = 1;
our $max1 = 0;
our $max2 = 0;
our $structure_pobability;
our $structure_cores;

require "arg_process.pl";

#get the replacement matrix, right now it is set, could allow this as an argument later very easly
our %repl_matrix;


our $num_seq = -1;
our @sequences;
our @names;
our @structure;
our @structure_prob;


sub loadFile{
	our @sequences;
	our @names;
	my $fileName = shift;
	$num_seq = -1;
	
	#print STDERR $fileName."\n";
	#open the file, if the filename is not specified, then read the file from stdin
	if($file ne ""){
		open(FILE,"<$fileName") or die("cannot open file: $fileName\n");
	}else{
		print STDERR "No filename given, expecting input from STDIN\n";
		open(FILE,"<-"); 
	}

	if($ftype eq "fasta"){
		while(<FILE>){
		 chomp($_); #remove newline 
			#print STDERR "ERROR: $_\n";
		 if(substr($_,0,1) eq "#" || length($_)==0){
			
			}elsif(substr($_,0,1) eq ">"){
				$num_seq++;
				$names[$num_seq] = substr($_,1);
				$sequences[$num_seq] = "";
			}else{
				$sequences[$num_seq] .= $_;
			}
		}
		for(my $i=0;$i<$num_seq;$i++){
			#print "$names[$i]\n$sequences[$num_seq]\n";
		}
		#exit;
	}#end fasta
	elsif($ftype eq "clustal"){
		my %seq;
		while(<FILE>){
			chomp($_);
			if(length($_)==0){
				next;
			}
			my @sq = split(/\s+/,$_);
			if(!exists($seq{$sq[0]})){
				$seq{$sq[0]} = "";
			}
			$seq{$sq[0]} .= $sq[1];
		}
		while(my ($k,$v)=each(%seq)){
			$names[++$num_seq] = $k;
			$sequences[$num_seq] = $v;
		}
	}#end clustal
	else{
		#FILE FORMAT NOT RECOGNISED
		die "The file format -$ftype not recognised!!\n";
	}
	close FILE;
	
	our $length = length($sequences[0]);
	#print STDERR $length."\n";
}

loadFile($file);

for(my $i=0;$i<=$num_seq;$i++){
  if(length($sequences[$i])!=$length){
    die "The length of one of the sequences is inconsistant!! ($names[$i]:".length($sequences[$i])."!=$length)\n";
  }
}

if($sFile ne ""){
	if($structure_pobability){
		@structure = getStructure($sFile,1);
		@structure_prob = getStructure($sFile,1);
	}else{
		@structure = getStructure($sFile,0);
		@structure_prob = getStructure($sFile.".prob",1);
	}
}

sub getStructure{
	my $inFile = shift;
	my $probability = shift;
	my @structure;
	open(SFILE,"$inFile") or return;
	@structure = <SFILE>;
	for(my $i=0;$i<scalar(@structure);$i++){
		chomp($structure[$i]);
		my $k=0;
		my $s = "";
		my $l = ($probability)?int($i/3):$i;
		for(my $j=0;$j<length($sequences[$l]);$j++){
			if(substr($sequences[$l],$j,1) =~ /[\.-]/){
				if($probability){
					$s .= "----- ";
				}else{
					$s .= "-";
				}
			}else{
				if($probability){
					$s .= substr($structure[$i],$k+1,6);
					$k+= 6;
				}else{
					$s .= substr($structure[$i],$k,1);
					$k++;
				}
				
			}
		}
		#print "$s\n";
		$structure[$i] = $s;
	}	
	close SFILE;
	return @structure;
}

for(my $j=0;$j<=$num_seq;$j++){
	my $ct = 0;
	for(my $k=0;$k<$length;$k++){
		if(!(substr($sequences[$j],$k,1) =~ /[-\.]/)){
			$ct++;
		}
	}
	if($ct>$max1){
		$max2 = $max1;
		$max1 = $ct;
	}elsif($ct>$max2){
		$max2 = $ct;
	}
}

sub same_gap{
	my $same = 1;
	my $a = shift;
	my $b = shift;
	our @sequences;
	for(my $j=0;$j<=$num_seq;$j++){
		my $ag = ((substr($sequences[$j],$a,1)=~/[\.-]/)?1:0);
		my $bg = ((substr($sequences[$j],$b,1)=~/[\.-]/)?1:0);
		if($ag!=$bg){
			$same = 0;
		}
	}
	return $same;
}

our @gap_cols;
my @gap_group;
my $gap_g = 0;
for(my $i=0;$i<$length;$i++){
	$gap_cols[$i] = 0;
	for(my $j=0;$j<=$num_seq;$j++){
		if(substr($sequences[$j],$i,1)=~/[\.-]/){
			$gap_cols[$i] = 1;
		}
	}
	if($gap_cols[$i]==1){
		my $same = 1;
		if($i>1){
			$same = same_gap($i,$i-1);
		}
		if($same==0){ $gap_g++; }
		$gap_group[$i] = $gap_g;
	}else{
		$gap_group[$i] = 0;
	}
}

our @gap_len;
our @gap_key;
my $len = 1;
my $start = 0;
for(my $i=1;$i<$length;$i++){
	$gap_len[$i] = 0;
	if($gap_cols[$i]){
		if($gap_group[$i]!=$gap_group[$i-1]){
			$gap_len[$start] = $len;
			$len = 1;
			$start = $i;
		}else{
			$len++;
		}
	}
}
$gap_len[$start] = $len;

my $last_key = 0;
$gap_key[0] = $gap_cols[0];
for(my $i=1;$i<$length;$i++){
	$gap_key[$i] = 0;
	if($gap_len[$i]>$small_group_cutoff){
		if(same_gap($last_key,$i)!=1){
			$gap_key[$i] = 1;
			$last_key = $i;
		}
	}
}



our %equiv;
if($equiv_class eq "12"){
	%equiv = ("A"=>1,"G"=>1, "T"=>1, "S"=>1, 
	"P"=>2,
	"I"=>3, "L"=>3, "V"=>3,
	"M"=>4,
	"N"=>5,
	"D"=>6, "E"=>6,
	"Q"=>7,
	"R"=>8, "K"=>8,
	"H"=>9,
	"F"=>10, "Y"=>10,
	"W"=>11,
	"C"=>12);
}
elsif($equiv_class eq "6"){
	%equiv = ("A"=>1,"G"=>1,"P"=>1,"T"=>1,"S"=>1,
	"I"=>2,"L"=>2,"M"=>2,"V"=>2,
	"N"=>3,"D"=>3,"Q"=>3,"E"=>3,
	"R"=>4,"H"=>4,"K"=>4,
	"F"=>5,"W"=>5,"Y"=>5,
	"C"=>6);
}elsif($equiv_class eq "10"){
	%equiv = ("A"=>1,"T"=>1,"S"=>1,
	"G"=>2,
	"P"=>3,
	"I"=>4,"L"=>4,"M"=>4,"V"=>4,
	"N"=>5,"D"=>5,"E"=>5,
	"Q"=>6,"R"=>6,"K"=>6,
	"H"=>7,
	"F"=>8,"Y"=>8,
	"W"=>9,
	"C"=>10);
}elsif($equiv_class eq "15"){
	%equiv = ("A"=>1,
	"G"=>2,
	"P"=>3,
	"T"=>4,"S"=>4,
	"I"=>5,"V"=>5,
	"L"=>6,"M"=>6,
	"N"=>7,
	"D"=>8,
	"Q"=>9,"E"=>9,
	"R"=>10,"K"=>10,
	"H"=>11,
	"F"=>12,
	"W"=>13,
	"Y"=>14,
	"C"=>15);
}else{
	%equiv = ("A"=>1,
	"G"=>2,
	"P"=>3,
	"T"=>4,
	"S"=>5,
	"I"=>6,
	"L"=>7,
	"M"=>8,
	"V"=>9,
	"N"=>10,
	"D"=>11,
	"Q"=>12,
	"E"=>13,
	"R"=>14,
	"H"=>15,
	"K"=>16,
	"F"=>17,
	"W"=>18,
	"Y"=>19,
	"C"=>20);
}

sub is_equiv{
	my $A1 = shift;
	my $A2 = shift;
	if($equiv{uc($A1)} eq $equiv{uc($A2)}){ return 1; }
	else{ return 0; }
}

sub equiv_set{
	my $A = uc(shift);
	my $c = 0;
	my $r = "";
	if($A =~  /[\.-]/){
		return "-";
	}
	foreach my $k (keys %equiv){
		if($k eq $A){
			$c = $equiv{$k};
		}
	}
	foreach my $k (keys %equiv){
		if($equiv{$k}==$c){
			$r .= $k;
		}
	}
	#print STDERR "$r\t--\t$A\n";
	return $r;
}

sub min{
	my ($a,$b) = (shift,shift);
	if($a>$b){ return $b; }
	else{ return $a; }
}

# from Dr Kececioglu's book
sub aaFrequencyInd{
	my $A = shift;
	if($A =~ /[Aa]/){return 0.0760;}
	if($A =~ /[Cc]/){return 0.0189;}
	if($A =~ /[Dd]/){return 0.0521;}
	if($A =~ /[Ee]/){return 0.0632;}
	if($A =~ /[Ff]/){return 0.0397;}
	if($A =~ /[Gg]/){return 0.0719;}
	if($A =~ /[Hh]/){return 0.0228;}
	if($A =~ /[Ii]/){return 0.0529;}
	if($A =~ /[Kk]/){return 0.0581;}
	if($A =~ /[Ll]/){return 0.0917;}
	if($A =~ /[Mm]/){return 0.0229;}
	if($A =~ /[Nn]/){return 0.0436;}
	if($A =~ /[Pp]/){return 0.0520;}
	if($A =~ /[Qq]/){return 0.0417;}
	if($A =~ /[Rr]/){return 0.0523;}
	if($A =~ /[Ss]/){return 0.0715;}
	if($A =~ /[Tt]/){return 0.0587;}
	if($A =~ /[Vv]/){return 0.0649;}
	if($A =~ /[Ww]/){return 0.0131;}
	if($A =~ /[Yy]/){return 0.0321;}

	return 0;

}

sub aaFrequency{
	my $r = 0;
	my $s = shift;
	for(my $i=0;$i<length($s);$i++){
		$r += aaFrequencyInd(substr($s,$i,1));
	}
	return $r;
}


return TRUE;
