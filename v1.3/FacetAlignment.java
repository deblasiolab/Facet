package facet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.lang.StringBuffer;

public class FacetAlignment {
	public int numberOfSequences;
	public int width;
	public String[] name;
	public String[] sequence;
	public String[] structure;
	public float[][][] structure_prob;
	public float bigN;
	
	public FacetAlignment(String[] se, String[] st, String[] na, String[][][] sp){
		sequence = se.clone();
		name = na.clone();
		structure = st.clone();
		int max1 = 0,max2 = 0;
		width=sequence[0].length();
		numberOfSequences = se.length;
		structure_prob = new float[numberOfSequences][width][3];
		for(int i=0;i<numberOfSequences;i++){
			int k=0;
			if(st[i].length()>max1){
				max2 = max1;
				max1 = st[i].length();
			}else if(st[i].length()>max2) max2 = st[i].length();
			for(int j=0;j<width;j++){
				if(sequence[i].charAt(j) == '-'){
					structure[i] = (new StringBuffer(structure[i])).insert(j, "-").toString();
					structure_prob[i][j][0]=-1;
					structure_prob[i][j][1]=-1;
					structure_prob[i][j][2]=-1;
				}
				else{
					structure_prob[i][j][0] = Float.parseFloat(sp[i][k][0]);
					structure_prob[i][j][1] = Float.parseFloat(sp[i][k][1]);
					structure_prob[i][j][2] = Float.parseFloat(sp[i][k][2]);
					k++;
				}
			}
		}

		setBigN(max1,max2);
		
	}
	
	public FacetAlignment(int[][] al, char[] chars, String structureFile, String structureProbFile) throws FileNotFoundException{
		numberOfSequences = al.length;
		sequence = new String[al.length];
		name = new String[al.length];
		for(int i=0;i<al.length;i++){
			sequence[i] = "";
			name[i] = "";
			for(int j=0;j<al[i].length;j++){
				if(al[i][j]>=0 && al[i][j]<chars.length){
					sequence[i] = sequence[i].concat(String.valueOf(chars[al[i][j]]));
				}else{
					sequence[i] = sequence[i].concat("-");
				}
			}
			//System.err.println(sequence[i]);
		}
		width = sequence[0].length();
		readStructureFromFile(structureFile);
		readStructureProbFromFile(structureProbFile);
	}
	
	public FacetAlignment(String seqFile, String structureFile, String structureProbFile) throws FileNotFoundException{
		numberOfSequences = numberOfSequencesInFile(seqFile);
		readSequencesFromFile(seqFile);
		width = sequence[0].length();
		readStructureFromFile(structureFile);
		readStructureProbFromFile(structureProbFile);
	}
	
	public static int numberOfSequencesInFile(String filename) throws FileNotFoundException{
		int numberOfNames = 0;
		Scanner sc = new Scanner(new File(filename));
		sc.useLocale(Locale.US);
		while(sc.hasNext()){
			String line = sc.next();
			if(line.charAt(0) == '>'){
				numberOfNames++;
			}
		}
		return numberOfNames;
	}
	
	private int numberNotGap(String s){
		int ct = 0;
		for(int i=0;i<s.length();i++){
			ct += (s.charAt(i)!='-')?1:0;
		}
		return ct;
	}
	
	private void setBigN(int max1, int max2){
		bigN = (float) ((((numberOfSequences+1.0)*((float)numberOfSequences))/2.0)*max1+max2);
	}
	
	public void readSequencesFromFile(String filename) throws FileNotFoundException{
		Scanner sc = new Scanner(new File(filename));
		sc.useLocale(Locale.US);
		int i = -1;
		sequence = new String[numberOfSequences];
		name = new String[numberOfSequences];
		int max1 = 0, max2 = 0;
		while(sc.hasNext()){
			String line = sc.next();
			if(line.charAt(0) == '>'){
				if(i>=0){
					int ct = numberNotGap(sequence[i]);
					if(ct>max1){max2 = max1; max1 = ct;}
					else if(ct>max2){max2 = ct;}
				}
				i++;
				name[i] = line.substring(1);
				sequence[i] = "";
			}else{
				sequence[i] = sequence[i].concat(line);
			}
		}
		int ct = numberNotGap(sequence[i]);
		if(ct>max1){max2 = max1; max1 = ct;}
		else if(ct>max2){max2 = ct;}
		setBigN(max1,max2);
	}
	
	public void readStructureFromFile(String filename) throws FileNotFoundException{
		Scanner sc = new Scanner(new File(filename));
		sc.useLocale(Locale.US);
		int i = 0;
		structure = new String[numberOfSequences];
		while(sc.hasNext()){
			structure[i] = sc.next();
			for(int j=0;j<width;j++){
				System.err.println(sequence[i] + "\n" + structure[i]);
				//System.err.println("The structure length of " + i + " is " + structure[i].length());
				if(sequence[i].charAt(j) == '-'){
					structure[i] = (new StringBuffer(structure[i])).insert(j, "-").toString();
				}
			}
			i++;
		}
	}
	
	public void readStructureProbFromFile(String filename) throws FileNotFoundException{
		Scanner sc = new Scanner(new File(filename));
		sc.useLocale(Locale.US);
		int i = 0, j=0, k=0;
		structure_prob = new float[numberOfSequences][width][3];
		while(sc.hasNextFloat()){
			if(sequence[i].charAt(j) == '-'){
				structure_prob[i][j][k] = -1;
			}else{
				structure_prob[i][j][k] = sc.nextFloat();
			}
			
			j++;
			if(j>=width){
				j = 0;
				k++;
			}
			if(k>=3){
				k = 0;
				i++;
			}
		}
	}
}
