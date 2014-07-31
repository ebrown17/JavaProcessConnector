package main.java;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
	private static String[][] processData;
	private static Commands command = Commands.getInstance();
	private static final String CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";
	private static List<VirtualMachineDescriptor> vmdl= new ArrayList<VirtualMachineDescriptor>();
	private static List<String> processList = new ArrayList<String>();
	private static List<String[]> test = new ArrayList<String[]>();
	private static HashMap<String, JavaProcess> processMap = new HashMap<String, JavaProcess>();
	private static JavaProcess javaProcess;
	private static int i;
	private static long heapUsed,maxHeap;
	private static double percentMem;
	private static  DecimalFormat df = new DecimalFormat("#.##");
	
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
		
		processList.clear();
		
		try { 
			vmdl = VirtualMachine.list();
			for(VirtualMachineDescriptor vd : vmdl) {				
				
				pid = Integer.parseInt(vd.id());
       	 		LocalVirtualMachine lvm = LocalVirtualMachine.getLocalVirtualMachine(pid);
       	 		
       	 		names = lvm.displayName().split("\\.");
       	 		name = names[names.length-1].toString();
       	 		
       	 		//short lived java processes cause exception, can't attach to process
       	 		if(name.equals("Jps"))continue; 
       	 		
       	 		processList.add(vd.id());
       	 	
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
       	 	     	 	
       	 		
	       	 	
	       	 /*	System.out.println("Target VM is: "+ name+" " +vd.id());
	       	 	System.out.println("Live Threads: "+remoteThreading.getThreadCount());
	       	 	System.out.println("Peak Threads: "+remoteThreading.getPeakThreadCount());
	       	 	System.out.println("Daemon Threads: "+remoteThreading.getDaemonThreadCount());
	       	 	System.out.println("Total started Threads: "+remoteThreading.getTotalStartedThreadCount());
	       	 	System.out.println(percentMem + "\n");*/
	       	 	//System.out.println(name);
	       	 	
	       	 	if(!processMap.containsKey(vd.id())){
	       	 	
	       	 		javaProcess= new JavaProcess(name, vd.id());
	       	 		javaProcess.setLiveThreads(String.valueOf(remoteThreading.getThreadCount()));
	       	 		javaProcess.setPeakThreads(String.valueOf(remoteThreading.getPeakThreadCount()));
	       	 		javaProcess.setDaemonThreads(String.valueOf(remoteThreading.getDaemonThreadCount()));
	       	 		javaProcess.setStartedThreads(String.valueOf(remoteThreading.getTotalStartedThreadCount()));
	       	 		javaProcess.setHeapUsed(String.valueOf(df.format(percentMem)));
	       	 		
	       	 		processMap.put(vd.id(), javaProcess);
	       	 		
	       	 	} else if (processMap.containsKey(vd.id())){
	       	 		
	       	 		javaProcess = processMap.get(vd.id());
		       	 	javaProcess.setLiveThreads(String.valueOf(remoteThreading.getThreadCount()));
	       	 		javaProcess.setPeakThreads(String.valueOf(remoteThreading.getPeakThreadCount()));
	       	 		javaProcess.setDaemonThreads(String.valueOf(remoteThreading.getDaemonThreadCount()));
	       	 		javaProcess.setStartedThreads(String.valueOf(remoteThreading.getTotalStartedThreadCount()));
	       	 		javaProcess.setHeapUsed(String.valueOf(df.format(percentMem)));
	       	 		
	       	 	}
	       	
	       	 	connector.close(); 
	       	 	lvm=null;
	       	 	pid=null;
	       	 	names=null;
	       	 	name=null;
			}	
			
			if(processList.size() != processMap.size()){
				
				//removed elements from hashmap this way because it helps avoid ConcurrentModificationExceptions that the for each causes.
				
				Iterator<Map.Entry<String,JavaProcess>> iterator = processMap.entrySet().iterator();
			    while (iterator.hasNext()) {
			        Map.Entry<String,JavaProcess> values = (Map.Entry<String, JavaProcess>)iterator.next();
			        if(!processList.contains(values.getKey())){
			        	iterator.remove();
					}
			    }			
			}
			
			processData = new String[processMap.size()][10];
			//i= 0;
			for(JavaProcess process : processMap.values()){
				
				test.add(process.getProcessData());
				//Arrays.fill(processData[i++], process.getProcessData());
			}
			
			for(int i =0; i<test.size();i++){
				for(int y =0; y <test.get(i).length;y++){
					
					processData[i][y]=Arrays.toString(test.get(i));
				}
			}
			
			GUI.setProcessTableText(processData);
			processData=null;
			System.out.println(processList.size() + " " +processList +  " " + processMap.size());
			vmdl=null;
			test.clear();
		} catch(Exception e){
			e.printStackTrace();
			System.out.println("Error in updating process table " + e);
		}
	}

}
