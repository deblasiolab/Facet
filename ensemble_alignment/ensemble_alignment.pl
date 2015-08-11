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
my $facet_executable = "java -cp ~/facet_v1.4 facet.Facet";

my @commands;
$commands[0] = "$mummals_executable $input_fname -ss 3 -solv 2 -unaligned 1 -param $mummals_hmm_path/dataset_0.20_0.40_0.60_abcd.dali.solv2_ss3.mat -outfile $temp_folder/mummals.aln 2>/dev/null >/dev/null ; perl convertClustalToFasta.pl $temp_folder/mummals.aln $input_fname";
$commands[1] = "$opal_executable --gamma 45 --gamma_term 2 --lambda 45 --lambda_term 45 --cost VTML200 $input_fname";
$commands[2] = "$mummals_executable $input_fname -ss 3 -solv 2 -unaligned 1 -param $mummals_hmm_path/dataset_0.15_0.20_0.60_abcd.dali.solv2_ss3.mat -outfile $temp_folder/mummals.aln 2>/dev/null >/dev/null ; perl convertClustalToFasta.pl $temp_folder/mummals.aln $input_fname";
$commands[3] = "$opal_executable --gamma 45 --gamma_term 18 --lambda 42 --lambda_term 39 --cost VTML200 $input_fname";
$commands[4] = "$probcons_executable -c 2 -ir 500 -pre 0 $input_fname";
$commands[5] = "$probcons_executable -c 2 -ir 0 -pre 3 $input_fname";
$commands[6] = "$opal_executable --gamma 95 --gamma_term 4 --lambda 42 --lambda_term 39 --cost VTML40 $input_fname";
$commands[7] = "$kalign_executable -bonus 1 -gpo 70 -gpe 10 -tgpe 5 $input_fname";
$commands[8] = "$mummals_executable $input_fname -ss 1 -solv 1 -unaligned 0 -param $mummals_hmm_path/dataset_0.00_0.10_0.60_abcd.dali0.solv1_ss1.mat -outfile $temp_folder/mummals.aln 2>/dev/null >/dev/null ; perl convertClustalToFasta.pl $temp_folder/mummals.aln $input_fname";
$commands[9] = "$opal_executable --gamma 95 --gamma_term 38 --lambda 40 --lambda_term 40 --cost BLOSUM62 $input_fname";
$commands[10] = "$clustalo_executable --iter=3 --max-guidetree-iterations=3 --max-hmm-iterations=1 --full --full-iter --in $input_fname";
$commands[11] = "$opal_executable --gamma 45 --gamma_term 18 --lambda 40 --lambda_term 40 --cost VTML40 $input_fname";
$commands[12] = "$kalign_executable -bonus 0 -gpo 70 -gpe 7 -tgpe 3.5 $input_fname";
$commands[13] = "$kalign_executable -bonus 0 -gpo 55 -gpe 8.5 -tgpe 3.5 $input_fname";
$commands[14] = "$opal_executable --gamma 45 --gamma_term 18 --lambda 45 --lambda_term 45 --cost BLOSUM62 $input_fname";
$commands[15] = "$mafft_executable --op 0.7515 --ep 0.123 --bl 62 $input_fname";
$commands[16] = "$opal_executable --gamma 70 --gamma_term 27 --lambda 42 --lambda_term 39 --cost BLOSUM62 $input_fname";
$commands[17] = "$probcons_executable -c 3 -ir 100 -pre 1 $input_fname";
$commands[18] = "$probcons_executable -c 1 -ir 0 -pre 2 $input_fname";
$commands[19] = "$mummals_executable $input_fname -ss 1 -solv 1 -unaligned 1 -param $mummals_hmm_path/dataset_0.15_0.20_0.60_abcd.dali.solv1_ss1.mat -outfile $temp_folder/mummals.aln 2>/dev/null >/dev/null ; perl convertClustalToFasta.pl $temp_folder/mummals.aln $input_fname";
$commands[20] = "$probalign_executable -T 3 -go 11 -ge 0.5 $input_fname";
$commands[21] = "$probcons_executable -c 0 -ir 500 -pre 2 $input_fname";
$commands[22] = "$opal_executable --gamma 45 --gamma_term 18 --lambda 45 --lambda_term 45 --cost VTML200 $input_fname";
$commands[23] = "$opal_executable --gamma 70 --gamma_term 3 --lambda 40 --lambda_term 40 --cost BLOSUM62 $input_fname";
$commands[24] = "$probalign_executable -T 3 -go 22 -ge 0.5 $input_fname";

my $facet_max = 0;
my $max_index = -1;
foreach my $i(0...$cardinality-1){
	print STDERR "Running command ".($i+1)."\r";
	system("$commands[$i] > $temp_folder/$i.mfa 2>/dev/null");
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

