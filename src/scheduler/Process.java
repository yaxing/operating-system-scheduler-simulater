package scheduler;

/**
 * Process entity
 * @author Yaxing Chen
 *
 */
public class Process {
	
	public int id;
	public int cpuTime;
	public int ioTime;
	public int arrivalTime;
	public int startCycle = -1;
	public int finishCycle = -1;
	public int runCycle = 0;
	
	Process(int id, int cpuTime, int ioTime, int arrivalTime) {
		this.id = id;
		this.cpuTime = cpuTime;
		this.ioTime = ioTime;
		this.arrivalTime = arrivalTime;
	}
	
	Process(String[] param) {
		this.id = Integer.parseInt(param[0]);
		this.cpuTime = Integer.parseInt(param[1]);
		this.ioTime = Integer.parseInt(param[2]);
		this.arrivalTime = Integer.parseInt(param[3]);
	}
	
}
