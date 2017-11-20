/**
 * 
 */



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

import scpsolver.constraints.LinearBiggerThanEqualsConstraint;
import scpsolver.constraints.LinearSmallerThanEqualsConstraint;
import scpsolver.lpsolver.LinearProgramSolver;
import scpsolver.lpsolver.SolverFactory;
import scpsolver.problems.LinearProgram;


/**
 * @author dimitris
 *
 */

class EpicInstance {
	private static LinearProgramSolver solver = SolverFactory.newDefault();
	private int m;
	private int n;
	
	double distribution[][];
	
	/*
	 * Constructor.
	 */
	EpicInstance(JTable inputTable) {
		n = inputTable.getColumnCount();
		m = inputTable.getRowCount();
		
		distribution = new double[m][n];

		System.out.println(m);
		System.out.println(n);
		
		for(int i=0;i<n;++i) {
			for(int j=0;j<m;++j) {
				distribution[j][i]= Double.parseDouble(inputTable.getModel().getValueAt(j, i).toString());
			}
		}
	}
	
	/*
	 * Solve this instance and return the value of the objective function and the solution in a list.	
	 */
	double solve(ArrayList<Double> sol)
	{	
		
		/****** TO DO: CREATE OBJECTIVE FUNCTION **********/
		
		int varNo = 3; /*NUMBER OF VARIABLES */
		double objective[] = new double[varNo];
		
		objective[0]=1;
		
		/****** This creates a new LP with the objective defined above **********/
		LinearProgram lp = new LinearProgram(objective);
		
		/****** TO DO: CREATE CONSTRAINTS **********/
		
		/* Example 1: Sum of variables less than or equal to 1 */
		
		double constr[]= new double[varNo];
		
		for (int j=0; j<varNo; ++j)
			constr[j]=1;
		
		lp.addConstraint(new LinearSmallerThanEqualsConstraint(constr, 1, "c0"));
		
		/* Example 2: All variables are non-negatives */
		
		for (int i=0; i<varNo; ++i) {
			for (int j=0; j<varNo; ++j) {
				if(i == j )
					constr[j]=1;
				else
					constr[j]=0;
			}
			
			/* x_i>=0 */
			lp.addConstraint(new LinearBiggerThanEqualsConstraint(constr, 0, "c"+(i+1)));
		}
		
		/* Is this a min or a max problem? */
		lp.setMinProblem(false); 
		
		double solution[] = solver.solve(lp);

		for(int i=0; i<varNo; ++i)
			sol.add(solution[i]);
		
		return lp.evaluate(solution);
	}
}

public class Epic {
	
	private static JFrame frame;
	
	private static JTextPane values;
	
	private static JTextField numI, numV;

	private static JButton setn, setm, solve;
	
	private static JTable inputTable;
	
	private static GroupLayout layout;
	
	private static GroupLayout.SequentialGroup topToBottom;
	
	private static GroupLayout.ParallelGroup leftToRight;
	
	private static int n, m;

	/*
	 * Create the part of the frame corresponding to Items
	 */
	private static void createItemsSection()
	{
	    JTextPane items = createTextPane("Number of Items: ", 12, true);
		
		numI = new JTextField("",3);
		
		setn = new JButton("Set");		
		setn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
					if((n=Integer.parseInt(numI.getText()))>0) {
						setn.setEnabled(false);
						numI.setEditable(false);
						values.setVisible(true);
						numV.setVisible(true);
						setm.setVisible(true);
						frame.pack();
					    
					} else {
						JOptionPane.showMessageDialog(null, "Input Error: Not a positive integer.");
						numI.setText("");
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Input Error: Not a positive integer.");
					numI.setText("");
				}
				
				
			}
		});
		
		GroupLayout.SequentialGroup g = layout.createSequentialGroup();  
		
		g.addComponent(items);
		g.addComponent(numI);
		g.addComponent(setn);
		
		
		
		GroupLayout.ParallelGroup p = layout.createParallelGroup();  
		
		p.addComponent(items);
		p.addComponent(numI);
		p.addComponent(setn);
		
		leftToRight.addGroup(g);
		topToBottom.addGroup(p);
	}
	
	/*
	 * Solve this instance and show the results.
	 */
	private static void solve()
	{
		double value;
		ArrayList<Double> solution = new ArrayList<Double>();
		
		if(inputTable.getCellEditor()!=null)
			inputTable.getCellEditor().stopCellEditing();
		
		EpicInstance instance = new EpicInstance(inputTable);
		value = instance.solve(solution);
		
		/* Print the solution */
		System.out.println(value);
	}
	
	/*
	 * Create the part of the frame corresponding to Input
	 */
	private static void createInputSection()
	{
		try {
			if((m = Integer.parseInt(numV.getText()))>0) {

				numV.setEditable(false);
				setm.setEnabled(false);
				
				inputTable = new JTable(m, n);
							
				solve = new JButton("Solve");
				solve.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						solve();
					}
				});
				
				JScrollPane sp = new JScrollPane();
				
				sp.getViewport().add(inputTable);
				
				leftToRight.addComponent(sp);
				topToBottom.addComponent(sp);
				
				leftToRight.addComponent(solve);
				topToBottom.addComponent(solve);
										
				frame.pack();
				frame.setLocationRelativeTo(null);
			    frame.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(null, "Input Error: Not a positive integer.");
				numV.setText("");
			}
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(null, "Input Error: Not a positive integer.");
			numV.setText("");
		}
	}
	
	
	/*
	 * Create the part of the frame corresponding to Values
	 */
	private static void createValuesSection()
	{
		values = createTextPane("Number of Values: ", 12, false);

	    numV = new JTextField("",3);
	    numV.setVisible(false);
	    
	    setm = new JButton("Set");
	    setm.setVisible(false);
		setm.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				createInputSection();			
			}
		});
		
		GroupLayout.SequentialGroup g = layout.createSequentialGroup();  
		
		g.addComponent(values);
		g.addComponent(numV);
		g.addComponent(setm);
		
		
		
		GroupLayout.ParallelGroup p = layout.createParallelGroup();  
		
		p.addComponent(values);
		p.addComponent(numV);
		p.addComponent(setm);
		
		leftToRight.addGroup(g);
		topToBottom.addGroup(p);
		
	}
	
	
	
	
	/*
	 * Create the arrangement of items in the frame.
	 */
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
	
	
	/*
	 * Draw a frame.
	 */
	
	static void draw()
	{
		createLayout();
		createItemsSection();
		createValuesSection();
	        
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	}
	

	/*
	 * Create a text pane.
	 */
	static JTextPane createTextPane(String caption, int size, boolean visible)
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
	
		
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		draw();
	}

}
