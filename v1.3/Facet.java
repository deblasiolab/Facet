package facet;

import java.io.FileNotFoundException;
import java.io.File;
import java.util.*;
import java.lang.StringBuffer;
import facet.*;
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
 		
        if(args.length!=3){
         System.err.println("input format: java Facet <sequence fasta> <psipred prediction> <psipred probabilities>\n");
         System.err.println("For examples of psipred file formats see README");
         System.exit(15);
        }
        
        try{
         FacetAlignment a = new FacetAlignment(args[0],args[1],args[2]);
         System.out.println(args[0] + ":\t" + Facet.defaultValue(a));
        }catch(FileNotFoundException e){
         System.err.println("ERROR: "+e.toString());
         System.exit(15);
        }
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
