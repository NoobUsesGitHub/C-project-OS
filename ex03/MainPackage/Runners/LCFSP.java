package MainPackage.Runners;

import MainPackage.ProcessPackage.ProcessPCB;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

public class LCFSP implements ProcessRunner {//preemptive

    @Override
    public void runProcess(Collection<ProcessPCB> ar) {
        if (ar.size() == 1) {
            ProcessPCB only = ar.iterator().next();
            System.out.println("LCFSP (P): mean turnaround = " + only.getTimeNeeded());
            return;
        }
        if (ar.isEmpty()) {
            System.out.println("LCFSP (P): mean turnaround = 0");
            return;
        }
        Stack<ProcessPCB> waiting = new Stack<>();
        Iterator<ProcessPCB> itP = ar.iterator();
        ProcessPCB current = itP.next();//the current running process
        ProcessPCB nextP = null;//the current running process
        double globalTime = current.getStartTime();
        double nextStart = 0, runFor = 0;
        double turnAroundResult = 0;
        int done = 0;
        nextP = itP.hasNext() ? itP.next() : null;
        while (done < ar.size() && nextP != null) {
            //global time?
            globalTime = Math.max(globalTime, current.getStartTime());
            while (nextP != null && nextP.getStartTime() <= globalTime) {
                waiting.push(nextP);
                nextP = itP.hasNext() ? itP.next() : null;
            }
            if (nextP != null && nextP.getStartTime() > globalTime && (globalTime + current.getTimeNeeded() - current.getTimeUsed()) > nextP.getStartTime()) {
                nextStart = nextP.getStartTime();
                runFor = Math.max(0, nextStart - globalTime);

                current.setTimeUsed(current.getTimeUsed() + runFor);
                globalTime += runFor;
                waiting.push(current);
                current = nextP;
                nextP = itP.hasNext() ? itP.next() : null;
                continue;
            }
            if (!current.isTerminated()) {
                //current doesnt have someone waiting for them for now

                current.setEndTime(globalTime + (current.getTimeNeeded() - current.getTimeUsed()));
                turnAroundResult += current.getTurnaroundTime();
                //run the waiting queue one after another
                //run until time needed is 0
                globalTime = current.getEndTime();
                current.setTerminated();
                done++;
                current = nextP;

                nextP = itP.hasNext() ? itP.next() : null;
            }
            while (!waiting.empty()) {
                if (nextP == null) {
                    break;
                }
                current = waiting.pop();
                globalTime = Math.max(globalTime, current.getStartTime());
                while (nextP != null && nextP.getStartTime() <= globalTime) {
                    waiting.push(nextP);
                    nextP = itP.hasNext() ? itP.next() : null;
                }

                if (nextP != null && nextP.getStartTime() > globalTime && (globalTime + current.getTimeNeeded() - current.getTimeUsed()) > nextP.getStartTime()) {
                    nextStart = nextP.getStartTime();
                    runFor = Math.max(0, nextStart - globalTime);

                    current.setTimeUsed(current.getTimeUsed() + runFor);
                    globalTime += runFor;
                    waiting.push(current);
                    current = nextP;
                    nextP = itP.hasNext() ? itP.next() : null;

                    break;
                }
                if (!current.isTerminated()) {
                    globalTime = Math.max(globalTime, current.getStartTime());
                    current.setEndTime(globalTime + (current.getTimeNeeded() - current.getTimeUsed()));
                    turnAroundResult += current.getTurnaroundTime();
                    globalTime = current.getEndTime();
                    current.setTerminated();
                    done++;
                }
            }
        }
        if (!current.isTerminated()) {
            globalTime = Math.max(globalTime, current.getStartTime());
            current.setEndTime(globalTime + (current.getTimeNeeded() - current.getTimeUsed()));
            turnAroundResult += current.getTurnaroundTime();
            //run the waiting queue one after another
            //run until time needed is 0
            globalTime = current.getEndTime();
            current.setTerminated();
            done++;
        }
        //the last process before we run the "waiting stack"

        while (!waiting.empty()) {
            current = waiting.pop();
            globalTime = Math.max(globalTime, current.getStartTime());
            current.setEndTime(globalTime + (current.getTimeNeeded() - current.getTimeUsed()));
            turnAroundResult += current.getTurnaroundTime();
            //run the waiting queue one after another
            //run until time needed is 0
            globalTime = current.getEndTime();
            current.setTerminated();
            done++;
        }

        turnAroundResult = turnAroundResult / ar.size();
        System.out.println("LCFS (P): mean turnaround = " + turnAroundResult);

    }

}
