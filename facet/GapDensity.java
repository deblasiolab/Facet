package facet;

public class GapDensity {
	private static int number_gaps;
	private static int number_opens;
	private static int total_width;
	public static float open(FacetAlignment a, Configuration c){
		run_loops(a,c);
		if(c.normalizeBigN){
			return (float) 1.0-(number_opens/a.bigN);
		}else{
			return (float) 1.0-(number_opens/number_gaps);
		}
	}
	
	public static float extension(FacetAlignment a, Configuration c){
		run_loops(a,c);
		if(c.normalizeBigN){
			return (float) 1.0-(number_gaps/a.bigN);
		}else{
			return (float) 1.0-(number_gaps/total_width);
		}
	}
	
	private static void run_loops(FacetAlignment a, Configuration c){
		number_gaps = 0 ; number_opens = 0 ; total_width = 0;
		for(int i=0;i<a.numberOfSequences;i++){
			boolean in_gap = false;
			for(int j=0;j<a.width;j++){
				total_width++;
				if(a.sequence[i].charAt(j) == '-'){
					if(!in_gap) number_opens++;
					number_gaps++;
					in_gap = true;
				}else{
					in_gap = false;
				}
			}
		}
	}
	
	public static double consensus(FacetAlignment a, Configuration c){
		int count_total=0;
		int count_satisfy=0;
		for(int i=0;i<a.width;i++){
			for(int j=i+1;j<a.width;j++){
				count_total++;
				int cc = 0;
				int ce = 0;
				int ec = 0;
				int ee = 0;
				for(int k=0;k<a.numberOfSequences;k++){
					if(a.sequence[k].charAt(i) == '-' && a.sequence[k].charAt(j) == '-') cc++;
					else if(a.sequence[k].charAt(i) == '-') ce++;
					else if(a.sequence[k].charAt(j) == '-') ec++;
					else ee++;
					
					count_total++;
					if(ee==0 || ec==0 || ce==0 || cc==0) count_satisfy++;
				}
			}
		}
		if(count_total==0){ return 0; }
		return ((double)count_satisfy/(double)count_total);
	}
	
}
