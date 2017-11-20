import java.util.ArrayList;

public class Utilities {

	public static String double2DArrayString(double[][] ar) {
		double currNum;
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < ar.length; i++) {
			for (int j = 0; j < ar[i].length; j++) {
				currNum = ar[i][j];
				sb.append(currNum + " ");
			}
			sb.append("\n");
		}

		return sb.toString();
	}

	public static String doubleArrayToString(double[] ar) {
		double currNum;
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < ar.length; i++) {
			currNum = ar[i];
			sb.append(currNum + " ");
		}
		sb.append("\n");
		return sb.toString();
	}

	// Assumes commas delimit columns, semicolons delimit rows, should all be
	// doubles
	public static double[][] parseString2DArray(String arStr) {
		ArrayList<double[]> ALar = new ArrayList<double[]>();

		// convert to ArrayList of double arrays
		String[] rows = arStr.split(";");
		for (String row : rows) {
			ALar.add(parseStringArray(row));
		}

		// turn previously created list to array of array of doubles
		double[][] ar = new double[ALar.size()][];
		for (int i = 0; i < ALar.size(); i++) {
			ar[i] = ALar.get(i);
		}

		return ar;
	}

	// Assumes comma delimited list of doubles
	public static double[] parseStringArray(String arStr) {
		String[] rowStrings = arStr.split(",");
		double[] rowDoubles = new double[rowStrings.length];

		for (int i = 0; i < rowStrings.length; i++) {
			rowDoubles[i] = Double.parseDouble(rowStrings[i]);
		}

		return rowDoubles;
	}

	public static boolean is2DDoubleArrayRectangular(double[][] obj) {

		int rowLength = obj[0].length;
		for (int i = 0; i < obj.length; i++) {
			if (obj[i].length != rowLength) {
				return false;
			}
		}

		return true;
	}

	// Checks if current distribution is valid
	// - all rows and columns sum to one
	// - all sub arrays are of the same size
	public static boolean checkDist(double[][] jointDist) {
		int n = jointDist.length; // corresponds to number of r values
		int m = jointDist[0].length; // corresponds to number of v values

		// check all sub arrays are of same size
		for (int i = 1; i < n; i++) {
			if (jointDist[i].length != m) {
				System.err.println("error in length of one v row");
				return false;
			}
		}

		// check all rows sum to one and all values are inbetween 0 and 1
		double currSum = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				if (jointDist[i][j] < 0 || jointDist[i][j] > 1) {
					return false;
				}
				currSum += jointDist[i][j];
			}
		}

		if (Math.abs(1.0 - currSum) > .000000000001) {
			System.err.println("Error in sum of elements " + currSum);
			return false;
		}
		return true;
	}

	public static boolean checkDist(double[] probs) {
		double[][] wrapper = { probs };
		return checkDist(wrapper);
	}

	public static double[][] createNormalizedDist(int numR, int numV, double[] rawProbs) {

		if (rawProbs.length != numR * numV) {
			throw new IllegalArgumentException();
		}

		double currProb;
		double[][] dist = new double[numR][numV];
		double sum = sum(rawProbs);
		int rawIndex = 0;
		for (int rIndex = 0; rIndex < numR; rIndex++) {
			for (int vIndex = 0; vIndex < numV; vIndex++) {
				currProb = (double) (rawProbs[rawIndex]) / sum;
				dist[rIndex][vIndex] = currProb;
				rawIndex++;
			}
		}

		return dist;

	}

	public static double sum(double[] array) {
		double sum = 0.0;
		for (double i : array) {
			sum +=  i;
		}
		return sum;
	}

	
	//null indicates no changes have been made
	//this is intended for repeated calls, where the array is not modified by anything but this function
	//Analogous to a number-system with a base of the threshold, that is having the incAmount added each time
	//when a digit reaches "capacity" it overflows to the next digit, then all the other "digits" count up until overflow again
	public static double[] incrementArrayToThresh(double[] array, double threshold, double incAmount){
		return incrementArrayToThresh(array,threshold,incAmount,0);
	}
	
	public static double[] incrementArrayToThresh(double[] array, double threshold, double incAmount,int startIndex) {
		int i = startIndex;
		double oldVal,rollOverVal;
		boolean updated = false;
		while (i < array.length && !updated) {
			if (array.length < threshold) {
				updated = true; //will only need to update one value;
				oldVal = array[i];
				if (oldVal + incAmount > threshold) {
					if (i != array.length - 1) {
						// special case when
						array[i + 1] = incAmount;
						for(int j=0; j<i+1; j++){
							array[j] = 0;
						}
					}else{
						array[i] = threshold;
					}
				} else {
					array[i] = oldVal + incAmount;
				}
			}
			i++;
		}
		if(i >= array.length && !updated){
			return null; //we've been through every value, assuming iteration and no other modifications
		}
		return array;
	}

}
