// Names: עוז בן משה 208639906 דוד נורמן 206395592
// Names: Oz Ben Moshe, David Norman, IDs: 208639906, 206395592
package MainPackage.Runners;

import MainPackage.Comparators.AscCompare;
import MainPackage.ProcessPackage.ProcessPCB;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;

public class SJF implements ProcessRunner {

    /**
     * STRF / SRTF (Preemptive):
     * Always run the ready process with the smallest remaining time (timeLeft).
     * If a new process arrives before the current one finishes, preempt and re-picks
     */
    @Override
    public void runProcess(Collection<ProcessPCB> ar) {
        if (ar == null || ar.isEmpty()) {
            System.out.println("SJF (P): mean turnaround = 0");
            return;
        }
        if (ar.size() == 1) {
            ProcessPCB only = ar.iterator().next();
            System.out.println("SJF (P): mean turnaround = " + only.getTimeNeeded());
            return;
        }
        if (ar.isEmpty()) {
            System.out.println("SJF (P): mean turnaround = 0");
            return;
        }


        ArrayList<ProcessPCB> procs = new ArrayList<>(ar);
        procs.sort(new AscCompare()); //double sort to ensure stable ordering
        
        
        
         // Ready queue ordered by remaining time (STRF), then arrival, then index
        Comparator<ProcessPCB> strfCmp = Comparator
                .comparingDouble(ProcessPCB::getTimeLeft)
                .thenComparingDouble(ProcessPCB::getStartTime)
                .thenComparingInt(ProcessPCB::getIndex);

        PriorityQueue<ProcessPCB> ready = new PriorityQueue<>(strfCmp);
        
        
        
        
        
        double globalTime = 0;
        double turnAroundResult = 0;
        ProcessPCB current = null;        
        int i = 0;                      // next process to "arrive" into the system
        int n = procs.size();

      // Continue while there are future arrivals or unfinished work in ready/current
        while (i < n || current != null || !ready.isEmpty()) {

            // If CPU idle and nothing ready, jump to next arrival (idle time)
            if (current == null && ready.isEmpty() && i < n) {
                globalTime = Math.max(globalTime, procs.get(i).getStartTime());
            }

            // Add all processes that have arrived by time globalTime
            while (i < n && procs.get(i).getStartTime() <= globalTime) {
                ready.add(procs.get(i));
                i++;
            }

            // If nothing running, pick the shortest remaining time job
            if (current == null) {
                current = ready.poll();
                if (current == null) continue; // safety
            }

            // Determine next arrival time (or infinity if none)
            double nextArrival = (i < n) ? procs.get(i).getStartTime() : Double.POSITIVE_INFINITY;

            // Remaining time for current
            double remaining = current.getTimeLeft();
            double finishTime = globalTime + remaining;

            if (finishTime <= nextArrival) {
                // Finish current before next arrival
                globalTime = finishTime;
                current.setTimeUsed(current.getTimeNeeded()); // mark fully used
                current.setEndTime(globalTime);
                current.setTerminated();
                turnAroundResult += current.getTurnaroundTime();
                current = null; // CPU free
            } else {
                // Run current until the next arrival, then preempt
                double runFor = nextArrival - globalTime; // > 0
                current.setTimeUsed(Math.min(current.getTimeNeeded(), current.getTimeUsed() + runFor));
                globalTime = nextArrival;

                // Preempt: put it back; next loop will pick best remaining among all ready
                ready.add(current);
                current = null;
            }
        }

        turnAroundResult = turnAroundResult / ar.size();
        System.out.println("SJF (P): mean turnaround = " + turnAroundResult);
    }
    
}
