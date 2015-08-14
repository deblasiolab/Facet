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
	public char[] consensus;
	public char[] structure_consensus;
	
	public enum AlignmentType {
		Protein,
		DNA,
		RNA
	}
	
	public AlignmentType type;
	
	private void setConsensus(){
		consensus = new char[width];
		//System.err.println("Consensus length:" + width);
		for(int i=0;i<width;i++){
			consensus[i] = sequence[0].charAt(i);
			int count = 0;
			for(int j=0;j<numberOfSequences;j++){
				int temp_count = 0;
				for(int k=0;k<numberOfSequences;k++){
					if(sequence[j].charAt(i) == sequence[k].charAt(i)) temp_count++;
				}
				if(temp_count>count){
					count = temp_count;
					consensus[i] = sequence[j].charAt(i);
				}
			}
			//System.err.print(consensus[i]);
		}
		//System.err.println();
		if(type == AlignmentType.Protein){
			structure_consensus = new char[width];
			for(int i=0;i<width;i++){
				double sum_c = 0;
				double sum_h = 0;
				double sum_e = 0;
				for(int j=0;j<numberOfSequences;j++){
					if(structure_prob[j][i][0]>=0){
						sum_c += structure_prob[j][i][0];
						sum_h += structure_prob[j][i][1];
						sum_e += structure_prob[j][i][2];
					}
				}
				if(sum_c>sum_h && sum_c>sum_e) structure_consensus[i] = 'C';
				else if(sum_h>sum_e) structure_consensus[i] = 'H';
				else structure_consensus[i] = 'E';
			}
		}
	}
	
	public FacetAlignment(String[] se, String[] st, String[] na, String[][][] sp){
		sequence = se.clone();
		name = na.clone();
		structure = st.clone();
		int max1 = 0,max2 = 0;
		width=sequence[0].length();
		numberOfSequences = se.length;
		structure_prob = new float[numberOfSequences][width][3];
		
		type=AlignmentType.Protein;
		
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
		setConsensus();
		
	}
	
	public FacetAlignment(int[][] al, char[] chars, String structureFile, String structureProbFile) throws Exception{
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
		
		if(structureFile == null && structureProbFile == null) type=AlignmentType.DNA;
		else if(structureFile != null && structureProbFile != null) type=AlignmentType.Protein;
		else throw new IllegalArgumentException("Either both structure and probability need to be given for protein or neither should be given for DNA");
		
		if(type == AlignmentType.Protein){
			readStructureFromFile(structureFile);
			readStructureProbFromFile(structureProbFile);
		}
		setConsensus();
		
	}
	
	public FacetAlignment(String seqFile, String structureFile, String structureProbFile) throws Exception{
		
		if(structureFile == null && structureProbFile == null) type=AlignmentType.DNA;
		else if(structureFile != null && structureProbFile != null) type=AlignmentType.Protein;
		else throw new IllegalArgumentException("Either both structure and probability need to be given for protein or neither should be given for DNA");
		
		numberOfSequences = numberOfSequencesInFile(seqFile);
		readSequencesFromFile(seqFile);
		if(type == AlignmentType.Protein){
			readStructureFromFile(structureFile);
			readStructureProbFromFile(structureProbFile);
		}
		setConsensus();
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
		sc.close();
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
	
	public void readSequencesFromFile(String filename) throws Exception{
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
			if(!(sequence[i].matches("^[ACTGUactgu-]$")) && type == AlignmentType.DNA){
				throw new IllegalArgumentException("The input type was DNA but the sequences contain non-DNA characters."); 
			}
		}
		
		int ct = numberNotGap(sequence[i]);
		if(ct>max1){max2 = max1; max1 = ct;}
		else if(ct>max2){max2 = ct;}
		
		width = sequence[0].length();
		sc.close();
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
				//System.err.println(sequence[i] + "\n" + structure[i]);
				//System.err.println("The structure length of " + i + " is " + structure[i].length());
				if(sequence[i].charAt(j) == '-'){
					structure[i] = (new StringBuffer(structure[i])).insert(j, "-").toString();
				}
			}
			i++;
		}
		sc.close();
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
		sc.close();
	}
}
