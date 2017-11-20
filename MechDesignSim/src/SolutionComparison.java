import java.util.Arrays;

import javax.swing.JOptionPane;


//Asummed everything is 2x2 right now, can be modified in the future
class DistManager{
	
	private int rotateCount; //number of times current value
	double[][] dist;
	private double[] probVals;
	
	DistManager(int numR, int numV, double[] probs){
		if( numR<1 || numV <1 || probs.length != numR*numV || ! Utilities.checkDist(probs)){
			throw new IllegalArgumentException();
		}
		probVals = probs;
		dist = new double[numR][numV];
		rotateCount = 0;
		rotateDist();

	}
	
	public void changeProbVals(double[] pv ){
		if(!Utilities.checkDist(pv)){
			throw new IllegalArgumentException();
		}
		probVals = pv;
		rotateCount = 0;
		rotateDist();
	}
	
	public void rotateDist(){
		if(rotateCount >= probVals.length){
			rotateCount = 0;
		}
		
		int offsetIndex = rotateCount;
		//every value in probVals will be used once in the new dist, we did size check when initialized 
		for(int j=0; j<dist.length;j++){
			for(int k=0; k<dist[0].length;k++){
				dist[j][k] = probVals[offsetIndex];
				
				offsetIndex++;
				if(offsetIndex >= probVals.length){
					offsetIndex = 0;
				}
			}
		}
		rotateCount++;
	}
	
	private void fillLastProb(){
		int numR = dist.length;
		int numV = dist[0].length;
		double sum = 0;
		for(int i=0;i<numR;i++){
			for(int j=0;j<numV;j++){
				if(i != numR-1 && j != numV-1)
					sum += dist[i][j];
			}
		}
		if(sum >= 1)
			throw new IllegalStateException("invalid distribution");
		dist[numR-1][numV-1] = 1-sum;
	}
}

public class SolutionComparison {
	
	//create and solve problem(specify v values and dist)
	//1 value is fixed, the other is between 1 and x (incrementing by y)
	//2 by 2 dist specify 3 values 
	double[] probVals;
	double probValThreshold;
	double vValThreshold;
	double probInc;
	double vInc;
	int numR;
	int numV;
	double errorThreshold = .000001;
	
	public SolutionComparison(int numR,int numV, double pThresh, double pIncAmount, double vThresh, double vIncAmount){
		this.probValThreshold = pThresh;
		this.vValThreshold = vThresh;
		this.probInc = pIncAmount;
		this.vInc = vIncAmount;
		this.numR = numR;
		this.numV = numV;
		this.probVals = new double[ numV*numR];
	}
	
	public static void runProblemSet2x2(){
		
		double vMax = 1000;
		double vIncrementAmount = 1;
		double errorThreshold = .000001;
		double pIncAmount = .05;
		int numV = 2;
		int numR = 2;
		Mech_problem currProb;
		Mech_solution EPICsol,BICsol;
		ValueSetAndDistribution vsd;
		double[][] dist;
		
		double[] v = new double[numV];
		//assuming only 2 values for now
		v[0] = 1;
		v[1] = 1+vIncrementAmount;
		
	
		System.out.println("begining iteration of problem set");
		double p1 = 0;
		double p3 = 0;
		double p2 = 0;
		double p4 = 0;
		while(p1<=.5){
			System.out.println("new p1 "+p1);
			p3=0;
			while(p3<=(.5-p1)){
				p2 =0;
				while(p2 <= (1-p1-p3)){
						//go through each v value possibility
						
					p4 = 1 - p1 -p2 - p3;
					while(v[1]<vMax){
						dist = new double[][]{{p1,p2},{p3,p4}};
						vsd = new ValueSetAndDistribution(numR,v,dist);
						try {	
							currProb = new Mech_problem(vsd, Mech_problem.problemType.EPIC);
							EPICsol = currProb.solve(); //solve for EPIC version
							
							currProb = new Mech_problem(vsd, Mech_problem.problemType.BIC);
							BICsol = currProb.solve(); //solve for BIC version of problem
							
							//Compare solutions
							if(Math.abs(EPICsol.value() - BICsol.value()) > errorThreshold){
								System.out.println("difference in solutions:\n"+vsd);
								System.out.println(BICsol+"\n"+EPICsol);
							}
						} catch (Exception e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(null, "error in solver");
						}
						v[1] += vIncrementAmount;
					}
					//reset v[1]
					v[1] = 1+vIncrementAmount;
					p2 += pIncAmount;
				}
				p3+=pIncAmount;
			}
			p1+=pIncAmount;
		}
		System.out.println("finish problem set");
	}

}
