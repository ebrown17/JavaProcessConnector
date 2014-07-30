package main.java;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import sun.tools.jconsole.LocalVirtualMachine;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class RunCommands {
	
	private static Process process;
	private static Scanner scanner;
	private static Integer pid;
	private static String line,name;
	private static String [] free,names;
	private static Commands command = Commands.getInstance();
	private static final String CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";
	private static List<VirtualMachineDescriptor> vmdl= new ArrayList<VirtualMachineDescriptor>();
	private static long heapUsed,maxHeap;
	private static double percentMem;
	
	public static void updateMemoryTable(){
		
		try {
			process = command.getFree().start();
		} catch (IOException e) {
			
			System.out.println("Error running free command: " + e.getMessage());
		}
    	scanner = new Scanner(process.getInputStream());
    	
    	while(scanner.hasNext()){
        	line=scanner.nextLine();	        	
        	if(line.contains("+") || line.contains("Swap")||line.contains("total"))continue;
        	free = line.trim().split("\\s+");
        	String[] update = {free[1],free[2],free[3]};
        	GUI.setMemoryTableText(update);
	
        }
	}
	
	public static void updateJavaProcTable(){
		
		
		
		try { 
			vmdl = VirtualMachine.list();
			for(VirtualMachineDescriptor vd : vmdl) {
				
				pid = Integer.parseInt(vd.id());
       	 		LocalVirtualMachine lvm = LocalVirtualMachine.getLocalVirtualMachine(pid);
       	 	
       	 		final VirtualMachine vm = VirtualMachine.attach(vd.id());
       	 		String connectorAddress =null;
       	 		String agent = vm.getSystemProperties().getProperty("java.home") + File.separator + "lib" + File.separator + "management-agent.jar";
       	 		vm.loadAgent(agent);
       	 		connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
     
       	 		final JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(connectorAddress));
       	 		final MBeanServerConnection remote =  connector.getMBeanServerConnection();
    	
       	 		final ThreadMXBean remoteThreading = 
       	 				ManagementFactory.newPlatformMXBeanProxy(
    					remote,
    					ManagementFactory.THREAD_MXBEAN_NAME,
    					ThreadMXBean.class);    	 
       	     	 
       	 		MemoryMXBean memoryBean = 
       	 				ManagementFactory.newPlatformMXBeanProxy(
      					remote,
      					ManagementFactory.MEMORY_MXBEAN_NAME,
      					MemoryMXBean.class);
       	 		
       	 		heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
       	 		maxHeap = memoryBean.getHeapMemoryUsage().getMax();
       	 		percentMem = ((double)heapUsed/(double)maxHeap)*100;
       	 	     	 	
       	 		names = lvm.displayName().split("\\.");
       	 		name = names[names.length-1].toString();
	       	 	
	       	 	System.out.println("Target VM is: "+ name+" " +lvm.vmid());
	       	 	System.out.println("Live Threads: "+remoteThreading.getThreadCount());
	       	 	System.out.println("Peak Threads: "+remoteThreading.getPeakThreadCount());
	       	 	System.out.println("Daemon Threads: "+remoteThreading.getDaemonThreadCount());
	       	 	System.out.println("Total started Threads: "+remoteThreading.getTotalStartedThreadCount());
	       	 	System.out.println(percentMem + "\n");
	       	 	//System.out.println(name);
	       	 	
	       	 	connector.close(); 
	       	 	lvm=null;
	       	 	pid=null;
	       	 	names=null;
	       	 	name=null;
			}		
		
		vmdl=null;
		
		} catch(Exception e){
			System.out.println("Error in updating process table " + e.getMessage());
		}
	}

}
