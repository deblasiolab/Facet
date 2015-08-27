package facet;

import java.io.*;
import java.util.Scanner;
import java.util.Locale;

public class Configuration {
	public int equivelanceClassSize = 6;
	public boolean coreBasedAnalysis;
	public boolean normalizeBigN = true;
	public boolean phylogenyGradient;
	private boolean gapsInBlockiness = true;
	
	public enum Structure{
		Probability, Determined
	}
	public Structure structureType = Structure.Probability;
	
	public enum ReplacementMatrix{
		BLOSUM62, BLOSUM45, BLOSUM80, 
		VTML200, VTML20, VTML120, VTML40, VTML80,
		DNA, RNA
	}
	public enum Features{
		PercentIdentity, StructeIdentity, AverageReplacementScore, Blockiness, Support
	}
	private enum Letters{
		A,R,N,D,C,Q,E,G,H,I,L,K,M,F,P,S,T,W,Y,V,B,Z,X
	}
	
	public Features featureToRun;
	public ReplacementMatrix matrix = ReplacementMatrix.VTML200;
	
	public Configuration(){
		
	}
	
	
	public Configuration(String filename) throws FileNotFoundException{
		Scanner sc = new Scanner(new File(filename));
		sc.useLocale(Locale.US);
		while(sc.hasNext()){
			String line = sc.next();
			if(line == "CoreColumnsOnly"){
				coreBasedAnalysis = true;
			}
			if(line == "EquivelanceClass"){
				equivelanceClassSize = sc.nextInt();
			}
			if(line == "ReplacementMatrix"){
				matrix = ReplacementMatrix.valueOf(sc.next());
			}
			if(line == "Feature"){
				featureToRun = Features.valueOf(sc.next());
			}
			if(line == "NormalizaByBigN"){
				normalizeBigN = true;
			}
			if(line == "ConsistantGradient"){
				phylogenyGradient = true;
			}
		}
		sc.close();
	}

	public boolean hasStructureType(char a, char b){
		if(gapsInBlockiness){
			return a==b || a=='-';
		}
		return a==b;
	}
	
	public int baseToInt(char a)throws IllegalArgumentException{
		a = Character.toUpperCase(a);
		switch(equivelanceClassSize){
		case 20:
			return basesEquivelant20(a);
		case 6:
			return basesEquivelant6(a);
		case 10:
			return basesEquivelant10(a);
		case 12:
			return basesEquivelant12(a);
		case 15:
			return basesEquivelant15(a);
		}
		throw new IllegalArgumentException("The value of equivelanceClassSize must be one of (6,10,15,20), and its "+ equivelanceClassSize + "\n");
	
	}
	
	public boolean basesEquivelant( char a, char b) throws IllegalArgumentException{
		a = Character.toUpperCase(a);
		b = Character.toUpperCase(b);
		switch(equivelanceClassSize){
		case 20:
			return a == b;
		case 6:
			return basesEquivelant6(a) == basesEquivelant6(b) ;
		case 10:
			return basesEquivelant10(a) == basesEquivelant10(b) ;
		case 12:
			return basesEquivelant12(a) == basesEquivelant12(b) ;
		case 15:
			return basesEquivelant15(a) == basesEquivelant15(b) ;
		}
		throw new IllegalArgumentException("The value of equivelanceClassSize must be one of (6,10,15,20), and its "+ equivelanceClassSize + "\n");
	}
	private int basesEquivelant6( char a){
		switch(a){
		case 'A': case 'G': case 'P': case 'T': case 'S': return 1; case 'I': case 'L': case 'M': case 'V': return 2;
		case 'N': case 'D': case 'Q': case 'E': return 3;
		case 'R': case 'H': case 'K': return 4;
		case 'F': case 'W': case 'Y': return 5;
		case 'C': return 6; 
		case '-': return 0;
		}
		throw new IllegalArgumentException("Unrecognized character "+ a + "\n");
	}
	private int basesEquivelant10( char a){
		switch(a){
		case 'A': case 'T': case 'S': return 1;
		case 'G': return 2;
		case 'P': return 3;
		case 'I': case 'L': case 'M': case 'V': return 4;
		case 'N': case 'D': case 'E': return 5;
		case 'Q': case 'R': case 'K': return 6;
		case 'H': return 7;
		case 'F': case 'Y': return 8;
		case 'W': return 9;
        case 'C': return 10;
        case '-': return 0;
		}
		throw new IllegalArgumentException("Unrecognized character "+ a + "\n");
	}
	private int basesEquivelant15( char a){
		switch(a){
		case 'A': return 1;
		case 'G': return 2;
        case 'P': return 3;
        case 'T': case 'S': return 4;
        case 'I': case 'V': return 5;
        case 'L': case 'M': return 6;
        case 'N': return 7;
        case 'D': return 8;
        case 'Q': case 'E': return 9;
        case 'R': case 'K': return 10;
        case 'H': return 11;
        case 'F': return 12;
        case 'W': return 13;
        case 'Y': return 14;
        case 'C': return 15;
        case '-': return 0;
		}
		throw new IllegalArgumentException("Unrecognized character "+ a + "\n");
	}
	private int basesEquivelant20( char a){
		switch(a){
		case 'A': return 1;
		case 'G': return 2;
        case 'P': return 3;
        case 'T': return 16;
        case 'S': return 4;
        case 'I': return 17;
        case 'V': return 5;
        case 'L': return 18;
        case 'M': return 6;
        case 'N': return 7;
        case 'D': return 8;
        case 'Q': return 19;
        case 'E': return 9;
        case 'R': return 20;
        case 'K': return 10;
        case 'H': return 11;
        case 'F': return 12;
        case 'W': return 13;
        case 'Y': return 14;
        case 'C': return 15;
        case '-': return 0;
		}
		throw new IllegalArgumentException("Unrecognized character "+ a + "\n");
	}
	private int basesEquivelant12( char a){
		switch(a){
		case 'A': case 'G': case 'T': case 'S': return 1;
        case 'P': return 2;
        case 'I': case 'L': case 'V': return 3;
        case 'M': return 4;
        case 'N': return 5;
        case 'D': case 'E': return 6;
        case 'Q': return 7;
        case 'R': case 'K': return 8;
        case 'H': return 9;
        case 'F': case 'Y': return 10;
        case 'W': return 11;
        case 'C': return 12;
        case '-': return 0;
		}
		throw new IllegalArgumentException("Unrecognized character "+ a + "\n");
	}
	
	public float replacementValue(char a, char b){
		//System.err.println("Letters?" + a.toUpperCase() + " -- " + b.toUpperCase());
		if(Letters.valueOf(Character.toString(a).toUpperCase()).ordinal()<Letters.valueOf(Character.toString(b).toUpperCase()).ordinal()){
			char t = a;
			a = b;
			b = t;
		}
		switch(matrix){
		case BLOSUM45: return replacementValueBLOSUM45(a,b);
		case BLOSUM62: return replacementValueBLOSUM62(a,b);
		case BLOSUM80: return replacementValueBLOSUM80(a,b);
		case VTML20: return replacementValueVTML20(a,b);
		case VTML40: return replacementValueVTML40(a,b);
		case VTML80: return replacementValueVTML80(a,b);
		case VTML120: return replacementValueVTML120(a,b);
		case VTML200: return replacementValueVTML200(a,b);
		case DNA: return replacementValueDNA(a,b);
		case RNA: return replacementValueRNA(a,b);
		}
		throw  new IllegalArgumentException("The replacement matrix selection is invalid");
	}
	
	private float replacementValueDNA(char a, char b){
		if(a==b) return 0;
		if(Character.toUpperCase(a)=='A' && Character.toUpperCase(b)=='G') return (float)25;
		if(Character.toUpperCase(a)=='C' && Character.toUpperCase(b)=='T') return (float)25;
		return 100;
	}
	
	private float replacementValueRNA(char a, char b){
		if(a==b) return 0;
		return 100;
	}
	
	private float replacementValueBLOSUM62(char a, char b){
		int[][] tmp  = {
                {46},
                {83,36},
                {83,75,36},
                {85,83,64,33},
                {76,92,90,95,12},
                {78,67,73,74,91,38},
                {79,75,75,62,96,62,40},
                {73,88,77,84,90,86,87,35},
                {85,74,69,79,88,69,75,86,22},
                {82,91,93,96,81,90,95,98,94,44},
                {84,88,93,95,82,86,91,98,89,63,46},
                {80,60,73,77,93,65,68,84,78,91,89,42},
                {78,83,84,92,80,76,85,94,81,66,60,81,35},
                {88,92,90,97,86,93,96,95,82,74,69,95,72,31},
                {79,86,84,83,91,82,81,87,88,91,91,81,87,94,24},
                {67,79,70,74,79,75,75,77,78,89,89,75,83,89,78,46},
                {74,80,74,81,81,76,79,85,84,79,83,77,79,88,79,63,41},
                {91,89,92,100,91,88,94,91,83,87,81,91,85,64,95,93,91,0},
                {85,83,85,91,86,82,86,93,64,82,80,85,80,54,92,85,83,61,27},
                {75,89,90,95,79,87,90,95,93,56,68,87,71,78,88,85,75,90,82,46},
                {84,80,51,48,93,74,68,80,75,95,94,75,88,94,83,72,77,96,88,93,49},
                {78,72,74,67,94,53,49,87,73,93,89,67,81,95,81,75,78,92,84,89,72,50},
                {77,80,79,82,84,78,79,84,81,80,80,78,78,83,83,77,77,87,81,79,80,79,80}
        };
		return (float) tmp[Letters.valueOf(Character.toString(a).toUpperCase()).ordinal() ][Letters.valueOf(Character.toString(b).toUpperCase()).ordinal()];
	}
	private float replacementValueBLOSUM80(char a, char b){
		int[][] tmp  = {
	            {41},
	            {80,32},
	            {80,73,30},
	            {82,81,61,30},
	            {75,91,90,95,14},
	            {75,63,69,72,89,32},
	            {75,72,72,60,98,58,35},
	            {70,85,74,80,92,83,84,32},
	            {81,70,67,78,93,65,71,85,20},
	            {79,89,92,95,79,88,91,97,92,40},
	            {80,86,92,96,80,84,90,95,87,59,42},
	            {74,56,69,75,92,62,65,81,74,88,86,37},
	            {76,80,82,91,80,70,82,90,81,62,56,78,30},
	            {85,90,90,93,85,90,93,92,79,72,66,92,69,28},
	            {73,82,83,81,90,80,77,86,84,89,88,78,86,92,21},
	            {62,75,66,72,79,71,71,73,76,85,86,72,78,85,75,40},
	            {69,77,71,78,79,73,75,82,80,75,80,73,74,83,77,59,36},
	            {90,86,92,100,95,87,93,90,82,86,82,90,82,65,97,90,88,0},
	            {83,82,84,90,88,81,85,93,58,80,77,83,77,50,90,82,81,56,24},
	            {71,87,88,92,77,84,86,92,89,52,65,85,66,75,85,81,71,88,81,42},
	            {81,77,47,44,93,71,65,77,73,94,94,72,87,92,82,69,75,96,87,90,45},
	            {75,68,71,65,95,48,44,84,69,90,87,64,77,92,78,71,75,91,83,85,69,45},
	            {73,77,77,80,84,75,76,81,78,78,77,75,74,80,81,73,73,86,79,76,78,76,77}
	    };
		return (float) tmp[Letters.valueOf(Character.toString(a).toUpperCase()).ordinal() ][Letters.valueOf(Character.toString(b).toUpperCase()).ordinal()];
	}
	private float replacementValueBLOSUM45(char a, char b){
		int[][] tmp  = {
	            {51},
	            {87,39},
	            {84,79,43},
	            {89,84,69,36},
	            {80,95,86,96,7},
	            {82,71,77,76,90,41},
	            {84,78,80,65,94,68,45},
	            {77,90,79,87,93,90,90,38},
	            {86,78,72,79,89,74,81,89,23},
	            {85,95,92,98,89,91,94,100,95,48},
	            {87,91,93,96,88,86,92,100,88,66,50},
	            {84,63,75,80,94,70,71,87,80,95,92,46},
	            {82,87,85,93,85,82,86,95,83,70,64,86,39},
	            {91,93,89,97,86,95,95,96,83,76,70,95,74,34},
	            {85,90,86,86,92,85,81,87,88,91,95,86,91,93,26},
	            {72,83,74,80,82,80,81,79,84,90,91,80,88,89,84,51},
	            {79,86,76,84,83,83,82,87,85,82,85,81,83,87,83,67,48},
	            {91,84,90,96,97,81,90,89,85,83,81,87,88,66,87,96,91,0},
	            {90,83,88,89,90,85,89,96,71,82,82,88,79,57,91,87,85,60,30},
	            {77,92,90,98,82,90,92,96,94,58,72,92,74,79,93,87,78,88,83,51},
	            {86,81,57,51,91,77,72,83,76,95,95,77,89,93,86,77,80,93,88,94,54},
	            {83,75,79,69,93,58,54,90,78,93,90,71,85,95,82,81,83,86,87,92,77,55},
	            {80,83,81,84,87,81,82,85,83,83,83,82,81,84,85,80,80,85,83,82,82,81,82}
	    };
		return (float) tmp[Letters.valueOf(Character.toString(a).toUpperCase()).ordinal() ][Letters.valueOf(Character.toString(b).toUpperCase()).ordinal()];
	}
	private float replacementValueVTML20(char a, char b){
		int[][] tmp  = {
	            {19},
	            {66,15},
	            {64,60,13},
	            {63,85,47,15},
	            {53,68,69,92,2},
	            {60,50,55,58,88,12},
	            {60,78,60,45,91,46,17},
	            {56,68,59,62,68,69,65,17},
	            {66,54,52,58,65,48,62,68,7},
	            {67,72,73,86,61,76,77,97,74,17},
	            {66,70,75,95,86,64,72,82,67,48,20},
	            {62,42,54,61,88,49,51,68,60,73,70,17},
	            {61,64,67,75,58,57,72,77,84,48,44,61,7},
	            {70,78,78,100,88,70,93,80,59,59,53,91,52,12},
	            {58,66,70,64,74,60,64,70,65,79,68,62,78,73,13},
	            {49,62,50,59,54,57,59,58,59,75,71,60,71,66,57,17},
	            {53,64,55,62,61,59,62,70,62,59,67,58,57,70,63,46,16},
	            {76,70,79,79,97,95,98,74,64,64,64,73,87,52,74,70,94,0},
	            {71,67,63,90,58,85,67,79,47,69,64,70,82,41,94,64,70,49,10},
	            {53,73,75,75,55,66,69,77,70,40,54,69,54,63,69,70,56,89,69,18},
	            {63,73,30,31,81,57,53,61,55,80,85,57,71,89,67,54,59,79,76,75,30},
	            {60,64,58,52,89,29,31,67,55,77,68,50,65,82,62,58,61,96,76,68,55,30},
	            {60,64,61,69,69,61,65,68,60,66,65,62,63,67,66,59,60,72,65,63,65,63,64},
	            {100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,40}
	    };
		return (float) tmp[Letters.valueOf(Character.toString(a).toUpperCase()).ordinal() ][Letters.valueOf(Character.toString(b).toUpperCase()).ordinal()];
	}
	private float replacementValueVTML40(char a, char b){
		int[][] tmp  = {
	            {24},
	            {68,18},
	            {66,62,17},
	            {65,83,47,18},
	            {55,71,72,91,3},
	            {62,51,57,59,87,16},
	            {62,76,61,46,90,47,21},
	            {58,71,61,64,71,72,68,20},
	            {69,55,53,60,68,49,64,70,9},
	            {68,76,77,89,63,78,80,96,77,21},
	            {69,73,79,94,85,67,76,85,69,49,24},
	            {65,42,56,62,87,50,52,70,61,76,73,21},
	            {63,67,70,78,60,60,75,81,83,49,44,64,11},
	            {73,81,81,100,87,74,92,84,61,61,54,91,53,15},
	            {60,68,72,66,76,62,66,73,67,82,71,65,80,77,15},
	            {50,65,51,61,55,59,61,60,61,77,74,62,73,69,59,22},
	            {55,67,57,64,62,61,64,72,64,61,70,60,59,73,66,47,20},
	            {79,73,82,84,97,95,98,78,66,67,66,76,86,53,78,73,94,0},
	            {74,70,65,90,60,83,70,83,48,71,66,73,80,41,94,66,72,49,12},
	            {55,76,77,78,56,69,72,80,73,41,55,72,55,65,72,72,58,88,72,23},
	            {65,73,32,33,81,58,53,63,57,83,86,59,74,91,69,56,60,83,77,78,32},
	            {62,64,59,53,88,31,34,70,57,79,71,51,67,83,64,60,63,96,77,71,56,32},
	            {62,66,63,70,70,63,67,71,61,68,67,64,64,69,68,61,62,74,67,66,67,65,66},
	            {100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,46}
	    };
		return (float) tmp[Letters.valueOf(Character.toString(a).toUpperCase()).ordinal() ][Letters.valueOf(Character.toString(b).toUpperCase()).ordinal()];
	}
	private float replacementValueVTML80(char a, char b){
		int[][] tmp  = {
	            {31},
	            {71,24},
	            {68,65,24},
	            {68,82,50,24},
	            {57,75,75,91,5},
	            {66,54,60,61,86,24},
	            {65,74,63,48,89,49,28},
	            {61,74,64,67,74,75,71,24},
	            {72,58,56,63,72,52,66,74,15},
	            {70,80,81,91,65,80,84,97,80,29},
	            {72,77,82,94,84,71,80,90,74,51,31},
	            {68,44,59,65,87,53,55,73,64,80,77,29},
	            {67,71,75,82,64,64,78,85,82,51,46,69,18},
	            {76,84,84,100,86,78,93,89,64,63,56,91,56,20},
	            {63,71,74,69,79,66,69,76,71,84,76,68,82,81,19},
	            {53,68,54,64,58,62,64,63,64,79,78,65,75,74,62,31},
	            {58,69,60,67,65,64,67,75,67,65,73,64,63,76,69,50,28},
	            {84,78,86,90,97,95,99,83,69,71,69,81,85,54,82,78,94,0},
	            {77,74,69,90,64,83,75,87,50,74,68,77,78,41,93,70,76,50,17},
	            {59,79,80,82,59,74,76,84,77,43,57,76,57,68,76,74,62,88,75,31},
	            {68,73,37,37,83,61,55,65,60,86,88,62,78,92,72,59,63,88,79,81,37},
	            {66,64,62,54,88,37,39,73,59,82,76,54,71,85,67,63,66,97,79,75,58,38},
	            {65,69,66,72,72,66,70,74,64,71,70,67,67,72,72,64,66,77,69,69,69,68,69},
	            {100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,54}
	    };
		return (float) tmp[Letters.valueOf(Character.toString(a).toUpperCase()).ordinal() ][Letters.valueOf(Character.toString(b).toUpperCase()).ordinal()];
	}
	private float replacementValueVTML120(char a, char b){
		int[][] tmp  = {
	            {39},
	            {74,29},
	            {70,67,31},
	            {71,81,52,30},
	            {60,78,77,90,8},
	            {69,56,63,63,86,32},
	            {68,74,65,50,89,53,34},
	            {64,77,66,69,76,77,73,28},
	            {74,61,59,66,75,56,68,76,20},
	            {72,83,84,93,68,81,86,97,82,36},
	            {75,80,85,95,83,75,83,93,77,53,37},
	            {71,47,62,67,87,56,58,76,66,82,80,36},
	            {70,74,78,85,67,69,80,88,82,54,49,72,25},
	            {79,86,86,100,86,82,93,92,66,65,58,91,58,24},
	            {66,73,76,72,81,68,71,78,73,86,79,71,84,85,22},
	            {57,70,58,66,61,65,66,65,67,81,80,67,77,77,65,39},
	            {61,72,63,70,67,67,69,76,70,68,75,67,67,79,71,53,35},
	            {87,81,89,94,97,95,99,87,71,74,71,85,84,55,86,82,94,0},
	            {80,77,73,90,67,83,79,89,53,76,70,80,78,42,94,74,79,51,21},
	            {62,82,82,85,62,77,79,86,80,46,59,79,59,70,79,76,65,88,77,38},
	            {70,74,42,41,84,63,57,68,62,89,90,64,82,93,74,62,66,91,81,84,41},
	            {69,65,64,57,87,42,44,75,62,83,79,57,74,87,70,66,68,97,81,78,60,43},
	            {68,71,69,74,73,69,72,77,67,73,73,70,70,74,74,67,68,79,72,71,72,70,72},
	            {100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,59}
	    };
		return (float) tmp[Letters.valueOf(Character.toString(a).toUpperCase()).ordinal() ][Letters.valueOf(Character.toString(b).toUpperCase()).ordinal()];
	}
	private float replacementValueVTML200(char a, char b){
		int[][] tmp  = {
	            {52},
	            {77,40},
	            {74,71,44},
	            {75,80,58,40},
	            {65,82,80,90,13},
	            {73,62,68,67,86,47},
	            {73,74,68,55,89,60,46},
	            {68,80,70,73,79,79,76,35},
	            {78,66,65,70,79,63,72,80,32},
	            {75,86,88,94,71,84,88,97,85,47},
	            {78,84,88,95,83,80,87,95,81,57,46},
	            {75,53,67,70,87,62,64,79,70,85,84,49},
	            {75,79,83,88,73,76,84,91,83,59,54,78,40},
	            {83,89,88,100,85,86,94,95,70,69,62,91,63,33},
	            {71,77,78,75,83,73,75,81,77,88,83,74,86,89,29},
	            {64,74,65,70,67,70,71,70,72,83,84,72,80,82,70,53},
	            {66,75,68,73,71,72,73,79,74,73,78,72,73,82,74,61,50},
	            {91,86,92,98,97,96,100,92,75,78,75,89,84,56,91,87,95,0},
	            {83,81,78,91,72,84,84,92,58,78,73,84,78,45,94,79,82,52,28},
	            {69,84,85,88,66,81,83,89,83,52,62,83,63,73,82,79,71,88,80,49},
	            {74,76,51,49,85,67,61,71,68,91,91,69,86,94,77,67,71,95,85,86,50},
	            {73,68,68,61,87,53,53,78,67,86,83,63,80,90,74,71,73,98,84,82,64,53},
	            {73,75,74,77,76,73,76,80,72,77,76,74,75,77,77,73,73,81,75,76,76,75,75},
	            {100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,66}
	    };
		return (float) tmp[Letters.valueOf(Character.toString(a).toUpperCase()).ordinal() ][Letters.valueOf(Character.toString(b).toUpperCase()).ordinal()];
	}
	
}
