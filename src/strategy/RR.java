package strategy;

import java.util.ArrayList;

import scheduler.Process;
import scheduler.*;

public class RR extends Strategy{
	
	/**
	 * override running handling.
	 * 
	 * when one of following conditions is fulfilled, schedule another process for running:
	 * 
	 * 1) Process.runCycle = Process.cpuTime / 2
	 * 2) Process.runCycle = Process.cpuTime
	 * 3) Process.runCycle > 0 && Process.runCycle % 2 = 0
	 * 
	 * When scheduling, pick the process with the smallest id from ready queue (sorted by id)
	 *   
	 * @see insertIntoQueue
	 * @see Strategy
	 */
	@Override
	protected void handleRunning() {
		if(running != -1) {
			if(isPreemptive) {
				chkPreemptive();
			}
			Process curP = proc.get(running);
			if(curP.runCycle == curP.cpuTime) {
				procStatus[running] = null;
				curP.finishCycle = cycle - 1;
				running = -1;
			}
			else if(curP.ioTime > 0 && (curP.runCycle == curP.cpuTime / 2 || curP.runCycle % 2 == 0)) {
				insertIntoQueue(running, blockedQ);
				procStatus[running] = Status.BLOCKED;
				running = -1;
				curP.ioTime --;
			}
			else {
				curP.runCycle ++;
			}
		}
		if(running == -1) {
			if(readyQ != null && readyQ.size() > 0) {
				int curId = readyQ.get(0);
				running = curId;
				procStatus[curId] = Status.RUNNING;
				proc.get(curId).runCycle ++;
				readyQ.remove(0);
			}
			else {
				idleCycle ++;
			}
		}
	}
	
	/**
	 * non-preemptive
	 * @see Strategy
	 */
	@Override
	protected boolean chkPreemptive() {
		return false;
	}
	
	/**
	 * queues are sorted by id, smaller to bigger
	 * @see Strategy
	 */
	@Override
	protected void insertIntoQueue(int procId, ArrayList<Integer> queue) {
		
	}

}
