// Names: Oz Ben Moshe, David Norman, IDs: 208639906, 206395592
package MainPackage.Runners;

import MainPackage.ProcessPackage.ProcessPCB;
import java.util.Collection;
import java.util.Stack;

public class LCFSNP implements ProcessRunner {//non preemptive

    /**
     * LCFS (NP) = Last-Come First-Served, Non-Preemptive
     *
     * Idea: - Maintain a stack of "ready" processes (already arrived). - When
     * the CPU becomes free, pick the most recently arrived ready process (stack
     * pop). - Run it to completion (non-preemptive). - If nothing is ready,
     * jump time forward to the next arrival (idle time).
     *
     * Tie-breaking note: - We sort by (startTime, index) ascending before
     * pushing into the stack. - For equal startTime, higher index is pushed
     * later -> popped first.
     */
    @Override
    public void runProcess(Collection<ProcessPCB> ar) {
        if (ar == null || ar.isEmpty()) {
            System.out.println("LCFS (NP): mean turnaround = 0");
            return;
        }
        if (ar.size() == 1) {
            ProcessPCB only = ar.iterator().next();
            System.out.println("LCFS (NP): mean turnaround = " + only.getTimeNeeded());
            return;
        }
        Stack<ProcessPCB> s = new Stack<>();
        Object[] pr = ar.toArray();
        double globalTime = ((ProcessPCB) (pr)[0]).getStartTime();
        double minimunNext, turnAroundResult = 0.0;
        int processLeft = ar.size();
        while (processLeft > 0) {
            putPerTime(s, globalTime, pr);
            
            if (s.empty()) {
                //timeskip needed
                minimunNext = Double.MAX_VALUE;
                for (Object ptemp : pr) {
                    if (!((ProcessPCB) ptemp).isTerminated() && ((ProcessPCB) ptemp).getStartTime() >= globalTime) {//not terminated and in the future (next arrival)
                        minimunNext = Math.min(minimunNext, ((ProcessPCB) ptemp).getStartTime());
                    }
                }
                if (minimunNext == Double.MAX_VALUE) {
                    break;//something went wrong
                }
                globalTime = minimunNext;
                continue;
            }
            ProcessPCB p = s.pop();
            if (p.isTerminated()) {//an old process that has been terminated already
                continue;
            }

            //it's the first in the stack, therefore it will run now and will be the max time
            globalTime = Math.max(globalTime, p.getStartTime());
            p.setEndTime(globalTime + p.getTimeNeeded());
            turnAroundResult += p.getTurnaroundTime();
            globalTime = p.getEndTime();
            p.setTerminated();
            processLeft--;
        }
        turnAroundResult = turnAroundResult / ar.size();
        System.out.println("LCFS (NP): mean turnaround = " + turnAroundResult);
    }

    //input: stack, global time and an array of proceess
    //will fill the stack with processess that have arrived since the time recieved (only those that aren't terminated already)
    private void putPerTime(Stack<ProcessPCB> s, double time, Object[] processes) {
        for (int i = 0; i < processes.length; i++) {
            if (!(((ProcessPCB) processes[i]).isTerminated()) &&!(s.contains(((ProcessPCB) processes[i]))) &&  (((ProcessPCB) processes[i]).getStartTime() <= time)) {
                s.add(((ProcessPCB) processes[i]));
            }
        }
    }
}
