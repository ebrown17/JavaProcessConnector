package main.java;

import java.awt.BorderLayout;
import java.awt.Dimension;


import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import main.java.swingutil.AddTable;

import main.java.swingutil.CreateTable;
import main.java.swingutil.TabelModel;

public class JavaProcessGUI {
	
	JFrame frame;
	JTable memJTable,processJTable;
	
	JavaProcessGUI(){
		
		frame = new JFrame("Running Java Processes");
		frame.setSize(850,250);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		String[] memoryUsed = {"Total Memory in mb","Used Memory in mb","Free Memory in mb"};
		String[][] memoryData = {
				{"  updating  ","  updating  ","  updating  "}	
		};
		String[] javaProcesses = {"Process Name","PID","Live Threads","Peak Threads","Daemon Threads","Total Threads Started","Current CPU Use","Current Memory Use","Peak CPU Use","Peak Memory Use"};
		String[][] processData = {{"  updating  ","updating","updating","updating","updating","updating","updating","updating","updating","updating"}};
		
		memJTable = new CreateTable(memoryData, memoryUsed).getTable();
		
		processJTable = new CreateTable(processData, javaProcesses).getTable();
		
		
		AddTable.toFrame(frame,memJTable,BorderLayout.NORTH);
		AddTable.toFrame(frame,processJTable,BorderLayout.SOUTH);
		
        frame.setAlwaysOnTop(true);		
		frame.setResizable(true);
		
	}

	
}
