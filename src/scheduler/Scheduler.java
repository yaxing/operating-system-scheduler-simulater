package scheduler;

import java.util.*;

import strategy.*;

/**
 * main class
 * 
 * @assumption
 * 1. processes' ids are continuous, start from 0 to n.
 * 
 * @author Yaxing Chen(N16929794, yc1116@nyu.edu)
 *
 */
public class Scheduler {
	
	public static void main(String[] args) {
		ArrayList<Process> proc = null;

		// record realtime procStatus, for improving efficiency.
		// Status (enum)
		//@see Status.java
		Status[] procStatus = null;

//		if(args == null || args.length == 0) {
//			System.out.println("wrong parameter, args[0]: input file path, args[1]: scheduler mode[optional]");
//			System.exit(0);
//		}
//		System.out.println(Tool.read(args[0]));
		
		proc = Tool.fetch(Tool.read("inp1.txt"));
		procStatus = new Status[proc.size()];
		//System.out.println(proc.size());
		for(int i = 0; i < procStatus.length; i ++) {
			procStatus[i] = Status.NEW;
		}
		int method = 0;
		switch(method) {
		case 0:
			//FCFS ctrl = new FCFS();
			//SRJF ctrl = new SRJF();
			RR ctrl = new RR();
			ctrl.start(proc, procStatus);
			//Tool.writeFile("out_fcfs.txt");
			//Tool.writeFile("out_srjf.txt");
			Tool.writeFile("out_rr.txt");
			break;
		default:
			break;
		}
	}
}