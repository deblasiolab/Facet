package facet;

import java.util.*;



/**
 * 
 */

/**
 * @author deblasio
 *
 */
public class Facet {

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		ArgumentHandler argHandler = new ArgumentHandler(args);
			
		if(argHandler.alignment_fname == null){
			System.err.println("The alignment filename is required.");
			System.exit(15);
		}
		/*if(argHandler.structure_seqs_fname == null){
			System.err.println("The structure sequence filename is required.");
			System.exit(15);
		}
		if(argHandler.structure_prob_fname == null){
			System.err.println("The structure probability filename is required.");
			System.exit(15);
		}*/
		try{
			FacetAlignment a = new FacetAlignment(argHandler.alignment_fname, argHandler.structure_seqs_fname, argHandler.structure_prob_fname);
			System.out.println(argHandler.alignment_fname + ":\t" + Facet.value(a,argHandler));
			//System.out.println(argHandler.alignment_fname + ":\t" + Facet.defaultValue(a));
		}catch(Exception e){
			System.err.println("ERROR: "+e.toString());
			System.exit(15);
		}
	}
 
 	public static String value(FacetAlignment a, ArgumentHandler argHandler){
	    double total = 0;
      String rtn = "";
		Configuration c6 = new Configuration();
		Configuration c10 = new Configuration();
		Configuration c20 = new Configuration();
		c6.normalizeBigN = true;
		c10.normalizeBigN = true;
		c20.normalizeBigN = true;
		c6.equivelanceClassSize = 6;
		c10.equivelanceClassSize = 10;
		c20.equivelanceClassSize = 20;
		if(a.type == FacetAlignment.AlignmentType.DNA){
			c6.matrix = Configuration.ReplacementMatrix.DNA;
			c10.matrix = Configuration.ReplacementMatrix.DNA;
			c20.matrix = Configuration.ReplacementMatrix.DNA;
		}
		if(a.type == FacetAlignment.AlignmentType.RNA){
			c6.matrix = Configuration.ReplacementMatrix.RNA;
			c10.matrix = Configuration.ReplacementMatrix.RNA;
			c20.matrix = Configuration.ReplacementMatrix.RNA;
		}
		
    double RS = PercentIdentity.replacement_score(a, c20);
    double GO = GapDensity.open(a, c6);
    double GE = GapDensity.extension(a, c6);
    double GDC = GapDensity.consensus(a, c6);
    double PI = PercentIdentity.sequence(a, c10);
    double CCP = CoreColumn.percentage(a, c10);
    double CCC = CoreColumn.consensus(a, c20);
    double IC = InformationContent.evaluate(a, c20);
    double GC = Consistancy.gap(a, c20);
    double SC = Consistancy.sequence(a, c20);


		total += argHandler.average_replacement_score * 	RS;
		total += argHandler.gap_open_density * 				    GO;
		total += argHandler.gap_extension_density * 		  GE;
		total += argHandler.gap_phylogeny_consensus * 		GDC;
		total += argHandler.percent_identity *				    PI;
		total += argHandler.core_column_coverage *			  CCP;
		total += argHandler.core_column_phylogeny_consensus * CCC;
		total += argHandler.information_content *			    IC;
		total += argHandler.gap_consistancy * 				    GC;
		total += argHandler.sequence_consistancy * 			  SC;
		
    rtn += Double.toString(RS);
    rtn += "," + Double.toString(GO);
    rtn += "," + Double.toString(GE);
    rtn += "," + Double.toString(GC);
    rtn += "," + Double.toString(PI);
    rtn += "," + Double.toString(CCP);
    rtn += "," + Double.toString(CCC);
    rtn += "," + Double.toString(IC);
    rtn += "," + Double.toString(GC);
    rtn += "," + Double.toString(SC);
		
    if(a.type == FacetAlignment.AlignmentType.Protein){
      double SGCP = GapCoil.percentage(a, c6);
      double BL = Blockiness.evaluate(a, c6);
      double SPI = PercentIdentity.structure(a, c10); 
      double SP = Support.probability(a, c10);

			total += argHandler.structure_gap_coil_percentage * SGCP;
			total += argHandler.blockiness *					          BL;
			total += argHandler.structure_percent_identity *	  SPI;
			total += argHandler.supporting_probability * 		    SP;
    
       rtn += "," + Double.toString(SGCP);
       rtn += "," + Double.toString(BL);
       rtn += "," + Double.toString(SPI);
       rtn += "," + Double.toString(SP);

    }else if(argHandler.structure_gap_coil_percentage  != 0 || 
			argHandler.blockiness  != 0 || 
			argHandler.structure_percent_identity  != 0 || 
			argHandler.supporting_probability  != 0 ){
			throw new IllegalArgumentException("If the alignemnt type is protein structure based features must have 0 value coefficients");
		}

    if(argHandler.verbose) rtn += "\t";
    else rtn = "";
    rtn += Double.toString(total + argHandler.constant);
    //return (float) (total + argHandler.constant);
    return rtn;
 	}

	public static float defaultValue(FacetAlignment a){
		double total = 0.171730397;
		Configuration c = new Configuration();
		c.normalizeBigN = true;
		total += 0.105434529 * PercentIdentity.replacement_score(a, c);
		total += 0.172122922 * GapDensity.open(a, c);
		total += 0.174107269 * Blockiness.evaluate(a, c);
		c.equivelanceClassSize = 10;
		total += 0.176015402 * PercentIdentity.structure(a, c);
		total += 0.200589481 * Support.probability(a, c);
		return (float) total;
	}

}
