package MainPackage.Runners;

import MainPackage.ProcessPackage.ProcessPCB;
import java.util.Collection;

public class FCFS implements ProcessRunner{
    @Override
    public void runProcess(Collection<ProcessPCB> ar){
        double globalTime=0;
        double turnAroundResult=0;
        for(ProcessPCB p:ar)//zero times and delays don't matter here
        {
            //fix
            p.setEndTime(p.getStartTime()+p.getTimeNeeded());
            turnAroundResult=p.getEndTime()-p.getStartTime();
        }
        turnAroundResult=turnAroundResult/ar.size();
        System.out.println("FCFS: mean turnaround = "+turnAroundResult);
    }
}

