package main.java;


import java.awt.Dimension;
import java.util.Arrays;

import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;





public class GUI {
	
	static JavaProcessGUI gui;
	static String[] memData;
	static String[][] processData;

	public static void startGUI(){		
		SwingUtilities.invokeLater(new Runnable() {			
			public void run(){
				gui = new JavaProcessGUI();
			}
		});	
		
		
	}
	
	public static void setMemoryTableText(String[] data){
		memData=data;
		SwingUtilities.invokeLater(new Runnable() {			
			public void run(){
				DefaultTableModel model=	(DefaultTableModel) gui.memJTable.getModel();
				model.setValueAt(memData[0], 0, 0);
				model.setValueAt(memData[1], 0, 1);
				model.setValueAt(memData[2], 0, 2);
				model.fireTableRowsUpdated(0, 2);
				
			}
		});			
	}
	
	public static void setProcessTableText(String[][] data){
		processData = data;
		//System.out.println(processData.length);
		SwingUtilities.invokeLater(new Runnable() {			
			public void run(){
				DefaultTableModel model=	(DefaultTableModel) gui.processJTable.getModel();
				
				for(int i=0; i<processData.length;i++){
					
					for(int x = 0; x<processData[i].length; x ++) {
						
						String[] test = processData[i][x].replace("[", "").replace("]", "").split(",");
						
						System.out.println(processData[i][x]);
					
						if(processData.length < model.getRowCount()){
							
							for(int y =processData.length; y <= model.getRowCount();y++){
								model.removeRow(y);
							}
							
						} else if(processData.length > model.getRowCount()){
							
							
							
							for(int j =model.getRowCount(); j <=processData.length; j++){
								model.insertRow(j, test);
							}
							
						} else {
							for(int column=0; column < model.getColumnCount(); column++){
								//System.out.println(test[column] + " " + column + " " +model.getColumnCount() );
								model.setValueAt(test[column], i, column);
								
							}
						}
					}	
				}
								
				gui.processJTable.setPreferredScrollableViewportSize(gui.processJTable.getPreferredSize());
				gui.frame.pack();
				gui.frame.setVisible(true);
				processData=null;
				model.fireTableDataChanged();
				
			}
		});		
		
		
	}

	
	
}
