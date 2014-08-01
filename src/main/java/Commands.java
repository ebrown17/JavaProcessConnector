package main.java;

import java.util.ArrayList;
import java.util.List;

public class Commands {

	private static final Commands INSTANCE = new Commands();

	private List<String> free = new ArrayList<String>();

	private static ProcessBuilder pFree;

	private Commands() {
		free.add("/bin/bash");
		free.add("-c");
		free.add("free -m");
		pFree = new ProcessBuilder(free);
		pFree.redirectErrorStream(true);
	}

	public static Commands getInstance() {
		return INSTANCE;
	}

	public ProcessBuilder getFree() {
		return pFree;
	}

}
