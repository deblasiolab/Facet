#!/usr/bin/perl
use strict;

our $input_fname = shift;
our $structure_fname = shift;
our $structure_prob_fname = shift;
my $set_fname = shift;

if($set_fname eq ""){
	$set_fname = "-";
}

open SET,"<$set_fname" or die("$set_fname: $!\n");

my $temp_folder = "tmp";
system("mkdir -p $temp_folder");

our $mafft_executable = "/gsfs2/home/u21/deblasio/tcoffee/Version_10.00.r1613/plugins/linux/mafft";
our $probcons_executable = "/genome/ICEbin/probcons";
our $clustalo_executable = "~/bin/clustalo";
our $kalign_executable = "/genome/ICEbin/kalign";
our $opal_executable = "java -jar ~/ParamAdvising/Opal_3.0.b0/Opal.jar ";
our $mummals_executable = "~/mummals1.01/mummals";
our $mummals_hmm_path = "~/mummals1.01/hmm_parameters";
our $probalign_executable = "~/probalign1.4/probalign";
our $muscle_executable = "/genome/ICEbin/muscle";
our $t_coffee_executable = "/gsfs2/home/u21/deblasio/tcoffee/Version_10.00.r1613/bin/t_coffee";
our $prank_executable = "~/bin/prank";
our $dialign_executable = "~/bin/dialign-tx";
our $dialign_conf_dir = "~/alternate_programs/DIALIGN-TX_1.0.2/conf";
our $clustal_executable = "~/bin/clustalw";
our $clustal2_executable = "/uaopt/clustalw/2.1/bin/clustalw2 ";
our $sate_executable = "python ~/sate/run_sate.py";
our $poa_executable = "/genome/ICEbin/poa";
our $fsa_executable = "~/bin/fsa";
our $msaprobs_executable = "~/bin/msaprobs";

our $facet_path = "/home/u21/deblasio/facet_v1.4/";
my $facet_executable = "java -cp $facet_path facet.Facet";

my %default_commands;
$default_commands{"mummals"} = "$mummals_executable $input_fname -ss 3 -solv 1 -dali 0 -param $mummals_hmm_path/dataset_0.20_0.40_0.60_abcd.dali0.solv1_ss3.mat -outfile $temp_folder/mummals.aln 2>/dev/null >/dev/null >/dev/null 2>/dev/null ; perl convertClustalToFasta.pl $temp_folder/mummals.aln $input_fname";
$default_commands{"opal"} = "$opal_executable $input_fname";
$default_commands{"probalign"} = "$probalign_executable $input_fname";
$default_commands{"kalign"} = "$kalign_executable $input_fname";
$default_commands{"muscle"} = "$muscle_executable -in $input_fname > $temp_folder/muscle.fasta 2>/dev/null ; perl reorderFasta.pl $temp_folder/muscle.fasta $input_fname";
$default_commands{"t_coffee"} = "$t_coffee_executable -in $input_fname -outfile $temp_folder/t_coffee.aln >/dev/null 2>/dev/null ; perl convertClustalToFasta.pl $temp_folder/t_coffee.aln $input_fname";
$default_commands{"prank"} = "$prank_executable -d=$input_fname -o=$temp_folder/prank.fasta >/dev/null 2>/dev/null ; perl reorderFasta.pl $temp_folder/prank.fasta.best.fas $input_fname";
$default_commands{"dialign"} = "$dialign_executable $dialign_conf_dir $input_fname $temp_folder/dialign.fasta 2>/dev/null >/dev/null; perl reorderFasta.pl $temp_folder/dialign.fasta $input_fname";
$default_commands{"clustalo"} = "$clustalo_executable --in $input_fname";
$default_commands{"probcons"} = "$probcons_executable $input_fname";
$default_commands{"mafft"} = "$mafft_executable $input_fname";
$default_commands{"clustal"} = "$clustal_executable -INFILE=$input_fname -OUTFILE=$temp_folder/clustal.aln >/dev/null 2>/dev/null ; perl convertClustalToFasta.pl $temp_folder/clustal.aln $input_fname";
$default_commands{"clustal2"} = "$clustal2_executable -INFILE=$input_fname -OUTFILE=$temp_folder/clustal2.aln >/dev/null 2>/dev/null ; perl convertClustalToFasta.pl $temp_folder/clustal2.aln $input_fname";
$default_commands{"msaprobs"} = "$msaprobs_executable $input_fname";
$default_commands{"sate"} = "$sate_executable -i $input_fname --auto";
$default_commands{"poa"} = "$poa_executable -read_fasta $input_fname -clustal tmp/poa.aln $facet_path/matricies/BLOSUM80 2>/dev/null >/dev/null; perl convertClustalToFasta.pl $temp_folder/poa.aln $input_fname";
$default_commands{"fsa"} = "$fsa_executable $input_fname";

sub command_from_line{
	my $line = shift;
	#print $line."\n";
	if($line =~ /opal.sparse_(.*)\.(.*)\.(.*)\.(.*)\.(.*)/){
		return "$opal_executable --gamma $2 --gamma_term $3 --lambda $4 --lambda_term $5 --cost $1 $input_fname";
	}elsif($line =~ /mummals-dataset_(.*)_abcd.dali(.*).solv(.*)_ss(.*).mat/){
		my $dali = "";
		my $unaligned = " -unaligned 1 ";
		if($2 eq "0"){
			$dali = "0";
			$unaligned = " -unaligned 0 ";
		}
		return "$mummals_executable $input_fname -ss $4 -solv $3 $unaligned -param $mummals_hmm_path/dataset_$1_abcd.dali$dali.solv${3}_ss$4.mat -outfile $temp_folder/mummals.aln 2>/dev/null >/dev/null ; perl convertClustalToFasta.pl $temp_folder/mummals.aln $input_fname";
	}elsif($line =~ /probcons-(.*)_(.*)_(.*)/){
		return "$probcons_executable -c $1 -ir $2 -pre $3 $input_fname";
	}elsif($line =~ /kalign-align_(.*)_(.*)_(.*)_(.*)/){
		return "$kalign_executable -bonus $1 -gpo $2 -gpe $3 -tgpe $4 $input_fname";
	}elsif($line =~ /clustalo-(.*)_(.*)_(.*)_(.*)_(.*)/){
		my $tree = "";
		if($4 eq "full"){ $tree = "--full"; }
		my $itter_tree = "";
		if($5 eq "full"){ $itter_tree = "--full-iter"; }
		return "$clustalo_executable --iter=$1 --max-guidetree-iterations=$2 --max-hmm-iterations=$3 $tree $itter_tree --in $input_fname";
	}elsif($line =~ /mafft_(.*)_(.*)_(.*)/){
		my $op = $2;
		my $ep = $3;
		my $matrix = $1;
		my $matrix_class = "";
		my $matrix_value = "";
		if($matrix =~ /b(.*)/){
			$matrix_class = "bl";
			$matrix_value = $1;
		}elsif($matrix =~ /v(.*)/){
			$matrix_class = "aamatrix";
			$matrix_value = "$facet_path/matricies/VTML$1";
		}
		return "$mafft_executable --op $op --ep $ep --$matrix_class $matrix_value $input_fname";
	}elsif($line =~ /muscle-(.*)_(.*)_(.*)/){
		return "$muscle_executable -in $input_fname -gapopen $3 -$1 -objscore $2 > $temp_folder/muscle.fasta 2>/dev/null ; perl reorderFasta.pl $temp_folder/muscle.fasta $input_fname";
	}elsif($line =~ /probalign-alignments_(.*)_(.*)_(.*)/){
		return "$probalign_executable -T $1 -go $2 -ge $3 $input_fname";
	}elsif($line =~ /t_coffee-align_(.*)_(.*)_(.*)/){
		return "$t_coffee_executable -in $input_fname -matrix $1 -gapopen $2 -gapext $3 -outfile $temp_folder/t_coffee.aln >/dev/null 2>/dev/null ; perl convertClustalToFasta.pl $temp_folder/t_coffee.aln $input_fname";
	}elsif($line =~ /prank-(.*)_(.*)_(.*)/){
		my $termGap = "";
		if($3 == "termGap"){ $termGap = "-termGap"; }
		return "$prank_executable -d=$input_fname -gaprate=$1 -gapext=$2 $termGap -o=$temp_folder/prank.fasta >/dev/null 2>/dev/null ; perl reorderFasta.pl $temp_folder/prank.fasta.best.fas $input_fname"
	}
	
	die("Executable not found: $line\n");
}


my $facet_max = 0;
my $max_index = -1;
my $max_command = "";
my $i = 0;
foreach my $line(<SET>){
	chomp $line;
	#print STDERR $line . "\n";
	print STDERR "Running command $line                                \r";
	my $command = "";
	if(defined($default_commands{$line})){
		$command = $default_commands{$line};
	}else{
		$command = command_from_line($line);
	}
	#print "$command\n";
	system("$command > $temp_folder/$i.mfa 2>/dev/null");
	my $temp_value = `$facet_executable $temp_folder/$i.mfa $structure_fname $structure_prob_fname`;
	$temp_value =~ s/.*:\t//;
	if($temp_value > $facet_max){
		$facet_max = $temp_value;
		$max_index = $i;
		$max_command = $line;
	}
	$i++;
}
print STDERR "Best Facet value $facet_max\n";
print STDERR "Best Command $max_command\n";
system("cat $temp_folder/$max_index.mfa");
system("rm -rf $temp_folder");

close SET;
