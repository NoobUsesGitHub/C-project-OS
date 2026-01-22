// Names: עוז בן משה 208639906 דוד נורמן 206395592
// Names: Oz Ben Moshe, David Norman, IDs: 208639906, 206395592
package MainPackage.Runners;

import MainPackage.Comparators.AscCompare;
import MainPackage.ProcessPackage.ProcessPCB;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class LCFSP implements ProcessRunner {//preemptive

    /**
     * LCFS (P) = Last-Come First-Served, Preemptive
     *
     * Rule: - Always run the most recently arrived process among the ready
     * ones. - When a new process arrives, preempt the current one immediately
     * (push it back to the stack with its remaining time).
     *
     * Implementation approach: - Sort processes by (startTime, index) so
     * arrivals are processed in order. - Keep a "ready" stack for
     * arrived-but-not-finished processes. - Simulate time by jumping to the
     * next event: (a) next arrival, or (b) current process finishing
     */
    @Override
    public void runProcess(Collection<ProcessPCB> ar) {
        if (ar == null || ar.isEmpty()) {
            System.out.println("LCFS (P): mean turnaround = 0");
            return;
        }
        if (ar.size() == 1) {
            ProcessPCB only = ar.iterator().next();
            System.out.println("LCFSP (P): mean turnaround = " + only.getTimeNeeded());
            return;
        }

        // Work on a sorted list (Collection iteration order is not reliable).
        ArrayList<ProcessPCB> procs = new ArrayList<>(ar);
        procs.sort(new AscCompare());

        Stack<ProcessPCB> ready = new Stack<>();

        double globalTime = 0.0;
        double sumTurnaround = 0.0;

        int i = 0;                 // pointer to next process that hasn't "arrived" into the system yet
        int n = procs.size();
        ProcessPCB current = null; // currently running process (null if CPU idle)
        while (i < n || current != null || !ready.isEmpty()) {

            // If CPU is idle and nothing is ready, jump time to the next arrival.
            if (current == null && ready.isEmpty() && i < n) {
                globalTime = Math.max(globalTime, procs.get(i).getStartTime());
            }

            // Push all processes that have arrived by globalTime into the stack.
            // Important: pushing in sorted order means the "latest" arrival ends up on top.
            while (i < n && procs.get(i).getStartTime() <= globalTime) {
                ready.push(procs.get(i));
                i++;
            }

            // If nothing is currently running, pick the last arrived ready process.
            if (current == null) {
                if (ready.isEmpty()) {
                    continue; // rare : loop will time-skip above
                }
                current = ready.pop();
            }

            // Next arrival time (or infinity if no more arrivals)
            double nextArrival = (i < n) ? procs.get(i).getStartTime() : Double.POSITIVE_INFINITY;

            // Remaining time for the current process
            double remaining = current.getTimeLeft();

            // Case 1: current finishes before the next arrival -> run to completion
            if (globalTime + remaining <= nextArrival) {
                globalTime += remaining;
                current.setTimeUsed(current.getTimeNeeded()); // fully used
                current.setEndTime(globalTime);
                current.setTerminated();

                sumTurnaround += current.getTurnaroundTime();
                current = null; // CPU becomes free
                continue;
            }

            // Case 2: a new process arrives before current finishes -> run until that arrival, then preempt
            double runFor = nextArrival - globalTime;
            current.setTimeUsed(current.getTimeUsed() + runFor);
            globalTime = nextArrival;

            // Preempt: put the partially-run current back on the stack
            ready.push(current);
            current = null; // will pick the newest arrival next loop iteration
        }

        double meanTurnaround = sumTurnaround / n;
        System.out.println("LCFS (P): mean turnaround = " + meanTurnaround);
    }
}