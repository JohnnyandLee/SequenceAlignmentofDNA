import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.lang.StringBuilder;

public class Efficient {
	private static String firstString;
	private static String secondString;
	private static final int[][] ALPHA = {{0, 110, 48, 94}, {110, 0, 118, 48}, {48, 118, 0, 110}, {94, 48, 110, 0}};
	private static final int NOT_MATCHED_COST = 30;
	private static StringBuilder changeFirstBuilder;
	private static StringBuilder changeSecondBuilder;
	private static int[][] outputIndex;
	private static int[] visitDC;
	private static int[][] alignmentCostTableFront;//0414+
	private static int[][] alignmentCostTableBack;//0415+
	
	public static void main(String[] args) throws FileNotFoundException {
		String inputFileName = args[0];
		String outputFileName = args[1];

		readInputFile(inputFileName);
		
	   double beforeUsedMem = getMemoryInKB();
	   double startTime = getTimeInMilliseconds();

	   int theResultCost = findAlignmentCost(firstString, secondString);
		divideConquer();
		changeFirstBuilder = new StringBuilder(firstString);
		changeSecondBuilder = new StringBuilder(secondString);
		getTwoString(firstString.length(), secondString.length());

		double afterUsedMem = getMemoryInKB();
		double endTime = getTimeInMilliseconds();
		double totalUsage = afterUsedMem - beforeUsedMem;
		double totalTime = endTime - startTime;

	   try {
	      FileWriter myWriter = new FileWriter(outputFileName);
	      myWriter.write(theResultCost + "\n");
	      myWriter.write(changeFirstBuilder.toString() + "\n");
	      myWriter.write(changeSecondBuilder.toString() + "\n");
	      myWriter.write(totalTime + "\n");
	      myWriter.write(totalUsage + "");
	      myWriter.close();
	    }
	    catch (IOException e) {
	      e.printStackTrace();
	    }
	}

	public static void readInputFile(String inputFileName) throws FileNotFoundException {
		File inFile = new File(inputFileName);
		Scanner in = new Scanner(inFile);
		StringBuilder firstStrBuilder = new StringBuilder(in.nextLine());
		String nextWord = in.nextLine();
		while(nextWord.charAt(0) != 'T' && nextWord.charAt(0) != 'A' && nextWord.charAt(0) != 'C' && nextWord.charAt(0) != 'G'){
			int next = Integer.parseInt(nextWord);
			String insertString = firstStrBuilder.toString();
			firstStrBuilder.insert(next + 1, insertString);
			nextWord = in.nextLine();
		}
		StringBuilder secondStrBuilder = new StringBuilder(nextWord);
		while(in.hasNextLine()){
			String test = in.nextLine();
			if(!test.equals(""))
			{
				int next = Integer.parseInt(test);
				String insertString = secondStrBuilder.toString();
				secondStrBuilder.insert(next + 1, insertString);
			}
		}
		firstString = firstStrBuilder.toString();
		secondString = secondStrBuilder.toString();
   }
	
	public static int findAlignmentCost(String tempFirstStr, String tempSecondStr) {
		int sizeOfFirstString = tempFirstStr.length();
		int sizeOfSecondString = tempSecondStr.length();
		if(sizeOfFirstString == 0) {
			return NOT_MATCHED_COST*sizeOfSecondString;
		}
		if(sizeOfSecondString == 0) {
			return NOT_MATCHED_COST*sizeOfFirstString;
		}
		
		int[][] alignmentCostTable = new int [sizeOfFirstString + 1][2];
		
		for(int i = 0; i < sizeOfFirstString + 1; i++) {
			alignmentCostTable[i][0] = i*NOT_MATCHED_COST;
		}

		for(int col = 1; col < sizeOfSecondString + 1; col++) {
			for(int row = 1 ; row < sizeOfFirstString + 1; row++) {
				char first = tempFirstStr.charAt(row - 1);
				char second = tempSecondStr.charAt(col - 1);
				int firstIndex = charToIndex(first);
				int secondIndex = charToIndex(second);
				int matchedCost = ALPHA[firstIndex][secondIndex];
				alignmentCostTable[0][1] = col * NOT_MATCHED_COST;
				alignmentCostTable[row][1] = Math.min(matchedCost+alignmentCostTable[row - 1][0], Math.min(NOT_MATCHED_COST + alignmentCostTable[row - 1][1], NOT_MATCHED_COST + alignmentCostTable[row][0]));
			}
			for(int row = 0; row < sizeOfFirstString + 1; row++) {
				alignmentCostTable[row][0] = alignmentCostTable[row][1];
			}
		}		
		return alignmentCostTable[sizeOfFirstString][1];
	}

	public static void findLastColIndex() {
		if(outputIndex[1][secondString.length() - 1] == firstString.length()){
			outputIndex[0][secondString.length()] = firstString.length();
		}
		else{
			char first = firstString.charAt(outputIndex[1][secondString.length() - 1]);
			char second = secondString.charAt(secondString.length() - 1);
			if (first == second){
				outputIndex[0][secondString.length()] = outputIndex[1][secondString.length() - 1] + 1;
			}
			else if (first == 'A' && second == 'G'){
				outputIndex[0][secondString.length()] = outputIndex[1][secondString.length() - 1] + 1;
			}
			else if (first == 'G' && second == 'A'){
				outputIndex[0][secondString.length()] = outputIndex[1][secondString.length() - 1] + 1;
			}
			else if (first == 'C' && second == 'T'){
				outputIndex[0][secondString.length()] = outputIndex[1][secondString.length() - 1] + 1;
			}
			else if (first == 'T' && second == 'C'){
				outputIndex[0][secondString.length()] = outputIndex[1][secondString.length() - 1] + 1;
			}
			else{
				outputIndex[0][secondString.length()] = outputIndex[1][secondString.length() - 1];
			}
		}
	}

	public static int getTwoString(int firstStringPointer,int secondStringPointer) {
		if(firstStringPointer == 0 && secondStringPointer == 0){
			return 0;
		}
		//go up
		if(secondStringPointer == 0) {
			changeSecondBuilder.insert(secondStringPointer, '_');
			outputIndex[1][secondStringPointer]--;
			return getTwoString(firstStringPointer - 1, secondStringPointer);
		}

		//go left
		if(firstStringPointer == 0) {
			changeFirstBuilder.insert(firstStringPointer, '_');
			return getTwoString(firstStringPointer, secondStringPointer - 1);
		}

		//go left
		if(outputIndex[1][secondStringPointer - 1] == outputIndex[1][secondStringPointer]){
			changeFirstBuilder.insert(firstStringPointer, '_');
			return getTwoString(firstStringPointer, secondStringPointer - 1);
		}

		//go diagonal
		if(outputIndex[0][secondStringPointer] == outputIndex[1][secondStringPointer]){
			return getTwoString(firstStringPointer - 1, secondStringPointer - 1);
		}

		//the upper square is highlighted
		else{//(outputIndex[0][secondStringPointer]!=outputIndex[1][secondStringPointer]){
			//go up
			if(outputIndex[1][secondStringPointer - 1] != outputIndex[1][secondStringPointer] - 1){
				changeSecondBuilder.insert(secondStringPointer, '_');
				outputIndex[1][secondStringPointer]--;
				return getTwoString(firstStringPointer - 1,secondStringPointer);
			}

			//when there is a left top square is highlighted
			else if (outputIndex[1][secondStringPointer - 1] == outputIndex[1][secondStringPointer] - 1){
				//when the chars are the same go diagonal
				if(firstString.charAt(outputIndex[1][secondStringPointer] - 1) == secondString.charAt(secondStringPointer - 1)){
					return getTwoString(firstStringPointer - 1,secondStringPointer - 1);
				}

				//if the cost less than 60(which are AG and CT pairs), go diagonal
				else if(firstString.charAt(outputIndex[1][secondStringPointer] - 1) == 'A' && secondString.charAt(secondStringPointer - 1) == 'G'){
					return getTwoString(firstStringPointer - 1,secondStringPointer - 1);
				}
				else if(firstString.charAt(outputIndex[1][secondStringPointer] - 1) == 'G' && secondString.charAt(secondStringPointer - 1) == 'A'){
					return getTwoString(firstStringPointer - 1,secondStringPointer - 1);
				}
				else if(firstString.charAt(outputIndex[1][secondStringPointer] - 1) == 'C' && secondString.charAt(secondStringPointer - 1) == 'T'){
					return getTwoString(firstStringPointer - 1,secondStringPointer - 1);
				}
				else if(firstString.charAt(outputIndex[1][secondStringPointer]-1) =='T' && secondString.charAt(secondStringPointer-1) =='C'){
					return getTwoString(firstStringPointer - 1,secondStringPointer - 1);
				}
				//go up
				else{
					changeSecondBuilder.insert(secondStringPointer, '_');
					outputIndex[1][secondStringPointer]--;
					return getTwoString(firstStringPointer - 1,secondStringPointer);
				}
			}
		}
		return 0;
	}
	
	private static double getMemoryInKB() {
      double total = Runtime.getRuntime().totalMemory();
		return (total-Runtime.getRuntime().freeMemory())/10e3;
	}
	
	private static double getTimeInMilliseconds() {
		return System.nanoTime()/10e6;
	}

	public static int findAlignmentCostFront(String tempFirstStr, String tempSecondStr) {
		int sizeOfFirstString = tempFirstStr.length();
		int sizeOfSecondString = tempSecondStr.length();
		if(sizeOfFirstString == 0) {
			return NOT_MATCHED_COST * sizeOfSecondString;
		}
		
		if(sizeOfSecondString == 0) {
			for(int i = 0; i < sizeOfFirstString + 1; i++){
				alignmentCostTableFront[i][0] = i * NOT_MATCHED_COST;
			}
			return NOT_MATCHED_COST * sizeOfFirstString;
		}
		
		alignmentCostTableFront = new int [sizeOfFirstString+1][2];
		
		for(int i = 0; i < sizeOfFirstString + 1; i++) {
			alignmentCostTableFront[i][0] = i * NOT_MATCHED_COST;
		}
		for(int col = 1; col < sizeOfSecondString + 1; col++) {
	      for(int row = 1; row < sizeOfFirstString + 1; row++) {
				char first=tempFirstStr.charAt(row - 1);
				char second=tempSecondStr.charAt(col - 1);
				int firstIndex = charToIndex(first);
				int secondIndex = charToIndex(second);
				int matchedCost = ALPHA[firstIndex][secondIndex];
				alignmentCostTableFront[0][1]=col * NOT_MATCHED_COST;
				alignmentCostTableFront[row][1]=Math.min(matchedCost + alignmentCostTableFront[row - 1][0], Math.min(NOT_MATCHED_COST + alignmentCostTableFront[row - 1][1], NOT_MATCHED_COST + alignmentCostTableFront[row][0]));
			}	
	      
			for(int row=0;row<sizeOfFirstString+1;row++) {
			   alignmentCostTableFront[row][0]=alignmentCostTableFront[row][1];
			}
		}		
		return alignmentCostTableFront[sizeOfFirstString][1];
	}

	public static void divideConquer(){
		visitDC = new int[secondString.length() + 1];

		outputIndex = new int[2][secondString.length() + 1];
		for(int i = 1; i < secondString.length() + 1; i++){
			outputIndex[1][i] = Integer.MIN_VALUE;
		}

		divideAndConquer2(0,firstString.length(),secondString.length() / 2,0, secondString.length());

		outputIndex[1][secondString.length()] = firstString.length();
		findLastColIndex();

	}
	public static int divideAndConquer2(int firstStringBegin,int firstStingEnd,int secondStringIndex, int front, int end) {
		visitDC[secondStringIndex]=1;
		
		int[] frontCost = new int [firstStingEnd-firstStringBegin+1];
		int[] backCost = new int [firstStingEnd-firstStringBegin+1];
      int min = Integer.MAX_VALUE;
		
      		
      String frontSecondString = secondString.substring(front, secondStringIndex);
      String backSecondString = secondString.substring(secondStringIndex,end);

		findAlignmentCostFront(firstString.substring(firstStringBegin, firstStingEnd), frontSecondString);

		StringBuilder revBackSecondStr = new StringBuilder(backSecondString);
		revBackSecondStr.reverse();
		StringBuilder revBackFirstStr = new StringBuilder(firstString.substring(firstStringBegin,firstStingEnd));
		revBackFirstStr.reverse();
		findAlignmentCostBack(revBackFirstStr.toString(), revBackSecondStr.toString());
		
		for(int i = 0; i < firstStingEnd - firstStringBegin + 1; i++) {

			if(secondStringIndex == 0){
				frontCost[i] = alignmentCostTableFront[i][0];
			}
			else {
				frontCost[i] = alignmentCostTableFront[i][1];
			}

			backCost[i] = alignmentCostTableBack[firstStingEnd - firstStringBegin - i][1];
					
			if (frontCost[i] + backCost[i] < min) {
				min = frontCost[i] + backCost[i];
				outputIndex[0][secondStringIndex] = i + firstStringBegin;
			}		
		}
		
		min = Integer.MAX_VALUE;
		for(int i = firstStingEnd - firstStringBegin; i >= 0; i--) {
			if (frontCost[i] + backCost[i] < min) {
				min = frontCost[i] + backCost[i];
				outputIndex[1][secondStringIndex] = i + firstStringBegin;
			}	
		}
		
		
		//below 0413+
		if(visitDC[(front+secondStringIndex) / 2] == 1 && visitDC[(end+secondStringIndex) / 2] != 1) {
			return divideAndConquer2(outputIndex[1][secondStringIndex],firstStingEnd,(end+secondStringIndex) / 2,secondStringIndex,end);
		}
		
		if(visitDC[(front+secondStringIndex)/2] != 1 && visitDC[(end+secondStringIndex)/2] == 1) {
			return divideAndConquer2(firstStringBegin,outputIndex[1][secondStringIndex],(front+secondStringIndex) / 2,front,secondStringIndex);
		}
		if(visitDC[(front+secondStringIndex)/2] == 1 && visitDC[(end+secondStringIndex)/2] == 1) {
			return 10;
		}		
		//above 0413+
		return divideAndConquer2(firstStringBegin, outputIndex[1][secondStringIndex],(front+secondStringIndex) / 2, front, secondStringIndex)+
				 divideAndConquer2(outputIndex[1][secondStringIndex], firstStingEnd,(end+secondStringIndex) / 2, secondStringIndex, end);
	}

	public static int findAlignmentCostBack(String tempFirstStr, String tempSecondStr) {
		int sizeOfFirstString = tempFirstStr.length();
		int sizeOfSecondString = tempSecondStr.length();
		
		alignmentCostTableBack=new int [sizeOfFirstString+1][2];
		
		if(sizeOfFirstString == 0) {
			return NOT_MATCHED_COST * sizeOfSecondString;
		}
		if(sizeOfSecondString == 0) {
			for(int i = 0; i < sizeOfFirstString + 1; i++){
				alignmentCostTableBack[i][0] = i * NOT_MATCHED_COST;
			}
			return NOT_MATCHED_COST * sizeOfFirstString;
		}
		
		for(int i = 0; i < sizeOfFirstString + 1; i++) {
			alignmentCostTableBack[i][0] = i * NOT_MATCHED_COST;
		}
		
		for(int col = 1; col < sizeOfSecondString + 1; col++) {
	      for(int row = 1; row < sizeOfFirstString + 1; row++) {
				char first=tempFirstStr.charAt(row - 1);
				char second=tempSecondStr.charAt(col - 1);
				int firstIndex = charToIndex(first);
				int secondIndex = charToIndex(second);
				int matchedCost = ALPHA[firstIndex][secondIndex];
				alignmentCostTableBack[0][1] = col * NOT_MATCHED_COST;
				alignmentCostTableBack[row][1] = Math.min(matchedCost + alignmentCostTableBack[row - 1][0], Math.min(NOT_MATCHED_COST + alignmentCostTableBack[row - 1][1], NOT_MATCHED_COST + alignmentCostTableBack[row][0]));
			}	
	      
			for(int row=0;row<sizeOfFirstString+1;row++) {
			   alignmentCostTableBack[row][0]=alignmentCostTableBack[row][1];
			}
		}		
		return alignmentCostTableBack[sizeOfFirstString][1];
	}
	private static int charToIndex(char symbol) {
		int index = -1;
		if(symbol == 'A') {
			index = 0;
		}
		else if(symbol == 'C') {
			index = 1;
		}
		else if(symbol == 'G') {
			index = 2;
		}
		else if(symbol == 'T') {
			index = 3;
		}
		return index;
	}
}