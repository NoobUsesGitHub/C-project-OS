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
    ProcessPCB nextP;//the current running process
    double globalTime=current.getStartTime();
    double turnAroundResult=0;

    while(itP.hasNext()){
        nextP=itP.next();
        //global time?
        if((current.getStartTime()+current.getTimeNeeded())>nextP.getStartTime())
        {
            current.setTimeUsed(current.getStartTime()+current.getTimeNeeded()-nextP.getStartTime());
            waiting.add(current);
            current=nextP;
            continue;
        }
        //current doesnt have someone waiting for them for now
        current.setEndTime(globalTime+(current.getTimeNeeded()-current.getTimeUsed()));
        turnAroundResult+=current.getEndTime()-current.getStartTime();
        //run the waiting queue one after another
        //run until time needed is 0
        globalTime=current.getEndTime();   
        current.setTerminated();     
        
        while(!waiting.empty()){
            current=waiting.pop();
            if((current.getStartTime()+current.getTimeNeeded())>nextP.getStartTime())
            {
                current.setTimeUsed(current.getStartTime()+current.getTimeNeeded()-nextP.getStartTime());
                waiting.add(current);
                current=nextP;
                continue;
            }
                current.setEndTime(globalTime+(current.getTimeNeeded()-current.getTimeUsed()));
            turnAroundResult+=current.getEndTime()-current.getStartTime();
            //run the waiting queue one after another
            //run until time needed is 0
            globalTime=current.getEndTime();   
            current.setTerminated();     
        {
        }
    }

    //the last process before we run the "waiting stack"

    while(!waiting.empty()){
        current=waiting.pop();
        current.setEndTime(globalTime+current.getTimeNeeded()+current.getStartTime()+current.getTimeUsed());
        turnAroundResult+=current.getEndTime()-current.getStartTime();
        //run the waiting queue one after another
        //run until time needed is 0
        globalTime=current.getEndTime();
        current.setTerminated();
    }

    turnAroundResult=turnAroundResult/ar.size();
    System.out.println("LCFS (P): mean turnaround = "+turnAroundResult);



    // while(processLeft>0)
    // {
    //     putPerTime(s,globalTime,pr);
    //     if(s.empty())
    //     {
    //         //timeskip needed
    //         minimunNext=Double.MAX_VALUE;
    //         for (Object ptemp: pr){
    //             if(!((ProcessPCB)ptemp).isTerminated()&& ((ProcessPCB)ptemp).getStartTime()>=globalTime)
    //             {
    //                 minimunNext=Math.min(minimunNext,((ProcessPCB)ptemp).getStartTime());
    //             }
    //         }
    //         if (minimunNext == Double.MAX_VALUE) break;//something went wrong
    //         globalTime=minimunNext;
    //         continue;
    //     }
    //     ProcessPCB p=s.pop();
    //     if(p.isTerminated()){//an old process that has been terminated already
    //         continue;
    //     }
    //     globalTime=Math.max(globalTime,p.getStartTime());//it's the first in the stack, therefore it will run now and will be the max time
    //     p.setEndTime(globalTime+p.getTimeNeeded());
    //     turnAroundResult+=p.getEndTime()-p.getStartTime();
    //     globalTime=p.getEndTime();
    //     p.setTerminated();
    //     processLeft--;
    // }
    // turnAroundResult=turnAroundResult/ar.size();
    // System.out.println("LCFS: mean turnaround = "+turnAroundResult);
 }
 

 //input: stack, global time and an array of proceess
 //will fill the stack with processess that have arrived since the time recieved (only those that aren't terminated already)
 private void putPerTime(Stack<ProcessPCB> s, double time, Object[] processes){
    for(int i=0;i<processes.length;i++){
        if(!(s.contains(((ProcessPCB)processes[i])))&&!(((ProcessPCB)processes[i]).isTerminated())&&(((ProcessPCB)processes[i]).getStartTime()<=time))
        {
            s.add(((ProcessPCB)processes[i]));
        }
    }
 }
}

