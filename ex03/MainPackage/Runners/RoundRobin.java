package MainPackage.Runners;

import MainPackage.ProcessPackage.ProcessPCB;
import java.util.Collection;

public class RoundRobin implements ProcessRunner{
    private double timeQuantom=2;

    @Override
public void runProcess(Collection<ProcessPCB> ar){
    ProcessPCB[] arr=ar.toArray(new ProcessPCB[0]);
    
    int done=0;
    int i=0;
    double globalTime=arr[0].getStartTime();
    double turnAroundResult=0;
    while(done<ar.size()){
        if(!arr[i].isTerminated()){
            if(arr[i].getTimeNeeded()-timeQuantom>=0){//runs for time quantom and has extra
                globalTime+=timeQuantom;
                arr[i].setTimeNeeded(arr[i].getTimeNeeded()-timeQuantom);
                arr[i].setTimeUsed(arr[i].getTimeUsed()+timeQuantom);
                if(arr[i].getTimeNeeded()==0){
                    arr[i].setTerminated();
                    arr[i].setEndTime(globalTime);
                    turnAroundResult+=arr[i].getTurnaroundTime();
                    done++;
                }
            }else{//we finish before the time quantom needed
                double remaining= timeQuantom- arr[i].getTimeNeeded();
                globalTime+=remaining;
                arr[i].setTimeNeeded(arr[i].getTimeNeeded()-remaining);
                arr[i].setTimeUsed(arr[i].getTimeUsed()+remaining);
                arr[i].setTerminated();
                arr[i].setEndTime(globalTime);
                turnAroundResult+=arr[i].getTurnaroundTime();
                done++;
            }
            if(arr[(i+1)%ar.size()].getStartTime()>globalTime){
                //we stay in place, nobody is arriving for the next time quantom
                if(arr[i].isTerminated())//time skip
                {
                    globalTime=Math.max(globalTime,arr[(i+1)%ar.size()].getStartTime());
                }else{
                    i=i-1;
                }
            }
        }
        i=(i+1)%ar.size();
    }
    
    turnAroundResult=turnAroundResult/arr.length;
    System.out.println("RR: mean turnaround = "+turnAroundResult);
}
}
