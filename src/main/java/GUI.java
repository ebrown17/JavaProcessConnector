package main.java;


import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;





public class GUI {
	
	static JavaProcessGUI gui;
	static String[] row;
	

	public static void startGUI(){		
		SwingUtilities.invokeLater(new Runnable() {			
			public void run(){
				gui = new JavaProcessGUI();
			}
		});	
		
		
	}
	
	public static void setMemoryTableText(String[] rowData){
		row=rowData;
		SwingUtilities.invokeLater(new Runnable() {			
			public void run(){
				DefaultTableModel model=	(DefaultTableModel) gui.memJTable.getModel();
				model.setValueAt(row[0], 0, 0);
				model.setValueAt(row[1], 0, 1);
				model.setValueAt(row[2], 0, 2);
				model.fireTableRowsUpdated(0, 2);
				
			}
		});			
	}
	

	
	
}
