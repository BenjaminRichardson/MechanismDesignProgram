import java.io.BufferedReader;
import java.io.*;
import java.util.*;

public class DistWrapper {

	
	
	public static LinkedList<double[][]> fileToLinkedList(String fileName, int numR, int numV){
		LinkedList<double[][]> dists = new LinkedList<double[][]>();
		File file = new File(fileName);
		BufferedReader reader = null;

		try {
		    reader = new BufferedReader(new FileReader(file));
		    String text = null;
		    String[] rowlist = null;
		    String[] rowStr = null;
		    double[][] currDist; 

		    //each line is one list of doubles, semicolons indicate new row, commas a column separation
		    while ((text = reader.readLine()) != null) {
		    	currDist = new double[numR][numV];
		    	
		    	rowlist = text.split(";");
		        for(int i = 0; i< rowlist.length; i++){
		        	rowStr = rowlist[i].split(",");
		        	for(int j =0; j<rowStr.length; j++){
		        		currDist[i][j] = Double.parseDouble(rowStr[j]);
		        	}
		        }
		        
		        dists.add(currDist);
		    }
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {
		    }
		}
		
		return dists;
	}

}
