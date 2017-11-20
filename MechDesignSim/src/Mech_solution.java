
public class Mech_solution {

	
	private double objectiveValue;
	private double[][] allocationMatrix;
	private double[][] paymentMatrix;
	private Mech_problem.problemType type;
	
	public Mech_solution(double obj, double[][] allocM, double[][] paymentM, Mech_problem.problemType t){
		objectiveValue = obj;
		allocationMatrix = allocM;
		paymentMatrix = paymentM;
		type = t;
	}
	
	public double value(){
		return objectiveValue;
	}
	
	//matrix[r][v]
	public double[][] allocationMatrix(){
		return allocationMatrix;
	}
	
	public double[][] paymentMatrix(){
		return paymentMatrix;
	}
	
	public String toString(){
		StringBuilder message = new StringBuilder("------------------------\n\t"+type+"\t Solution\n-------------------------\n");
		
		message.append("Optimal Value: "+objectiveValue+"\n\n");
		message.append("Allocation Matrix: \n");
		message.append(Utilities.double2DArrayString(allocationMatrix)+"\n");
		message.append("Payment Matrix: \n");
		message.append(Utilities.double2DArrayString(paymentMatrix)+"\n");
		
		return message.toString();
	}
}
