package facet;

public class InformationContent{
	public static double evaluate(FacetAlignment a, Configuration c){
		double background_probability[] = new double[21];
		double column_probability[][] = new double[a.width][21];
		int total_count = 0;
		
		for(int j=0;j<=20;j++){
			background_probability[j] = 0;
		}
		
		for(int i=0;i<a.width;i++){
			int column_count = 0;
			for(int j=0;j<=20;j++){
				column_probability[i][j] = 0;
			}
			for(int j=0;j<a.numberOfSequences;j++){
				if(a.sequence[j].charAt(i) != '-'){
					column_count++;
					total_count++;
					background_probability[c.baseToInt(a.sequence[j].charAt(i))]++;
					column_probability[i][c.baseToInt(a.sequence[j].charAt(i))]++;
				}
			}
			for(int j=0;j<=20;j++){
				if(column_count != 0) column_probability[i][j] /= (double)column_count;
			}
		}
		
		for(int j=0;j<=20;j++){
			background_probability[j] /= (double)total_count;
		}
		
		double info_content = 0;
		for(int i=0;i<a.width;i++){
			for(int j=0;j<=20;j++){
				if(column_probability[i][j] != 0) info_content += (column_probability[i][j] * Math.log(column_probability[i][j]/background_probability[j]));
			}
		}
		
		return info_content/a.width;
	}
}
