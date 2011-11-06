package scheduler;

import java.util.*;

/**
 * Shortest remaining Job First control class
 * @author Yaxing Chen
 *
 */
public class SRJF extends Strategy{
	
	/**
	 * order by remaining CPU time, lower to higher
	 * 
	 * @see Strategy
	 */
	@Override
	protected void insertIntoQueue(int procId, ArrayList<Integer> queue) {
		if(queue.size() == 0 || queue.isEmpty()) {
			queue.add(0, procId);
			return;
		}
		Process curP = proc.get(procId);
		for(int i = 0; i < queue.size(); i ++) {
			Process qP = proc.get(queue.get(i));
			if(qP.cpuTime - qP.runCycle > curP.cpuTime - curP.runCycle) {
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
	
	/**
	 * pick out the 1st ele from ready list (the one with shortest remaining time in ready queue)
	 * compare it with current running process's remaining time
	 * decide whether swap or not
	 * and perform swap or return
	 */
	protected boolean chkPreemptive() {
		if(readyQ == null || readyQ.size() <= 0) {
			return false;
		}
		Process ready = proc.get(readyQ.get(0));
		Process cur = proc.get(running);
		if(ready.cpuTime - ready.runCycle < cur.cpuTime - cur.runCycle) {
			insertIntoQueue(cur.id, readyQ);
			procStatus[cur.id] = Status.READY;
			readyQ.remove(0);
			running = ready.id;
			procStatus[ready.id] = Status.RUNNING;
			return true;
		}
		return false;
	}
}
