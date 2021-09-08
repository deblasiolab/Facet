package facet;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

public class ArgumentHandler {
	
	public double percent_identity = 0;
	public double average_replacement_score = 0.105434529;
	public double gap_open_density =  0.172122922;
	public double gap_extension_density = 0;
	public double structure_percent_identity = 0.176015402;
	public double structure_gap_coil_percentage = 0;
	public double gap_phylogeny_consensus = 0;
	public double core_column_phylogeny_consensus = 0;
	public double core_column_coverage = 0;
	public double information_content = 0;
	public double supporting_probability = 0.200589481;
	public double blockiness = 0.174107269;
	public double gap_consistancy = 0;
	public double sequence_consistancy = 0;
	
	public double constant = 0.171730397;

	public String alignment_fname = null;
	public String structure_seqs_fname = null;
	public String structure_prob_fname = null;
	
  public boolean verbose = false;

	private boolean still_default = true;
	private void reset(){
		still_default = false;
		percent_identity = 0;
		average_replacement_score = 0;
		gap_open_density =  0;
		gap_extension_density = 0;
		structure_percent_identity = 0;
		structure_gap_coil_percentage = 0;
		gap_phylogeny_consensus = 0;
		core_column_phylogeny_consensus = 0;
		core_column_coverage = 0;
		information_content = 0;
		supporting_probability = 0;
		blockiness = 0;

	}
	
	private double coefficient_sum(){
		return (percent_identity + 
		average_replacement_score + 
		gap_open_density + 
		gap_extension_density + 
		structure_percent_identity + 
		structure_gap_coil_percentage + 
		gap_phylogeny_consensus + 
		core_column_phylogeny_consensus + 
		core_column_coverage + 
		information_content + 
		supporting_probability + 
		blockiness);
	}
	
	public ArgumentHandler (String argString) {
		this(argString.split("\\s"));
	}
	
	public ArgumentHandler (String[] argv) {
		LongOpt[] longopts = new LongOpt[17];
		int longopts_index=0;
		longopts[longopts_index++] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
		
		longopts[longopts_index++] = new LongOpt("percent_identity", LongOpt.REQUIRED_ARGUMENT, null, '1');
		longopts[longopts_index++] = new LongOpt("average_replacement_score", LongOpt.REQUIRED_ARGUMENT, null, '2');
		longopts[longopts_index++] = new LongOpt("gap_open_density", LongOpt.REQUIRED_ARGUMENT, null, '3');
		longopts[longopts_index++] = new LongOpt("gap_extension_density", LongOpt.REQUIRED_ARGUMENT, null, '4');
		longopts[longopts_index++] = new LongOpt("structure_percent_identity", LongOpt.REQUIRED_ARGUMENT, null, '5');
		longopts[longopts_index++] = new LongOpt("structure_gap_coil_percentage", LongOpt.REQUIRED_ARGUMENT, null, '6');
		longopts[longopts_index++] = new LongOpt("gap_phylogeny_consensus", LongOpt.REQUIRED_ARGUMENT, null, '7');
		longopts[longopts_index++] = new LongOpt("core_column_phylogeny_consensus", LongOpt.REQUIRED_ARGUMENT, null, '8');
		longopts[longopts_index++] = new LongOpt("core_column_coverage", LongOpt.REQUIRED_ARGUMENT, null, '9');
		longopts[longopts_index++] = new LongOpt("information_content", LongOpt.REQUIRED_ARGUMENT, null, 'A');
		longopts[longopts_index++] = new LongOpt("supporting_probability", LongOpt.REQUIRED_ARGUMENT, null, 'C');
		longopts[longopts_index++] = new LongOpt("blockiness", LongOpt.REQUIRED_ARGUMENT, null, 'D');
		longopts[longopts_index++] = new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v');

		longopts[longopts_index++] = new LongOpt("alignment", LongOpt.REQUIRED_ARGUMENT, null, 'i');
		longopts[longopts_index++] = new LongOpt("structure_seqs", LongOpt.REQUIRED_ARGUMENT, null, 's');
		longopts[longopts_index++] = new LongOpt("structure_prob", LongOpt.REQUIRED_ARGUMENT, null, 'p');

		Getopt g = new Getopt("facet", argv, "1:2:3:4:5:6:7:8:9:A:C:D:vi:s:p:h", longopts);		

		int c;
		while ((c = g.getopt()) != -1) {
			String arg = g.getOptarg();
            //System.out.println((char)c + " -- " + arg);
			switch (c)  {
				case '1':
					if(still_default) reset();
					percent_identity = Double.parseDouble(arg);
					break;
				case '2':
					if(still_default) reset();
					average_replacement_score = Double.parseDouble(arg);
					break;
				case '3':
					if(still_default) reset();
					gap_open_density = Double.parseDouble(arg);
					break;
				case '4':
					if(still_default) reset();
					gap_extension_density = Double.parseDouble(arg);
					break;
				case '5':
					if(still_default) reset();
					structure_percent_identity = Double.parseDouble(arg);
					break;
				case '6':
					if(still_default) reset();
					structure_gap_coil_percentage = Double.parseDouble(arg);
					break;
				case '7':
					if(still_default) reset();
					gap_phylogeny_consensus = Double.parseDouble(arg);
					break;
				case '8':
					if(still_default) reset();
					core_column_phylogeny_consensus = Double.parseDouble(arg);
					break;
				case '9':
					if(still_default) reset();
					core_column_coverage = Double.parseDouble(arg);
					break;
				case 'A':
					if(still_default) reset();
					information_content = Double.parseDouble(arg);
					break;
				case 'C':
					if(still_default) reset();
					supporting_probability = Double.parseDouble(arg);
					break;
				case 'D':
					if(still_default) reset();
					blockiness = Double.parseDouble(arg);
					break;
        case 'v':
          verbose = true;
          break;
        case 'i':
					alignment_fname = arg;
					break;
				case 's':
					structure_seqs_fname = arg;
					break;
				case 'p':
					structure_prob_fname = arg;
					break;
			}
		}
		constant = 1 - coefficient_sum();
		if(constant < 0){
			throw new IllegalArgumentException("The sum of the input coefficients must be less than or equal to 1, the input coeffieicnt sum is "+coefficient_sum());
		}
	}	
}
