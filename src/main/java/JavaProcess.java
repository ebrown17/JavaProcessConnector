package main.java;

public class JavaProcess {

	private String name, pid, lThreads = "", pThreads = "", dThreads = "", tSThreads = "", currentCPU = "", currentMem = "", heapUsage = "",
			peakCPU = "0", peakMemUse = "0";

	public JavaProcess(String name, String pid) {
		this.name = name;
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public String getPID() {
		return pid;
	}

	public void setLiveThreads(String lThreads) {
		this.lThreads = lThreads;
	}

	public void setPeakThreads(String pThreads) {
		this.pThreads = pThreads;
	}

	public void setDaemonThreads(String dThreads) {
		this.dThreads = dThreads;
	}

	public void setStartedThreads(String tSThreads) {
		this.tSThreads = tSThreads;
	}

	public void setCurrentCPU(String currentCPU) {
		this.currentCPU = currentCPU;
	}

	public void setMemUse(String currentMem) {
		this.currentMem = currentMem;
	}

	public void setHeapUsed(String heapUsage) {
		this.heapUsage = heapUsage;
	}

	public void setPeakCPUse(String peakCPU) {
		if (Double.parseDouble(peakCPU) > Double.parseDouble(this.peakCPU)){
			this.peakCPU = peakCPU;
		}
	}

	public String getPeakCPUse() {
		return peakCPU;
	}

	public void setPeakMemUse(String peakMemUse) {
		if (Double.parseDouble(peakMemUse) > Double.parseDouble(this.peakMemUse)){
			this.peakMemUse = peakMemUse;
		}
	}

	public String getPeakMemUsee() {
		return peakMemUse;
	}

	public String[] getProcessData() {

		return new String[] { name, pid, lThreads, pThreads, dThreads, tSThreads, currentCPU, currentMem, heapUsage, peakCPU, peakMemUse };
	}
}
