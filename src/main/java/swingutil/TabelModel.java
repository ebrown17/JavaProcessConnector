package main.java.swingutil;

import javax.swing.table.DefaultTableModel;

public class TabelModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TabelModel(Object[][] rowData, Object[] colNames) {
		super(rowData, colNames);
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}

}
