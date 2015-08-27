package facet;

public class Blockiness {
	private static int min(int i, int j){
		return (i>j)?j:i;
	}
	public static String evaluate(Configuration c){
		return "Blockiness";
	}
	public static float evaluate(FacetAlignment a, Configuration c){
		int distance_matrix[][] = new int[a.width+1][a.width+1];
		char type_matrix[][] = new char[a.width+1][a.width+1];
		int min_h=4, min_e=3, min_c=2;
		int max_min = (min_h>min_e && min_h>min_c)?min_h:((min_c>min_e)?min_c:min_h);
		for(int i=0;i<a.width;i++){
			boolean rowIn[][] = new boolean[a.numberOfSequences][3];
			for(int k=0;k<a.numberOfSequences;k++){
				rowIn[k][0] = c.hasStructureType(a.structure[k].charAt(i), 'H');;
				rowIn[k][1] = c.hasStructureType(a.structure[k].charAt(i), 'E');;
				rowIn[k][2] = c.hasStructureType(a.structure[k].charAt(i), 'C');;
			}
			for(int j=i+1;j<min(i+2*max_min+1,a.width);j++){
				int ct[] = new int[3];
				for(int k=0;k<a.numberOfSequences;k++){
					rowIn[k][0] &= c.hasStructureType(a.structure[k].charAt(j), 'H');
					rowIn[k][1] &= c.hasStructureType(a.structure[k].charAt(j), 'E');;
					rowIn[k][2] &= c.hasStructureType(a.structure[k].charAt(j), 'C');;
					ct[0] += (rowIn[k][0])?1:0;
					ct[1] += (rowIn[k][1])?1:0;
					ct[2] += (rowIn[k][2])?1:0;
				}
				if((j-i)>=min_h && (j-i)<2*min_h){
					//int score = (j-i) * ((ct[0]-1)*ct[0])/2;
					int score = 0;
					for(int l=i;l<=j;l++){
						int num = 0;
						for(int k=0;k<a.numberOfSequences;k++){
							if(rowIn[k][0] && a.sequence[k].charAt(l)!='-'){
								num++;
							}
						}
						score += ((num*(num-1))/2);
					}
					if(score>distance_matrix[i][j+1]){
						distance_matrix[i][j+1] = score;
						type_matrix[i][j+1] = 'H';
					}
				}
				if((j-i)>=min_e && (j-i)<2*min_e){
					//int score = (j-i) * ((ct[1]-1)*ct[1])/2;
					int score = 0;
					for(int l=i;l<=j;l++){
						int num = 0;
						for(int k=0;k<a.numberOfSequences;k++){
							if(rowIn[k][1] && a.sequence[k].charAt(l)!='-'){
								num++;
							}
						}
						score += ((num*(num-1))/2);
					}
					if(score>distance_matrix[i][j+1]){
						distance_matrix[i][j+1] = score;
						type_matrix[i][j+1] = 'E';
					}
				}
				if((j-i)>=min_c && (j-i)<2*min_c){
					//int score = (j-i) * ((ct[2]-1)*ct[2])/2;
					int score = 0;
					for(int l=i;l<=j;l++){
						int num = 0;
						for(int k=0;k<a.numberOfSequences;k++){
							if(rowIn[k][2] && a.sequence[k].charAt(l)!='-'){
								num++;
							}
						}
						score += ((num*(num-1))/2);
					}
					if(score>distance_matrix[i][j+1] || type_matrix[i][j+1] == '\u0000'){
						distance_matrix[i][j+1] = score;
						type_matrix[i][j+1] = 'C';
					}
				}
			}
		}
		
		
		int max[] = new int[a.width+1]; //max path from 0 to i
		for(int i=0;i<=a.width;i++){
			//from me, all the longest paths directly from me
			for(int j=i+1;j<=a.width;j++){
				int temp = distance_matrix[i][j] + max[i];
				if(max[j]<temp){
					max[j] = temp;
				}
			}
		}
		
	    int num_replacements = 0;
		for (int i=0; i<a.numberOfSequences; i++) {
			for (int j=i+1; j<a.numberOfSequences; j++) {
				for (int k=0; k<a.width; k++) {
					if(a.sequence[i].charAt(k)!='-' && a.sequence[j].charAt(k)!='-'){
						num_replacements++;
					}
				}
			}
		}
		return (float)max[a.width]/num_replacements;
	}
}
