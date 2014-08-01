package main.java;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import sun.tools.jconsole.LocalVirtualMachine;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class RunCommands {

	private static Process process;
	private static Scanner scanner;
	private static Integer pid;
	private static String line, name,formattedTime;
	private static String[] free, names,top;
	private static String[][] processData;
	private static Commands command = Commands.getInstance();
	private static final String CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";
	private static List<VirtualMachineDescriptor> vmdl = new ArrayList<VirtualMachineDescriptor>();
	private static List<JavaProcess> sortedList = new ArrayList<JavaProcess>();
	private static List<String> processList = new ArrayList<String>();
	private static List<String[]> processDataList = new ArrayList<String[]>();
	private static HashMap<String, JavaProcess> processMap = new HashMap<String, JavaProcess>();
	private static JavaProcess javaProcess;
	private static long heapUsed, maxHeap;
	private static double percentMem;
	private static DecimalFormat df = new DecimalFormat("#.##");
	private static ThreadMXBean remoteThreading;
	private static MemoryMXBean memoryBean;
	private static StringBuilder formatted;
	private static final SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss.SSS");
	
	public static void updateMemoryTable() {
		
		
		formattedTime = formatTime.format(new Date());
        //formatted = new StringBuilder();	        
        

		try{
			process = command.getFree().start();
		} catch (IOException e){

			System.out.println("Error running free command: " + e.getMessage());
		}
		scanner = new Scanner(process.getInputStream());

		while (scanner.hasNext()){
			line = scanner.nextLine();
			if (line.contains("+") || line.contains("Swap") || line.contains("total"))
				continue;
			free = line.trim().split("\\s+");
			String[] update = {formattedTime, free[1], free[2], free[3] };
			GUI.setMemoryTableText(update);

		}
	}

	public static void updateJavaProcTable() {

		//processList.clear();

		vmdl = VirtualMachine.list();
		for (VirtualMachineDescriptor vd : vmdl){

			pid = Integer.parseInt(vd.id());
			LocalVirtualMachine lvm = LocalVirtualMachine.getLocalVirtualMachine(pid);

			VirtualMachine vm;
			String connectorAddress = null;
			String agent;
			JMXConnector connector;
			MBeanServerConnection remote;

			// if process closes while connected, skip it before trying to
			// assign values
			try{
				vm = VirtualMachine.attach(vd.id());
				agent = vm.getSystemProperties().getProperty("java.home") + File.separator + "lib" + File.separator + "management-agent.jar";
				vm.loadAgent(agent);
				connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
				connector = JMXConnectorFactory.connect(new JMXServiceURL(connectorAddress));
				remote = connector.getMBeanServerConnection();

				remoteThreading = ManagementFactory.newPlatformMXBeanProxy(remote,ManagementFactory.THREAD_MXBEAN_NAME,ThreadMXBean.class);

				memoryBean = ManagementFactory.newPlatformMXBeanProxy(remote,ManagementFactory.MEMORY_MXBEAN_NAME,MemoryMXBean.class);

			} catch (AttachNotSupportedException e){
				continue;
				// e.printStackTrace();
			} catch (IOException e){
				continue;
				// e.printStackTrace();
			} catch (AgentLoadException e){
				continue;
				// e.printStackTrace();
			} catch (AgentInitializationException e){
				continue;
				// e.printStackTrace();
			}

			names = lvm.displayName().split("\\.");
			name = names[names.length - 1].toString();

			processList.add(vd.id());

			heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
			maxHeap = memoryBean.getHeapMemoryUsage().getMax();
			percentMem = ((double) heapUsed / (double) maxHeap) * 100;

			if (!processMap.containsKey(vd.id())){

				javaProcess = new JavaProcess(name, vd.id());
				javaProcess.setLiveThreads(String.valueOf(remoteThreading.getThreadCount()));
				javaProcess.setPeakThreads(String.valueOf(remoteThreading.getPeakThreadCount()));
				javaProcess.setDaemonThreads(String.valueOf(remoteThreading.getDaemonThreadCount()));
				javaProcess.setStartedThreads(String.valueOf(remoteThreading.getTotalStartedThreadCount()));
				javaProcess.setHeapUsed(String.valueOf(df.format(percentMem)));
				
				processMap.put(vd.id(),javaProcess);

			} else if (processMap.containsKey(vd.id())){

				javaProcess = processMap.get(vd.id());
				javaProcess.setLiveThreads(String.valueOf(remoteThreading.getThreadCount()));
				javaProcess.setPeakThreads(String.valueOf(remoteThreading.getPeakThreadCount()));
				javaProcess.setDaemonThreads(String.valueOf(remoteThreading.getDaemonThreadCount()));
				javaProcess.setStartedThreads(String.valueOf(remoteThreading.getTotalStartedThreadCount()));
				javaProcess.setHeapUsed(String.valueOf(df.format(percentMem)));
				
			}

			try{
				connector.close();
			} catch (IOException e){

				e.printStackTrace();
			}

			lvm = null;
			pid = null;
			names = null;
			name = null;
		}

		if (processList.size() != processMap.size()){

			// removed elements from hashmap this way because it helps avoid
			// ConcurrentModificationExceptions that the for each causes.

			Iterator<Map.Entry<String, JavaProcess>> iterator = processMap.entrySet().iterator();
			while (iterator.hasNext()){
				Map.Entry<String, JavaProcess> values = (Map.Entry<String, JavaProcess>) iterator.next();
				if (!processList.contains(values.getKey())){
					iterator.remove();
				}
			}
		}
		
		try{
			process = command.getTop().start();
		} catch (IOException e){
			
			e.printStackTrace();
		}
        scanner = new Scanner(process.getInputStream());
            
        while(scanner.hasNext()){
        	line=scanner.nextLine();
        	
        	top = line.trim().split("\\s+");
        	if(processMap.get(top[0])==null){
        		line=null;
        		top=null; 
        		continue;
        	}
        	
        	//System.out.println(top[top.length-4]);
        	javaProcess = processMap.get(top[0]);
        	javaProcess.setCurrentCPU(top[top.length-4]);
        	javaProcess.setPeakCPUse(top[top.length-4]);
        	javaProcess.setMemUse(top[top.length-3]);
        	javaProcess.setPeakMemUse(top[top.length-3]);
        }	
        
        
        
        /// add hashmap to list then sort
        for (JavaProcess process : processMap.values()){
        	
        	sortedList.add(process);
        	        	
		}
      
        Collections.sort(sortedList, new Comparator<JavaProcess>() {
            @Override
            public int compare(final JavaProcess object1, final JavaProcess object2) {
                return object1.getName().compareTo(object2.getName());
            }
           } );
       
        
        
		processData = new String[processMap.size()][11];

		for (JavaProcess process : sortedList){

			processDataList.add(process.getProcessData());
		}

		for (int i = 0; i < processDataList.size(); i++){
			for (int y = 0; y < processDataList.get(i).length; y++){

				processData[i][y] = Arrays.toString(processDataList.get(i));
			}
		}
		
		 

		GUI.setProcessTableText(processData);
		processData = null;
		line=null;
		javaProcess=null;
		// System.out.println(processList.size() + " " +processList + " " +
		// processMap.size());
		vmdl = null;
		sortedList.clear();
		processDataList.clear();
		processList.clear();

	}

}
