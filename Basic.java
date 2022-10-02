import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.lang.StringBuilder;

public class Basic {

	private static String firstString;
	private static String secondString;
	private static final int[][] ALPHA = {{0, 110, 48, 94}, {110, 0, 118, 48}, {48, 118, 0, 110}, {94, 48, 110, 0}};
	private static final int NOT_MATCHED_COST = 30;
	private static final int LEFT = 1;
	private static final int UP = 2;
	private static final int DIAGONAL = 3;
	private static int[][] directionTable;
	private static String changeFirstString;
	private static String changeSecondString;

	public static void main(String[] args) throws FileNotFoundException {
		String inputFileName = args[0];
		String outputFileName = args[1];
		readInputFile(inputFileName);

		double beforeUsedMem = getMemoryInKB();
		double startTime = getTimeInMilliseconds();
		int similarity = findAlignmentCost(firstString, secondString);
		getTwoStrLoop(firstString, secondString);
		double afterUsedMem = getMemoryInKB();
		double endTime = getTimeInMilliseconds();
		double totalUsage = afterUsedMem - beforeUsedMem;
		double totalTime = endTime - startTime;

		try {
	      FileWriter myWriter = new FileWriter(outputFileName);
	      myWriter.write(similarity + "\n");
	      myWriter.write(changeFirstString + "\n");
	      myWriter.write(changeSecondString + "\n");
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
		while(in.hasNextLine()) {
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

	public static int findAlignmentCost(String firstStr, String secondStr) {
		int sizeOfFirstString = firstStr.length();
		int sizeOfSecondString = secondStr.length();
		int [][]alignmentCostTable = new int [sizeOfFirstString + 1][sizeOfSecondString + 1];
		directionTable = new int [sizeOfFirstString + 1][sizeOfSecondString + 1];
		
		for(int i = 0; i < sizeOfFirstString + 1; i++) {
			alignmentCostTable[i][0] = i * NOT_MATCHED_COST;
			directionTable[i][0] = UP;
		}
		
		for(int j = 1; j < sizeOfSecondString + 1; j++) {
			alignmentCostTable[0][j] = j * NOT_MATCHED_COST;
			directionTable[0][j] = LEFT;
		}
		for(int col = 1; col < sizeOfSecondString + 1; col++) {
			for(int row = 1; row < sizeOfFirstString + 1; row++) {
				char first = firstStr.charAt(row - 1);
				char second = secondStr.charAt(col - 1);
				int firstIndex = charToIndex(first);
				int secondIndex = charToIndex(second);
				int matchedCost = ALPHA[firstIndex][secondIndex];
				alignmentCostTable[row][col] = Math.min(matchedCost + alignmentCostTable[row - 1][col - 1], Math.min(NOT_MATCHED_COST + alignmentCostTable[row - 1][col], NOT_MATCHED_COST + alignmentCostTable[row][col - 1]));
				if(alignmentCostTable[row][col] == matchedCost + alignmentCostTable[row - 1][col - 1]) {
					directionTable[row][col] = DIAGONAL;
				}
				else if(alignmentCostTable[row][col] == NOT_MATCHED_COST + alignmentCostTable[row - 1][col]) {
					directionTable[row][col] = UP;
				}
				else if(alignmentCostTable[row][col] == NOT_MATCHED_COST + alignmentCostTable[row][col - 1]) {
					directionTable[row][col] = LEFT;
				}
			}			
		}		
		return alignmentCostTable[sizeOfFirstString][sizeOfSecondString];
	}

	private static void getTwoStrLoop(String firstString, String secondString)
	{
		int firstStrPointer = firstString.length();
		int secondStrPointer = secondString.length();
		StringBuilder firstStrBuilder = new StringBuilder(firstString);
		StringBuilder secondStrBuilder = new StringBuilder(secondString);

		while (!(firstStrPointer == 0 && secondStrPointer == 0))
		{
			int direction = directionTable[firstStrPointer][secondStrPointer];
			if(direction == LEFT)
			{
				firstStrBuilder.insert(firstStrPointer, "_");
				secondStrPointer--;
			}
			else if(direction == UP)
			{
				secondStrBuilder.insert(secondStrPointer, "_");
				firstStrPointer--;
			}
			else
			{
				firstStrPointer--;
				secondStrPointer--;
			}
		}
		changeFirstString = firstStrBuilder.toString();
		changeSecondString = secondStrBuilder.toString();
	}

	private static double getMemoryInKB() {
		double total = Runtime.getRuntime().totalMemory();
		return (total-Runtime.getRuntime().freeMemory()) / 10e3;
	}

	private static double getTimeInMilliseconds() {
		return System.nanoTime() / 10e6;
	}

	private static int charToIndex(char symbol) {
		int index = -1;
		if(symbol == 'A') {
			index = 0;
		}
		else if(symbol == 'C'){
			index = 1;
		}
		else if(symbol == 'G'){
			index = 2;
		}
		else if(symbol == 'T'){
			index = 3;
		}
		return index;
	}
}