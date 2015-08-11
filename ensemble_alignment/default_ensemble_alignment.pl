#!/usr/bin/perl
use strict;

my $cardinality = shift;
my $input_fname = shift;
my $structure_fname = shift;
my $structure_prob_fname = shift;


my $temp_folder = "tmp";
system("mkdir -p $temp_folder");

my $mafft_executable = "/gsfs2/home/u21/deblasio/tcoffee/Version_10.00.r1613/plugins/linux/mafft";
my $probcons_executable = "/genome/ICEbin/probcons";
my $clustalo_executable = "~/bin/clustalo";
my $kalign_executable = "/genome/ICEbin/kalign";
my $opal_executable = "java -jar ~/ParamAdvising/Opal_3.0.b0/Opal.jar ";
my $mummals_executable = "~/mummals1.01/mummals";
my $mummals_hmm_path = "~/mummals1.01/hmm_parameters";
my $probalign_executable = "~/probalign1.4/probalign";

my $facet_executable = "java -cp /home/u21/deblasio/facet_v1.4/ facet.Facet";

my $muscle_executable = "/genome/ICEbin/muscle";
my $t_coffee_executable = "/gsfs2/home/u21/deblasio/tcoffee/Version_10.00.r1613/bin/t_coffee";
my $prank_executable = "~/bin/prank";
my $dialign_executable = "~/bin/dialign-tx";
my $dialign_conf_dir = "~/alternate_programs/DIALIGN-TX_1.0.2/conf";
my $clustal_executable = "~/bin/clustalw";
my $clustal2_executable = "/uaopt/clustalw/2.1/bin/clustalw2 ";
my $sate_executable = "python ~/sate/run_sate.py";
my $poa_executable = "/genome/ICEbin/poa";
my $fsa_executable = "~/bin/fsa";
my $msaprobs_executable = "~/bin/msaprobs";

my @commands;
$commands[0] = "$mummals_executable $input_fname -ss 3 -solv 1 -dali 0 -param $mummals_hmm_path/dataset_0.20_0.40_0.60_abcd.dali0.solv1_ss3.mat -outfile $temp_folder/mummals.aln 2>/dev/null >/dev/null >/dev/null 2>/dev/null ; perl convertClustalToFasta.pl $temp_folder/mummals.aln $input_fname";
$commands[1] = "$opal_executable $input_fname";
$commands[2] = "$probalign_executable $input_fname";
$commands[3] = "$kalign_executable $input_fname";
$commands[4] = "$muscle_executable -in $input_fname > $temp_folder/muscle.fasta 2>/dev/null ; perl reorderFasta.pl $temp_folder/muscle.fasta $input_fname";
$commands[5] = "$t_coffee_executable -in $input_fname -outfile $temp_folder/t_coffee.aln >/dev/null 2>/dev/null ; perl convertClustalToFasta.pl $temp_folder/t_coffee.aln $input_fname";
$commands[6] = "$prank_executable -d=$input_fname -o=$temp_folder/prank.fasta >/dev/null 2>/dev/null ; perl reorderFasta.pl $temp_folder/prank.fasta.best.fas $input_fname";
$commands[7] = "$dialign_executable $dialign_conf_dir $input_fname $temp_folder/dialign.fasta 2>/dev/null >/dev/null; perl reorderFasta.pl $temp_folder/dialign.fasta $input_fname";
$commands[8] = "$clustalo_executable --in $input_fname";
$commands[9] = "$probcons_executable $input_fname";
$commands[10] = "$mafft_executable $input_fname";
$commands[11] = "$clustal_executable -INFILE=$input_fname -OUTFILE=$temp_folder/clustal.aln >/dev/null 2>/dev/null ; perl convertClustalToFasta.pl $temp_folder/clustal.aln $input_fname";
$commands[12] = "$clustal2_executable -INFILE=$input_fname -OUTFILE=$temp_folder/clustal2.aln >/dev/null 2>/dev/null ; perl convertClustalToFasta.pl $temp_folder/clustal2.aln $input_fname";
$commands[13] = "$msaprobs_executable $input_fname";
$commands[14] = "$sate_executable -i $input_fname --auto";
$commands[15] = "$poa_executable -read_fasta $input_fname -clustal tmp/poa.aln blosum80.mat 2>/dev/null >/dev/null; perl convertClustalToFasta.pl $temp_folder/poa.aln $input_fname";
$commands[16] = "$fsa_executable $input_fname";

my $facet_max = 0;
my $max_index = -1;
foreach my $i(0...$cardinality-1){
	#print STDERR $commands[$i] . "\n";
	print STDERR "Running command ".($i+1)."\r";
	print STDERR "\n";
	system("$commands[$i] > $temp_folder/$i.mfa 2>/dev/null");
	#print "$facet_executable $temp_folder/$i.mfa $structure_fname $structure_prob_fname\n";
	my $temp_value = `$facet_executable $temp_folder/$i.mfa $structure_fname $structure_prob_fname`;
	$temp_value =~ s/.*:\t//;
	if($temp_value > $facet_max){
		$facet_max = $temp_value;
		$max_index = $i;
	}
}
print STDERR "Best Facet value $facet_max\n";
print STDERR "Best Command $commands[$max_index]\n";
system("cat $temp_folder/$max_index.mfa");
system("rm $temp_folder/mummals.aln");

