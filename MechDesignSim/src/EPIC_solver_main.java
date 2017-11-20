import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.table.TableModel;

import org.apache.commons.cli.*;

// @ Author Dimitris, Ben

public class EPIC_solver_main {

	private static GroupLayout layout;
	private static GroupLayout.SequentialGroup topToBottom;
	private static GroupLayout.ParallelGroup leftToRight;
	
	private static JFrame frame;
	private static JTextPane vValues;
	private static JTextPane jointTableDescription;
	private static JTextField numV,numR;
	private static JButton setV, setR, validateAndContinue,validateAndSolve,validateAndSolveEPIC,validateAndSolveBIC;
	
	private static JTable vValueTable;
	private static JTable jointDistTable;
	
	private static JScrollPane spV,spD;
	
	
	private static double[] v_vals;
	private static int num_r_vals;
	private static double[][] dist;
	
	private static void createLayout()
	{
		JPanel panel = new JPanel();
		layout = new GroupLayout(panel);
	    layout.setAutoCreateContainerGaps(true);
	    layout.setAutoCreateGaps(true);
	    panel.setLayout(layout);
		
	    frame = new JFrame("Create Instance");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	    topToBottom = layout.createSequentialGroup();
	    leftToRight = layout.createParallelGroup();
  	    
	    layout.setHorizontalGroup(leftToRight);
	    layout.setVerticalGroup(topToBottom);
	    
	    frame.add(panel);
	}
	
	private static void createNumVValues(){
		vValues = createTextPane("Number of v values: ", 12, true);
		numV = new JTextField("",3);
		setV = new JButton("Set");
		
		setV.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e){
				try {
					int n=Integer.parseInt(numV.getText());
					if(n>0) {
						setV.setEnabled(false);
						numV.setEditable(false);
						
						v_vals = new double[n];
						//show v value entry window
						vValueTable = new JTable(1,v_vals.length);
						vValueTable.putClientProperty("terminateEditOnFocusLost", true);
						spV.setPreferredSize(new Dimension(spV.getPreferredSize().width,
															3*vValueTable.getRowHeight()));
						spV.getViewport().add(vValueTable);
						
						//create input table for joint pdf
						frame.pack();
					    
					} else {
						JOptionPane.showMessageDialog(null, "Input Error: Not a positive integer.");
						numV.setText("");
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Input Error: Not a positive integer.");
					numV.setText("");
				}
			}
		});
		
		GroupLayout.SequentialGroup g = layout.createSequentialGroup();  
		g.addComponent(vValues);
		g.addComponent(numV);
		g.addComponent(setV);
		
		GroupLayout.ParallelGroup p = layout.createParallelGroup();  
		p.addComponent(vValues);
		p.addComponent(numV);
		p.addComponent(setV);
		
		leftToRight.addGroup(g);
		topToBottom.addGroup(p);
	}
	
	private static void createNumRValues(){

		JTextPane rValues = createTextPane("Number of r values: ", 12, true);
		numR = new JTextField("",3);
		setR = new JButton("Set");
		
		setR.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e){
				try {
					int n = Integer.parseInt(numR.getText());
					if(n>0) {
						setR.setEnabled(false);
						numR.setEditable(false);
						
						num_r_vals = n;
	
						vValues.setVisible(true);
						numV.setVisible(true);
						setV.setVisible(true);
						
						frame.pack();
					    
					} else {
						JOptionPane.showMessageDialog(null, "Input Error: Not a positive integer.");
						numR.setText("");
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Input Error: Not a positive integer.");
					numR.setText("");
				}
			}
		});
		
		GroupLayout.SequentialGroup g = layout.createSequentialGroup();  
		g.addComponent(rValues);
		g.addComponent(numR);
		g.addComponent(setR);
		
		GroupLayout.ParallelGroup p = layout.createParallelGroup();  
		p.addComponent(rValues);
		p.addComponent(numR);
		p.addComponent(setR);
		
		leftToRight.addGroup(g);
		topToBottom.addGroup(p);
	
	}
	
	private static void createVValues(){
		spV = new JScrollPane();
		
		leftToRight.addComponent(spV);
		topToBottom.addComponent(spV);
	}
	
	
	private static JTextPane createTextPane(String caption, int size, boolean visible)
	{
		JTextPane t = new JTextPane();
		
		t = new JTextPane();
		t.setFont(new Font("default", Font.BOLD, size));
		t.setEditable(false);
		t.setText(caption);
		t.setVisible(visible);
		t.setOpaque(false);
		
		return t;
	}
	
	private static void validateVR(){
		
		validateAndContinue = new JButton("Continue to distribution entry");
		
		validateAndContinue.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e){
				try {
					//check that text boxes are not enabled
					if( !numV.isEditable() &&
						!numR.isEditable()) 
					{
						createDistTable();
					    
					} else {
						JOptionPane.showMessageDialog(null, "Input Error: Not all info has been entered.");
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Input Error: Not a positive integer.");
				}
			}
		});
		
		GroupLayout.SequentialGroup g = layout.createSequentialGroup();  
		g.addComponent(validateAndContinue);

		
		GroupLayout.ParallelGroup p = layout.createParallelGroup();  
		p.addComponent(validateAndContinue);

		
		leftToRight.addGroup(g);
		topToBottom.addGroup(p);
	}
	
	private static void createDistTable(){
		
		
		jointTableDescription = createTextPane("Enter Joint Distribution, r - rows, v - columns: ", 12, true);
		leftToRight.addComponent(jointTableDescription);
		topToBottom.addComponent(jointTableDescription);
		
		spD = new JScrollPane();
		jointDistTable = new JTable(num_r_vals,v_vals.length);
		jointDistTable.putClientProperty("terminateEditOnFocusLost", true);
		spD.getViewport().add(jointDistTable);
		leftToRight.addComponent(spD);
		topToBottom.addComponent(spD);
		
		validateAndSolve = new JButton("Solve Both");//TODO: create three buttons, one for EPIC, one for BIC, one for both

		validateAndSolve.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e){
				try {
					GUIExtractAndSolve();
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Input Error.");
				}
			}
		});
		
		validateAndSolveEPIC = new JButton("Solve EPIC");//TODO: create three buttons, one for EPIC, one for BIC, one for both

		validateAndSolveEPIC.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e){
				try {
					GUIExtractAndSolve(Mech_problem.problemType.EPIC);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Input Error.");
				}
			}
		});
		
		validateAndSolveBIC = new JButton("Solve BIC");//TODO: create three buttons, one for EPIC, one for BIC, one for both

		validateAndSolveBIC.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e){
				try {
					GUIExtractAndSolve(Mech_problem.problemType.BIC);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Input Error.");
				}
			}
		});
			
		GroupLayout.SequentialGroup g = layout.createSequentialGroup();
		
		g.addComponent(jointTableDescription);
		g.addComponent(validateAndSolveEPIC);
		g.addComponent(validateAndSolveBIC);
		g.addComponent(validateAndSolve);

		
		GroupLayout.ParallelGroup p = layout.createParallelGroup();
		
		p.addComponent(jointTableDescription);
		p.addComponent(validateAndSolveEPIC);
		p.addComponent(validateAndSolveBIC);
		p.addComponent(validateAndSolve);
		
		leftToRight.addGroup(g);
		topToBottom.addGroup(p);
		
		frame.pack();
	}
	
	private static void GUIExtractAndSolve(){
		GUIExtractAndSolve(Mech_problem.problemType.BIC);
		GUIExtractAndSolve(Mech_problem.problemType.EPIC);
	}
	
	
	private static void GUIExtractAndSolve(Mech_problem.problemType type){
		
		double currNum;
		dist = new double[num_r_vals][v_vals.length];
		// get contents of V as double array
		TableModel vTableModel = vValueTable.getModel();
		for( int i = 0; i < v_vals.length; i ++){
			currNum = Double.parseDouble(vTableModel.getValueAt(0, i).toString());
			v_vals[i] = currNum;
		}
		// get dist as double array, ensure validity
		for(int i = 0; i < num_r_vals; i++){
			for(int j =0; j < v_vals.length; j++){
				currNum = Double.parseDouble(jointDistTable.getValueAt(i, j).toString());
				System.out.println(currNum);
				dist[i][j] = currNum;
			}
		}
		// send into EPIC Problem
		solveAndDisplay(num_r_vals,v_vals,dist,true,type);
		
	}
	
	public static void solveAndDisplay(int r,double[] v, double[][] distribution,boolean guiEnabled,Mech_problem.problemType type){
		ValueSetAndDistribution vsd = new ValueSetAndDistribution(r,v,distribution);
		if(vsd.checkDist()){
			try {
				System.out.println(vsd);
				Mech_problem ep = new Mech_problem(vsd,type);
				Mech_solution sol = ep.solve();
				System.out.print(sol);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "error in solver");
			}
		}else{
			if(guiEnabled){
				JOptionPane.showMessageDialog(null, "Distribution is not valid, please reenter");
			}else{
				System.err.println("Error in specified joint distribution");
			}
			
		}
	}
	
	
	
	public static void main(String args[]){
		
		if(args.length >= 1){
			Options options = new Options();
			
			options.addOption("r", true, "number of possible r values");
			options.addOption("v",true,"comma delimited list of possible v values");
			options.addOption("dist", true, "Joint distribution of r and v, r values are rows, v values are columns \n "
												+ "commas indicate seperate columns, semicolons indicate a new row\n"
												+ "ie 1,2,3;4,5,6 would be matrix with index 1,1 containing 1. index(1,2) containing 2. index(2,1) containting 4\n"
												+ "must be a rectangular (all rows same length)\n"
												+"maps to r and v in order that they are entered left to right, top to bottom");
			options.addOption("t", true, "type of problem, maps to Mech_problem enum");
			CommandLineParser parser = new DefaultParser();
			
			try{
				CommandLine line = parser.parse( options, args );

				
				if(line.hasOption("r") && line.hasOption("v") && line.hasOption("dist") && line.hasOption("t")){
					
					String rRaw = line.getOptionValue("r");
					String vRaw = line.getOptionValue("v");
					String distRaw = line.getOptionValue("dist");
					String typeRaw = line.getOptionValue("t");
					
					int r = Integer.parseInt(rRaw);
					double[] v = Utilities.parseStringArray(vRaw);
					double[][] distrib = Utilities.parseString2DArray(distRaw);
					
					Mech_problem.problemType type;
					if(typeRaw.equals(Mech_problem.problemType.BIC.toString())){
						type = Mech_problem.problemType.BIC;
					} else if(typeRaw.equals(Mech_problem.problemType.EPIC.toString())){
						type = Mech_problem.problemType.EPIC;
					}else{
						type = null;
					}
					
					if(Utilities.is2DDoubleArrayRectangular(distrib) && r == distrib.length && v.length == distrib[0].length && type != null){
						solveAndDisplay(r,v,distrib,false,type);
					}else{
						System.err.println("Error invalid command line argumets, size of dist does not match r and v");
					}
					
				}else{
					System.err.println("Error invalid command line argumets");
				}
			}catch( ParseException exp ) {
		        // oops, something went wrong
		        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
		    }
			
			
		}else{
			graphicMain();
		}
	}
		
	private static void graphicMain(){
		createLayout();
		createNumRValues();
		createNumVValues();
		createVValues();
		validateVR();
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	//CommandLine
	// specifiy array r, array v, 2-d array matrix pdf (or type of joint pdf?)
	// print solution object
	
	//GUI
	//user specifies number of r values & number of v values
	//joint pmf
	//validate pmf
	// solve, return x matrix, p matrix, obj value
	
}
