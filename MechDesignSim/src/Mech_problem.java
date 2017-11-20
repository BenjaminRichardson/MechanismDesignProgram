

import scpsolver.constraints.LinearBiggerThanEqualsConstraint;
import scpsolver.constraints.LinearSmallerThanEqualsConstraint;
import scpsolver.lpsolver.LinearProgramSolver;
import scpsolver.lpsolver.SolverFactory;
import scpsolver.problems.LinearProgram;

/* Author: Ben Ricardson 
 */

//allows for easy calculation of index of mega-vector in LP
//vector has one entry for every r,v combination and then an entry for allocation(x) and payment(p)
class XP_Matrix{
	
	int numV;
	int numR;
	int vectorSize;
	
	XP_Matrix(int numberOfRVals, int numberOfVVals){
		numV = numberOfVVals;
		numR = numberOfRVals;
		vectorSize = numV*numR*2;//vector has one entry for every r,v combination and then an entry for allocation(x) and an entry for payment(p)
	}
	
	public int getIndex(int rIndex,int vIndex,boolean isAllocation) throws Exception{
		
		if(rIndex < 0 || rIndex >= numR)
			throw new Exception("invlaid r index");
		if(vIndex < 0 || vIndex >= numV)
			throw new Exception("invlaid v index");
		
		
		int isAllocationFactor;
		if(isAllocation)
			isAllocationFactor = 0;
		else
			isAllocationFactor = 1;
		
		int index =  rIndex + numR*vIndex + isAllocationFactor* (numV*numR); // in theory this maps to a unique id
		
		return index;
	}
}




public class Mech_problem {
	private LinearProgram lp;
	private ValueSetAndDistribution valueSet;
	private XP_Matrix xpm;
	private problemType type;//0 - EPIC, 1 - BIC, 2 - EPIC dual, 3 BIC dual
	
	public enum problemType{
		EPIC,BIC,EPIC_dual,BIC_dual
	}
	
	public Mech_problem(ValueSetAndDistribution vs, problemType t) throws Exception{
		this.type = t;
		valueSet = vs;
		xpm = new XP_Matrix(vs.num_r_values, vs.v_values.length);
		double[] objVect = this.createObjectiveFunction();
		lp = new LinearProgram(objVect); 
		
	}
	
	public Mech_solution solve() throws Exception{
		
		lp.setMinProblem(false);
		this.addFeasibilityContraints();
		this.addRationalityContraints();
		
		
		switch(this.type){
		case EPIC :	this.addTruthtellingContraints();
					break;
		case BIC: 	this.addTruthTellingContraintsInExpection();
					break;
		case EPIC_dual:
		case BIC_dual:
		}
		if(type == problemType.EPIC )
			this.addTruthtellingContraints();
		if(type == problemType.BIC)
			this.addTruthTellingContraintsInExpection();
		
		
		LinearProgramSolver solver  = SolverFactory.newDefault(); 
		double[] solVector = solver.solve(lp);
		
		double[][] solutionAllocMatrix = createSolutionMatrix(solVector,true);
		double[][] solutionPaymentMatrix = createSolutionMatrix(solVector,false);
		
		double solutionValue = 0;
		int index;
		double[] objVector = this.createObjectiveFunction();
		for(int i = 0; i < xpm.numR; i++){
			for(int j=0; j < xpm.numV; j ++){
				index = xpm.getIndex(i, j, false);//since we only concerned with payment values
				solutionValue += objVector[index]*solVector[index]; 
			}
		}
		
		Mech_solution solObject = new Mech_solution(solutionValue,solutionAllocMatrix,solutionPaymentMatrix,type);
		return solObject;
	}
	
	private double[][] createSolutionMatrix(double[] solV, boolean allocationMatrix) throws Exception{
		double[][] matrix = new double[xpm.numR][xpm.numV];
		for(int i = 0; i < xpm.numR; i++){
			for(int j=0; j < xpm.numV; j ++){
				matrix[i][j] = solV[xpm.getIndex(i, j, allocationMatrix)];
			}
		}
		return matrix;
	}
	
	// Creates objective vector, one allocation value x and one payment value p for all possibe (r,v) tuples
	private double[] createObjectiveFunction() throws Exception{
		double[] objVect = new double[xpm.vectorSize];
		
		for(int i = 0; i < xpm.numR; i++){
			for(int j=0; j <xpm.numV; j ++){
				objVect[xpm.getIndex(i, j, true)] = 0 ;//set allocation values to zero since they don't affect objective value
				objVect[xpm.getIndex(i, j, false)] = valueSet.jointDist[i][j];
			}
		}
		
		return objVect;
	}
	
	private void addTruthtellingContraints() throws Exception{
		
		 double[] newConstraint;
		 double value;
		 // i is index of r value
		 // j is index of true v value for agent
		 // k is index of untruthful v value for agent
		 //v[j]* x(i,j) -v[j]*x(i,k) + p(i,k) - p(i,j) >= 0 
		 for(int i=0; i < xpm.numR; i ++){
			 for(int j=0; j < xpm.numV; j ++){
				 value = valueSet.v_values[j]; //current true-value for this contraint
				 //now add all possible other values

				 for(int k = 0;k<xpm.numV; k++){
					 //Mathematically it'd be ok to include the same must be greater, but not necisarry (it's just 0>=0). Causes issues with indexing
					 if(k != j ) {
						 newConstraint = new double[xpm.vectorSize]; // java autofills with zeros
						 
						 newConstraint[xpm.getIndex(i, j, true)] = value; //sets the vector corresponding to alloc(i,j) to true value
						 newConstraint[xpm.getIndex(i, k, true)] = -1.0*value; // sets the vector corresponding to alloc(i,k) to negative true value
						 newConstraint[xpm.getIndex(i, k, false)] = 1;
						 newConstraint[xpm.getIndex(i, j, false)] = -1;
					 
						 lp.addConstraint(new LinearBiggerThanEqualsConstraint(newConstraint,0,"value["+i+"](x("+i+","+j+")-x("+i+","+k+"))+(p("+i+","+k+")-p("+i+","+j+")>=0"));
					 }
				 }	 
				 
			 }
		 }
	}
	
	private void addTruthTellingContraintsInExpection() throws Exception{
		
		double[] newConstraint;
		double value;
		double conditionalProb;
		
		
		// j index for the true v value
		// k index of v value agent could lie about
		for(int j=0; j<xpm.numV; j++){//iterate over all true values
			value = valueSet.v_values[j];// true value the agent has
			for(int k=0; k < xpm.numV; k++ ){
				if(j != k){
					newConstraint = new double[xpm.vectorSize];//java autofills with zeros
					
					for(int i =0; i<xpm.numR; i++){//iterate over all r values
						//note as this loop iterates, it modifies the same constraint
						conditionalProb = valueSet.probRGivenV(i,j);//we know what v is, so we condition prob of r given what we know v is.
						
						//everything is multiplied by the probability of being in (r,v) value
						newConstraint[xpm.getIndex(i, j, true)] = value*conditionalProb; //sets the vector corresponding to alloc(i,j) to true value
						newConstraint[xpm.getIndex(i, k, true)] = -1.0*value*conditionalProb; // sets the vector corresponding to alloc(i,k) to negative true value
						newConstraint[xpm.getIndex(i, k, false)] = 1*conditionalProb;
						newConstraint[xpm.getIndex(i, j, false)] = -1*conditionalProb;
						
					}
					
					
					lp.addConstraint(new LinearBiggerThanEqualsConstraint(newConstraint,0,"expected truthtelling constraint for true value "+j+", false value "+k));
					
				}
			}
			
			
		}
		
	}
	
	
	// v*x(i,j) - p(i,j) >= 0
	private void addRationalityContraints() throws Exception{
		
		 double[] newConstraint;
		 double value;
		 // i is index of r value
		 // j is index of true v value for agent
		 //v[j]* alloc(i,j) - p(i,j) >= 0 
		 for(int i=0; i < xpm.numR; i ++){
			 for(int j=0; j < xpm.numV; j ++){
				 value = valueSet.v_values[j]; //current true-value for this contraint
				 //now add all possible other values

				 newConstraint = new double[xpm.vectorSize]; // java autofills with zeros
				 newConstraint[xpm.getIndex(i, j, true)] = value; //sets the vector corresponding to alloc(i,j) to true value
				 newConstraint[xpm.getIndex(i, j, false)] = -1;
				 lp.addConstraint(new LinearBiggerThanEqualsConstraint(newConstraint,0,"value["+i+"]x("+i+","+j+")-p("+i+","+j+")>=0"));
				 
			 }
		 }
	}
	
	
	//  0 <= x(i,j) <= 1
	private void addFeasibilityContraints() throws Exception{
		
		 double[] newConstraint;
		 for(int i=0; i < xpm.numR; i ++){
			 for(int j=0; j < xpm.numV; j ++){
				 //greater than or equal to zero
				 newConstraint = new double[xpm.vectorSize]; // java autofills with zeros
				 newConstraint[xpm.getIndex(i, j, true)] = 1;
				 lp.addConstraint(new LinearBiggerThanEqualsConstraint(newConstraint,0,"x("+i+","+j+")>=0")); // greater than or equal to zero
				 
				 lp.addConstraint(new LinearSmallerThanEqualsConstraint(newConstraint,1,"x("+i+","+j+")<=1")); // less than or equal to 1
			 }
		 }
	}
	
	
	
}
