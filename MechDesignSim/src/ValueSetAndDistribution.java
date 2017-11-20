


//currently assumes single dimensional v and r 
public class ValueSetAndDistribution{
	
	public int num_r_values;
	public double[] v_values;
	public double[] v_dist;
	public double[][] jointDist; // let the first array's size match the number of r values and the sub arrays match the number of v vals
	
	
	//Constructor
	ValueSetAndDistribution(int numRVals, double[] vVals, double[][] dist){
		
		num_r_values = numRVals;
		v_values = vVals;
		jointDist = dist; // r[0],v[3] corresponds to jointDist[0][3]
		createVDist(); //specify v value, gives probability of a certain r value, specify each [r][v] same as joint dist
		
	}
	
	private void createVDist(){
		v_dist = new double[v_values.length]; // java automatically fills with zeros
		for(int i=0;i<v_values.length;i++){
			//sum out over all r values
			for(int j=0;j<num_r_values;j++){
				v_dist[i] += jointDist[j][i];
			}
		}
	}
	
	public double probRGivenV(int r_index, int v_index){
		return jointDist[r_index][v_index]/v_dist[v_index];
	}
	
	//Checks if current distribution is valid 
	// - all rows and columns sum to one
	// - all sub arrays are of the same size
	public boolean checkDist(){
		int n = jointDist.length; //corresponds to number of r values
		int m = jointDist[0].length; // corresponds to number of v values
		
		if( n != num_r_values ||  m != v_values.length ){
			System.out.println("error in length or r and v");
			return false;
		}
			
		//check all sub arrays are of same size
		for(int i = 1 ; i < n; i ++){
			if(jointDist[i].length != m){
				System.out.println("error in length of one v row");
				return false;
			}
		}
		
		//check all rows sum to one and all values are inbetween 0 and 1
		double currSum = 0;
		for(int i=0; i < n; i++){
			for(int j=0; j < m; j++){
				if( jointDist[i][j] < 0 || jointDist[i][j] > 1){ return false; }
				currSum += jointDist[i][j];
			}
		}
		
		
		if(Math.abs(1.0-currSum) > .000000000001 ){ 
			System.out.println("Error in sum of elements "+currSum);
			return false; 
		}
		return true;
	}
	
	public String toString(){
		
		StringBuilder sb = new StringBuilder("v and r values and distribution:\n\n");
		sb.append("r: "+num_r_values+"\n");
		sb.append("v: "+Utilities.doubleArrayToString(v_values)+"\n");
		sb.append("\ndist: \n"+Utilities.double2DArrayString(jointDist)+"\n");
		
		return sb.toString();
	}
}