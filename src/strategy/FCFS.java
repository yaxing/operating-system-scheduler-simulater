package strategy;

import java.util.ArrayList;

import scheduler.Process;
import scheduler.Status;

/**
 * First Come First Serve control class
 * 
 * @assumption preemptive / non-preemptive can be set for FCFS
 * default as preemptive
 * 
 * @implementation
 * each cycle:
 * 1. check if there are new coming process, if so, put them into ready queue,
 *    set status as READY, sorted by arrival time
 * 2. scan blocked queue, if not null, -1 i/o time of all processes in blocked queue (sorted by arrival time)
 *    after -1, if i/o time <=0, put it into ready queue, change status as READY,
 *    remove it from blocked queue
 * 3. handle running process:
 *    first, if there is one process running:
 *     1) check ready queue, if readyQ.get(0).arrivalTime > running.arrivalTime, preemptive, 
 *        put running one into ready queue, change status to READY,
 *        then put readyQ.get(0) as running one, change status as RUNNING
 *     2) check current running process's cpu time, if consumed 1/2 cpu time, then put it into blocked queue,
 *        set status as BLOCKED, clear running as -1
 *     3) or if consumed all cpu time, then set status as null, clear running as -1
 *    then, if there is no process running:
 *      pick the first one in ready queue as running one
 *    or if there is still running process, decrease by 1 it's cpu time 
 * 
 * 4. at the end of each cycle, scan all process status, if all are null, meaning all processes end.
 *    then exit;
 * 
 * @author Yaxing Chen
 *
 */

public class FCFS extends Strategy{
	
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
	@Override
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
						procStatus[curId] = null;
						readyQ.set(i, null);
					}
					else {
						running = curId;
						procStatus[curId] = Status.RUNNING;
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
	 * check the first process in ready queue,
	 * if it's arrival time is smaller than current running one,
	 * switch with current running one and put current running one into ready queue
	 * @return boolean true: switched, false: not switched
	 * @see Strategy
	 */
	@Override
	protected boolean chkPreemptive() {
		if(readyQ == null || readyQ.size() <= 0) {
			return false;
		}
		Process ready = proc.get(readyQ.get(0));
		Process cur = proc.get(running);
		if(ready.arrivalTime < cur.arrivalTime) {
			running = ready.id;
			procStatus[ready.id] = Status.RUNNING;
			readyQ.remove(0);
			this.insertIntoQueue(cur.id, readyQ);
			procStatus[cur.id] = Status.READY;
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * processes are order by arrival time in queues
	 */
	@Override
	protected void insertIntoQueue(int procId, ArrayList<Integer> queue) {
		if(queue.size() == 0) {
			queue.add(0, procId);
			return;
		}
		Process curP = proc.get(procId);
		for(int i = 0; i < queue.size(); i ++) {
			if(proc.get(queue.get(i)).arrivalTime > curP.arrivalTime) {
				queue.add(i, procId);
				break;
			}
			else if(i == queue.size() - 1) {
				queue.add(procId);
				break;
			}
		}
		return;
	}
}
