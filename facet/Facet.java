package facet;

import java.io.FileNotFoundException;
import java.io.File;
import java.util.*;
import java.lang.StringBuffer;
import facet.*;

import gnu.getOpt.*;
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
 		if(argHandler.structure_seqs_fname == null){
			System.err.println("The structure sequence filename is required.");
			System.exit(15);
		}
 		if(argHandler.structure_prob_fname == null){
			System.err.println("The structure probability filename is required.");
			System.exit(15);
		}
        try{
         FacetAlignment a = new FacetAlignment(argHandler.alignment_fname, argHandler.structure_seqs_fname, argHandler.structure_prob_fname);
         System.out.println(argHandler.alignment_fname + ":\t" + Facet.value(a,argHandler));
         //System.out.println(argHandler.alignment_fname + ":\t" + Facet.defaultValue(a));
        }catch(FileNotFoundException e){
         System.err.println("ERROR: "+e.toString());
         System.exit(15);
        }
 }
 
 public static float value(FacetAlignment a, ArgumentHandler argHandler){
	    double total = 0;
		Configuration c = new Configuration();
		c.normalizeBigN = true;
		total += argHandler.average_replacement_score * 	PercentIdentity.replacement_score(a, c);
		total += argHandler.gap_open_density * 				GapDensity.open(a, c);
		total += argHandler.gap_extension_density * 		GapDensity.extension(a, c);
		total += argHandler.gap_phylogeny_consensus * 		GapDensity.consensus(a, c);
		total += argHandler.structure_gap_coil_percentage * GapCoil.percentage(a, c);
		total += argHandler.blockiness *					Blockiness.evaluate(a, c);
		
		c.equivelanceClassSize = 10;
		total += argHandler.structure_percent_identity *	PercentIdentity.structure(a, c);
		total += argHandler.supporting_probability * 		Support.probability(a, c);
		total += argHandler.percent_identity *				PercentIdentity.sequence(a, c);
		total += argHandler.core_column_coverage *			CoreColumn.percentage(a, c);
		total += argHandler.core_column_phylogeny_consensus *CoreColumn.consensus(a, c);
		
		
		c.equivelanceClassSize = 20;
		total += argHandler.information_content *			InformationContent.evaluate(a, c);
		return (float) (total + argHandler.constant);
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
