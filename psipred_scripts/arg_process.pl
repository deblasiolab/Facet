#!/usr/bin/perl

#print STDERR "Feature Set Calculations\n";
#print STDERR "v0.1a\n";

my $help_message = "\n$0 [<options>] -<functions> [<file>]

Functions -- explainations of each function are at the end of this help message:
\t1\tPercent Identity
\t2\tAverage Replacement Score
\t3\tGap Open Density
\t4\tGap Extension Density
\t5\tStructure Sequence Percent Identity
\t6\tStructure Gap/Coil Percentage
\t7\tGap Phylogeny Consensus
\t8\tCore Column Phylogeny Consensus
\t9\tCore Column Coverage
\tA\tInformation Content

Options:
\t-t <fasta|clustal>\tFile type of input file, either ClustalW (aln) 
\t\t\t\tformat or multiple FASTA format [Default: fasta]
\t-r <name>\t\tReplacement scoring matrix, note bust be a file in 
\t\t\t\tthis directory.  Use the Blossum65.pl file as a guide for custom 
\t\t\t\treplacement matricies [Default:Blossum65]
\t-e <20|15|10|6>\t\tEquvelance class to use. [Default: 20]
\t-n\t\t\tnon-gap column only normalization (vs. all columns) for core column percenatge
\t-b\t\t\tnormalize core columns percentage by total number of bases not columns
\t-p\t\t\tcontinuous phylogeny calculation for 4-gamites condition (vs. 1/0)
\t-c <number>\t\tCore column identity threshold value [Default: 0.9]
\t-h <number>\t\tCore column identity threshold for phylogeny analysis [Default: 0.7]
\t-d [0|1|2|3]\t\tDebug level [Default: 0]
\t-g <number>\t\tGapping group size minimum [Default: 1]
\t-f [TRUE|FALSE]\t\tCore column only analysis for Structure Identity and Average Subsditution Score
\t-m \t\tmax-length normalization for ARS, GOD and GED
\t-s \t\tstructure probabilities for SGCP, SSPI and Core Calculations
\t-a \t\talignment specific background frequency in IC
\t-o \t\tStructure Based Core Calculations
\t-k\t\tTwo class only phylogeny calculations

";
# left as options for input params
# a i j l o q u v w x y 

our $file;
our $sFile;
our @files;
our $ftype;
our $repl_name; 
our %features;
our $equiv_class;
our $norm_parameter;
our $phylogeny_exact;
our $core_threshold;
our $debug = 0;
our $core_phylo_threshold = .9;
our $small_group_cutoff;
our $core_only_analysis;
our $structure_pobability;
our $specific_background_frequency;
our $structure_cores;
our $two_class_phylogeny = 0;
our %feature_names = (
												"1" => "PercentIdentity.pl",							#1
												"2" => "AverageReplacement.pl",						#2
												"3" => "GapOpenDensity.pl",								#3
												"4" => "GapExtensionDensity.pl",					#4
												"5" => "StructurePairwiseIdentity.pl",		#5
												"6" => "StructureGapInCoil.pl",						#6
												"7" => "GapPhylogeny.pl",								  #7
												"8" => "CorePhylogeny.pl",								#8
												"9" => "CoreColumnPercentage.pl",		  					#9
												"A" => "InformationContent.pl",						
												"B" => "normd.pl",						
												"C" => "Blockiness.pl",
												"D" => "supporting_probabilities.pl",
												"E" => "core_column_svm.pl");							

for(my $i=0;$i<scalar(@ARGV);$i++){
	if($ARGV[$i] eq "--help" || $ARGV[$i] eq "-?"){
		print $help_message;
		exit();
	}
	my $char = substr($ARGV[$i],0,1);
  if($char eq '-'){
		$char = substr($ARGV[$i],1,1);
		#print "char: $char\n";
		if($char eq "t"){
			$ftype = $ARGV[++$i];
		}elsif($char eq "r"){
			$repl_name = $ARGV[++$i];
		}elsif($char eq "e"){
			$equiv_class = $ARGV[++$i];
		}elsif($char eq "n"){
			$norm_parameter = "nongap";
		}elsif($char eq "p"){
			$phylogeny_exact = "close";
		}elsif($char eq "c"){
			$core_threshold = 0+$ARGV[++$i];
		}elsif($char eq "h"){
			$core_phylo_threshold = 0+$ARGV[++$i];
		}elsif($char eq "d"){
			$debug = 0+$ARGV[++$i];
		}elsif($char eq "g"){
			$small_group_cutoff = 0+$ARGV[++$i];
		}elsif($char eq "f"){
			$core_only_analysis = ((uc($ARGV[++$i]) eq "TRUE")?1:0);
		}elsif($char eq "b"){
			$norm_parameter = "acids";
		}elsif($char eq "m"){
			$big_n = 1;
		}elsif($char eq "s"){
			$structure_pobability = 1;
		}
		elsif($char eq "a"){
			$specific_background_frequency = 1;
		}
		elsif($char eq "o"){
			$structure_cores = 1;
		}
		elsif($char eq "k"){
			$two_class_phylogeny = 1;
		}
		
		else{
			for(my $j=1;$j<length($ARGV[$i]);$j++){
				$char = substr($ARGV[$i],$j,1);
				$features{$feature_names{$char}} = TRUE;
			}
		}
	}else{
    if($file eq ""){ $file = $ARGV[$i]; }
		else{ $sFile = $ARGV[$i]; }
		push(@files,$ARGV[$i]);
  }
}

our %extra_args = (
	"5" => "-c $core_phylo_threshold"
);

return 1;
