import java.io.PrintWriter;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class SolutionComparisonMain {

	//dual NEXT
	public static void main(String args[]){
		
		Mech_problem currProb;
		Mech_solution EPICsol,BICsol;
		ValueSetAndDistribution vsd;
		double errorThreshold = .1;
		
		///SolutionComparison.runProblemSet2x2();
		int valueInc = 1;
		int valueMax = 100;
		
		//read in file into buffer
		try{
			PrintWriter writer = new PrintWriter("SimulationOutcomes.txt", "UTF-8");
			LinkedList<double[][]> distList =  DistWrapper.fileToLinkedList("3x3dist.txt",3,3);
			double maxRatio = 0;
			double currRatio = 0;
			for(double[][] dist: distList){
				System.out.println("newDist:"+System.currentTimeMillis());
				//iterate through value options
				for(int v2 = valueInc+1;v2 < valueMax;v2+=valueInc){
					for(int v3 = valueInc+1; v3 < valueMax;v3+=valueInc){
						for(int oneIndex = 0;oneIndex<3;oneIndex++){
							double[] v = new double[3];
							if(oneIndex == 0 ){
								v[0] = 1;
								v[1] = v2;
								v[2] = v3;
							}else if(oneIndex ==1 ){
								v[1] = 1;
								v[2] = v2;
								v[0] = v3;
							}else{
								v[2] = 1;
								v[0] = v2;
								v[1] = v3;
							}
							vsd = new ValueSetAndDistribution(3,v,dist);
							try {	
								currProb = new Mech_problem(vsd, Mech_problem.problemType.EPIC);
								EPICsol = currProb.solve(); //solve for EPIC version
								
								currProb = new Mech_problem(vsd, Mech_problem.problemType.BIC);
								BICsol = currProb.solve(); //solve for BIC version of problem
								
								//Compare solutions
								if(Math.abs(EPICsol.value() - BICsol.value()) > errorThreshold){
									System.out.println("difference in solutions:\n"+vsd);
									System.out.println(BICsol+"\n"+EPICsol);
									
									
									currRatio = BICsol.value()/EPICsol.value();
									if(currRatio > maxRatio){
										maxRatio = currRatio;
									}
									writer.println("-----------------------------------");
									writer.println("difference="+currRatio);
									writer.println("difference in solutions:\n"+vsd);
									writer.println(BICsol+"\n"+EPICsol);
								}
							} catch (Exception e) {
								e.printStackTrace();
								JOptionPane.showMessageDialog(null, "error in solver");
							}
						}
					}
				}
				
			}
			writer.println("-----------------------------------");
			writer.println("-----------------------------------");
			writer.println("max ratio:"+maxRatio);
			System.out.println(maxRatio);
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error in file IO");
		}
		System.out.println("Fin");
		
	}
}
