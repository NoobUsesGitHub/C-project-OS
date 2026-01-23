// Names: עוז בן משה 208639906 דוד נורמן 206395592
// Names: Oz Ben Moshe, David Norman, IDs: 208639906, 206395592
package MainPackage.Runners;

import MainPackage.ProcessPackage.ProcessPCB;
import java.util.Collection;

public class FCFS implements ProcessRunner{
    
    /**
     * First-Come First-Served (non-preemptive):
     * - Processes are executed in order of arrival time (startTime).
     * - If the CPU becomes idle (no process has arrived yet), globalTime jumps forward
     *   to the next process's startTime.
     *
     * Assumption: 'ar' is already sorted by (startTime, index) using AscCompare.
     */
    @Override
    public void runProcess(Collection<ProcessPCB> ar){
        // Edge case: no processes -> mean turnaround is 0 (or you can print a message)
        if (ar == null || ar.isEmpty()) {
            System.out.println("FCFS: mean turnaround = 0");
            return;
        }
        double globalTime=0.0;
        double turnAroundResult=0.0;
        for(ProcessPCB p:ar)//zero times and delays don't matter here
        {
             // If CPU is idle until this process arrives, jump time forward to arrival.
            globalTime = Math.max(globalTime, p.getStartTime());

            // In FCFS, once we start the process, it runs to completion.
            p.setEndTime(globalTime + p.getTimeNeeded());

            // Turnaround = end - start (arrival)
            turnAroundResult += p.getTurnaroundTime();

            // Advance global time to the completion of this process.
            globalTime = p.getEndTime();
        }
        System.out.println("FCFS: mean turnaround = "+turnAroundResult/ar.size());
    }
}

