package main.java;

import java.util.Scanner;



public class JavaProcessConnector extends Thread{
	
	
	
	public static void main(String[] args){
		
		
		
		GUI.startGUI();
		
		while(true){
			
			RunCommands.updateMemoryTable();
			RunCommands.updateJavaProcTable();
			
			try {
				Thread.sleep(2000);
				
			} catch (InterruptedException e) {
				
				System.out.println("Error Thread sleep interrupted: " + e.getMessage());
			}
		
		}
		
			
				
		
	}

}
