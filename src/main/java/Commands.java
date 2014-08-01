package main.java;

import java.util.ArrayList;
import java.util.List;

public class Commands {

	private static final Commands INSTANCE = new Commands();

	private List<String> free = new ArrayList<String>();
	private List<String> top = new ArrayList<String>();
	
	
	private static ProcessBuilder pFree;
	private static ProcessBuilder pTop;

	
	private Commands() {
		free.add("/bin/bash");
		free.add("-c");
		free.add("free -m");
		pFree = new ProcessBuilder(free);
		pFree.redirectErrorStream(true);
		
		top.add("/bin/bash");
       	top.add("-c");
		top.add("top -n1 -b | grep java");		
		pTop = new ProcessBuilder(top);
		pTop.redirectErrorStream(true);
	}

	public static Commands getInstance() {
		return INSTANCE;
	}

	public ProcessBuilder getFree() {
		return pFree;
	}
	
	public  ProcessBuilder getTop(){
		return pTop;
	}

}
