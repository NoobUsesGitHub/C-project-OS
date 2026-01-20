package MainPackage.Runners;

import MainPackage.ProcessPackage.ProcessPCB;
import java.util.Collection;

public class FCFS implements ProcessRunner{
    @Override
    //runs the processes on each process (after sorting them on run time)
    //calculates turnaround for every process
    public void runProcess(Collection<ProcessPCB> ar){
        double globalTime=0;
        double turnAroundResult=0;
        for(ProcessPCB p:ar)//zero times and delays don't matter here
        {
            //fix
            globalTime=Math.max(globalTime,p.getStartTime());
            p.setEndTime(globalTime+p.getTimeNeeded());
            turnAroundResult+=p.getTurnaroundTime();
            globalTime=p.getEndTime();
        }
        turnAroundResult=turnAroundResult/ar.size();
        System.out.println("FCFS: mean turnaround = "+turnAroundResult);
    }
}

