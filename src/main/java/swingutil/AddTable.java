package main.java.swingutil;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class AddTable {

	public static void toFrame(JFrame frame, JTable table, String location) {

		frame.add(table.getTableHeader(),location);
		frame.add(table,location);
		frame.add(new JScrollPane(table),location);

	}

}
