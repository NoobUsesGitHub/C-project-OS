package MainPackage.Runners;

import MainPackage.ProcessPackage.ProcessPCB;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;

public class SJF implements ProcessRunner {

    @Override
    public void runProcess(Collection<ProcessPCB> ar) {
        if (ar.size() == 1) {
            ProcessPCB only = ar.iterator().next();
            System.out.println("SJF (P): mean turnaround = " + only.getTimeNeeded());
            return;
        }
        double tempTurnAround=0;
        if (ar.isEmpty()) {
            System.out.println("SJF (P): mean turnaround = 0");
            return;
        }
        double globalTime = 0;
        double turnAroundResult = 0;
        ArrayList<ProcessPCB> arr = new ArrayList<>(ar);
        Comparator<ProcessPCB> sjfCmp
                = Comparator.comparingDouble(ProcessPCB::getTimeLeft) // total time needed
                        .thenComparingDouble(ProcessPCB::getStartTime) // arrival
                        .thenComparingInt(ProcessPCB::getIndex);
        PriorityQueue<ProcessPCB> readyQueue = new PriorityQueue<>(sjfCmp);
        ProcessPCB currentProcess = null;
        int current = 0, readyable = -1;
        while (current < arr.size()) {
            if (globalTime >= arr.get(current).getStartTime()) {
                if (readyable < current) {
                    readyQueue.add(arr.get(current));
                    readyable = current;
                }

                while (readyable + 1 < arr.size() && arr.get(readyable + 1).getStartTime() <= globalTime) {
                    readyable++;
                    readyQueue.add(arr.get(readyable));
                }
            }

            if ((currentProcess != null || !readyQueue.isEmpty())) {
                if (currentProcess == null) {
                    currentProcess = readyQueue.poll();
                }

                //is the first to work, but still needs to see if someone else will cut it off
                double remaining = currentProcess.getTimeNeeded() - currentProcess.getTimeUsed();
                double finishTime = globalTime + remaining;
                double nextTime = 0;
                if (readyable + 1 < arr.size()) {
                    nextTime = arr.get(readyable + 1).getStartTime(); 
                }else {
                    nextTime = Double.MAX_VALUE;
                }

                if (nextTime > globalTime && nextTime < finishTime) {

                    // run current until next arrival (positive duration guaranteed)
                    currentProcess.setTimeUsed(currentProcess.getTimeUsed() + (nextTime - globalTime));
                    globalTime = nextTime;

                    while (readyable + 1 < arr.size() && arr.get(readyable + 1).getStartTime() <= globalTime) {
                        readyable++;
                        readyQueue.add(arr.get(readyable));
                    }

                    // preempt ONLY if a shorter TOTAL timeNeeded is waiting
                    if (!readyQueue.isEmpty() && readyQueue.peek().getTimeNeeded() < currentProcess.getTimeNeeded()) {
                        readyQueue.add(currentProcess);
                        currentProcess = null; // next loop will poll the shorter job
                    }

                } else {
                    // we can finish this process before the next arrival (or no future arrival)
                    globalTime = finishTime;
                    currentProcess.setEndTime(globalTime);
                    currentProcess.setTerminated();
                    tempTurnAround=currentProcess.getTurnaroundTime();
                    turnAroundResult += tempTurnAround;
                    currentProcess = null;
                }
            } else {
                //time skip
                globalTime = Math.max(globalTime, arr.get(current).getStartTime());
                if (readyable < current) {
                    readyQueue.add(arr.get(current));
                    readyable = current;
                }
            }
            current = readyable + 1;
        }
        if (currentProcess != null) {
            globalTime += (currentProcess.getTimeNeeded() - currentProcess.getTimeUsed());
            currentProcess.setEndTime(globalTime);
            currentProcess.setTerminated();
            tempTurnAround=currentProcess.getTurnaroundTime();
            turnAroundResult += tempTurnAround;
            currentProcess = null;
        }

        while (!readyQueue.isEmpty()) {
            currentProcess = readyQueue.poll();
            //run current to finish nothing left to preempt it 
            globalTime = globalTime + currentProcess.getTimeNeeded() - currentProcess.getTimeUsed();//finish current
            currentProcess.setTerminated();
            currentProcess.setEndTime(globalTime);
            tempTurnAround=currentProcess.getTurnaroundTime();
            turnAroundResult += tempTurnAround;
        }

        turnAroundResult = turnAroundResult / ar.size();
        System.out.println("SJF (P): mean turnaround = " + turnAroundResult);
    }
//sjf Looks at total, not what's left
}
