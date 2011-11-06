//======================================//
|Operating Systems Project #2: Scheduler |
|Prof. Mohamed Zahran                    |
|                                        |
|Student Name: Yaxing Chen               |
|NID: 16929794                           |
|email: yc1116@nyu.edu                   |
//======================================//

//===========Environment================//
JRE 6


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
6. jar cvfm proj_scheduler_YaxingChen.jar jar-manifest -C src/ .


//===============How to run=============//

java -jar proj
