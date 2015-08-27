package facet;

public class Consistancy {
	private static int consistant_pairs;
	private static int pairs;
	private static float consistant_pair_float;
	
	public static String sequence(Configuration c){
		return "Sequence Consistancy" + ((c.equivelanceClassSize == 20)?"":(" E" + c.equivelanceClassSize));
	}
	public static String gap(Configuration c){
		return "Gap Consistancy";
	}
	
	public static float sequence(FacetAlignment a, Configuration c){
		pairs = 0; consistant_pairs = 0;
		boolean key[] = new boolean[a.width];
		key[0] = true;
		boolean is_consensus[][] = new boolean[a.width][a.numberOfSequences];
		for(int i=0;i<a.width;i++){
			int max = 0;
			char cons = '-';
			for(int j1=0;j1<a.numberOfSequences;j1++){
				int ct = 0;
				for(int j2=j1+1;j2<a.numberOfSequences;j2++){
					ct += (c.basesEquivelant(a.sequence[j1].charAt(i),a.sequence[j1].charAt(i)))?1:0;
				}
				if(ct>max){
					max = ct;
					cons = a.sequence[j1].charAt(i);
				}
			}
			for(int j=0;j<a.numberOfSequences;j++){
				is_consensus[i][j] = c.basesEquivelant(a.sequence[j].charAt(i),cons);
			}
		}
		
		int last_key = 0;
		key[0] = true;
		for(int i=1;i<a.width;i++){
			key[i] = false;
			for(int j=0;j<a.numberOfSequences;j++){
				key[i] |= (is_consensus[i][j]!=is_consensus[i-1][j]);
			}
			if(key[i] && i-last_key<2){
				key[last_key] = false;
			}
			if(key[i]){
				last_key = i;
			}
		}
		
		last_key = -1;
		boolean changed_one = true;
		int num_keys=0;
		while(changed_one){
			changed_one = false;
			num_keys = 0;
			for(int i=1;i<a.width;i++){
				if(key[i] && last_key == -1){last_key = i; num_keys++;}
				else if(key[i]){
					key[i] = false;
					for(int j=0;j<a.numberOfSequences;j++){
						key[i] |= (is_consensus[i][j]!=is_consensus[i-1][j]);	
					}
					if(key[i]){
						last_key = i;
						num_keys++;
					}else{
						changed_one = true;
					}
				}
			}
		}
		
		boolean check[][] = new boolean[num_keys][a.numberOfSequences];	
		int ct = 0;
		for(int i=1;i<a.width;i++){
			if(key[i]){
				for(int j=0;j<a.numberOfSequences;j++){
					check[ct][j] = (is_consensus[i][j]);
				}
				ct++;
			}
		}
		checkConsistancy(check);

		if(c.phylogenyGradient){
			return (float) ((float)consistant_pair_float/pairs);
		}else{
			return (float) ((float)consistant_pairs/pairs);
		}
	}
	
	public static float gap(FacetAlignment a, Configuration c){
		pairs = 0; consistant_pairs = 0;
		boolean key[] = new boolean[a.width];
		key[0] = true;
		int last_key = 0;
		for(int i=1;i<a.width;i++){
			key[i] = false;
			for(int j=0;j<a.numberOfSequences;j++){
				boolean g1 = (a.sequence[j].charAt(i)=='-');
				boolean g2 = (a.sequence[j].charAt(i-1)=='-');
				key[i] |= (g1!=g2);
			}
			if(key[i] && i-last_key<2){
				key[last_key] = false;
			}
			if(key[i]){
				last_key = i;
			}
		}
		
		last_key = -1;
		boolean changed_one = true;
		int num_keys=0;
		while(changed_one){
			changed_one = false;
			num_keys = 0;
			for(int i=1;i<a.width;i++){
				if(key[i] && last_key == -1){last_key = i; num_keys++;}
				else if(key[i]){
					key[i] = false;
					for(int j=0;j<a.numberOfSequences;j++){
						boolean g1 = (a.sequence[j].charAt(i)=='-');
						boolean g2 = (a.sequence[j].charAt(last_key)=='-');
						key[i] |= (g1!=g2);
					}
					if(key[i]){
						last_key = i;
						num_keys++;
					}else{
						changed_one = true;
					}
				}
			}
		}
		boolean check[][] = new boolean[num_keys][a.numberOfSequences];	
		int ct = 0;
		for(int i=1;i<a.width;i++){
			if(key[i]){
				for(int j=0;j<a.numberOfSequences;j++){
					check[ct][j] = (a.sequence[j].charAt(i)=='-');
				}
				ct++;
			}
		}
		checkConsistancy(check);
		if(c.phylogenyGradient){
			return (float) ((float)consistant_pair_float/pairs);
		}else{
			return (float) ((float)consistant_pairs/pairs);
		}
	}
	
	private static void checkConsistancy(boolean a[][]){
		for(int i1=0;i1<a.length;i1++){
			for(int i2=i1+1;i2<a.length;i2++){
				int ct[][] = {{0,0},{0,0}};
				for(int j=0;j<a[i1].length;j++){
					ct[(a[i1][j])?1:0][(a[i2][j])?1:0]++;
				}
				int non0=0;
				int min = a[i1].length;
				for(int j1=0;j1<=1;j1++){
					for(int j2=0;j2<=1;j2++){
						non0 += (ct[j1][j2]>0)?1:0;
						min = (min<ct[j1][j2])?min:ct[j1][j2];
					}
				}
				if(non0<=3){
					consistant_pairs++;
				}
				consistant_pair_float = min/a[i1].length;
				pairs++;
			}
		}
	}
}
