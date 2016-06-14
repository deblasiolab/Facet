package facet;

public class CoreColumn {
	public static float[] getCoreColumns(FacetAlignment a, Configuration c){
		float[] columns = new float[a.width];
		for(int i=0;i<a.width;i++){
			columns[i] = 1;
			int max_same = 0;
			for(int j=0;j<a.numberOfSequences;j++){
				if(a.sequence[j].charAt(i)=='-'){
					columns[i] = 0;
				}else{
					int same = 0;
					for(int k=0;k<a.numberOfSequences;k++){
						try{
							if(c.basesEquivelant(a.sequence[j].charAt(i), a.sequence[k].charAt(i))) same++;
						}catch(IllegalArgumentException e){
							
						}
					}
					if(same > max_same) max_same = same;
				}
			}
			if(columns[i]==1 && ((double)max_same/(double)a.numberOfSequences) > 0.9) columns[i] = 1;
			else if(columns[i]==1 && ((double)max_same/(double)a.numberOfSequences) > 0.35) columns[i] = (float) 0.5;
			else columns[i] = 0;
		}
		return columns;
	}

	public static String percentage(Configuration c){
		return "Core Column Percentage" + ((c.equivelanceClassSize == 20)?"":(" E" + c.equivelanceClassSize));
	}
	public static String consensus(Configuration c){
		return "Core Column Consistancy" + ((c.equivelanceClassSize == 20)?"":(" E" + c.equivelanceClassSize));
	}
	public static double percentage(FacetAlignment a, Configuration c){
		float core_columns[] = getCoreColumns(a,c);
		int ct_core = 0;
		for(int i=0;i<a.width;i++){
  			if(core_columns[i] > 0){ ct_core++; }
  			//else{System.err.print(" "); }
		}
		//System.err.println("");

		int normalize = a.width;
        	/*ct_core *= a.numberOfSequences;
        	normalize = 0;
        	for(int i=0;i<a.numberOfSequences;i++){
                	for(int j=0;j<a.width;j++){
                        	if(a.sequence[i].charAt(j)!='-'){
                                	normalize++;
                        	}
                	}
        	}*/


		if(normalize==0){ return 0; }
		return ((double)ct_core/(double)normalize);
	}
	
	public static double consensus(FacetAlignment a, Configuration c){
		float core_columns[] = getCoreColumns(a,c);
		int count_total=0;
		int count_satisfy=0;
		for(int i=0;i<a.width;i++){
			//System.err.print(((core_columns[i]>0)?"+":" "));
			for(int j=i+1;j<a.width;j++){
				if(core_columns[i]>0 && core_columns[j]>0){

					int cc = 0;
					int ce = 0;
					int ec = 0;
					int ee = 0;
					for(int k=0;k<a.numberOfSequences;k++){
						try{
							if(c.basesEquivelant(a.sequence[k].charAt(i), a.consensus[i]) &&
									c.basesEquivelant(a.sequence[k].charAt(j), a.consensus[j])) cc++;
							else if(c.basesEquivelant(a.sequence[k].charAt(i), a.consensus[i])) ce++;
							else if(c.basesEquivelant(a.sequence[k].charAt(j), a.consensus[j])) ec++;
							else ee++;
						}catch(IllegalArgumentException e){
							
						}
					}
					//System.err.println("(" + i + "," + j + ")\t(" + cc + "," + ce + "," + ec + "," + ee + ")");
					count_total++;
					if(ee==0 || ec==0 || ce==0 || cc==0){ 
						count_satisfy++;
						//System.err.print("+");
					}else{
						//System.err.print(" ");
					}
				}
			}
		}
		//System.err.println("");
		//System.err.println((double)count_satisfy +"/"+ (double)count_total);
		if(count_total==0){ return 0; }
		return ((double)count_satisfy/(double)count_total);
	}

}
