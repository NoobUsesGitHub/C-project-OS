package MainPackage.Runners;

import MainPackage.ProcessPackage.ProcessPCB;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

public class LCFSP implements ProcessRunner{//preemptive
    @Override
 public void runProcess(Collection<ProcessPCB> ar)
 {
    Stack<ProcessPCB> waiting=new Stack<>();
    Iterator<ProcessPCB> itP=ar.iterator();
    ProcessPCB current=itP.next();//the current running process
    ProcessPCB nextP=null;//the current running process
    double globalTime=current.getStartTime();
    double turnAroundResult=0;
    int done=0;
    if(ar.size()==1){
        
        System.out.println("LCFS (P): mean turnaround = "+current.getTimeNeeded());
        return ;
    }
                nextP=itP.hasNext()?itP.next():null;
    while(done<ar.size()&&nextP!=null){
        //global time?
        globalTime=Math.max(globalTime,current.getStartTime());
        if((globalTime+current.getTimeNeeded()-current.getTimeUsed())>nextP.getStartTime())
        {
            current.setTimeUsed(nextP.getStartTime() - globalTime+current.getTimeUsed());
            waiting.add(current);
            globalTime=nextP.getStartTime();
            current=nextP;

                nextP=itP.hasNext()?itP.next():null;
            continue;
        }
        if(!current.isTerminated()){
        //current doesnt have someone waiting for them for now
        current.setEndTime(globalTime+(current.getTimeNeeded()-current.getTimeUsed()));
        turnAroundResult+=current.getTurnaroundTime();
        //run the waiting queue one after another
        //run until time needed is 0
        globalTime=current.getEndTime();
        current.setTerminated();
        done++;
        current=nextP;

                nextP=itP.hasNext()?itP.next():null;
        }
        while(!waiting.empty()){
            if(nextP==null) break;
            current=waiting.pop();
            globalTime = Math.max(globalTime, current.getStartTime());
            if((globalTime+current.getTimeNeeded()-current.getTimeUsed())>nextP.getStartTime()){
                current.setTimeUsed(nextP.getStartTime() - globalTime+current.getTimeUsed());
                waiting.add(current);
globalTime=nextP.getStartTime();
                current=nextP;
                nextP=itP.hasNext()?itP.next():null;
                break;
            }
            if(!current.isTerminated())
            {current.setEndTime(globalTime+(current.getTimeNeeded()-current.getTimeUsed()));
            turnAroundResult+=current.getTurnaroundTime();
            //run the waiting queue one after another
            //run until time needed is 0
            globalTime=current.getEndTime();
            current.setTerminated();
            done++;
            }
        }
    }
    if(!current.isTerminated()){
     current.setEndTime(globalTime+(current.getTimeNeeded()-current.getTimeUsed()));
        turnAroundResult+=current.getTurnaroundTime();
        //run the waiting queue one after another
        //run until time needed is 0
        globalTime=current.getEndTime();
        current.setTerminated(); 
        done++;
    }
    //the last process before we run the "waiting stack"

    while(!waiting.empty()){
        current=waiting.pop();
        globalTime=Math.max(globalTime,current.getStartTime());
        current.setEndTime(globalTime + (current.getTimeNeeded() - current.getTimeUsed()));
        turnAroundResult+=current.getTurnaroundTime();
        //run the waiting queue one after another
        //run until time needed is 0
        globalTime=current.getEndTime();
        current.setTerminated();
        done++;
    }

    turnAroundResult=turnAroundResult/ar.size();
    System.out.println("LCFS (P): mean turnaround = "+turnAroundResult);

 }
 
 }
