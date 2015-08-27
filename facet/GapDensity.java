package facet;

public class GapDensity {
	
	public static String open(Configuration c){
		return "Gap Open Density" + ((c.normalizeBigN)?" N":"");
	}
	public static String extension(Configuration c){
		return "Gap Extension Density"+ ((c.normalizeBigN)?" N":"");
	}
	public static String consensus(Configuration c){
		return "Gap Consistancy";
	}
	
	public static float open(FacetAlignment a, Configuration c){
		int[] counts = run_loops(a,c);
		int number_opens = counts[1];
		int number_gaps = counts[0];
		if(c.normalizeBigN){
			//System.err.println(number_opens+"/"+a.bigN);
			return (float) 1.0-(number_opens/a.bigN);
		}else{
			return (float) 1.0-(number_opens/number_gaps);
		}
	}
	
	public static float extension(FacetAlignment a, Configuration c){
		int[] counts = run_loops(a,c);
		int number_gaps = counts[0];
		int total_width = counts[2];
		if(c.normalizeBigN){
			//System.err.println(number_gaps+"/"+a.bigN);
			return (float) 1.0-(number_gaps/a.bigN);
		}else{
			return (float) 1.0-(number_gaps/total_width);
		}
	}
	
	private static int[] run_loops(FacetAlignment a, Configuration c){
		int number_gaps = 0 ; 
		int number_opens = 0 ; 
		int total_width = 0;
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
		int[] rtn  = new int[3];
		rtn[0] = number_gaps;
		rtn[1] = number_opens;
		rtn[2] = total_width;
		return rtn;
	}
	
	private static boolean same_gap(FacetAlignment a, int i, int j){
		if(i>=a.width || j>=a.width) return false;
		boolean same = true;
		for(int k = 0; k < a.numberOfSequences;k++){
			if((a.sequence[k].charAt(i) == '-' && a.sequence[k].charAt(j) != '-') ||
					(a.sequence[k].charAt(i) != '-' && a.sequence[k].charAt(j) == '-')){
				same = false;
			}
		}
		return same;
	}
	
	public static double consensus(FacetAlignment a, Configuration c){

		boolean gap_cols[] = new boolean[a.width];
		int gap_group[] = new int[a.width];
		int gap_g = 0;
		for(int i=0;i<a.width;i++){
			gap_cols[i] = false;
			for(int j=0;j<a.numberOfSequences;j++){
				if(a.sequence[j].charAt(i) == '-'){
					gap_cols[i] = true;
				}
			}
			if(gap_cols[i]){
				boolean same = true;
				if(i>1){
					same = same_gap(a, i,i-1);
				}
				if(!same){ gap_g++; }
				gap_group[i] = gap_g;
			}else{
				gap_group[i] = 0;
			}
		}

		int gap_len[] = new int[a.width];
		boolean gap_key[] = new boolean[a.width];
		int len = 1;
		int start = 0;
		for(int i=1;i<a.width;i++){
			gap_len[i] = 0;
			if(gap_cols[i]){
				if(gap_group[i]!=gap_group[i-1]){
					gap_len[start] = len;
					len = 1;
					start = i;
				}else{
					len++;
				}
			}
		}
		gap_len[start] = len;

		int last_key = 0;
		gap_key[0] = gap_cols[0];
		for(int i=1;i<a.width;i++){
			gap_key[i] = false;
			if(gap_len[i]>1){
				if(!same_gap(a,last_key,i)){
					gap_key[i] = true;
					last_key = i;
				}
			}
		}
		
		int count_total=0;
		int count_satisfy=0;
		for(int i=0;i<a.width;i++){
			for(int j=i+1;j<a.width;j++){
				if(gap_key[i] && gap_key[j]){
					int cc = 0;
					int ce = 0;
					int ec = 0;
					int ee = 0;
					for(int k=0;k<a.numberOfSequences;k++){
						if(a.sequence[k].charAt(i) == '-' && a.sequence[k].charAt(j) == '-') cc++;
						else if(a.sequence[k].charAt(i) == '-') ce++;
						else if(a.sequence[k].charAt(j) == '-') ec++;
						else ee++;
						
						
					}
					//System.err.println("(" + i + "," + j + ")\t(" + cc + "," + ec + "," + ce + "," + ee + ")");
					
					
					count_total++;
					if(ee==0 || ec==0 || ce==0 || cc==0) count_satisfy++;
				}
			}
		}
		//System.err.println((double)count_satisfy + "/" + (double)count_total);
		if(count_total==0){ return 0; }
		return ((double)count_satisfy/(double)count_total);
	}
	
}
