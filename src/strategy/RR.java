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
