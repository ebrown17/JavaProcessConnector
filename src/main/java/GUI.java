package main.java;

import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class GUI {

	static JavaProcessGUI gui;
	static String[] memData, columnData;
	static String[][] processData;
	private static boolean firstRun = true;

	public static void startGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui = new JavaProcessGUI();
			}
		});

	}

	public static void setMemoryTableText(String[] data) {
		memData = data;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DefaultTableModel model = (DefaultTableModel) gui.memJTable.getModel();
				model.setValueAt(memData[0],0,0);
				model.setValueAt(memData[1],0,1);
				model.setValueAt(memData[2],0,2);
				model.fireTableRowsUpdated(0,2);
				memData = null;
				model = null;
			}
		});
	}

	public static void setProcessTableText(String[][] data) {
		processData = data;
		// System.out.println(processData.length);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DefaultTableModel model = (DefaultTableModel) gui.processJTable.getModel();

				for (int i = 0; i < processData.length; i++){

					for (int x = 0; x < processData[i].length; x++){

						columnData = processData[i][x].replace("[","").replace("]","").split(",");

						// System.out.println(processData[i][x]);

						if (processData.length < model.getRowCount()){

							for (int y = processData.length; y <= model.getRowCount(); y++){
								model.removeRow(y);
							}

						}
						if (processData.length > model.getRowCount()){

							for (int j = model.getRowCount(); j <= processData.length; j++){
								model.insertRow(j,columnData);
							}

						}
						for (int column = 0; column < model.getColumnCount(); column++){
							// System.out.println(test[column] + " " + column +
							// " " +model.getColumnCount() );
							model.setValueAt(columnData[column],i,column);

						}
					}
				}

				gui.processJTable.setPreferredScrollableViewportSize(gui.processJTable.getPreferredSize());
				gui.frame.pack();
				if (firstRun == true){
					gui.frame.setVisible(true);
					firstRun = false;
				}
				model.fireTableDataChanged();
				columnData = null;
				processData = null;
				model = null;

			}
		});

	}

}
