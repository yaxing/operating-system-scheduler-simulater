//======================================//
|Operating Systems Project #2: Scheduler |
|Prof. Mohamed Zahran                    |
|                                        |
|Student Name: Yaxing Chen               |
|NID: 16929794                           |
|email: yc1116@nyu.edu                   |
//======================================//

//===========Environment================//
JRE 6, JDK 1.6


//===========Things in .zip===============//
1. Runnable Jar: proj_scheduler_YaxingChen.jar
2. Source code: ./src
3. JAR MANIFEST file: jar-manifest
4. Test input files: input-1, input-2
5. Output: FCFS: output_fcfs.txt  RR: output_rr.txt  SRJF: output_srjf.txt
6. README.txt


//=====How to compile(interpret)=========//
 
1. unzip os-scheduler-YaxingChen-N16929794.zip
2. cd os-linker-YaxingChen-N16929794/src/
3. javac strategy/*.java
4. javac scheduler/*.java
5. cd ..
6. jar cvfm scheduler.jar jar-manifest -C src/ .


//===============How to run=============//

java -jar scheduler.jar <input file path> [strategy] [mode]

usage: Scheduler.jar <input file path> [strategy] [mode]
 strategy: 
           -1[default]: all
           0: fcfs
           1: rr
           2: srjf
 mode:  (invalid for rr)
           1[default]: preemptive
           0: un-preemptive
           
 e.g.
     All strategies: java -jar scheduler.jar input1.txt
     
     Preemptive FCFS: java -jar scheduler.jar input1.txt 0
     
     Un-preemptive FCFS: java -jar scheduler.jar input1.txt 0 0
     
     RR: java -jar scheduler.jar input1.txt 1
     
     Preemptive SRJF: java -jar scheduler.jar input1.txt 2
     Un-preemptive SRJF: java -jar scheduler.jar input1.txt 2 0
     
     
//==========Result=====================//
FCFS: ./output_fcfs.txt  
RR:   ./output_rr.txt  
SRJF: ./output_srjf.txt


//============Assumptions & rules======//
based on certain facts, this program followed few rules & hypotheses:

1) Process status diagram followed by this program:
               
                             --->
              NEW ---> READY      RUNNING
                             <---
                        ^            /
                        |           /
                        |          /
                        |         V
                                 
                         BLOCKED

2) Simulator CPU only pick process from ready queue for running, 
   therefore, blocked process cannot become running immediately, instead, it should become ready first.
 
3) Assume all processes' ids are consecutive positive decimal integers, from m to n (0 <= m < n < infinite).

4) Weak input error detection, based on project requirement, assuming all inputs are correct.  


//=========Basic implementation introduction========//
(Please refer to comments for more detail)

1. 1 register and 2 queues are maintained:
   
   running: current running process id;
   ready queue: current ready processes' ids. sorted.
   blocked queue: current blocked processes' ids. sorted.
   
2. 1 extra process status array is maintained:
    
    (in order to improve status query & modify efficiency)
    procStatus: enum array, index is process's id, value is Status
    
3. Basic logic for each cycle:
     check new coming processes, put them into ready queue
	     |
	     |
	     V
     scan blocked queue's processes, consume their i/o time, if i/o is finished,
     move them to ready queue
	     |
	     |
	     V
	  running arbitration:
	  	if there is one process running,
	  		check preemptive, compare with ready queue, if fulfill preemptive,
	  		replace current running process;
	  		
	  		check current running process's CPU time, if consumed 1/2, move to blocked queue,
	  		if consumed all, delete
	  		else, running cycle ++
	  		
	  		for RR, need to check quantum
	  		
	  	   then, if no process is running (running one may be moved after former steps)
	  	   check ready queue, pick the one fulfilled running condition as running and running cycle ++;
	  	   
4. FCFS, RR, SJFS has the same basic logit that has been defined in their super class: Strategy.
   they only need to override their own running arbitrate & queue sorting & preemptive detection functions.

//===========FCFS specific==========//

queues are sorted by arrival time, 
therefore, every time when need to swap (detect preemptive or blocked or finished),
only need to pick out the first one in ready queue.

//===========RR specific=============//

no preemptive.
queues are sorted by process id.
for running arbitrate, other than blocked, finished, 
should swap when running cycle > 0 & running cycle % quantum == 0 (quantum = 2)

//============SJFS specific============//

queues are sorted by remainning cpu time (cpu time - running cycle).
then use the same logic as FCFS. 