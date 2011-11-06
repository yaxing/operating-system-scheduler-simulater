package scheduler;

import java.util.ArrayList;

/**
 * Super class of scheduler strategies
 * 
 * @author Yaxing Chen
 *
 */
abstract public class Strategy {
	
	/**
	 * process list
	 */
	protected ArrayList<Process> proc = null;
	
	/**
	 * tracking each process's status
	 * index is process id
	 * @see Status
	 */
	protected Status[] procStatus = null;
	
	/**
	 * running register
	 * current running process id
	 * if -1 then no running process, CPU is idle
	 */
	protected int running = -1;
	
	/**
	 * ready queue
	 * ids of ready processes
	 * sorted by certain order based on different strategies (lower to higher)
	 * 
	 * FCFS: sorted by arrival time
	 * SJF: sorted by remaining time
	 * RR:  sorted by process id
	 */
	protected ArrayList<Integer> readyQ = new ArrayList<Integer>();
	
	/**
	 * blocked queue
	 * ids of blocked processes
	 * sorted by certain order based on different strategies
	 */
	protected ArrayList<Integer> blockedQ = new ArrayList<Integer>();
	
	/**
	 * cpu cycle counter
	 */
	protected int cycle = 0;
	
	/**
	 * idle cycle counter
	 */
	protected int idleCycle = 0;
	
	protected boolean isPreemptive = true;
	
	/**
	 * buffer for output from each cycle
	 */
	protected StringBuilder buf = new StringBuilder();
	
	/**
	 * schedule control func
	 * @param proc process table
	 * @param procStatus process status table, for efficiently outputing
	 */
	public ArrayList<Process> start(ArrayList<Process> proc, Status[] procStatus) {
		this.proc = proc;
		this.procStatus = procStatus;
		for(; ; cycle ++) {
			handleNew();
			handleBlockedQ();
			handleRunning();
			if(endChk()) {
				end();
				break;
			}
			Tool.writeBuffer(cycle + buf.toString());
			buf.setLength(0);
		}
		return proc;
	}
	
	/**
	 * check if there is new comming process
	 * if so, move to ready queue
	 */
	protected void handleNew() {
		for(int i = 0; i < proc.size(); i ++) {
			Process curProc = proc.get(i);
			if(curProc.arrivalTime <= cycle && procStatus[curProc.id] == Status.NEW) {
				procStatus[curProc.id] = Status.READY;
				this.insertIntoQueue(curProc.id, readyQ);
				curProc.startCycle = cycle;
			}
		}
	}
	
	/**
	 * handle blocked queue
	 * 
	 * check blocked process, if finished i/o time, move to ready queue
	 */
	protected void handleBlockedQ() {
		if(blockedQ == null || blockedQ.size() == 0) {
			return;
		}
		boolean remove = false;
		for(int i = 0; i < blockedQ.size(); i ++) {
			if(proc.get(blockedQ.get(i)).ioTime-- <= 0) {
				this.insertIntoQueue(blockedQ.get(i), readyQ);
				this.procStatus[proc.get(blockedQ.get(i)).id] = Status.READY;
				blockedQ.set(i, null);
				remove = true;
			}
		}
		if(!remove) {
			return;
		}
		this.removeElesFromList(blockedQ);
	}
	
	/**
	 * check running process
	 * 
	 * decide whether to switch running process
	 * 
	 * @implementation
	 * 
	 * 1. if there is one process running
	 * 
	 *    1) if isPreemptive, call chkPreemptive to process preemptive running process selection
	 * 
	 *    2) if cpu time consumed 1/2, change it to block
	 *    
	 *    3) else if cpu time elapsed, change status to null: finished. clear running id.
	 *    
	 * 2. then, if there is no process running
	 *    
	 *    1) if readyQ is null, cpu idle
	 *    2) else, pick the first one in readyQ
	 * 
	 */
	protected void handleRunning() {
		if(running != -1) {
			if(isPreemptive) {
				chkPreemptive();
			}
			Process curP = proc.get(running);
			if(curP.runCycle == curP.cpuTime / 2 && curP.ioTime > 0) {
				insertIntoQueue(running, blockedQ);
				procStatus[running] = Status.BLOCKED;
				running = -1;
				curP.ioTime --;
			}
			else if(curP.runCycle == curP.cpuTime) {
				procStatus[running] = null;
				curP.finishCycle = cycle - 1;
				running = -1;
			}
			else {
				curP.runCycle ++;
			}
		}
		if(running == -1) {
			if(readyQ != null && readyQ.size() > 0) {
				for(int i = 0; i < readyQ.size(); i ++) {
					int curId = readyQ.get(i);
					if(proc.get(curId).runCycle == proc.get(curId).cpuTime / 2 && proc.get(curId).ioTime > 0) {
						procStatus[proc.get(curId).id] = Status.BLOCKED;
						proc.get(curId).ioTime --;
						blockedQ.add(curId);
						readyQ.set(i, null);
					}
					else if(proc.get(curId).runCycle == proc.get(curId).cpuTime) {
						procStatus[proc.get(curId).id] = null;
						readyQ.set(i, null);
					}
					else {
						running = curId;
						procStatus[proc.get(curId).id] = Status.RUNNING;
						proc.get(curId).runCycle ++;
						readyQ.set(i, null);
						break;
					}
				}
				removeElesFromList(readyQ);
			}
			else {
				idleCycle ++;
			}
		}
	}
	
	/**
	 * based on different strategies, check & handle preemptive for each cycle
	 */
	abstract protected boolean chkPreemptive();
	
	/**
	 * insert into a designated queue, while maintaining order by certain attribute,
	 * based on different strategies
	 * insertion sort
	 * FCFS: order by arrival time
	 * SJF: remaing cpu time
	 * RR: id
	 */
	abstract protected void insertIntoQueue(int procId, ArrayList<Integer> queue);
	/**
	 * remove given ids from list
	 * @param ids
	 * @param list
	 */
	protected void removeElesFromList(ArrayList<Integer> list) {
		for(int i = 0; i < list.size(); i ++) {
			if(list.get(i) == null) {
				list.remove(i);
				i = 0;
			}
		}
	}
	
	/**
	 * after each cycle, check whether all processes are finished or not
	 * at the same time, print out all alive processes' status
	 * @return boolean true: end, false: continue
	 */
	protected boolean endChk() {
		boolean end = true;
		for(int i = 0; i < procStatus.length; i ++) {
			Status s = procStatus[i];
			if(s == null) {
				end &= true;
			}
			else {
				if(s == Status.NEW) {
					continue;
				}
				buf.append(" " + i + ":" + s);
				end &= false;
			}
		}
		return end;
	}
	
	/**
	 * record summary scheduler information
	 */
	protected void end() {
		Tool.writeBuffer("");
		Tool.writeBuffer("Finishing time: " + --cycle);
		Double util = (1 - new Double((idleCycle - 1)) / new Double(cycle + 1));
		java.text.DecimalFormat format = new java.text.DecimalFormat("#0.00");
		Tool.writeBuffer("CPU utilization: " + format.format(util));
		for(int i = 0; i < proc.size(); i ++) {
			int turnaround = proc.get(i).finishCycle - proc.get(i).arrivalTime + 1;
			Tool.writeBuffer("Turnaround process " + proc.get(i).id + ": " + turnaround);
		}
	}
	
}
