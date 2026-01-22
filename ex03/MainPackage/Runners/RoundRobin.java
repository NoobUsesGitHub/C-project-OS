// Names: עוז בן משה 208639906 דוד נורמן 206395592
// Names: Oz Ben Moshe, David Norman, IDs: 208639906, 206395592
package MainPackage.Runners;

import MainPackage.ProcessPackage.ProcessPCB;
import java.util.Collection;

public class RoundRobin implements ProcessRunner {
    // Time quantum for Round Robin (can be changed if needed)

    private final double timeQuantum = 2.0;

    /**
     * Round Robin (RR):
     * the head for min(quantum, remaining time). - If it still has time left,
     * push it back to the tail. - If no process is ready, jump time to the next
     * arrival (CPU idle).
     */
    @Override
    public void runProcess(Collection<ProcessPCB> ar) {
        if (ar == null || ar.isEmpty()) {
            System.out.println("RR: mean turnaround = 0");
            return;
        }
        if (ar.size() == 1) {
            ProcessPCB only = ar.iterator().next();
            System.out.println("RR: mean turnaround = " + only.getTimeNeeded());
            return;
        }
        ProcessPCB[] procs = ar.toArray(new ProcessPCB[0]);
        int done = 0;
        int i = 0;
        int size=ar.size();
        double globalTime = procs[0].getStartTime();
        double turnAroundResult = 0;

        
        while (done < ar.size()) {
              // If this process hasn't arrived yet or is already terminated -> just skip it
            if (procs[i].isTerminated() || procs[i].getStartTime() > globalTime) {
                // If we're about to loop and nobody is ready, we need to time-skip to the next arrival
                if (!anyReady(procs, globalTime)) {
                    globalTime = nextArrivalTime(procs, globalTime);
                }
                i = (i + 1) % size;
                continue;
            }

            double remaining = procs[i].getTimeLeft();
            double runFor = Math.min(this.timeQuantum, remaining); //might not need full quantum
            globalTime += runFor;
            procs[i].setTimeUsed(procs[i].getTimeUsed() + runFor);
             // If finished now, mark endTime + terminated and accumulate turnaround
            if (procs[i].getTimeLeft() <= 0) {
                procs[i].setTerminated();
                procs[i].setEndTime(globalTime);
                turnAroundResult += procs[i].getTurnaroundTime();
                done++;
            }

            
            // If after this slice no process is ready at current time, time-skip forward.
            if (!anyReady(procs, globalTime) && done < size) {
                globalTime = nextArrivalTime(procs, globalTime);
            }
            i = (i + 1) % size;
        }
        turnAroundResult = turnAroundResult / procs.length;
        System.out.println("RR: mean turnaround = " + turnAroundResult);
    }


    
    /**
     * @return true if at least one process is arrived (startTime <= time) and not terminated
     */
    private boolean anyReady(ProcessPCB[] procs, double time) {
        for (ProcessPCB p : procs) {
            if (!p.isTerminated() && p.getStartTime() <= time) {
                return true;
            }
        }
        return false;
    }

    
    /**
     * Finds the next startTime strictly after 'time' among non-terminated processes.
     * If none exist, returns the same time (should not happen if done < n).
     */
    private double nextArrivalTime(ProcessPCB[] procs, double time) {
        double next = Double.POSITIVE_INFINITY;
        for (ProcessPCB p : procs) {
            if (!p.isTerminated() && p.getStartTime() > time) {
                next = Math.min(next, p.getStartTime());
            }
        }
        return (next == Double.POSITIVE_INFINITY) ? time : next;
    }
}
