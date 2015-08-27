package facet;

public class Support {
	
	public static String probability(Configuration c){
		return "Supporting Probability";
	}
	
	public static float probability(FacetAlignment a, Configuration c){
		float rtn = 0;
		int ct = 0;
		for(int column=2;column<(a.width-3);column++){
			for(int i=0;i<a.numberOfSequences;i++){
				for(int j=i+1;j<a.numberOfSequences;j++){
					for(int k=0;k<a.numberOfSequences;k++){
						if(a.sequence[i].charAt(column) != '-' && a.sequence[j].charAt(column) != '-' && a.sequence[k].charAt(column) != '-') {
							if(i!=k && j!=k){
								rtn += scoreTripleNeighborhood(a,i,j,k,column);
								ct++;
							}
						}
					}
				}
			}
		}
		//System.err.println(rtn + "/" + ct);
		return rtn/ct;
	}
	
	private static float[] scoreTriple(FacetAlignment a, int i, int j, int k, int column){
		if(a.sequence[i].charAt(column) == '-' || a.sequence[j].charAt(column) == '-' || a.sequence[k].charAt(column) == '-') return null;
		float sum_i = a.structure_prob[i][column][0] + a.structure_prob[i][column][1] + a.structure_prob[i][column][2];
		float sum_j = a.structure_prob[j][column][0] + a.structure_prob[j][column][1] + a.structure_prob[j][column][2];
		float sum_k = a.structure_prob[k][column][0] + a.structure_prob[k][column][1] + a.structure_prob[k][column][2];
		float rtn[] = new float[2];
		rtn[0] = a.structure_prob[i][column][1]*a.structure_prob[k][column][1];
		rtn[0] += a.structure_prob[i][column][2]*a.structure_prob[k][column][2];
		rtn[0] /= (sum_i*sum_k);

		rtn[1] = a.structure_prob[j][column][1]*a.structure_prob[k][column][1];
		rtn[1] += a.structure_prob[j][column][2]*a.structure_prob[k][column][2];
		rtn[1] /= (sum_j*sum_k);
		return rtn;
	}
	
	private static float scoreTripleNeighborhood(FacetAlignment a, int i, int j, int k, int column){
		if(a.sequence[i].charAt(column) == '-' || a.sequence[j].charAt(column) == '-' || a.sequence[k].charAt(column) == '-') return 0;
		if(i==k || j==k) return 0;
		float rtn[] = new float[2];
		double weight[] = {0.07,0.24,0.38,0.24,0.07};
		for(int c=-2;c<=2;c++){
			float temp[] = scoreTriple(a,i,j,k,column+c);
			if(temp != null){
				rtn[0] += temp[0] * weight[c+2];
				rtn[1] += temp[1] * weight[c+2];
			}
		}
		return rtn[0]*rtn[1];
	}
}
